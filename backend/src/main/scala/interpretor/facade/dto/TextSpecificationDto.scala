package interpretor.facade.dto

import scala.beans.BeanProperty

class TextSpecificationDto(
                         @BeanProperty
                         var text: String = "",
                         @BeanProperty
                         var unique: Array[Int] = Array.empty
                       ) {
}
