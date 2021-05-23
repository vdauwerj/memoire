package interpretor.dsl.model

import scala.beans.BeanProperty

/**
  * Created by Olivier on 20/05/2016.
  */
@BeanProperty
case class ProcessSpec(@BeanProperty id:ProcessId, @BeanProperty process: Mcrl2Object) extends Mcrl2Object {
  @BeanProperty
  val term = "processSpec"
  override def toString: String ={
    id.toString+"="+process.toString
  }
}
