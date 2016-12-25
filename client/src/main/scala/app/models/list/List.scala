package app.models.list

import java.util.UUID

import diode.Action
case class AppModel(list:LineList)

case class LineList(val l:Seq[Line])

case class Line(id:LineID,text:String)

case class LineID(id:UUID)

object LineID {
  def random = new LineID(UUID.randomUUID)
}

// actions

case class AddLine(text:String ) extends Action
case class DeleteLine(id:LineID) extends Action
case class MoveLine(id:LineID, newPos:Int) extends  Action
case class RenameLine(id:LineID, text : String) extends  Action

