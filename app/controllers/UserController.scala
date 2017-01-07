package controllers

import javax.inject._

import play.api.Logger
import play.api.libs.json._
import play.api.mvc._
import play.api._
import play.modules.reactivemongo._
import reactivemongo.api.ReadPreference
import reactivemongo.play.json._
import reactivemongo.play.json.collection._
import models.User
import reactivemongo.bson.BSONObjectID

import scala.concurrent.{ExecutionContext, Future}



@Singleton
class UserController @Inject()(val reactiveMongoApi: ReactiveMongoApi)(implicit exec: ExecutionContext) extends Controller with MongoController with ReactiveMongoComponents {
  def collection: JSONCollection = db.collection[JSONCollection]("users")
  def collectionAuth: JSONCollection = db.collection[JSONCollection]("authTokens")

  def createUser(user: User) = {
    collection.insert(user).map{lastError =>
      if(lastError.ok)
        Ok(Json.obj("result" -> "success", "ObjectId" ->user._id))
      else 
        InternalServerError(Json.obj("result" -> "failed", "reason" -> "database error"))

    }
  }


  def createUserWithoutChecking = Action.async(parse.json){ request =>
    val objectId = BSONObjectID.generate
    request.body.validate[User].map{user =>
      createUser(user.copy(_id=Some(objectId)))

    }.getOrElse(Future.successful(BadRequest(Json.obj("result" -> "failed", "reason" -> "invalid json"))))
    
  }

  def checkSessionAndCreateUser = Action.async(parse.json){ request =>
    val objectId = BSONObjectID.generate
    request.session.get("userId").map { validated =>
      request.body.validate[User].map{user =>
        createUser(user.copy(_id=Some(objectId)))

      }.getOrElse(Future.successful(BadRequest(Json.obj("result" -> "failed", "reason" -> "invalid json"))))
    }.getOrElse(Future(Unauthorized("Oops, you are not connected")))
  }

  



  
}


