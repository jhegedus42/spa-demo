package app.root.pages.sortableList

import _learning.interop.wrappers.react_sortable_hoc.SortableContainer.Props
import _learning.interop.wrappers.react_sortable_hoc.{SortableContainer, SortableElement}
import japgolly.scalajs.react.ReactComponentC.ReqProps
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object EditableSortableListDemo {

  trait Action
  case class RenameItem(s:String) extends Action

  type Handler=Action=>Unit

  object CompWithState {
    case class UpdateElement(s:String) extends  Action

    class Backend($: BackendScope[Unit, String]) {
      def handler(s: String): Unit  =
      {
        println("handler:"+s)
        $.setState(s)
        //r(new RenameItem(s))
      }

      def render(s:String) = {
        println("state:"+s)
        <.div(
          <.span(_react_fragReactNode(s), ^.onClick ==> ((e:ReactEventI)=>Callback(println("clicked2")))),
        //  EditableText(s, handler _)(),
          <.span("XXXXXXXX PINA"))
      }
    }

    val Component = (initialState:String)=>
      ReactComponentB[Unit]("EditableText with state").initialState(initialState).backend(new Backend(_))
      .renderBackend.build
  }

  // Equivalent of ({value}) => <li>{value}</li> in original demo
  val itemView: ReqProps[String, Unit, Unit, TopNode] =  ReactComponentB[String]("liView")
    .render(d => {
      //<.div( CompWithState.Component(d.props)(), <.span(s"${d.props}"))}
      <.span(s"${d.props}")} )
    .build

  // As in original demo
  val sortableItem = SortableElement.wrap(itemView)

  val listView = ReactComponentB[List[String]]("listView")
    .render(d => {
      <.div(
        d.props.zipWithIndex.map {
          case (value, index) =>
            sortableItem(SortableElement.Props(index = index))(value)
        }
      )
    })
    .build

  // As in original demo
  val sortableList: (Props) => (List[String]) => ReactComponentU_ =
      SortableContainer.wrap(listView)

  def render(props: Unit, items: List[String],scope:BackendScope[Unit, List[String]]) = {
    def handler = (a: Action) => {
      println(s"action dispatched ${a.toString}")
    }
    sortableList(
      SortableContainer.Props(
        onSortEnd = p => {
          println("sort is ending: " + p)
          scope.modState(l => p.updatedList(l))
        },
        useDragHandle = false,
        helperClass = "react-sortable-handler",
        pressDelay = 200
      )
    )(items)
  }
  // As in original SortableComponent
  case class Backend(scope: BackendScope[Unit, List[String]])

  val defaultItems = Range(0, 10).map("Item " + _).toList

  val c = ReactComponentB[Unit]("SortableContainerDemo")
    .initialState(defaultItems)
    .backend(new Backend(_))
    .render(s => render(s.props, s.state, s.backend.scope))
    .build
 // val fc=
}

