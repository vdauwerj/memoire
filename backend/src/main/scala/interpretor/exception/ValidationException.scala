package interpretor.exception

/**
  * Created by Olivier on 21/05/2016.
  */
case class ValidationException(message: String = null, cause: Throwable = null) extends Exception(message, cause)
