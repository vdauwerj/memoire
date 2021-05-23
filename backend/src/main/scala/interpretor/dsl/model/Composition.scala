package interpretor.dsl.model

import scala.beans.BeanProperty

/**
  * Created by Olivier on 20/05/2016.
  */
@BeanProperty
case class Composition(@BeanProperty var unique:Int, @BeanProperty op:String, @BeanProperty var left:Mcrl2Object, @BeanProperty var right:Mcrl2Object) extends Mcrl2Object {
  def this() {
    this(-4, "", new Action(), new Action())
  }
  @BeanProperty
  var disabled = true
  @BeanProperty
  val term = "composition"
  @BeanProperty
  val readable:String = toString

  override def toString: String ={
    left.toString+op+right.toString
  }

}
