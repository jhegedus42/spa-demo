package interop

import japgolly.scalajs.react.ReactComponentB.PSB
import japgolly.scalajs.react.ReactComponentC.ReqProps
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{ReactComponentB, ReactComponentU, TopNode}

import scala.scalajs.js
import scala.scalajs.js.Any
import scala.scalajs.js.annotation.JSExport
/**
  * Created by joco on 30/11/16.
  */

@JSExport
object Experiment {

  @js.native
  trait Props extends js.Object {
    val value: String = js.native
  }

  @JSExport
  val TestCompB: ReqProps[Props, Unit, Unit, TopNode] = {
    val b1: PSB[Props, Unit, Unit]#Out =
      ReactComponentB._defaultBuildStep_noBackend(ReactComponentB[Props]("HelloMessage"))
        .render($ => {
            val h = $.props.value
            <.div("Hello " + h) })
    ReactComponentB._defaultBuildStep_builder(b1).build
  }

  @JSExport
  val wrapTestCompB: js.Function1[Props, ReactComponentU[Props, Unit, Unit, TopNode]] =
    Any.fromFunction1((x: Props) => TestCompB(x))

}
