package org.byrde

import org.byrde.controllers.TodoController
import org.byrde.controllers.directives.RequestResponseHandlingDirective
import org.byrde.guice.ModulesProvider
import org.byrde.models.responses.CommonJsonServiceResponseDictionary.E0200

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MarshallingEntityWithRequestDirective
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.util.Timeout

import scala.concurrent.ExecutionContext

trait Routes extends RequestResponseHandlingDirective with MarshallingEntityWithRequestDirective {
  implicit def main: ExecutionContext

  implicit def timeout: Timeout

  def storage: ExecutionContext

  def modulesProvider: ModulesProvider

  lazy val defaultRoutes: Route =
    complete(E0200("pong"))

  lazy val pathBindings =
    Map(
      "ping" -> defaultRoutes,
      "todo" -> new TodoController(modulesProvider.todoStorage)(storage).routes
    )

  lazy val routes: Route =
    requestResponseHandler {
      pathBindings.map {
        case (k, v) => path(k)(v)
      } reduce (_ ~ _)
    }
}