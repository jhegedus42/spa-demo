package app.models.todo

import java.util.UUID

import app.material.AppRouter.Pages.TodoFilter
import diode.Action

// Define our application model
case class AppModel(todos: Todos)

case class TodoId(id: UUID)

object TodoId {
  def random = new TodoId(UUID.randomUUID)
}

case class Todos(todoList: Seq[Todo])

case class Todo(id: TodoId, title: String, isCompleted: Boolean)


// define actions
case object InitTodos extends Action

case class AddTodo(title: String) extends Action

case class ToggleAll(checked: Boolean) extends Action

case class ToggleCompleted(id: TodoId) extends Action

case class Update(id: TodoId, title: String) extends Action

case class Delete(id: TodoId) extends Action

case class SelectFilter(filter: TodoFilter) extends Action

case object ClearCompleted extends Action
