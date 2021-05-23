package interpretor.dsl.model

import scala.beans.BeanProperty

/**
  * Created by Olivier on 20/05/2016.
  */
@BeanProperty
case class Delta() extends Mcrl2Object with Leaf{
  val term = "delta"
  @BeanProperty
  val readable = toString
  override def toString: String ={
    "delta"
  }
}
