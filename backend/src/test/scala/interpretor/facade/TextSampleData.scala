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
}
