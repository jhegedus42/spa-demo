package app.material

import japgolly.scalajs.react.ReactComponentC.ConstProps
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.scalajs.js

object View{
  def touch(c: Callback): js.UndefOr[ReactTouchEventH => Callback] = {
    println("touch callback")
    e: ReactTouchEventH => e.preventDefaultCB >> c
  }

  def staticView(name: String)(e: ReactElement) = ReactComponentB[Unit](name)
    .render(_ => e)
    .build


  def homeView(s:String): ConstProps[Unit, Unit, Unit, TopNode] = staticView("home")(
    <.div (
      ^.margin := "24px",
      <.h3("Home"),
      s
    )
  )


}
