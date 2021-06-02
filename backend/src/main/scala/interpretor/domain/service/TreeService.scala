package interpretor.domain.service

import interpretor.dsl.model.{Action, Block, Composition, Delta, Mcrl2Object, ProcessId, ProcessSpec}
import interpretor.dsl.validation.Util
import interpretor.facade.dto._
import org.springframework.stereotype.Service

@Service
class TreeService {

  /**
   * select an action
   *
   * @param unique
   * @param currentSpecification
   * @param processes
   * @return current specification with disabled set after selection
   */
  def select(unique: Int, currentSpecification: Mcrl2Object, processes: Array[ProcessDto]) = {
    disableNode(unique, currentSpecification, processes, recursive = true)
  }

  /**
   * add the process name into the specification param
   *
   * @param specification
   * @param name
   * @return specification with all items linked to process name
   */
  def addProcessName(specification: Mcrl2Object, name: String): Mcrl2Object = {
    specification match {
      case comp: Composition => {
        comp.left = addProcessName(comp.left, name)
        comp.right = addProcessName(comp.right, name)
        comp.processName = name
      }
      case act: Action => {
        act.processName = name
      }
      case block: Block => addProcessName(block.specification, name)
      case _ =>
    }
    specification
  }

  /**
   * active next executable action of specification
   *
   * @param spec
   * @param processes
   * @return
   */
  def setUpTree(spec: Mcrl2Object, processes: Array[ProcessDto], blockedActions: Array[Action]): Mcrl2Object = {
    spec match {
      case comp: Composition => {
        setUpComposition(comp, processes, blockedActions)
      }
      case act: Action => {
        if (blockedActions.exists(action => action.name == act.name)) {
          new Delta
        } else {
          act.setDisabled(false)
          act
        }
      }
      case pid: ProcessId => {
        pid.setDisabled(false)
        val currentProcess = processes.find(proc => proc.name.equals(pid.name)).get
        val node = setUpTree(copySpec(currentProcess.specification, currentProcess.name), processes, blockedActions)
        Util.giveIdsToSpec(node)
        node.processName = currentProcess.name
        node
      }
      case ps: ProcessSpec => {
        setUpTree(ps.process, processes, blockedActions)
      };
      case block: Block => {
        setUpTree(block.specification, processes, block.actions.toArray)
      }
    }
  }

  /**
   * Provide textual representation of action and next items after action
   *
   * @param unique
   * @param currentSpecification
   * @return textual representation of action and next items after action
   */
  def getTextFromUnique(unique: Array[Int], currentSpecification: Mcrl2Object): String = {
    val node = findParentNode(currentSpecification, unique.last, currentSpecification)
    transformToText(node)
  }

  private def transformToText(specification: Mcrl2Object): String = {
    var result: String = ""
    specification match {
      case comp: Composition => {
        val left = transformToText(comp.left)
        val right = transformToText(comp.right)
        result = result + left + comp.op + right
      }
      case act: Action => {
        result = result + act.name
      }
      case pid: ProcessId => {
        result = result + pid.name
      }

    }
    result
  }

