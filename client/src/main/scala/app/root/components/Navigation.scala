package app.root.components

import chandu0101.scalajs.react.components.materialui._
import japgolly.scalajs.react.extra.Reusability
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{Callback, _}

import scala.scalajs.js

object Navigation {

  case class Props[P](routerCtl: RouterCtl[P], resolution: Resolution[P], page: P, navs: Map[String, P], title: String)

  case class State(drawerOpen: Boolean)

  class Backend[P](scope: BackendScope[Props[P], State]) {

    val toggleDrawerOpen: Callback = {
      println("callback running");
      Callback.info(s"opening drawer") >>
      scope.modState(s => s.copy(drawerOpen = !s.drawerOpen))
    }

    val onRequestChange: (Boolean, String) => Callback =
      (open, reason) =>
        Callback.info(s"onRequestChange: open: $open, reason: $reason") >>
          scope.modState(s => s.copy(drawerOpen = !s.drawerOpen))

    def render(p: Props[P], s: State) = {
      println("render navigation");
      System.out.flush();
      <.div(
        MuiAppBar(
          title = p.title: ReactNode,
          onLeftIconButtonTouchTap  =
            {
              println("on left icon button touch tap")
              View.touch(toggleDrawerOpen)
            },
          showMenuIconButton = true,
          style = js.Dynamic.literal(
            "position" -> "fixed",
            "top" -> "0px"
          )
        )(),
        MuiDrawer(
          onRequestChange = onRequestChange,  //Toggle open state
          docked          = false,
          open            = s.drawerOpen
        )(
          //TODO get the color from Material-UI theme, or is there a component that does this?
          <.div(
            ^.backgroundColor := "#757575",
            ^.color           := "rgb(255, 255, 255)",
            ^.height          := "64px"
          ),
          MuiMenu()(
            p.navs.map {
              case (name, page) =>
                MuiMenuItem(
                  key         = name,
                  primaryText = name: ReactNode,
                  checked     = p.page == page,
                  insetChildren = p.page != page,   //Allow space for icon/checkmark when it's not displayed
                  onTouchTap  = View.touch(p.routerCtl.set(page) >> toggleDrawerOpen),
                  style       = js.Dynamic.literal(
                    "cursor" -> "pointer",
                    "user-select" -> "none"
                  )
                )()
            }
          )
        ),
        <.div(
          ^.paddingTop    := "64px",
          p.resolution.render()
        )
      )
    }
  }

  //TODO make only the contents care about resolution
  //Reusable if all fields are equal except routerCtl, where we use its own reusability
  implicit def navPropsReuse[P]: Reusability[Props[P]] = Reusability.fn{
    case (a, b) if a eq b => true // First because most common case and fastest
    case (a, b) if a.page == b.page && a.navs == b.navs && a.title == b.title && a.resolution == b.resolution => RouterCtl.reusability[P].test(a.routerCtl, b.routerCtl)
    case _ => false
  }

  //Just make the component constructor - props to be supplied later to make a component
  def apply[P] = ReactComponentB[Props[P]]("Nav")
    .initialState(State(false))
    .backend(new Backend[P](_))
    .render(s => s.backend.render(s.props, s.state))
    .build

}
