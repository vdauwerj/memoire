package interpretor.domain.service

import interpretor.dsl.model.{Action, Composition}
import org.junit.Test

class TreeServiceTest {

  val treeService = new TreeService
  def given_simpleAlternativeSpecification_when_selectFirstAction_then_theTwoAlternativeActionAreDisabled(): Unit ={
    val currentSpecification = treeService.select(3, TreeSampleData.ALTERNATIVE, null)
    assert(currentSpecification.asInstanceOf[Composition].left.asInstanceOf[Action].disabled)
    assert(currentSpecification.asInstanceOf[Composition].right.asInstanceOf[Action].disabled)
  }
  @Test
  def given_simpleSequentialSpecification_when_selectFirstAction_then_secondActionIsDisabledTrue(): Unit ={
    val currentSpecification = treeService.select(2,TreeSampleData.SEQUENTIAL, null)
    assert(currentSpecification.asInstanceOf[Composition].left.asInstanceOf[Action].disabled)
    assert(!currentSpecification.asInstanceOf[Composition].right.asInstanceOf[Action].disabled)
  }

  @Test
  def given_simpleParallelSpecification_when_selectAllParallelActions_then_allActionsAreDisabled(): Unit ={
    var currentSpecification = treeService.select(2,TreeSampleData.PARALLEL , null)
    currentSpecification = treeService.select(3, currentSpecification , null)
    assert(currentSpecification.asInstanceOf[Composition].left.asInstanceOf[Action].disabled)
    assert(currentSpecification.asInstanceOf[Composition].right.asInstanceOf[Action].disabled)
    assert(currentSpecification.asInstanceOf[Composition].disabled)
  }
  @Test
  def given_simpleParallelSpecification_when_selectOneParallelActions_then_allActionsAreDisabled(): Unit ={
    val currentSpecification = treeService.select(2, TreeSampleData.PARALLEL, null)
    assert(currentSpecification.asInstanceOf[Composition].left.asInstanceOf[Action].disabled)
    assert(!currentSpecification.asInstanceOf[Composition].right.asInstanceOf[Action].disabled)
    assert(currentSpecification.asInstanceOf[Composition].disabled)
  }

  @Test
  def given_sequenceThenAlternativeComposition_when_selectSequenceAction_then_theTwoAlternativeActionsAreDisabledFalse(): Unit ={
    val currentSpecification = treeService.select(2, TreeSampleData.SEQUENCE_THEN_ALTERNATIVE, null)
    assert(currentSpecification.asInstanceOf[Composition].left.asInstanceOf[Action].disabled)
    val alternative = currentSpecification.asInstanceOf[Composition].right.asInstanceOf[Composition]
    assert(! alternative.left.asInstanceOf[Action].disabled)
    assert(! alternative.right.asInstanceOf[Action].disabled)
  }

  @Test
  def given_alternativeThenSequenceComposition_when_selectAlternativeThatContainsSequence_then_firstActionOfSequenceDisabledFalse(): Unit ={
    val currentSpecification = treeService.select(5, TreeSampleData.ALTERNATIVE_THEN_SEQUENCE, null)
    assert(currentSpecification.asInstanceOf[Composition].disabled)
    assert(currentSpecification.asInstanceOf[Composition].left.asInstanceOf[Action].disabled)
    val sequence = currentSpecification.asInstanceOf[Composition].right.asInstanceOf[Composition]
    assert(sequence.disabled)
    assert( sequence.left.asInstanceOf[Action].disabled)
    assert(! sequence.right.asInstanceOf[Action].disabled)
  }

  @Test
  def given_sequenceThenParallel_when_firstSequenceIsSelected_then_allActionsParallelAreDisabledFalse(): Unit ={
    val currentSpecification = treeService.select(2, TreeSampleData.SEQUENCE_THEN_PARALLEL , null)
    assert(currentSpecification.asInstanceOf[Composition].disabled)
    assert(currentSpecification.asInstanceOf[Composition].left.asInstanceOf[Action].disabled)
    val parallel = currentSpecification.asInstanceOf[Composition].right.asInstanceOf[Composition]
    assert(!parallel.disabled)
    assert(! parallel.left.asInstanceOf[Action].disabled)
    assert(! parallel.right.asInstanceOf[Action].disabled)
  }

  @Test
  def given_sequence_when_getTextFromAction_then_receivedParentAndNextItemsText(): Unit ={
    val text = treeService.getTextFromUnique(Array(2), TreeSampleData.SEQUENTIAL )
    assert(text == "action1.action2")
  }
}
