package dto

import java.util.UUID

import db.model.GroupId
import play.api.libs.json.{JsValue, Json}

case class QuillGroupResponse(groupId: UUID, ids: List[GroupId])

object QuillGroupResponse {
  implicit val quillGroupResponseFormat = Json.format[QuillGroupResponse]

  implicit class QuillGroupResponseOps(val gr: QuillGroupResponse) extends AnyVal {
    def toJson: JsValue = Json.toJson(gr)
  }
}