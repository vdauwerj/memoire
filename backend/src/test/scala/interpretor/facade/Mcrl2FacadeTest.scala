package interpretor.facade

import interpretor.domain.tree.Input
import interpretor.facade.dto.SelectRequestDto
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(classOf[SpringRunner])
@SpringBootTest()
class Mcrl2FacadeTest {

  @Autowired
  var mcrl2Facade: Mcrl2Facade = null

  @Test
  def given_textSpecificationWithProcessInit_when_loadSpecification_then_processIsNextItemsAndProcessInformationIsCorrect(): Unit = {
    val input: Input = new Input()
    input.setCode(TextSampleData.SPECIFICATION_WITH_ALTERNATIVE_COMPOSITION_PROCESS)

    val specificationInformation = mcrl2Facade.loadSpecification(input)

    val nextItems = specificationInformation.nextExecutableItems
    assert(nextItems.single.length == 2)
    assert(nextItems.single(0).process == "Process1")
    assert(nextItems.single(0).name == "action1")
    assert(nextItems.single(1).process == "Process1")
    assert(nextItems.single(1).name == "action2")
    assert(nextItems.multi.isEmpty)

  }
  @Test
  def given_textSpecificationWithTwoActionsParallelIntraInit_when_loadSpecification_then_parallelActionsAreNextItemsAndProcessInformationIsCorrect(): Unit = {
    val input: Input = new Input()
    input.setCode(TextSampleData.SPECIFICATION_WITH_PARALLEL_COMPOSITION_INIT)

    val specificationInformation = mcrl2Facade.loadSpecification(input)

    val firstAction = specificationInformation.processes(0).actions(0)
    assert(firstAction.process == "INIT")
    assert(firstAction.name == "action2")

    val secondAction = specificationInformation.processes(0).actions(1)
    assert(secondAction.process == "INIT")
    assert(secondAction.name == "action1")

    val bothAction = specificationInformation.processes(0).actions(2)
    assert(bothAction.process == "INIT")
    assert(bothAction.name == "action1 | action2")

    val nextItems = specificationInformation.nextExecutableItems
    assert(nextItems.single.length == 3)
    assert(nextItems.single(0).process == "INIT")
    assert(nextItems.single(1).process == "INIT")
    assert(nextItems.single(2).process == "INIT")

    assert(nextItems.single(0).name == "action1")
    assert(nextItems.single(1).name == "action2")
    assert(nextItems.single(2).name == "action1 | action2")
    assert(nextItems.single(2).unique sameElements Array.concat(nextItems.single(0).unique, nextItems.single(1).unique))
    assert(nextItems.multi.isEmpty)
  }
  @Test
  def given_textSpecificationWithActionInit_when_loadSpecification_then_actionIsNextItems(): Unit = {
    val input: Input = new Input()
    input.setCode(TextSampleData.SPECIFICATION_WITH_ALTERNATIVE_COMPOSITION_INIT)
    val specificationInformation = mcrl2Facade.loadSpecification(input)

    val firstAction = specificationInformation.processes(0).actions(0)
    assert(firstAction.process == "INIT")
    assert(firstAction.name == "action2")

    val secondAction = specificationInformation.processes(0).actions(1)
    assert(secondAction.process == "INIT")
    assert(secondAction.name == "action1")

    val nextItems = specificationInformation.nextExecutableItems
    assert(nextItems.single.length == 2)
    assert(nextItems.single(0).name == "action1")
    assert(nextItems.single(0).process == "INIT")
    assert(nextItems.single(1).name == "action2")
    assert(nextItems.single(1).process == "INIT")
    assert(nextItems.multi.isEmpty)
  }
  @Test
  def given_textSpecificationWithTwoActionsParallelInterProcess1AndProcess2_when_loadSpecification_then_multiExistWithParallelActionsInNextItemsAndProcessInformationIsCorrect(): Unit = {
    val input: Input = new Input()
    input.setCode(TextSampleData.SPECIFICATION_WITH_PARALLEL_COMPOSITION_INTER_PROCESS)

    val specificationInformation = mcrl2Facade.loadSpecification(input)

    val initProcess = specificationInformation.processes(2)
    assert(initProcess.name == "INIT")


    val process1 = specificationInformation.processes(0)
    assert(process1.name == "Process1")
    val actionsProcess1 = process1.actions
    assert(actionsProcess1.length == 1)
    assert(actionsProcess1(0).process == "Process1")
    assert(actionsProcess1(0).name == "action1")

    val process2 = specificationInformation.processes(1)
    assert(process2.name == "Process2")
    val actionsProcess2 = process2.actions
    assert(actionsProcess2.length == 1)
    assert(actionsProcess2(0).process == "Process2")
    assert(actionsProcess2(0).name == "action2")

    val nextItems = specificationInformation.nextExecutableItems;
    assert(nextItems.single.length == 2)
    assert(nextItems.single(0).process == "Process1")
    assert(nextItems.single(0).name == "action1")
    assert(nextItems.single(1).process == "Process2")
    assert(nextItems.single(1).name == "action2")

    assert(nextItems.multi(0).actions.length == 2)
    assert(nextItems.multi(0).actions(0).name == "action1")
    assert(nextItems.multi(0).actions(0).process == "Process1")
    assert(nextItems.multi(0).actions(1).name == "action2")
    assert(nextItems.multi(0).actions(1).process == "Process2")
  }

