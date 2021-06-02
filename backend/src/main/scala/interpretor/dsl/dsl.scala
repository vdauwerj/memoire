package interpretor.dsl

import interpretor.domain.tree.Specification
import interpretor.dsl.model.{Action, Block, Composition, Delta, Mcrl2Object, ProcessId, ProcessSpec}
import interpretor.dsl.validation.Util
import interpretor.exception.ValidationException

import scala.::
import scala.util.parsing.combinator.JavaTokenParsers

/**
 * Created by Olivier Croegaert on 8/11/2015.
 *
 * This scala class describes the syntax for mcrl2 and the
 * structures to build when parsed code is considered as being valid.
 */
object dsl {
  var unique: Int = 0;
  /**
   * First defining constants values to define precedence rules among mcrl2 operators
   */
  val ALT_COMP = 10
  val PAR_COMP = 20
  val SEQ_COMP = 30
  val BLOCK_COMP = 40
  /**
   * Storing a technical name chosen for the process "init" which embed all the mcrl2
   * application logic
   */
  val INIT_PROC_ID = "INIT"

  /**
   * The class
   */
  class Mcrl2Parser extends JavaTokenParsers {


    /**
     * Defining a parser to describe what a mcrl2 specification is
     *
     * A mcrl2 specification is either one clause "init" or either a definition of actions eventually followed
     * by some process definitions and a (mandatory) clause init.
     *
     */
    def mcrl2Specification = opt(actionsDecl ~ opt(processes)) ~ init ^^ {
      case None ~ i => buildTree(i)
      case Some(a ~ None) ~ i => buildTree(a, i)
      case Some(a ~ Some(p)) ~ i => buildTree(a, p, i)
    }


    /**
     * Action definition
     */
    def action: Parser[Action] =
      """[a-z][a-zA-Z0-9]*""".r ^^ {
        case a => {
          unique += 1
          new Action(unique, a)
        }
      }

    def delta: Parser[Delta] = "delta" ^^ {
      case a => {
        new Delta
      }
    }

    /**
     * ProcessName definition
     */
    def process: Parser[ProcessId] =
      """[A-Z][a-zA-Z0-9]*""".r ^^ {
        case a => {
          unique += 1
          new ProcessId(unique, a)
        }
      }

    /**
     * Alternative composition definition
     */
    def compositionAlt: Parser[Mcrl2Object] = (compositionPar | term) ~ opt("+" ~ (block | compositionAlt) ) ^^ {
      case a ~ None => a
      case a ~ Some(b ~ c) => {
        unique += 1
        new Composition(unique, b, a, c)
      }
    }


    /**
     * Block definition
     */
    def block: Parser[Block] = "block" ~ "({" ~> actions ~ "}," ~ (block | compositionAlt | term) <~ ")" ^^ {
      case actions ~ a ~ specification => {
        new Block(actions, specification)
      }
    }
    /**
     * Parallel composition definition
     */
    def compositionPar: Parser[Mcrl2Object] = (compositionSeq | term) ~ opt("||" ~ compositionPar) ^^ {
      case a ~ None => a
      case a ~ Some(b ~ c) => {
        unique += 1
        new Composition(unique, b, a, c)
      }
    }

    /**
     * Sequential composition definition
     */
    def compositionSeq: Parser[Mcrl2Object] = term ~ opt("." ~ compositionSeq) ^^ {
      case a ~ None => a
      case a ~ Some(b ~ c) => {
        unique += 1
        new Composition(unique, b, a, c)
      }
    }

    /**
     * A leaf is either an action or a process name
     */
    def leaf: Parser[Mcrl2Object] = action | process | delta

    /**
     * A term is either a leaf or either a parens (subComposition), or either the deadlock
     */
    def term: Parser[Mcrl2Object] = leaf | parens

    /**
     * A parens is a subComposition which has left precedence
     */
    def parens: Parser[Mcrl2Object] = "(" ~> (block | compositionAlt) <~ ")"


