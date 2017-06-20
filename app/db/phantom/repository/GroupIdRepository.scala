package db.phantom
package repository

import java.util.UUID

import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.dsl._
import com.typesafe.config.ConfigFactory
import db.model._
import db.phantom.connector.Connector

import scala.concurrent.Future

class GroupIdDB(override val connector: CassandraConnection) extends Database[GroupIdDB](connector) {
  object groupIds extends GroupIdTable with Connector
}


trait GroupIdRepo {

  def findByGroupId(groupId: UUID): Future[List[GroupId]]
  def findById(groupId: UUID, id: UUID): Future[Option[GroupId]]
  def saveEntry(groupId: GroupId): Future[Boolean]
  def delete(groupId: UUID, id: UUID): Future[Boolean]

}

object GroupDatabase {

  private val config = ConfigFactory.load()
  private val useSSL: Boolean = config.getBoolean("db.cassandra.ssl")

  val db = new GroupIdDB(Connector.connector(useSSL))
}


class GroupIdRepository extends DatabaseProvider[GroupIdDB] with GroupIdRepo {

  val database = GroupDatabase.db


  private[this] val findByGroupQuery = database.groupIds.select.where(_.groupId eqs ?).prepare()
  private[this] val findByIdQuery = database.groupIds.select.where(_.groupId eqs ?).and(_.id eqs ?).prepare()
  private[this] val deleteByIdQuery = database.groupIds.delete.where(_.groupId eqs ?).and(_.id eqs ?).prepare()

  /**
    *
    * @param groupId
    * @return
    */
  def findByGroupId(groupId: UUID): Future[List[GroupId]] =
  findByGroupQuery.bind(groupId).fetch()


  def findById(groupId: UUID, id: UUID): Future[Option[GroupId]] = findByIdQuery.bind(groupId, id).one()

  val saveQuery = database.groupIds.insert
    .p_value(_.groupId, ?)
    .p_value(_.id, ?)
    .p_value(_.createTs, ?)
    .prepare()

  /**
    *
    * @param ae
    * @return
    */
  def saveEntry(ae: GroupId): Future[Boolean] = {
    save(ae, isNew = true)
  }

  /**
    * Save an GroupId
    *
    * @param gi
    * @return
    */
  def save(gi: GroupId, isNew: Boolean): Future[Boolean] = {
   saveQuery.bind(gi).future() map (_ => true)
  }

  /**
    * Delete a GroupId
    *
    * @param groupId
    * @param id
    *
    * @return
    */
  def delete(groupId: UUID, id: UUID): Future[Boolean] = {
    for {
      del2 <- deleteByIdQuery.bind(groupId, id).future().map(_.wasApplied())
    } yield del2
  }
}
