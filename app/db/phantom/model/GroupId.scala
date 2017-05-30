package db.phantom.model

import java.util.UUID

import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter, ISODateTimeFormat}
import play.api.libs.json._
import _root_.util.Util

case class GroupId(groupId: UUID, id: UUID, createTs: DateTime)

object GroupId {

  implicit val readsJodaLocalDateTime = Reads[DateTime](js =>
    js.validate[String].map[DateTime](dtString =>
      DateTime.parse(dtString, Util.fmt)
    )
  )

  implicit val groupIdFormat = Json.format[GroupId]
}