  /**
   * Disables a node in a given spec and spreads the modification to all the tree to keep coherence and adequation
   * with µCRL2 logic
   *
   * If the node is a processId, it just replaces it by the corresponding term
   *
   * @param unique    the identifier for the node
   * @param spec      the specification to look in
   * @param proc      the list of processSpec (needed in case a processId is clicked)
   * @param recursive is used to know if the call to this function is initiated by the user or by a recursive call
   *                  (this is necessary to avoid indefinitely replace all processIds and potential
   *                  infinite recursive call)
   * @return The updated tree
   */
  private def disableNode(unique: Int, spec: Mcrl2Object, proc: Array[ProcessDto], recursive: Boolean): Mcrl2Object = {
    var node = findNode(spec, unique)
    node match {
      case process: ProcessId => node = setUpTree(process, proc, Array.empty)
      case composition: Composition =>
        val disabled = !(composition.op == "||" && isNodeEnabled(composition.left) && isNodeEnabled(composition.right))
        composition.setDisabled(disabled)

      case action: Action => action.setDisabled(true)
    }

    if (Util.findTraceToNode(spec, unique).size > 1) {
      val parentNode = findNode(spec, Util.findTraceToNode(spec, unique).tail.head)
      parentNode match {
        case comp: Composition => {
          var newSpec = spec
          if (comp.op == "+") {
            Util.disableOtherChildOfComposition(comp, unique)
          }
          if (comp.op == "." && Util.getUnique(comp.left) == unique && !isNodeEnabled(comp.left)) {
            val newNodeRight = setUpTree(comp.right, proc, Array.empty)
            comp.right = newNodeRight
          }

          return disableNode(comp.unique, newSpec, proc, recursive = true)
        }
      }
    }
    spec
  }


  private def isNodeEnabled(spec: Mcrl2Object): Boolean = {
    spec match {
      case act: Action => !act.disabled
      case pid: ProcessId => !pid.disabled
      case comp: Composition => isNodeEnabled(comp.left) || isNodeEnabled(comp.right)
    }
  }

  /**
   * Gets the term having the given unique id
   *
   * @param spec   the term to search within
   * @param unique the id of the term to find
   * @return the term corresponding to the given id
   */
  private def findNode(spec: Mcrl2Object, unique: Int): Mcrl2Object = {
    spec match {
      case comp: Composition => {
        if (comp.unique == unique) {
          comp
        } else {
          val left = findNode(comp.left, unique)
          val right = findNode(comp.right, unique)
          if (left == null) right else left
        }
      }
      case act: Action => {
        if (act.unique == unique) {
          act
        } else {
          null
        }
      }
      case pid: ProcessId => {
        if (pid.unique == unique) {
          pid
        } else {
          null
        }
      }
    }
  }

  private def findParentNode(spec: Mcrl2Object, unique: Int, parent: Mcrl2Object): Mcrl2Object = {
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
   * Method used to 'deep' copy a µCRL2 object. Necessary when using several
   * times a process (to avoids collision-problems)
   *
   * @param mcrl2Object The object to copy
   * @return a new object with the same values
   */
  private def copySpec(mcrl2Object: Mcrl2Object, process: String): Mcrl2Object = {
    mcrl2Object match {
      case act: Action => {
        var x = new Action(act.unique, new String(act.name))
        x.processName = process
        x.setDisabled(act.disabled)
        x
      }
      case pid: ProcessId => {
        var x = new ProcessId(pid.unique, new String(pid.name))
        x.processName = process
        x.setDisabled(pid.disabled)
        x
      }
      case pss: ProcessSpec => new ProcessSpec(copySpec(pss.id, process) match { case p: ProcessId => p }, copySpec(pss.process, process))
      case comp: Composition => {
        var x = new Composition(comp.unique, new String(comp.op), copySpec(comp.left, process), copySpec(comp.right, process))
        x.processName = process
        x.setDisabled(comp.disabled)
        x
      }
      case delta: Delta => new Delta
      case block: Block => new Block(block.actions, copySpec(block.specification, process))
    }
  }

  /**
   * Method used to set up a specific composition regarding its nature (enabling needed nodes)
   *
   * @param comp the composition to set up
   * @return the prepared composition (where accessible nodes are enabled)
   */
  private def setUpComposition(comp: Composition, processes: Array[ProcessDto], blockedActions: Array[Action]): Composition = {
    if (comp.op == ".") {
      comp.setLeft(setUpTree(comp.left, processes, blockedActions))
      return comp
    }
    if (comp.op == "||") {
      comp.setDisabled(false)
    }
    comp.setLeft(setUpTree(comp.left, processes, blockedActions))
    comp.setRight(setUpTree(comp.right, processes, blockedActions))
    comp
  }
}
