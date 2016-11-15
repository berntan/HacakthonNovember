package hackaton

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js.JSApp
import scalatags.JsDom
import scalatags.JsDom.all._
import scalatags.JsDom.tags2
import scalatags.rx.all._

import org.scalajs.dom
import org.scalajs.dom.raw.Event

import autowire._
import hackaton.Client.api
import rx._
import upickle.Js
import upickle.default._


object App extends JSApp {
  lazy implicit val ctxOwner: Ctx.Owner = Ctx.Owner.safe()

  def main() = {
    val page = div(
      navBar,
      div(cls := "container")(
        userCreation
      )
    )

    dom.document.body.appendChild(page.render)
  }

  val navBar = {
    tags2.nav(
      div(cls := "container")(
        div(cls := "nav-wrapper")(
          span(cls := "center")("Hackaton"),
          i(cls := "material-icons right")("person")
        )
      )
    )
  }

  lazy val userCreation = {
    val nameBox = input.render
    val currentName: Var[String] = Var("")

    nameBox.onchange = {(ev: Event) => currentName() = nameBox.value}
    nameBox.onkeyup = {(ev: Event) => currentName() = nameBox.value}

    def createClick() = {
      api.createUser(nameBox.value).call()
      nameBox.value = ""
    }

    card(
      title = "Opprett bruker",
      content = div(
        nameBox, color := currentName,
        currentName
      ),
      actionButtons = List("Opprett bruker" -> createClick)
    )
  }

  def card(title: String, content: JsDom.Modifier, actionButtons: List[(String, () => Unit)]) =
    div(cls := "card")(
      div(cls := "card-content")(
        span(cls := "card-title")(title),
        content
      ),
      div(cls := "card-action")(
        actionButtons.map { case (text, clickAction) =>
          button(onclick := clickAction)(text)
        }
      )
    )
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
