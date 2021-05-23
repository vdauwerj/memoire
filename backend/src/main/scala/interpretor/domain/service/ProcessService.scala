package interpretor.domain.service

import interpretor.dsl.model.{Action, Composition, Mcrl2Object, ProcessId, ProcessSpec}
import interpretor.facade.dto.{ActionDto, ProcessDto}
import org.springframework.stereotype.Service

@Service
class ProcessService {

  /**
   * Generate a process dto from a process spec
   * @param initProcess
   * @param processesInSpec
   * @return process dto of each process
   */
  def getProcess(initProcess: ProcessSpec, processesInSpec: List[ProcessSpec]) = {
    val processes = if (processesInSpec != null) processesInSpec.toArray else Array.empty[ProcessSpec]
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
        if(comp.op.equals("||")){
          left.foreach(left => right.foreach(right => {
            val action = new ActionDto()
            action.setProcess(processId)
            action.setName(left.name + " | " + right.name)
            answer ::= action
          }))
        }
        answer :::= left
        answer :::= right
      }
      case act: Action => {
        val action = new ActionDto
        action.setName(act.name)
        action.setProcess(processId)
        answer = answer :+ action
      }
      case proc: ProcessId => answer = answer
    }
    answer.distinct
  }
}
