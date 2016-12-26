package app.root.pages.todo

import app.root.AppRouter.Pages.{Page, TodoFilter}
import diode.Action
import diode.react.ModelProxy
import _learning.interop.wrappers.react_sortable_hoc.{SortableContainer, SortableElement, SortableView}
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.ext.KeyCode

object TodoList {

  case class Props(proxy: ModelProxy[Todos], currentFilter: TodoFilter, ctl: RouterCtl[Page])

  case class State(editing: Option[TodoId])

  class Backend($: BackendScope[Props, State]) {
    def mounted(props: Props) = Callback {}

    def handleNewTodoKeyDown(dispatch: Action => Callback)(e: ReactKeyboardEventI): Option[Callback] = {
      val title = e.target.value.trim
      if (e.nativeEvent.keyCode == KeyCode.Enter && title.nonEmpty) {
        Some(Callback(e.target.value = "") >> dispatch(AddTodo(title)))
      } else {
        None
      }
    }

    def editingDone(): Callback = $.modState(_.copy(editing = None))

    val startEditing: TodoId => Callback = id => $.modState(_.copy(editing = Some(id)))

    def render(p: Props, s: State) = {
      val proxy = p.proxy()
      val dispatch: Action => Callback = p.proxy.dispatchCB
      val todos = proxy.todoList
      val filteredTodos = todos filter p.currentFilter.accepts
      val activeCount = todos count TodoFilter.Active.accepts
      val completedCount = todos.length - activeCount

      <.div(
        <.h1("todos"),
        <.header(
          ^.className := "header",
          <.input(
            ^.className := "new-todo",
            ^.placeholder := "What needs to be done?",
            ^.onKeyDown ==>? handleNewTodoKeyDown(dispatch),
            ^.autoFocus := true
          )
        ),
        todos.nonEmpty ?= todoList(SList.ListProps(dispatch, s.editing, filteredTodos, activeCount)),
        todos.nonEmpty ?= footer(p, dispatch, p.currentFilter, activeCount, completedCount)
      )
    }

    object SList{
      // As in original demo
      case class ItemProps(dispatch:Action => Callback, todo:Todo, editing:Option[TodoId])
      case class ListProps(dispatch: Action => Callback, editing: Option[TodoId], todos: Seq[Todo], activeCount: Int)

      val listItem = ReactComponentB[ItemProps]("sortableItem").render(d=>{
        val p =d.props
        <.div(
          ^.className := "react-sortable-item",
          SortableView.handle,
          TodoView(TodoView.Props(
            onToggle = p.dispatch(ToggleCompleted(p.todo.id)),
            onDelete = p.dispatch(Delete(p.todo.id)),
            onStartEditing = startEditing(p.todo.id),
            onUpdateTitle = title => p.dispatch(Update(p.todo.id, title)) >> editingDone(),
            onCancelEditing = editingDone(),
            todo =p.todo,
            isEditing = p.editing.contains(p.todo.id)
          )
        )
      )}).build

      val sortableItem = SortableElement.wrap(listItem)

      val listView  = ReactComponentB[ListProps]("listView").render(d=> {
        <.div(
          ^.className := "react-sortable-list",
          d.props.todos.zipWithIndex.map {
            case (todo, index) =>
              sortableItem(SortableElement.Props(index = index))(ItemProps(d.props.dispatch,todo,d.props.editing))
          }
        )
      }).build

      val sortableList = SortableContainer.wrap(listView)(
            SortableContainer.Props(
              onSortEnd = p => Callback.info("updated list:"+p) ,
              useDragHandle = true,
              helperClass = "todo-list"
            )
          )
    }

    def todoList(p:SList.ListProps) =
      <.section(
        //^.className := "main",
        <.input.checkbox(
          ^.className := "toggle-all",
          ^.checked := p.activeCount == 0,
          ^.onChange ==> { e: ReactEventI => p.dispatch(ToggleAll(e.target.checked)) }
        ),
          <.div(^.className := "todo-list",SList.sortableList(p))
      )

    def footer(p: Props, dispatch: Action => Callback, currentFilter: TodoFilter, activeCount: Int,
               completedCount: Int): ReactElement =
      Footer(Footer.Props(
        filterLink = p.ctl.link,
        onSelectFilter = f => dispatch(SelectFilter(f)),
        onClearCompleted = dispatch(ClearCompleted),
        currentFilter = currentFilter,
        activeCount = activeCount,
        completedCount = completedCount ))
  }

  private val component = ReactComponentB[Props]("TodoList")
    .initialState_P(p => State(None))
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(proxy: ModelProxy[Todos], currentFilter: TodoFilter, ctl: RouterCtl[Page]) = component(Props(proxy, currentFilter, ctl))
}
