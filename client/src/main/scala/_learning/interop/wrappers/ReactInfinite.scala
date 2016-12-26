package _learning.interop.wrappers

import chandu0101.macros.tojs.JSMacro
import japgolly.scalajs.react._

import scala.scalajs.js
import scala.scalajs.js.Dynamic
import scala.scalajs.js.JSConverters.JSRichGenTraversableOnce


case class ReactInfinite( containerHeight: Int, elementHeight: Double )
{
  def apply(children: Seq[ReactElement]): ReactComponentU_ = {
    val props = JSMacro[ReactInfinite](this)
    val f: Dynamic = React.asInstanceOf[js.Dynamic]. createFactory(js.Dynamic.global.Infinite) // what is this ?
    f(props, children.toJSArray).asInstanceOf[ReactComponentU_]
  }
}

@js.native
trait ReactInfiniteM extends js.Object {
  def getScrollTop(): Double = js.native
}