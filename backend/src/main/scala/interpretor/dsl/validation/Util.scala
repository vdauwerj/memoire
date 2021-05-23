package interpretor.dsl.validation

import interpretor.dsl.dsl
import interpretor.dsl.model.{Action, Composition, Delta, Mcrl2Object, ProcessId, ProcessSpec}
import interpretor.exception.ValidationException

import scala.collection.mutable.ListBuffer


/**
 * Created by Olivier on 1/12/2015. This class is a toolbox to work on specifications
 */
object Util {

  /**
   * Method used to retrieve a specific processSpec with the help of a given processId (String format)
   *
   * @param proc A list of ProcessSpec to search in
   * @param id   the id of the process to search in String format
   * @return If found, the corresponding processSpec
   *
   *         Throws an exception if the id is unknown (this should never happen with our current use case)
   */
  def findProcessById(proc: List[ProcessSpec], id: String): ProcessSpec = {
    var answer = new ProcessSpec(null, null)
    for (i <- proc.indices) {
      if (proc(i).id.name == id) {
        answer = proc(i)
      }
    }
    if (answer.id == null)
      throw new ValidationException("Error : unknown pid \"" + id + "\"")
    answer
  }

  /**
   * Gathers a complete list of all actions contained within a given process
   *
   * @param p The Mrcl2Object (a Process Algebra term representation)
   * @return A list containing all the actions in a process
   */
  def getActionsFromProcessSpec(p: Mcrl2Object): List[Action] = {
    var answer: List[Action] = List.empty
    p match {
      case comp: Composition => {
        answer :::= getActionsFromProcessSpec(comp.left)
        answer :::= getActionsFromProcessSpec(comp.right)
      }
      case act: Action => answer = answer :+ act
      case proc: ProcessId => answer = answer
    }
    answer
  }

  /**
   * Gathers a complete list of all processIds contained within a given process
   *
   * @param p The Mrcl2Object (a Process Algebra term representation)
   * @return A list containing all the processIds in a process
   */
  def getProcFromProcessSpec(p: Mcrl2Object): List[ProcessId] = {
    var answer: List[ProcessId] = List.empty
    p match {
      case comp: Composition => {
        answer :::= getProcFromProcessSpec(comp.left)
        answer :::= getProcFromProcessSpec(comp.right)
      }
      case act: Action => answer = answer
      case proc: ProcessId => answer = answer :+ proc
    }
    answer
  }

  /**
   * Method to transform a given action list to a string list (containing the names of actions)
   *
   * @param actions the action list
   * @return the list of the actions name
   */
  def actionListToStringList(actions: List[Action]): List[String] = {
    var answer: List[String] = List.empty
    for (i <- actions.indices) {
      answer = answer :+ actions(i).name
    }
    answer
  }

  /**
   * Method to transform a given processId list to a string list (containing the names of processIds)
   *
   * @param proc the action list
   * @return the list of the actions name
   */
  def pidToStringList(proc: List[ProcessId]): List[String] = {
    var answer: List[String] = List.empty
    for (i <- proc.indices) {
      answer = answer :+ proc(i).name
    }
    answer
  }

  /**
   * Boolean method to detect if all the actions listed in the second argument are present
   * in the list in the first argument
   *
   * @param defined defined actions
   * @param toCheck actions list
   * @return true inf all actions in toCheck are present in defined, false otherwise
   */
  def areActionsDefined(defined: List[Action], toCheck: List[Action]): Boolean = {
    for (i <- toCheck.indices) {
      if (!actionListToStringList(defined).contains(actionListToStringList(toCheck)(i))) {
        return false
      }
    }
    true
  }

  /**
   * Boolean method to detect if all the processId listed in the second argument are present
   * in the list in the first argument
   *
   * @param defined defined process specifications
   * @param toCheck process ids list
   * @return true inf all processids in toCheck are present in defined, false otherwise
   */
  def areProcDefined(defined: List[ProcessSpec], toCheck: List[ProcessId]): Boolean = {
    var definedIds: List[ProcessId] = List.empty
    for (i <- defined.indices) {
      definedIds = definedIds :+ defined(i).id
    }
    for (i <- toCheck.indices) {
      if (!pidToStringList(definedIds).contains(pidToStringList(toCheck)(i))) {
        return false
      }
    }
    true
  }

