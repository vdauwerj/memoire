package interpretor

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class InterpretorWebApplication extends App {
//    SpringApplication.run(classOf[InterpretorWebApplication]);
}

object ApplicationLauncher {
  def main(args: Array[String]): Unit = {
    SpringApplication run classOf[InterpretorWebApplication]
  }
}
