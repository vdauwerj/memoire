package interpretor.dsl.model

import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}

import scala.beans.BeanProperty

/**
 * Created by Olivier on 06/05/216.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(Array(
  new Type(value = classOf[Action], name = "x"),
  new Type(value = classOf[Composition], name = "y"),
  new Type(value = classOf[ProcessId], name = "z")
))
abstract class Mcrl2Object{
  @BeanProperty
  var processName: String = ""
  override def clone = this
}