  /**
   * Detects whether a processSpec lists contains only one definition for each processId
   *
   * @param p a list of processSpec
   * @return true if all process are defined only once, false otherwise
   */
  def areProcUnique(p: List[ProcessSpec]): Boolean = {
    for (i <- p.indices) {
      for (j <- i + 1 until p.size) {
        if (p(i).id.name == p(j).id.name) {
          return false
        }
      }
    }
    true
  }

  /**
   * Detects whether a actions lists contains only one definition for each action
   *
   * @param a a list of actions
   * @return true if all actions are defined only once, false otherwise
   */
  def areActionsUnique(a: List[Action]): Boolean = {
    for (i <- a.indices) {
      for (j <- i + 1 until a.size) {
        if (a(i).name == a(j).name) {
          return false
        }
      }
    }
    true
  }

  /**
   * Gets the term having the given unique id
   *
   * @param spec   the term to search within
   * @param unique the id of the term to find
   * @return the term corresponding to the given id
   */
  def findNode(spec: Mcrl2Object, unique: Int): Mcrl2Object = {
    spec match {
      case comp: Composition => {
        if (comp.unique == unique) {
          return comp
        } else {
          val left = findNode(comp.left, unique)
          val right = findNode(comp.right, unique)
          if (left == null) return right else return left
        }
      }
      case act: Action => {
        if (act.unique == unique) {
          return act
        } else {
          return null
        }
      }
      case pid: ProcessId => {
        if (pid.unique == unique) {
          return pid
        } else {
          return null
        }
      }
    }
  }

  def findParentNode(spec: Mcrl2Object, unique: Int, parent: Mcrl2Object): Mcrl2Object = {
    spec match {
      case comp: Composition => {
        if (comp.unique == unique) {
          return parent
        } else {
          val left = findParentNode(comp.left, unique, comp)
          val right = findParentNode(comp.right, unique, comp)
          if (left == null) return right else return left
        }
      }
      case act: Action => {
        if (act.unique == unique) {
          return parent
        } else {
          return null
        }
      }
      case pid: ProcessId => {
        if (pid.unique == unique) {
          return parent
        } else {
          return null
        }
      }
    }
  }

  /**
   * Methods use to give all term a unique Id in for the current session to the given processSpec
   *
   * @param spec The spec to give ids to
   */
  def giveIdsToSpec(spec: Mcrl2Object): Unit = {
    spec match {
      case act: Action => {
        dsl.unique += 1
        act.unique = dsl.unique
      }
      case pid: ProcessId => {
        dsl.unique += 1
        pid.unique = dsl.unique
      }
      case comp: Composition => {
        dsl.unique += 1
        comp.unique = dsl.unique
        giveIdsToSpec(comp.left)
        giveIdsToSpec(comp.right)
      }
    }
  }

  /**
   * Replace a term in a given specification
   *
   * @param spec    The specification in which we need to replace the term
   * @param unique  The id of the term to replace
   * @param newNode The new term to put at that place
   * @return The modified specification (term)
   */
  def replaceNode(spec: Mcrl2Object, unique: Int, newNode: Mcrl2Object): Mcrl2Object = {
    if (findTraceToNode(spec, unique).size == 1) {
      return newNode
    } else {
      val parentNode = findNode(spec, findTraceToNode(spec, unique).tail.head)
      parentNode match {
        case comp: Composition => {
          comp.left match {
            case pid: ProcessId => {
              if (pid.unique == unique) {
                comp.setLeft(newNode)
                return spec
              }
            }
            case _ =>
          }
          comp.right match {
            case pid: ProcessId => {
              if (pid.unique == unique) {
                comp.setRight(newNode)
                return spec
              }
            }
            case _ =>
          }
          spec
        }
      }
    }
  }

