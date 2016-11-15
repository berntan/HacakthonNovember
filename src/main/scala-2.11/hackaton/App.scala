package hackaton

import scala.scalajs.js.JSApp

import org.scalajs.dom


object App extends JSApp {
  def main() = {
    dom.document.body.innerHTML = "This is greeting."
  }
}
