package interpretor.facade

object TextSampleData {
  def SPECIFICATION_WITH_ALTERNATIVE_COMPOSITION_PROCESS: String = {
    val builder = StringBuilder.newBuilder
    builder.append("act action1, action2;")
    builder.append("proc Process1 = action1 + action2;")
    builder.append("init Process1;")
    builder.toString()
  }

  def SPECIFICATION_WITH_ALTERNATIVE_COMPOSITION_INIT: String = {
    val builder = StringBuilder.newBuilder
    builder.append("act action1, action2;")
    builder.append("init action1 + action2;")
    builder.toString()
  }
  def SPECIFICATION_WITH_SEQUENTIAL_COMPOSITION_INIT: String = {
    val builder = StringBuilder.newBuilder
    builder.append("act action1, action2;")
    builder.append("init action1 . action2;")
    builder.toString()
  }
  def SPECIFICATION_WITH_PARALLEL_COMPOSITION_INIT: String = {
    val builder = StringBuilder.newBuilder
    builder.append("act action1, action2;")
    builder.append("init action1 || action2;")
    builder.toString()
  }
  def SPECIFICATION_WITH_PARALLEL_COMPOSITION_INTER_PROCESS: String = {
    val builder = StringBuilder.newBuilder
    builder.append("act action1, action2;")
    builder.append("proc Process1 = action1;")
    builder.append("    Process2 = action2;")
    builder.append("init Process1 || Process2;")
    builder.toString()
  }

  def SPECIFICATION_WITH_PARALLEL_COMPOSITION_INTRA_PROCESS: String = {
    val builder = StringBuilder.newBuilder
    builder.append("act action1, action2;")
    builder.append("proc Process1 = action1 || action2;")
    builder.append("init Process1;")
    builder.toString()
  }

  def SPECIFICATION_WITH_BLOCK: String = {
    val builder = StringBuilder.newBuilder
    builder.append("act action1;")
    builder.append("init block({action1}, action1);")
    builder.toString()
  }
  def SPECIFICATION_WITH_BLOCK_TWO_ACTIONS: String = {
    val builder = StringBuilder.newBuilder
    builder.append("act action1, action2;")
    builder.append("init block({action1}, action1 || action2);")
    builder.toString()
  }
  def SPECIFICATION_WITH_BLOCK_IN_PROCESS: String = {
    val builder = StringBuilder.newBuilder
    builder.append("act b,c;")
    builder.append("proc P = block({b}, b||c);")
    builder.append("init P;")
    builder.toString()
  }
  def SPECIFICATION_WITH_BLOCK_MULTI_PROCESS: String = {
    val builder = StringBuilder.newBuilder
    builder.append("act b,c;")
    builder.append("proc P1 = b . c;")
    builder.append(" P2 = c + b;")
    builder.append("init block({b}, P1||P2);")
    builder.toString()
  }
}
