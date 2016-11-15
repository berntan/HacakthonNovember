import upickle.default._
import upickle.Js
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.concurrent.ExecutionContext.Implicits.global

import hackaton.Api


object Template {

  import scalatags.Text.all._

  val indexHtml =
    "<!DOCTYPE html>" +
      html(
        head(
          title := "Example Scala.js application",
          meta(httpEquiv := "Content-Type", content := "text/html; charset=UTF-8"),
          script(`type` := "text/javascript", src := "/client-fastopt.js")
        ),
        body(
          script("hackaton.App().main()")
        )
      )
}

object AutowireServer extends autowire.Server[Js.Value, Reader, Writer] {
  def read[Result: Reader](p: Js.Value) = upickle.default.readJs[Result](p)
  def write[Result: Writer](r: Result) = upickle.default.writeJs(r)
}

object ServerApi extends Api {
  def list(path: String) = {
    val chunks = path.split("/", -1)
    val prefix = "./" + chunks.dropRight(1).mkString("/")
    val files = Option(new java.io.File(prefix).list()).toSeq.flatten
    files.filter(_.startsWith(chunks.last))
  }
}

object Server {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    val route = {
      get {
        pathSingleSlash {
          complete {
            HttpEntity(
              ContentTypes.`text/html(UTF-8)`,
              Template.indexHtml
            )
          }
        } ~
          getFromResourceDirectory("")
      } ~
        post {
          path("api" / Segments) { s =>
            extract(_.request.entity match {
              case HttpEntity.Strict(nb: ContentType.NonBinary, data) =>
                data.decodeString(nb.charset.value)
            }) { e =>
              complete {
                AutowireServer.route[Api](ServerApi)(
                  autowire.Core.Request(
                    s,
                    upickle.json.read(e).asInstanceOf[Js.Obj].value.toMap
                  )
                ).map(upickle.json.write(_))
              }
            }
          }
        }
    }
    Http().bindAndHandle(route, "0.0.0.0", port = 8080)
  }
}
