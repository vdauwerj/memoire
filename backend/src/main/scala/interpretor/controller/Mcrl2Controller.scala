package interpretor.controller

import interpretor.domain.tree.Input
import interpretor.facade.Mcrl2Facade
import interpretor.facade.dto.{RequestTextSpecDto, SelectRequestDto}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation._

@RestController
@RequestMapping(Array("/v2/analyze"))
class Mcrl2Controller(@Autowired mcrl2Facade: Mcrl2Facade) {
  /**
   * Allow to submit a spécification.
   * @param input the specification
   * @return processes, actions, next actions executables and the currentSpecification
   */
  @PostMapping(value = Array("/send"))
  @ResponseStatus(HttpStatus.OK)
  def load(@RequestBody input: Input) = {
    mcrl2Facade.loadSpecification(input)
  }

  /**
   * Select an action
   * @param selectRequest
   * @return next actions executables and the new currentSpecification
   */
  @PostMapping(value = Array("/select"))
  @ResponseStatus(HttpStatus.OK)
  def select(@RequestBody selectRequest: SelectRequestDto) = {
    mcrl2Facade.select(selectRequest)
  }

  /**
   * Provide the action and the following items in text
   * @param actions
   * @return the action and next items in text
   */
  @PostMapping(value = Array("/specification"))
  @ResponseStatus(HttpStatus.OK)
  def getParentActionInString(@RequestBody actions: RequestTextSpecDto) = {
    mcrl2Facade.getStringParent(actions)
  }

}
