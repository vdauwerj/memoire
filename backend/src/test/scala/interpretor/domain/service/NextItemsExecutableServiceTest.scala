package interpretor.domain.service

import org.junit.Test


class NextItemsExecutableServiceTest {
  val nextItemsExecutableService: NextItemsExecutableService = new NextItemsExecutableService

  @Test
  def given_alternativeSpecification_when_findNextExecutableElements_then_receiveTwoActionsOfAlternative(): Unit = {
    val nextExecutableItemsDto = nextItemsExecutableService.findNextExecutableElements(TreeSampleData.ALTERNATIVE)
    assert(nextExecutableItemsDto.single.length == 2)
    assert(nextExecutableItemsDto.single(0).name == "action1")
    assert(nextExecutableItemsDto.single(0).unique sameElements Array(2))
    assert(nextExecutableItemsDto.single(1).name == "action2")
    assert(nextExecutableItemsDto.single(1).unique sameElements Array(3))
    assert(nextExecutableItemsDto.multi.isEmpty)
  }
  @Test
  def given_sequentialSpecification_when_findNextExecutableElements_then_receiveLeftActionOfSequence(): Unit = {
    val nextExecutableItemsDto = nextItemsExecutableService.findNextExecutableElements(TreeSampleData.SEQUENTIAL)
    assert(nextExecutableItemsDto.single.length == 1)
    assert(nextExecutableItemsDto.single(0).name == "action1")
    assert(nextExecutableItemsDto.single(0).unique sameElements Array(2))
    assert(nextExecutableItemsDto.multi.isEmpty)
  }
  @Test
  def given_intraParallelSpecification_when_findNextExecutableElements_then_receiveSingleActionsOfParallel(): Unit = {
    val nextExecutableItemsDto = nextItemsExecutableService.findNextExecutableElements(TreeSampleData.PARALLEL)
    assert(nextExecutableItemsDto.single.length == 3)
    assert(nextExecutableItemsDto.single(0).name == "action1")
    assert(nextExecutableItemsDto.single(0).unique sameElements Array(2))
    assert(nextExecutableItemsDto.single(1).name == "action2")
    assert(nextExecutableItemsDto.single(1).unique sameElements Array(3))
    assert(nextExecutableItemsDto.single(2).name == "action1 | action2")
    assert(nextExecutableItemsDto.single(2).unique sameElements Array(2, 3))
    assert(nextExecutableItemsDto.multi.isEmpty)
  }
  @Test
  def given_interParallelSpecification_when_findNextExecutableElements_then_receiveSingleMultiActionsOfParallel(): Unit = {
    val nextExecutableItemsDto = nextItemsExecutableService.findNextExecutableElements(TreeSampleData.PARALLEL_INTER_PROCESS)
    assert(nextExecutableItemsDto.single.length == 2)
    assert(nextExecutableItemsDto.single(0).name == "action1")
    assert(nextExecutableItemsDto.single(0).process == "Process1")
    assert(nextExecutableItemsDto.single(0).unique sameElements Array(2))
    assert(nextExecutableItemsDto.single(1).name == "action2")
    assert(nextExecutableItemsDto.single(1).process == "Process2")
    assert(nextExecutableItemsDto.single(1).unique sameElements Array(3))
    assert(nextExecutableItemsDto.multi.length == 1)
    assert(nextExecutableItemsDto.multi(0).actions.length == 2)
    assert(nextExecutableItemsDto.multi(0).actions(0).name == "action1")
    assert(nextExecutableItemsDto.multi(0).actions(0).unique  sameElements Array(2))
    assert(nextExecutableItemsDto.multi(0).actions(1).name == "action2")
    assert(nextExecutableItemsDto.multi(0).actions(1).unique  sameElements Array(3))
  }

  @Test
  def given_interAndIntraParallelSpecification_when_findNextExecutableElements_then_receiveSingleMultiActionsOfParallel(): Unit = {
    val nextExecutableItemsDto = nextItemsExecutableService.findNextExecutableElements(TreeSampleData.PARALLEL_INTER_AND_INTRA_PROCESS)
    assert(nextExecutableItemsDto.single.length == 4)
    val firstSingle = nextExecutableItemsDto.single(0)
    assert(firstSingle.name == "action3")
    assert(firstSingle.process == "Process2")
    assert(firstSingle.unique sameElements Array(5))

    val secondSingle = nextExecutableItemsDto.single(1)
    assert(secondSingle.name == "action2")
    assert(secondSingle.process == "Process2")
    assert(secondSingle.unique sameElements Array(3))

    val thirdSingle = nextExecutableItemsDto.single(2)
    assert(thirdSingle.name == "action3 | action2")
    assert(thirdSingle.process == "Process2")
    assert(thirdSingle.unique sameElements Array(5, 3))

    val fourthSingle = nextExecutableItemsDto.single(3)
    assert(fourthSingle.name == "action1")
    assert(fourthSingle.process == "Process1")
    assert(fourthSingle.unique sameElements Array(2))

    assert(nextExecutableItemsDto.multi.length == 3)

    val firstMulti = nextExecutableItemsDto.multi(0)
    assert(firstMulti.actions.length == 2)
    assert(firstMulti.actions(0).name == "action3")
    assert(firstMulti.actions(0).process == "Process2")
    assert(firstMulti.actions(0).unique  sameElements Array(5))
    assert(firstMulti.actions(1).name == "action1")
    assert(firstMulti.actions(1).process == "Process1")
    assert(firstMulti.actions(1).unique  sameElements Array(2))

    val secondMulti = nextExecutableItemsDto.multi(1)
    assert(secondMulti.actions.length == 2)
    assert(secondMulti.actions(0).name == "action2")
    assert(secondMulti.actions(0).process == "Process2")
    assert(secondMulti.actions(0).unique  sameElements Array(3))
    assert(secondMulti.actions(1).name == "action1")
    assert(secondMulti.actions(1).process == "Process1")
    assert(secondMulti.actions(1).unique  sameElements Array(2))

    val thirdMulti = nextExecutableItemsDto.multi(2)
    assert(thirdMulti.actions.length == 2)
    assert(thirdMulti.actions(0).name == "action3 | action2")
    assert(thirdMulti.actions(0).process == "Process2")
    assert(thirdMulti.actions(0).unique  sameElements Array(5, 3))
    assert(thirdMulti.actions(1).name == "action1")
    assert(thirdMulti.actions(1).process == "Process1")
    assert(thirdMulti.actions(1).unique  sameElements Array(2))

  }
}
