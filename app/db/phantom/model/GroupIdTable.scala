package db.phantom.model

import java.util.UUID

import com.outworkers.phantom.CassandraTable
import com.outworkers.phantom.dsl._
import org.joda.time.DateTime

import scala.concurrent.Future

abstract class GroupIdTable extends Table[GroupIdTable, GroupId] {

  override def tableName: String = "group_id"

  // First the partition key, which is also a Primary key in Cassandra.
  object id extends UUIDColumn with PartitionKey

  // Only keyed fields can be queried on
  object groupId extends UUIDColumn with PartitionKey {override lazy val name = "group_id"}
  object createTs extends DateTimeColumn with ClusteringOrder {override lazy val name = "create_ts"}

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
    Console.println("Called Account Entry Id Table save. ")
    store(groupId)
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