    /**
     * A process spec is an assignation of a composition to a Process id
     */
    def processSpec: Parser[ProcessSpec] = (process <~ "=") ~ (block | compositionAlt) ^^ {
      case a ~ b => new ProcessSpec(a, b)
    }


    /**
     * Returns a list of actions if well defined in mcrl2 code
     */
    def actions: Parser[List[Action]] = action ~ rep("," ~> action)  ^^ {
      case n1 ~ n2 => n1 :: n2
    }

    /**
     * Returns a list of actions if well defined in mcrl2 code
     */
    def actionsDecl: Parser[List[Action]] = "act" ~> actions <~ ";" ^^ {
      case actions  => actions
    }

    /**
     * Returns a list of processes if well defined in mcrl2 code
     */
    def processes: Parser[List[ProcessSpec]] = "proc" ~> rep(processSpec <~ ";")


    /**
     * Init is the initial process needed to start the specification evaluation
     */
    def init: Parser[ProcessSpec] = "init" ~> (block | compositionAlt) <~ ";" ^^ {

      case p => {
        unique += 1
        new ProcessSpec(new ProcessId(unique, INIT_PROC_ID), p);
      }
    }
  }

  /**
   * Method used to build the syntax tree (without µcrl2 logic setup)
   *
   * @param act  the list of defined actions
   * @param proc the list of defined processSpec
   * @param init the init processSpec
   * @return Specification which contains the tree
   */
  private def buildTree(act: List[Action], proc: List[ProcessSpec], init: ProcessSpec): Specification = {
    val answer = new Specification
    var initActions: List[Action] = List.empty
    var initProcesses: List[ProcessId] = List.empty

    for (i <- proc.indices) {
      initActions :::= Util.getActionsFromProcessSpec(proc(i).process)
      initProcesses :::= Util.getProcFromProcessSpec(proc(i).process)
    }
    initActions :::= Util.getActionsFromProcessSpec(init.process)
    initProcesses :::= Util.getProcFromProcessSpec(init.process)

    if (!Util.areActionsDefined(act, initActions))
      throw new ValidationException("It seems some actions used in one or more processes aren't previously defined")

    if (!Util.areProcDefined(proc, initProcesses))
      throw new ValidationException("It seems some process ids used in one or more processes aren't previously defined")

    if (!Util.areProcUnique(proc))
      throw new ValidationException("Some processes are defined twice or more")


    if (!Util.areActionsUnique(act))
      throw new ValidationException("Some actions are defined twice or more")


    answer.actions = act
    answer.processes = proc
    answer.initialSpec = init
    answer
  }

  /**
   * Method used to build the syntax tree (without µcrl2 logic setup)
   *
   * @param act  the list of defined actions
   * @param init the init processSpec
   * @return Specification which contains the tree
   */
  def buildTree(act: List[Action], init: ProcessSpec): Specification = {

    if (init.id.name != dsl.INIT_PROC_ID)
      throw new ValidationException("Initialisation process \"" + init.id.name + "\" is not allowed here")
    val initActions = Util.getActionsFromProcessSpec(init.process)
    if (!Util.areActionsDefined(act, initActions))
      throw new ValidationException("It seems some actions in init process aren't previously defined")

    val answer = new Specification
    answer.actions = act
    answer.initialSpec = init

    answer
  }

  /**
   * Method used to build the syntax tree (without µcrl2 logic setup)
   *
   * @param init the init processSpec
   * @return Specification which contains the tree
   */
  def buildTree(init: ProcessSpec): Specification = {
    val answer: Specification = new Specification
    init.process match {
      case act: Action => {
        if (act.name != "delta") {
          throw new ValidationException("No actions defined. only \"delta\" is allowed in inti process")
        } else {
          answer.initialSpec = init
        }
      }
    }
    if (answer.initialSpec == null) {
      throw new ValidationException("Only \"init delta;\" is possible in the case no action are defined")
    }
    answer
  }
}
