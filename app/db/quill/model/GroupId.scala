package db.quill.model

import java.util.{Date, UUID}

import play.api.libs.json.Json
import play.api.libs.json._
import _root_.util.Util

/**
  * Created by yleung on 2017-05-26.
  */
case class GroupId(groupId: UUID, id: UUID, createTs: Date)

object GroupId {

  implicit val readsDate: Reads[Date] = Reads[Date](js =>
    js.validate[String].map[Date](dtString => Util.sdf.parse(dtString))
  )

  implicit val groupIdFormat: OFormat[GroupId] = Json.format[GroupId]

}
