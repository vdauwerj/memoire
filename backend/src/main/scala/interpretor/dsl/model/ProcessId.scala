package interpretor.dsl.model

import scala.beans.BeanProperty

/**
  * Created by Olivier on 20/05/2016.
  */
@BeanProperty
case class ProcessId(@BeanProperty var unique:Int, @BeanProperty name:String) extends Mcrl2Object with Leaf{
  def this() {
    this(-4, "")
  }
  @BeanProperty
  var disabled = true
  @BeanProperty
  val readable = toString
  @BeanProperty
  val term = "processId"
  override def toString: String ={
    name
  }
}
