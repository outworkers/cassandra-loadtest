package db.model

import java.util.Date

import dto.ErrorCode.ErrorCode
import dto.{EnumerationHelpers, ErrorCode, ErrorResponse, GroupResponse}
import play.api.libs.json._

class JsonProtocol {

  implicit lazy val format = Json.format[ErrorResponse]

  implicit lazy val dateFormat: OFormat[Date] = new OFormat[Date] {

    override def reads(json: JsValue): JsResult[Date] = (json \ "date").validate[Long].map(new Date(_))

    override def writes(o: Date): JsObject = JsObject(Seq("date" -> JsNumber(o.getTime)))
  }

  implicit lazy val groupIdFormat: OFormat[GroupId] = Json.format[GroupId]

  implicit lazy val groupResponseFormat = Json.format[GroupResponse]

  implicit lazy val enumReads: Reads[ErrorCode] = EnumerationHelpers.enumReads(ErrorCode)
  implicit lazy val enumWrites: Writes[ErrorCode] = EnumerationHelpers.enumWrites
}

object JsonProtocol extends JsonProtocol {
  implicit class JsonHelper[T](val obj: T) extends AnyVal {
    def asJValue()(implicit writes: Writes[T]): JsValue = Json.toJson(obj)
  }
}
