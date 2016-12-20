package Demo

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import interop.wrappers.ReactInfinite

import scalacss.Defaults._
import scalacss.ScalaCssReact._

object ReactInfiniteDemo {

  object styles extends StyleSheet.Inline {

    import dsl._


    val item = style(
      width(300 px),
      textAlign.center,
      //      height(70 px),
      padding(20 px)
    )

  }


  // EXAMPLE:START

  case class State(isLoading: Boolean = true, data: Vector[String] = Vector())

  class Backend(t: BackendScope[_, State]) {

    def renderRow(s: String): ReactElement = {
      <.div(styles.item, s, ^.key := s)
    }

    def loadData() = {
      val data = (1 to 500).toVector.map(i => s"List Item $i")
      t.modState(_.copy(isLoading = false, data = data))
    }
    def render(S: State) = {
      <.div(
        if (S.isLoading) <.div("Loading ..")
        else ReactInfinite(elementHeight = 70,
          containerHeight = 400)(S.data.map(renderRow))
      )
    }
  }

  val component = ReactComponentB[Unit]("ReactSelectDemo")
    .initialState(State())
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.loadData())
    .build

  // EXAMPLE:END

  def apply() = component()

}
