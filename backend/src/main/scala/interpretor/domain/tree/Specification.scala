package interpretor.domain.tree

import interpretor.dsl.model.{Action, ProcessSpec}

class Specification{
  var actions: List[Action] = _
  var processes: List[ProcessSpec] = _
  var initialSpec: ProcessSpec = _
}
