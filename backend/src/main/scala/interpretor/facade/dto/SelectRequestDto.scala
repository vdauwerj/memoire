package interpretor.facade.dto

import interpretor.dsl.model.Mcrl2Object

import scala.beans.BeanProperty

class SelectRequestDto {

  @BeanProperty
  var unique: Array[Int] = Array.empty
  @BeanProperty
  var currentSpecification: Mcrl2Object = null
  @BeanProperty
  var processes: Array[ProcessDto] = Array.empty

}
