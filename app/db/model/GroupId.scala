package db.model

import java.util.{Date, UUID}

import org.joda.time.DateTime
import play.api.libs.json._
import _root_.util.Util

case class GroupId(groupId: UUID, id: UUID, createTs: Date)

object GroupId {

  implicit val readsJodaLocalDateTime = Reads[DateTime](js =>
    js.validate[String].map[DateTime](dtString =>
      DateTime.parse(dtString, Util.fmt)
    )
  )

  implicit val groupIdFormat = Json.format[GroupId]
}
