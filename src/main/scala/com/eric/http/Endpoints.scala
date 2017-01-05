package com.eric.http


import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.eric.DispatcherActor
import com.eric.common._
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
  import com.eric.common.Serialize._

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
      path("e")(parameters('t.as[String], 'id.as[Long]).as(GetEntity)(p => blocking[Entity](p))) ~
      path("es")(parameters('t.as[String], 'ids.as[String]).as(GetEntities)(p => blocking[ValueLists](p))) ~
      path("user")(parameters('id.as[Int] ? 1).as(GetUser)(p => blocking[UserProfile](p)))
      
    }

    /*
    ~

    post {
       path("login")(entity(as[Login])(p => blocking[UserProfile](p)))
    }
    */

  }

}
