package interop.wrappers.react_sortable_hoc

import org.scalajs.dom.raw.HTMLDocument

import scala.scalajs.js

@js.native
object DOMGlobalScope extends js.GlobalScope {
  val document: HTMLDocument = js.native

  def alert(message: String): Unit = js.native
  def sortableElement(wrappedComponent:js.Any)=js.native
  def sortableContainer(wrappedComponent:js.Any)=js.native

}

object SortableHOC{
  import japgolly.scalajs.react.vdom.all._
  li("bla")
  // create ({value}) => <li >{value} </li>
  // eli (props) => { var value = props.value; return <li>{value}</li> }
  // eli { var value = props.value; return <li>{value}</li> }
}

//
//import React, {Component} from 'react';
//import {render} from 'react-dom';
//import {SortableContainer, SortableElement, arrayMove} from 'react-sortable-hoc';
//
//const SortableItem = SortableElement(({value}) => <li >{value} </li>);
//
//const SortableList = SortableContainer(({items}) => {
//  return (
//        <ul>
//          {items.map((value, index) =>
//              <SortableItem key={`item-${index}`} index={index} value={value} />
//          )}
//        </ul>
//      );
//  });
//
//class SortableComponent extends Component {
//  state = {
//    items: ['Item 1', 'Item 2', 'Item 3', 'Item 4', 'Item 5', 'Item 6']
//  }
//  onSortEnd = ({oldIndex, newIndex}) => {
//    this.setState({
//      items: arrayMove(this.state.items, oldIndex, newIndex)
//    });
//  };
//  render() {
//    return (
//        <SortableList items={this.state.items} onSortEnd={this.onSortEnd} />
//      )
//  }
//}
//
//render(<SortableComponent/>,
//document.body.appendChild(document.createElement('div')));