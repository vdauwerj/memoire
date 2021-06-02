package interpretor.domain.service

import interpretor.dsl.model.{Action, Block, Composition, Mcrl2Object, ProcessId, ProcessSpec}
import interpretor.facade.dto.{ActionDto, ProcessDto}
import org.springframework.stereotype.Service

@Service
class ProcessService {

  /**
   * Generate a process dto from a process spec
   *
   * @param initProcess
   * @param processesDeclared
   * @return process dto of each process
   */
  def getProcess(initProcess: ProcessSpec, processesDeclared: List[ProcessSpec]) = {
    val processes = if (processesDeclared != null) processesDeclared.toArray else Array.empty[ProcessSpec]
    val processesWithInit = processes :+ initProcess
    processesWithInit.map(process => {
      val actions = findActions(process.process, process.id.name).toArray
      new ProcessDto(process.id.name, actions, process.process)
    })
  }

  private def findActions(specification: Mcrl2Object, processId: String): List[ActionDto] = {
    var answer: List[ActionDto] = List.empty
    specification match {
      case comp: Composition => {
        val left = findActions(comp.left, processId)
        val right = findActions(comp.right, processId)
        if (comp.op.equals("||")) {
          left.foreach(left => right.foreach(right =>
            answer ::= new ActionDto(null, left.name + " | " + right.name, processId)
          ))
        }
        answer :::= left
        answer :::= right
      }
      case act: Action => answer = answer :+ new ActionDto(null, act.name, processId)
      case proc: ProcessId => answer = answer
      case block: Block => findActions(block.specification, processId)
    }
    answer.distinct
  }
}
