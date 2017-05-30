package db.model

import java.util.{Date, UUID}
import play.api.libs.json._

case class GroupId(
  groupId: UUID,
  id: UUID,
  createTs: Date
)

object GroupId {
  implicit val groupIdFormat: OFormat[GroupId] = Json.format[GroupId]
}
