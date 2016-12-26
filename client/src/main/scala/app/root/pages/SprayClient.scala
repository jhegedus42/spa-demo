package app.root.pages

import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.html
import simple.FileData
import scalajs.concurrent.JSExecutionContext.Implicits.runNow

import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._

@JSExport
object SprayClient extends{
  @JSExport
  def main(container: html.Div) = {
    val inputBox = input.render
    val outputBox = ul.render
    def update() = Ajax.post("/ajax/list", inputBox.value).foreach{ xhr =>
      val data = upickle.default.read[Seq[FileData]](xhr.responseText)
      outputBox.innerHTML = ""
      for(FileData(name, size) <- data){
        outputBox.appendChild(
          li(
            b(name), " - ", size, " bytes"
          ).render
        )
      }
    }
    inputBox.onkeyup = (e: dom.Event) => update()
    update()
    container.appendChild(
      div(
        h1("File Search"),
        inputBox,
        outputBox
      ).render
    )
  }
}