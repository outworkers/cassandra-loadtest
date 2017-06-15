package db.phantom

import java.util.UUID

import com.outworkers.phantom.dsl.{ context => _, _ }
import db.model.GroupId

import scala.concurrent.Future

abstract class GroupIdTable extends Table[GroupIdTable, GroupId] {

  override def tableName: String = "group_id"

  // First the partition key, which is also a Primary key in Cassandra.
  object groupId extends UUIDColumn with PartitionKey {
    override lazy val name = "group_id"
  }

  object id extends UUIDColumn with PrimaryKey {
    override lazy val name = "create_ts"
  }

  // Only keyed fields can be queried on

  object createTs extends DateColumn with PrimaryKey

  def findByGroupId(groupId: UUID): Future[List[GroupId]] =
    select
      .where(_.groupId eqs groupId)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .fetch()

  def findById(groupId: UUID, id: UUID): Future[Option[GroupId]] =
    select
      .where(_.groupId eqs groupId)
      .and(_.id eqs id)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .one()

  def save(groupId: GroupId): Future[ResultSet] = {
    insert
      .value(_.id, groupId.id)
      .value(_.groupId, groupId.groupId)
      .value(_.createTs, groupId.createTs)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .future()
  }

  def deleteById(groupId: UUID,  id: UUID): Future[ResultSet] =
    delete
      .where(_.groupId eqs groupId)
      .and(_.id eqs id)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .future()

}
