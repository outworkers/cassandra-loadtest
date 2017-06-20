import java.util.{Date, UUID}

import db.model.GroupId
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.OptionValues
import org.scalatestplus.play._
import play.api.libs.json._
import play.api.test.Helpers.{route, _}
import play.api.test._
import db.model.JsonProtocol._
import dto.GroupResponse
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

class ControllerSpec extends PlaySpec with GuiceOneAppPerSuite with OptionValues {

  val fmtBack: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(DateTimeZone.UTC)
  val groupId = UUID.randomUUID
  val id = UUID.randomUUID
  val createTs: DateTime = new DateTime(DateTimeZone.UTC)
  val createDate: Date = new Date
  val groupIdObject = GroupId(groupId, id, createTs.toDate)
  val groupIdJson = groupIdObject.asJValue()
  val respJson = GroupResponse(id, groupIdObject :: Nil).asJValue()


  "GroupController /groups/{groupId}/ids POST" should {
    "return a ok when new group id is posted" in {


      val request = FakeRequest(POST, "/groups/" + groupId + "/ids")
        .withHeaders("Host" -> "localhost")
        .withBody(groupIdJson)

      val response = route(app, request).value

      status(response) mustBe CREATED
      contentAsJson(response) mustBe Json.obj("status" -> "created")
    }

    "return a group id when get end point with the groupId UID is hit" in {
      val request = FakeRequest(GET, "/groups/" + groupId + "/ids")
        .withHeaders("Host" -> "localhost", "Accept-Language" -> "en")

      val response = route(app, request).value

      status(response) mustBe OK
      contentAsJson(response).toString() mustBe respJson.toString()
    }

  /*

  "return ok when the group id entry is deleted from DELETE" in {

      val deleteRequest = FakeRequest(DELETE, "/groups/" + groupId + "/ids/" + id)
        .withHeaders("Host" -> "localhost")

      val deleteResponse = route(app, deleteRequest).get
      status(deleteResponse) mustBe NO_CONTENT

      val getRequest = FakeRequest(GET, "/groups/" + groupId + "/ids")
        .withHeaders("Host" -> "localhost", "Accept-Language" -> "en")

      val getResponse = route(app, getRequest).get
      status(getResponse) mustBe OK

      val content = (contentAsJson(getResponse) \ "ids").get
      content mustBe JsArray()
    }

    "return empty json on GET" in {
      val getRequest = FakeRequest(GET, "/groups/" + groupId + "/ids")
        .withHeaders("Host" -> "localhost", "Accept-Language" -> "en")

      val getResponse = route(app, getRequest).get
      status(getResponse) mustBe OK

      val content = (contentAsJson(getResponse) \ "ids").get
      content mustBe JsArray()
    }*/

  }


  "GroupController /groups2/{groupId}/ids POST" should {
    "return a ok when new group id is posted" in {

      val request = FakeRequest(POST, "/groups2/" + groupId + "/ids")
        .withHeaders("Host" -> "localhost")
        .withBody(groupIdJson)

      info("DATE USED: " + createDate.getTime)

      val response = route(app, request).value

      status(response) mustBe CREATED

      contentAsJson(response) mustBe Json.obj(
        "status" -> "created"
      )

    }

    "return a group id when get end point with the groupId UID is hit" in {
      val request = FakeRequest(GET, "/groups2/" + groupId + "/ids")
        .withHeaders("Host" -> "localhost", "Accept-Language" -> "en")

      info("DATE GOT BACK: " + createDate.getTime)

      val response = route(app, request).value
      status(response) mustBe OK
      contentAsJson(response).toString() mustBe respJson.toString()
    }
  }


}