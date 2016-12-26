package _learning.Demo

import japgolly.scalajs.react.ReactComponentC.ReqProps
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import _learning.interop.wrappers.react_sortable_hoc.{SortableContainer, SortableElement}

object SortableContainerDemo {

  // Equivalent of ({value}) => <li>{value}</li> in original demo
  val itemView: ReqProps[String, Unit, Unit, TopNode] = ReactComponentB[String]("liView")
    .render(d => {
      <.div(
        <.span(s"${d.props}")
      )
    })
    .build

  // As in original demo
  val sortableItem = SortableElement.wrap(itemView)

  // Equivalent of the `({items}) =>` lambda passed to SortableContainer in original demo
//  ({items}) => {
//    return (
//      <ul>
//        {items.map((value, index) =>
//          <SortableItem key={`item-${index}`} index={index} value={value} />
//      )}
//      </ul>
//      );

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
  val sortableList = SortableContainer.wrap(listView)

  // As in original SortableComponent
  class Backend(scope: BackendScope[Unit, List[String]]) {
    def render(props: Unit, items: List[String]) = {
      sortableList(
        SortableContainer.Props(
          onSortEnd = p =>
            scope.modState(
              l => p.updatedList(l)
            ),
          useDragHandle = false,
          helperClass = "react-sortable-handler"
        )
      )(items)
    }
  }

  val defaultItems = Range(0, 10).map("Item " + _).toList

  val c = ReactComponentB[Unit]("SortableContainerDemo")
    .initialState(defaultItems)
    .backend(new Backend(_))
    .render(s => s.backend.render(s.props, s.state))
    .build

}
