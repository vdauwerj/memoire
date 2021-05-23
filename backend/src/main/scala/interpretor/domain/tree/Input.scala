package interpretor.domain.tree

import scala.beans.BeanProperty

/**
  * This class purpose is to store the input mcrl2 specification received from web-app
  */
class Input{

  @BeanProperty
  var code: String = _

}
