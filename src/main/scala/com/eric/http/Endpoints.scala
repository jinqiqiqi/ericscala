package com.eric.http

import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.eric.DispatcherActor
import com.eric.common._
import com.eric.common.Constants._
import spray.http.StatusCodes
import spray.httpx.SprayJsonSupport._
import spray.httpx.marshalling.ToResponseMarshallable
import spray.json.RootJsonFormat
import spray.routing.Directives._
import spray.routing.Route

/**
 * Created by kinch on 12/20/16.
 */
trait Endpoints {
  import Serialize._

  def routes(system: ActorSystem)(implicit timeout: Timeout, ec: ExecutionContext): Route = {
    val dispatcher = system.actorSelection(DispatcherActor.actorPath)

    def blocking[T: RootJsonFormat: ClassTag](req: Request): Route =
      complete(dispatcher.ask(req).map[ToResponseMarshallable] {
        case err: Failed => (StatusCodes.BadRequest, err)
        case response: T => response
      })

    def nonblocking(req: Request): Route = {
      dispatcher ! req
      complete(StatusCodes.OK)
    }
    get {
      path("user")(parameters('uid.as[Int] ? 1).as(GetUser)(p => blocking[UserProfile](p)))
    } ~
    post {
      path("login")(entity(as[Login])(p => blocking[UserProfile](p)))
    }

  }

}
