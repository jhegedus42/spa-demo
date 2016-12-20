package interop.wrappers.react_sortable_hoc_mine

import japgolly.scalajs.react.ReactComponentC.ReqProps
import japgolly.scalajs.react.{ReactComponentB, ReactComponentU, _}
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.Any
import scala.scalajs.js.annotation.{JSExport, JSExportAll}

@js.native
object DOMGlobalScope extends js.GlobalScope {
  def SortableElement[T](f:T):T = js.native
}

@JSExport
object ComponentToBeWrappedBySortableContainer {
  @js.native
  trait Props extends js.Object {
    val items: js.Array[String] = js.native
  }

  @JSExport
  val component : ReqProps[Props, Unit, Unit, TopNode] = { // this is a first order component
  val plan = ReactComponentB[Props]("HelloMessage")
    .render($ => {
      val f: ((String,Int))=> ReactElement =
        tuple=> {
          val p=SortableItem.ScalaProps("Scala "+tuple._1,tuple._2,tuple._1)
          val sip=js.use(p).as[SortableItem.Props]
          val s: SortableItem.RCScala = SortableItem.LIComp_Scala(sip) // if this works
          val sjs: SortableItem.RCScala = SortableItem.LIComp_JS(sip) // and this works
          //val si: SortableItem.RCScala = SortableItem.MySortableItem(sip) // then why does this not work ?
          sjs
        }
      val v: mutable.Seq[(String, Int)] = $.props.items.zipWithIndex
      <.ul(v.map(f))
    }
    )
    plan.build
  }
  @JSExport
  val wrapComponent: js.Function1[Props, ReactComponentU[Props, Unit, Unit, TopNode]] =
    (x: Props) => component(x)
}

@JSExport
object SortableItem
{
  type RCScala=ReactComponentU[SortableItem.Props, Unit, Unit, TopNode]
  type RCJS=js.Function1[Props, RCScala]

  @js.native
  trait Props extends js.Object {
    def value: String = js.native
    def index: Int = js.native
    def key: String = js.native
  }

  @JSExportAll
  case class ScalaProps(value:String, index:Int, key:String)

  @JSExport
  val LIComp_Scala: ReqProps[Props, Unit, Unit, TopNode] =
    ReactComponentB[Props]("HelloMessage")
      .render($ => { <.li("HelloiBela " + $.props.value) } )
      .build

  @JSExport
  val LIComp_JS: RCJS = Any.fromFunction1((x: Props) => { LIComp_Scala(x) } )

  @JSExport
  val MySortableItem: RCJS= DOMGlobalScope.SortableElement(LIComp_JS)

  val MySortableItem_Scala:Props => RCScala=MySortableItem
  //need to turn a js component to scala component, but how ?
  case class Wrapper( )
  // todo write wrapper as in https://github.com/chandu0101/scalajs-react-components/blob/master/doc/InteropWithThirdParty.md
}