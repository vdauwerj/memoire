package interpretor.facade.dto

import scala.beans.BeanProperty

class ActionDto(
                 @BeanProperty
                 var unique: Array[Int] = Array.empty,
                 @BeanProperty
                 var name: String = "",
                 @BeanProperty
                 var process: String = ""
               ) {
def this(){
  this(Array(-4), "", "")
}

  def canEqual(other: Any): Boolean = other.isInstanceOf[ActionDto]

  override def equals(other: Any): Boolean = other match {
    case that: ActionDto =>
      (that canEqual this) &&
        name == that.name &&
        process == that.process
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(name, process)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}
