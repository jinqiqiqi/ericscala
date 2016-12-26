package com.eric.http

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout

import spray.http.StatusCodes
import spray.httpx.SprayJsonSupport._
import spray.httpx.marshalling.ToResponseMarshallable
import spray.json.RootJsonFormat
import spray.routing.Route
import spray.routing.Directives._

import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag

import com.eric.DispatcherActor
import com.eric.common._

/**
  * Created by kinch on 12/20/16.
  */
trait Endpoints {
  import Serialize._

  def routes(system: ActorSystem)(implicit timeout:Timeout, ec: ExecutionContext): Route = {
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
      path("user")(parameters('uid.as[Int]? 1).as(GetUser)(p =>blocking(p)))
    }
  }

}