  def replaceNode(spec: Mcrl2Object, newNode: Mcrl2Object): Mcrl2Object = {
    spec match {
      case process: ProcessId => {
        newNode
      }
      case comp: Composition => {
        comp.left match {
          case pid: ProcessId => {
              comp.setLeft(newNode)
              return spec
          }
          case _ =>
        }
        comp.right match {
          case pid: ProcessId => {
              comp.setRight(newNode)
              return spec
          }
          case _ =>
        }
        spec
      }

    }
}

/**
 * Method use to get the trace from root of a term to a given term
 *
 * @param spec   The spec in which we are looking for the term
 * @param unique The id of the term that we want to know the trace
 * @return An ordered list of ids from searched term to root
 */
def findTraceToNode (spec: Mcrl2Object, unique: Int): ListBuffer[Int] = {
  var traceToReach: ListBuffer[Int] = ListBuffer.empty;
  isRightPath (spec, unique, traceToReach)
  traceToReach
}

  /**
   * Utility method used in findTraceToNode to populate the trace of the given term id
   *
   * @param spec   The spec in which we are looking for the term
   * @param unique The id of the term that we want to know the trace
   * @param trace  the trace to populate
   * @return
   */
  def isRightPath (spec: Mcrl2Object, unique: Int, trace: ListBuffer[Int] ): Boolean = {
  spec match {
  case act: Action => {
  if (act.unique != unique) {
  return false
} else {
  trace += unique
  return true
}
}
  case pid: ProcessId => {
  if (pid.unique != unique) {
  return false
} else {
  trace += unique
  return true
}
}
  case comp: Composition => {
  if (comp.unique == unique || isRightPath (comp.left, unique, trace) || isRightPath (comp.right, unique, trace) ) {
  trace += comp.unique
  return true
} else {
  return false
}
}
}
  false
}

  /**
   * Method to now if a term is enabled. It means if the term itself or at least one of its (sub)children is not
   * disabled
   *
   * @param spec the term to search whithin
   * @return true if yes, false otherwise
   */
  def isNodeEnabled (spec: Mcrl2Object): Boolean = {
  spec match {
  case act: Action => {
  return ! act.disabled
}
  case pid: ProcessId => {
  return ! pid.disabled
}
  case comp: Composition => {
  return isNodeEnabled (comp.left) || isNodeEnabled (comp.right)
}
}
}

  /**
   * Method (used for the altenative composition) to find in a composition the child which HAS NOT the given id and
   * disables it (entirely)
   *
   * @param comp   the composition to look
   * @param unique the id of the child 'brother' of the one which has to be disabled
   */
  def disableOtherChildOfComposition (comp: Composition, unique: Int): Unit = {
  comp.left match {
  case act: Action => {
  if (act.unique != unique) {
  act.setDisabled (true)
}
}
  case pid: ProcessId => {
  if (pid.unique != unique) {
  pid.setDisabled (true)
}
}
  case comp: Composition => {
  if (comp.unique != unique) {
  comp.setDisabled (true)
  disableFullComposition (comp)
}
}
}
  comp.right match {
  case act: Action => {
  if (act.unique != unique) {
  act.setDisabled (true)
}
}
  case pid: ProcessId => {
  if (pid.unique != unique) {
  pid.setDisabled (true)
}
}
  case comp: Composition => {
  if (comp.unique != unique) {
  disableFullComposition (comp)
}
}
}
}

  /**
   * Method used to disable a composition and all of its children
   *
   * @param comp the composiotn to disable
   */
  def disableFullComposition (comp: Composition): Unit = {
  comp.setDisabled (true)
  comp.left match {
  case act: Action => {
  act.setDisabled (true)
}
  case pid: ProcessId => {
  pid.setDisabled (true)
}
  case comp: Composition => {
  comp.setDisabled (true)
  disableFullComposition (comp)
}
}
  comp.right match {
  case act: Action => {
  act.setDisabled (true)
}
  case pid: ProcessId => {
  pid.setDisabled (true)

}
  case comp: Composition => {
  comp.setDisabled (true)
  disableFullComposition (comp)

}

}
}

