package interop.wrappers.react_sortable_hoc_mine

import chandu0101.macros.tojs.JSMacro
import japgolly.scalajs.react.vdom.ReactTagOf
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{BackendScope, ReactComponentB, ReactElement, _}
import org.scalajs.dom.html.Div

import scala.scalajs.js
import scala.scalajs.js.Dynamic
import scala.scalajs.js.JSConverters.JSRichGenTraversableOnce
/**
  * Created by joco on 31/10/16.
  */

case class SortableListWrapper( containerHeight: Int, elementHeight: Double )
{
  def apply(children: Seq[ReactElement]): ReactComponentU_ = {
    val props = JSMacro[SortableListWrapper](this)
    val si=js.Dynamic.global.SortableItem
    println("si="+si)
    val f: Dynamic = React.asInstanceOf[js.Dynamic].createFactory(si)
    //todo 3 this needs to change - but why?
    f(props, children.toJSArray).asInstanceOf[ReactComponentU_]
  }
}

object SortableListDemo{
  case class State(isLoading: Boolean = true, data: Vector[String] = Vector())

  class Backend(t: BackendScope[_, State]) {

    def renderRow(s: String): ReactElement = {
      val x: ReactTagOf[Div] = <.div
      x( s, ^.key := s)
    }

    def loadData() = {
      val data = (1 to 500).toVector.map(i => s"List Item $i")
      t.modState(_.copy(isLoading = false, data = data))
    }
    def render(S: State) = {
      <.div(
        if (S.isLoading) <.div("Loading ..")
        else SortableListWrapper(elementHeight = 70,
          containerHeight = 400)(S.data.map(renderRow))
      )
    }
  }

  val component = ReactComponentB[Unit]("SortableListDemo")
    .initialState(State())
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.loadData())
    .build

  // EXAMPLE:END

  def apply() = component()

}