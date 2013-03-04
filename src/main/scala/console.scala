package org.codeswarm.setec.console

import org.rogach.scallop._

object Console {

  class Conf(args: Array[String]) extends ScallopConf(args) {

    val file = opt[String]("file", default = Some(".config/setec/default.setec"))

    val setPassword = opt[Boolean]("set-password", default = Some(false),
      descr = """Generates a new private key, initialize the file if necessary, """ +
              """and re-encrypt existing passwords if necessary.""")

  }

  def main(args: Array[String]) {
    new Conf(args).printHelp()
  }

}
