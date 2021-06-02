package interpretor.dsl.model

import scala.beans.BeanProperty

/**
 * Created by Olivier on 20/05/2016.
 */
@BeanProperty
case class Block(@BeanProperty var actions: List[Action], @BeanProperty specification: Mcrl2Object) extends Mcrl2Object with Leaf {
  def this() {
    this(List.empty, null)
  }
}