  /**
   * Method used to get the unique identifier of a term
   *
   * @param mcrl2Object the term we want to know the id
   * @return the id of the given term
   */
  def getUnique (mcrl2Object: Mcrl2Object): Int = {
  mcrl2Object match {
  case act: Action => act.unique
  case pid: ProcessId => pid.unique
  case comp: Composition => comp.unique
}
}

  /**
   * This method provides a list of all the enabled node identifiers of a specification
   *
   * @param spec the specification (Mcrl2Object)
   * @param pl   if true, takes parallel composition into account, if false, jumps over them
   * @return The wanted list of active node identifiers
   */
  def collectActiveNodes (spec: Mcrl2Object, pl: Boolean): ListBuffer[Int] = {
  var answer: ListBuffer[Int] = ListBuffer.empty
  spec match {
  case act: Action => {
  if (! act.disabled)
  answer = answer :+ act.unique
}
  case pid: ProcessId => {
  if (! pid.disabled)
  answer = answer :+ pid.unique
}
  case comp: Composition => {
  if ((! comp.disabled) && pl)
  answer = answer :+ comp.unique
  answer = answer ++ collectActiveNodes (comp.left, pl)
  answer = answer ++ collectActiveNodes (comp.right, pl)
}
}
  answer
}

  /**
   * Method used to provide a random number in a list of number
   *
   * @param l the list of number
   * @return a number of the list
   */
  def pickANumber (l: ListBuffer[Int] ): Int = {
  val r = scala.util.Random
  val index = r.nextInt (l.size)
  l (index)
}

  /**
   * Method used to pick randomly some numbers of a given list of number
   *
   * @param l the given list
   * @return a list with some numbers of the given list
   */
  def pickSomeNumbers (l: ListBuffer[Int] ): ListBuffer[Int] = {
  var answer: ListBuffer[Int] = ListBuffer.empty
  val r = scala.util.Random
  var nbToTake = r.nextInt (l.size + 1)
  if (nbToTake == 0)
  nbToTake = 1
  for (i <- 0 until nbToTake) {
  var value = l.remove (r.nextInt (l.size) )
  answer += value
}
  answer
}


  /**
   * Method used to 'deep' copy a µCRL2 object. Necessary when using several
   * times a process (to avoids collision-problems)
   *
   * @param mcrl2Object The object to copy
   * @return a new object with the same values
   */
  def copySpec (mcrl2Object: Mcrl2Object, process: String): Mcrl2Object = {
  mcrl2Object match {
  case act: Action => {
  var x = new Action (act.unique, new String (act.name) )
  x.processName = process
  x.setDisabled (act.disabled)
  x
}
  case pid: ProcessId => {
  var x = new ProcessId (pid.unique, new String (pid.name) )
  x.processName = process
  x.setDisabled (pid.disabled)
  x
}
  case pss: ProcessSpec => new ProcessSpec (copySpec (pss.id, process) match {
  case p: ProcessId => p
}, copySpec (pss.process, process) )
  case comp: Composition => {
  var x = new Composition (comp.unique, new String (comp.op), copySpec (comp.left, process), copySpec (comp.right, process) )
  x.processName = process
  x.setDisabled (comp.disabled)
  x
}
  case delta: Delta => new Delta
}
}

  def copySpecBack (mcrl2Object: Mcrl2Object): Mcrl2Object = {
  mcrl2Object match {
  case act: Action => {
  var x = new Action (act.unique, new String (act.name) )
  x.processName = act.processName
  x.setDisabled (act.disabled)
  x
}
  case pid: ProcessId => {
  var x = new ProcessId (pid.unique, new String (pid.name) )
  x.processName = pid.processName
  x.setDisabled (pid.disabled)
  x
}
  case pss: ProcessSpec => new ProcessSpec (copySpecBack (pss.id) match {
  case p: ProcessId => p
}, copySpecBack (pss.process) )
  case comp: Composition => {
  var x = new Composition (comp.unique, new String (comp.op), copySpecBack (comp.left), copySpecBack (comp.right) )
  x.processName = comp.processName
  x.setDisabled (comp.disabled)
  x
}
  case delta: Delta => new Delta
}
}
}
