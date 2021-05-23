package interpretor.facade.dto

import interpretor.dsl.model.Mcrl2Object

import scala.beans.BeanProperty

class RequestTextSpecDto {

  @BeanProperty
  var actions : Array[ActionDto] = Array.empty
  @BeanProperty
  var currentSpecification: Mcrl2Object = null
}
