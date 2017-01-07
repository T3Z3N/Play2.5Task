package models

import play.api.libs.json.Json
import play.modules.reactivemongo.json._
import reactivemongo.bson.BSONObjectID


case class Company(
	companyName:String,
	companyType:String
	)
case class User(
	_id:Option[BSONObjectID],
	firstName: String,
	lastName: String,
	company:Company
	)

object User {
  implicit val companyFormat = Json.format[Company]
  implicit val userFormat = Json.format[User]
}
