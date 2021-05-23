package interpretor.facade.dto

import interpretor.dsl.model.Mcrl2Object

import scala.beans.BeanProperty

class NextExecutableItemsDto {

  @BeanProperty
  var single: Array[ActionDto] = Array.empty;
  @BeanProperty
  var multi: Array[ActionsDto] = Array.empty
  @BeanProperty
  var currentSpecification: Mcrl2Object = null

}