  @Test
  def given_textSpecificationWithTwoActionsParallelInterProcess1AndProcess2_when_selectOnThenNextSpecification_then_noActionIsSelectableAfter(): Unit = {
    val input: Input = new Input()
    input.setCode(TextSampleData.SPECIFICATION_WITH_PARALLEL_COMPOSITION_INTER_PROCESS)

    val specificationInformation = mcrl2Facade.loadSpecification(input)

    val selectRequest = new SelectRequestDto
    selectRequest.currentSpecification = specificationInformation.currentSpecification
    selectRequest.processes = specificationInformation.processes
    selectRequest.setUnique( specificationInformation.nextExecutableItems.single(0).unique)
    var nextPossibility = mcrl2Facade.select(selectRequest)

    assert(nextPossibility.single.length == 1)
    assert(nextPossibility.single(0).process == "Process2")
    assert(nextPossibility.single(0).name == "action2")
    assert(nextPossibility.multi.isEmpty)
  }

  @Test
  def given_textSpecificationWithTwoActionsAlternativeInit_when_executeOneAction_then_noNextItemsIsPossible(): Unit = {
    val input: Input = new Input()
    input.setCode(TextSampleData.SPECIFICATION_WITH_ALTERNATIVE_COMPOSITION_INIT)
    val specificationInformation = mcrl2Facade.loadSpecification(input)
    val selectRequest = new SelectRequestDto
    selectRequest.currentSpecification = specificationInformation.currentSpecification
    selectRequest.processes = specificationInformation.processes
    selectRequest.setUnique(specificationInformation.nextExecutableItems.single(0).unique)
    val nextSelectableItems = mcrl2Facade.select(selectRequest)

    val nextItems = nextSelectableItems
    assert(nextItems.single.isEmpty)
    assert(nextItems.multi.isEmpty)
  }
  @Test
  def given_textSpecificationWithTwoActionsSequenceInit_when_executeFirstAction_then_lastActionIsExecutable(): Unit = {
    val input: Input = new Input()
    input.setCode(TextSampleData.SPECIFICATION_WITH_SEQUENTIAL_COMPOSITION_INIT)
    val specificationInformation = mcrl2Facade.loadSpecification(input)
    val selectRequest = new SelectRequestDto
    selectRequest.currentSpecification = specificationInformation.currentSpecification
    selectRequest.processes = specificationInformation.processes
    selectRequest.setUnique(specificationInformation.nextExecutableItems.single(0).unique)
    val nextSelectableItems = mcrl2Facade.select(selectRequest)

    val nextItems = nextSelectableItems
    assert(nextItems.single.length == 1)
    assert(nextItems.single(0).process == "INIT")
    assert(nextItems.single(0).name == "action2")
    assert(nextItems.multi.isEmpty)
  }
  @Test
  def given_textSpecificationWithTwoActionsParallelInit_when_executeBothActions_then_noActionIsExecutable(): Unit = {
    val input: Input = new Input()
    input.setCode(TextSampleData.SPECIFICATION_WITH_PARALLEL_COMPOSITION_INIT)
    val specificationInformation = mcrl2Facade.loadSpecification(input)
    val selectRequest = new SelectRequestDto
    selectRequest.currentSpecification = specificationInformation.currentSpecification
    selectRequest.processes = specificationInformation.processes
    val uniquesToSelect = specificationInformation.nextExecutableItems.single(2).unique
    selectRequest.setUnique(uniquesToSelect)
    val nextSelectableItems = mcrl2Facade.select(selectRequest)

    val nextItems = nextSelectableItems
    assert(nextItems.single.isEmpty)
    assert(nextItems.multi.isEmpty)
  }
  @Test
  def given_textSpecificationWithTwoActionsParallelInit_when_executeOneAction_then_lastActionIsExecutable(): Unit = {
    val input: Input = new Input()
    input.setCode(TextSampleData.SPECIFICATION_WITH_PARALLEL_COMPOSITION_INIT)
    val specificationInformation = mcrl2Facade.loadSpecification(input)
    val selectRequest = new SelectRequestDto
    val uniquesToSelect = specificationInformation.nextExecutableItems.single(0).unique
    selectRequest.currentSpecification = specificationInformation.currentSpecification
    selectRequest.processes = specificationInformation.processes
    selectRequest.setUnique(uniquesToSelect)
    val nextSelectableItems = mcrl2Facade.select(selectRequest)

    val nextItems = nextSelectableItems
    assert(nextItems.single.length == 1)
    assert(nextItems.single(0).process == "INIT")
    assert(nextItems.single(0).name == "action2")
    assert(nextItems.multi.isEmpty)
  }
}
