package app.routing

import app.components.TopNav
import app.models.Menu
import app.pages.{HomePage, ListPage}
import diode.data.Pot
import diode.react.{ModelProxy, ReactConnectProxy}
import japgolly.scalajs.react.extra.router.StaticDsl.{Route, StaticRouteB}
import japgolly.scalajs.react.extra.router.{BaseUrl, Path, Redirect, RedirectToPage, Resolution, Router, RouterConfig, RouterConfigDsl, RouterCtl, StaticDsl}
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{ReactComponentU, _}
import spatutorial.client.modules.Todo
import spatutorial.client.services.{SPACircuit, Todos}


object AppRouter {

  sealed trait AppPage

  case object Home extends AppPage
  //case class Items(p : Item) extends AppPage
  case object LineList extends AppPage
  case object TodoPage extends AppPage
  case class ItemPage(id: Int) extends AppPage


  val config: RouterConfig[AppPage] = RouterConfigDsl[AppPage].buildConfig { dsl =>
    import dsl._

    object HomeRule {
      val r: Path = root
      val route: Route[Unit] = _auto_route_from_routeB(r)
      val sRoute: StaticRouteB[AppPage, dsl.Rule] = staticRoute(route, Home)
      val action: Action = render(HomePage(): ReactComponentU[Unit, Unit, Unit, TopNode])
      val staticRouteRule: StaticDsl.Rule[AppPage] = sRoute ~> action
    }

    object ListRule {
      val sRoute: StaticRouteB[AppPage, dsl.Rule] = staticRoute("#/lineList", LineList)
      val action: Action = render(ListPage(): ReactComponentU[Unit, Unit, Unit, TopNode])
      val staticRouteRule: StaticDsl.Rule[AppPage] = sRoute ~> action
    }

    object SPATodoRule {
      val todoWrapper: ReactConnectProxy[Pot[Todos]] = SPACircuit.connect(_.todos)
      val todoRender: (Any) => ReactComponentU[(ModelProxy[Pot[Todos]]) => ReactElement, Pot[Todos], Any, TopNode] = _ => todoWrapper(Todo(_))
      val todoRule=staticRoute("#todo", TodoPage) ~> renderR(todoRender )
    }

    val rule:            StaticDsl.Rule[AppPage]          =  trimSlashes |  HomeRule.staticRouteRule |
                                                                            ListRule.staticRouteRule |
                                                                            SPATodoRule.todoRule

    val rules:           StaticDsl.Rules[AppPage]         =  _auto_rules_from_rulesB(rule)
    val redirect:        RedirectToPage[AppPage]          =  redirectToPage(Home)(Redirect.Replace)
    val notfound:        RouterConfig[AppPage]            =  rules.notFound(_auto_notFound_from_parsed(redirect))
    val rendered:        RouterConfig[AppPage]            =  notfound.renderWith(layout)
    rendered
  }

  val mainMenu = Vector(
    Menu("Home",Home),
    Menu("Items",LineList),
    Menu("Todos",TodoPage)
  )

  def layout(c: RouterCtl[AppPage], r: Resolution[AppPage]) = {
    <.div(
      TopNav(TopNav.Props(mainMenu,r.page,c)),
      r.render()
    )
  }

  val baseUrl = BaseUrl.fromWindowOrigin / "src/main/"

  val router = Router(baseUrl, config)

}