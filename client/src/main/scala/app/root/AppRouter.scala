package app.root

import app.common.View
import app.root.org.rebeam.tree_material_ui.view.Navigation
import app.root.sortableList.SortableContainerDemo
import app.root.todo.{AppCircuit, Todo, TodoList}
import diode.data.Pot
import diode.react.{ModelProxy, ReactConnectProxy}
import japgolly.scalajs.react.{ReactComponentU, ReactElement, TopNode}
import japgolly.scalajs.react.extra.router.{BaseUrl, Redirect, Resolution, Router, RouterConfigDsl, RouterCtl}
import spatutorial.client.services.{SPACircuit, Todos}

object AppRouter {

  object Pages
  {
    sealed trait Page
    case object Home          extends Page
    case object TodoSPA       extends Page
    case object ReorderList      extends Page
    sealed abstract class TodoFilter(val link: String, val title: String, val accepts: Todo => Boolean) extends Page

    val title = "Joco App"

    object TodoFilter {

      object All extends TodoFilter("all", "All", _ => true)

      object Active extends TodoFilter("active", "Active", !_.isCompleted)

      object Completed extends TodoFilter("completed", "Completed", _.isCompleted)

      val values = List[TodoFilter](All, Active, Completed)
    }
  }

  import Pages._
  val routerConfig = RouterConfigDsl[Page].buildConfig { dsl =>
    import dsl._

    object TodoRule{
      val todoConnection = AppCircuit.connect(_.todos)
      def filterRoute(s: TodoFilter): Rule = staticRoute("#todo/" + s.link, s) ~> renderR(router => todoConnection(p => TodoList(p, s, router)))
      val filterRoutes: Rule = TodoFilter.values.map(filterRoute).reduce(_ | _)
    }

    object SPATodoRule {
      val todoWrapper: ReactConnectProxy[Pot[Todos]] = SPACircuit.connect(_.todos)
      val todoRender: (Any) => ReactComponentU[(ModelProxy[Pot[Todos]]) => ReactElement, Pot[Todos], Any, TopNode] = _ => todoWrapper(spatutorial.client.modules.Todo(_))
      val todoRule=staticRoute("#todo", TodoSPA) ~> renderR(todoRender )
    }

    (trimSlashes
      | staticRoute(root,   Home) ~> render(View.homeView("1")())
      | TodoRule.filterRoutes
      | staticRoute("#reorder_list", ReorderList) ~> render(SortableContainerDemo.c())
      | SPATodoRule.todoRule
      )

      .notFound(redirectToPage(Home)(Redirect.Replace))
      .renderWith(layout(_,_))
      .verify(Home, ReorderList, TodoFilter.All)
  }


  val navs = Map(
    "Home" -> Home,
    "Todo List" -> TodoFilter.All,
    "Reorder List" -> ReorderList,
    "TODO SPA Tutorial" -> TodoSPA
  )


  val navigation = Navigation.apply[Page]

  def layout(ctl: RouterCtl[Page], r: Resolution[Page]) = {
    val np = Navigation.Props(ctl, r, r.page, navs, title)
    navigation(np)
  }

  val baseUrl = BaseUrl.fromWindowOrigin_/

  def router = Router(baseUrl, routerConfig.logToConsole)

}
