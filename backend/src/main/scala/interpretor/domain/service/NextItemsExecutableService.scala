package interpretor.domain.service

import interpretor.dsl.model.{Action, Composition, Mcrl2Object}
import interpretor.facade.dto.{ActionDto, ActionsDto, NextExecutableItemsDto}
import org.springframework.stereotype.Service

@Service
class NextItemsExecutableService {

  /**
   * retrieve all next executable actions and split them into two categories : multi or single
   * @param specification
   * @return  all next executable actions
   */
  def findNextExecutableElements(specification: Mcrl2Object): NextExecutableItemsDto = {
    val result = new NextExecutableItemsDto
    specification match {
      case act: Action => {
        if (!act.disabled) {
          result.single = result.single :+ new ActionDto(Array(act.unique), act.name, act.processName)
        }
      }
      case comp: Composition => {
        val leftResult = findNextExecutableElements(comp.left)
        val rightResult = findNextExecutableElements(comp.right)

        result.single = Array.concat(result.single, leftResult.single, rightResult.single)
        result.multi = Array.concat(result.multi, leftResult.multi, rightResult.multi)

        if (comp.op.equals("||") && !comp.disabled) {
          if (getProcess(leftResult).equals(getProcess(rightResult))) {
            result.single = Array.concat(result.single, manageIntraProcessParallel(leftResult, rightResult))
          } else {
            result.multi = Array.concat(result.multi, getAllMultiInterParallelItems(leftResult, rightResult))
          }
        }
      }
      case _ =>
    }
    result
  }

  private def manageIntraProcessParallel(leftResult: NextExecutableItemsDto, rightResult: NextExecutableItemsDto) = {
    var result = Array.empty[ActionDto]

    leftResult.single.foreach(left => rightResult.single.foreach(right => {
      val action = new ActionDto(Array.concat(left.unique, right.unique), left.name + " | " + right.name, getProcess(leftResult))
      result = result :+ action
    }))
    result
  }

  private def getAllMultiInterParallelItems(leftResult: NextExecutableItemsDto, rightResult: NextExecutableItemsDto) = {
    var result = Array.empty[ActionsDto]
    leftResult.single.map(left => rightResult.single.map(right => {
      val actionsDto = new ActionsDto(Array(left, right))
      result = result :+ actionsDto
    }))
    leftResult.multi.foreach(left => rightResult.multi.foreach(right => {
      val actionsDto = new ActionsDto(Array.concat(left.actions, right.actions))
      result = result :+ actionsDto
    }))
    leftResult.multi.foreach(left => rightResult.single.foreach(right => {
      val actionsDto = new ActionsDto(Array.concat(left.actions, Array(right)))
      result = result :+ actionsDto
    }))
    rightResult.multi.foreach(right => leftResult.single.foreach(left => {
      val actionsDto = new ActionsDto(Array.concat(right.actions, Array(left)))
      result = result :+ actionsDto
    }))

    result
  }


  private def getProcess(nextExecutableItem: NextExecutableItemsDto): String = {
    val single = nextExecutableItem.single
    val multi = nextExecutableItem.multi
    if (!single.isEmpty) {
      return single.last.process;
    }
    if (!multi.isEmpty) {
      return multi.last.actions.last.process;
    }
    null;
  }
}
