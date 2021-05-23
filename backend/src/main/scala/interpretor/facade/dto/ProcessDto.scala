package interpretor.facade.dto

import interpretor.dsl.model.Mcrl2Object

import scala.beans.BeanProperty

class ProcessDto( @BeanProperty var name: String = "",
                  @BeanProperty var actions: Array[ActionDto] = Array.empty,
                  @BeanProperty var specification: Mcrl2Object = null
                ) {
  def this(){
    this("", Array.empty, null)
  }
}
