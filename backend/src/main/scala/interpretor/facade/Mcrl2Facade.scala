package interpretor.facade

import interpretor.domain.service.{NextItemsExecutableService, ProcessService, TreeService}
import interpretor.domain.tree.{Input, Specification}
import interpretor.dsl.dsl.Mcrl2Parser
import interpretor.exception.ValidationException
import interpretor.facade.dto._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class Mcrl2Facade {

  @Autowired
  var processService: ProcessService = null
  @Autowired
  var nextExecutableService: NextItemsExecutableService = null
  @Autowired
  var treeService: TreeService = null

  /**
   * load a specification
   * @param specification
   * @return necessary information for the specification
   */
  def loadSpecification(specification: Input): SpecificationInformationDto = {
    val parser = new Mcrl2Parser
    val state = new SpecificationInformationDto
    try {
      val parsingResult = parser.parseAll(parser.mcrl2Specification, specification.getCode)
      parsingResult match {
        case parser.Success(r, n) => return init(parsingResult.get)
        case parser.Failure(msg, n) => state.setErrorMessage("PLEASE CHECK SYNTAX : " + msg)
        case parser.Error(msg, n) => state.setErrorMessage("PLEASE CHECK SYNTAX : " + msg)
      }
    } catch {
      case e: ValidationException => state.setErrorMessage(e.getMessage)
    }
    state
  }

  /**
   * select an action
   * @param selectRequest
   * @return next executables actions and the new current specification
   */
  def select(selectRequest: SelectRequestDto): NextExecutableItemsDto = {
    val unique = selectRequest.getUnique
    var currentSpecification = selectRequest.currentSpecification
    unique.foreach(unique => currentSpecification = treeService.select(unique, selectRequest.currentSpecification, selectRequest.processes))
    val nextExecutableActionsDto = nextExecutableService.findNextExecutableElements(currentSpecification)
    nextExecutableActionsDto.setCurrentSpecification(currentSpecification)
    nextExecutableActionsDto
  }

  /**
   * give an action and the following items in text
   * @param textRequest
   * @return an action and the following items in text
   */
  def getStringParent(textRequest: RequestTextSpecDto) = {
    textRequest.actions.map(action =>
      new TextSpecificationDto(treeService.getTextFromUnique(action.unique, textRequest.currentSpecification), action.unique))
  }

  private def init(parsingResultValue: Specification) = {
    val state = new SpecificationInformationDto
    val firstProcess = parsingResultValue.initialSpec.getProcess
    treeService.addProcessName(firstProcess, "INIT")

    val processes = processService.getProcess(parsingResultValue.initialSpec, parsingResultValue.processes)
    state.setCurrentSpecification(treeService.setUpTree(firstProcess, processes, Array.empty))

    val nextExecutableActionsDto = nextExecutableService.findNextExecutableElements(state.currentSpecification)
    state.setNextExecutableItems(nextExecutableActionsDto)
    state.setProcesses(processes)

    state
  }
}
