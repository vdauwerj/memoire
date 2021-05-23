package interpretor.facade.dto

import scala.beans.BeanProperty

class ChooseActionDto {
  @BeanProperty
  var unique: Int = 0
  @BeanProperty
  var name: String = ""
}
