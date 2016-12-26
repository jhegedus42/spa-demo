package _learning.interop.wrappers

import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react._

import scala.scalajs.js
import scala.scalajs.js.Any.fromFunction1
import scala.scalajs.js.Dynamic
import scala.scalajs.js.JSConverters.JSRichGenTraversableOnce
import scala.scalajs.js.annotation.JSExport



object EditableText {

  @js.native
  trait Obj extends js.Object {
    val v: String = js.native
  }

  case class EditableText(text: String, handler: String => Unit) {

    lazy val h: (String => Unit) => js.Function1[Obj, Unit] = (g) => fromFunction1((o: Obj) => {
      println("log :" + o.v);
      g(o.v);
    })

    def apply(children: ReactNode*): ReactComponentU_ = {
      val toJS: Object with Dynamic = {
        val p = js.Dynamic.literal();
        p.text=(text);
        p.paramName = "v";
       // p.stopPropagation= true;
        p.change = h(handler)
        p
      }
      val f: Dynamic = React.asInstanceOf[js.Dynamic].createFactory(js.Dynamic.global.InlineEdit)
      f(toJS, children.toJSArray).asInstanceOf[ReactComponentU_]
    }
  }


  object CompWithState {

    class Backend($: BackendScope[Unit, String]) {
      def handler(s: String): Unit  =
      {
          println("handler:"+s)
          val c: Unit =$.setState(s)
          c
      }

      def render(s:String) = {
        println("state:"+s)
        <.div(<.span(s),EditableText(s, handler _)())
      }
    }

    val Component = (s:String)=>ReactComponentB[Unit]("EditableText with state").initialState(s)
          .renderBackend[Backend].build
  }

}

