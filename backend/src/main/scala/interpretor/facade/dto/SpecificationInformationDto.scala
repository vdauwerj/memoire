package interpretor.facade.dto

import interpretor.dsl.model.Mcrl2Object

import scala.beans.BeanProperty


class SpecificationInformationDto {
  @BeanProperty
  var processes: Array[ProcessDto] = Array.empty
  @BeanProperty
  var nextExecutableItems: NextExecutableItemsDto= new NextExecutableItemsDto
  @BeanProperty
  var errorMessage: String = ""
  @BeanProperty
  var currentSpecification: Mcrl2Object = null
}
