package hackaton

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js.JSApp
import scalatags.JsDom.all._

import org.scalajs.dom

import autowire._
import upickle.Js
import upickle.default._

object App extends JSApp {
  import Client.api

  def main() = {
    dom.document.body.appendChild(fileBrowserExample.render)
  }

  val fileBrowserExample = {
    val inputBox = input.render
    val outputBox = div.render

    def updateOutput() = {
      api.list(inputBox.value).call().foreach { paths =>
        outputBox.innerHTML = ""
        outputBox.appendChild(
          ul(
            for (path <- paths) yield {
              li(path)
            }
          ).render
        )
      }
    }
    inputBox.onkeyup = { (e: dom.Event) =>
      updateOutput()
    }
    updateOutput()

    div(
      cls := "container",
      h1("File Browser"),
      p("Enter a file path to s"),
      inputBox,
      outputBox
    )
  }
}

object Client extends autowire.Client[Js.Value, Reader, Writer] {
  lazy val api = Client[Api]

  override def doCall(req: Request): Future[Js.Value] = {
    dom.ext.Ajax.post(
      url = "/api/" + req.path.mkString("/"),
      data = upickle.json.write(Js.Obj(req.args.toSeq: _*))
    ).map(_.responseText)
      .map(upickle.json.read)
  }

  def read[Result: Reader](p: Js.Value) = readJs[Result](p)
  def write[Result: Writer](r: Result) = writeJs(r)
}
