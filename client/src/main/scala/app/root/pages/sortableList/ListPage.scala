package app.root.pages.sortableList

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

import scalacss.Defaults._
import scalacss.ScalaCssReact._

object ListPage {

  object Style extends StyleSheet.Inline {
    import dsl._
    val content = style(textAlign.center,
      fontSize(30.px)
     // minHeight(450.px),
     // paddingTop(40.px)
     )
  }

  val component = ReactComponentB.static("ListPage",
    <.div(Style.content, EditableSortableListDemo.c())
  ).buildU

  def apply() = component()
}
