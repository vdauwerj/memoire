package interpretor.domain.service

import interpretor.dsl.model.{Action, Composition, ProcessId}

object TreeSampleData {
  def SEQUENCE_WITH_PROCESS = {
    val id = ProcessId(-1, "PROCESS")
    val right = Action(0, "action1")
    val sequential = Composition(1, ".", id, right)
    id.disabled = false
    sequential
  }

  def ALTERNATIVE = {
    val left = Action(2, "action1")
    val right = Action(3, "action2")
    val alternative = Composition(1, "+", left, right)

    left.disabled= false;
    right.disabled= false;
    alternative.disabled = false

    alternative
  }
  def SEQUENTIAL = {
    val left = Action(2, "action1")
    val right = Action(3, "action2")
    val sequential = Composition(1, ".", left, right)

    left.disabled= false;
    right.disabled= true;
    sequential.disabled = false

    sequential
  }

  def PARALLEL = {
    val left = Action(2, "action1")
    val right = Action(3, "action2")
    val parallel = Composition(1, "||", left, right)

    left.disabled= false;
    right.disabled= false;
    parallel.disabled = false

    parallel
  }
  def PARALLEL_INTER_PROCESS = {
    val left = Action(2, "action1")
    left.processName= "Process1"
    val right = Action(3, "action2")
    right.processName= "Process2"

    val parallel = Composition(1, "||", left, right)

    left.disabled= false;
    right.disabled= false;
    parallel.disabled = false

    parallel
  }
  def PARALLEL_INTER_AND_INTRA_PROCESS = {
    val left = Action(2, "action1")
    left.processName= "Process1"
    val right = Action(3, "action2")
    right.processName= "Process2"
    val right2 = Action(5, "action3")
    right2.processName= "Process2"

    val intraParallel = Composition(4, "||", right2, right)
    val interParallel = Composition(1, "||", intraParallel, left)

    left.disabled= false;
    right.disabled= false;
    right2.disabled= false;
    intraParallel.disabled = false
    interParallel.disabled = false

    interParallel
  }

  def SEQUENCE_THEN_ALTERNATIVE = {
    val first = Action(2, "action1")
    val left = Action(5, "action2")
    val right = Action(4, "action3")
    val alternative = Composition(3, "+", left, right)
    val sequence = Composition(1, ".", first, alternative)
    sequence.disabled = false
    first.disabled = false
    left.disabled= true;
    right.disabled= true;
    alternative.disabled = true
    sequence
  }
  def ALTERNATIVE_THEN_SEQUENCE = {
    val first = Action(2, "action1")
    val left = Action(5, "action2")
    val right = Action(4, "action3")
    val sequence = Composition(1, ".", left, right)
    val alternative = Composition(3, "+", first, sequence)
    sequence.disabled = false
    right.disabled= true;
    left.disabled= false;
    first.disabled = false
    alternative.disabled = false
    alternative
  }
  def SEQUENCE_THEN_PARALLEL = {
    val first = Action(2, "action1")
    val left = Action(5, "action2")
    val right = Action(4, "action3")
    val parallel = Composition(3, "||", left, right)
    val sequence = Composition(1, ".", first, parallel)
    sequence.disabled = false
    first.disabled = false
    left.disabled= true;
    right.disabled= true;
    parallel.disabled = true
    sequence
  }

}
