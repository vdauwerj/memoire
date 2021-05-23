package interpretor.facade.dto

import scala.beans.BeanProperty

class ActionsDto(@BeanProperty
                 var actions: Array[ActionDto] = Array.empty) {

}
