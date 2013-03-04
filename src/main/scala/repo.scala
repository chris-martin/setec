package org.codeswarm.setec

import scala.collection.{immutable, mutable}

import RichString._

trait Repo

/** A full representation of a `.setec` file (whitespace, comments, and all) in a syntax
  * tree structure that supports modification with minimal change to the resulting text,
  * allowing us to make changes to the file while respecting its existing formatting.
  * If we parse a repo file and then render it, the resulting string is the same as the
  * input that was parsed.
  */
class RepoFile extends Repo {

}

object RepoFile {


}

/** == Parsing ==
  *
  * A line starting with a twig is a continuation of a leaf.
  * A line is blank or starts with # is Sap and has no meaning,
  *   other than to end the parsing of a leaf of branch
  *   (a leaf can be multi-line but cannot contain sap).
  * Any other line starts a leaf or a branch.
  *   If it has a twig in it, it's a leaf.
  *   If the next line starts with a twig, it's a leaf.
  *   Otherwise it's branch.
  *
  * Identation matters only for branches.
  * Once a branch is started, subsequent lines belong to it
  * until we encounter a branch-starting line whose starting
  * column is <= that of the branch.
  *
  * A twig must be followed by a single space, unless there
  * is nothing else on the line.
  */
sealed trait Foliage {
  def render: String
}

object Foliage {

  def parse(string: String): Branch = {

    case class GrowingBranch(
      name: String,
      children: mutable.ArrayBuffer[Foliage] = new mutable.ArrayBuffer()
    ) {
      def finish: Branch = Branch(name, immutable.Seq(children:_*))
    }

    val rootBranch = GrowingBranch("")
    val branchStack = mutable.Stack(rootBranch)
    var currentLeaf: Option[Leaf] = None

    """.*[\r\n]+""".r.findAllIn(string) foreach { line =>

      ???

    }

    rootBranch.finish

  }

}

case class Sap(render: String) extends Foliage

case class Leaf(render: String) extends Foliage {

  val twig: Char = Leaf.twigRegex.findFirstIn(render).get(0)

  val classifier: Leaf.Classifier = Leaf.classifiers.find(_.twig == twig).get

  val key: String = """(.*)\Q%s\E (.*)""".format(classifier.twig).r.findFirstIn(render).get

  val value = immutable.Seq[String](
    """(?:.*\Q%s\E )(.*)""".format(classifier.twig).r.
      findAllMatchIn(render).map(_.group(1)).toSeq:_*)

}

object Leaf {

  sealed trait Classifier {
    def twig: Char
  }

  object Classifier {
    case object Plain extends Classifier { def twig: Char = '|' }
    case object Cipher extends Classifier { def twig: Char = '$' }
    val all = immutable.Seq(Plain, Cipher)
  }

  val classifiers = Classifier.all
  val twigs = classifiers.map(_.twig)
  val twigRegex = """[\Q%s\E]""".format(twigs.mkString).r

}

case class Branch(render: String, children: immutable.Seq[Foliage]) {

  val indent: Int = render.indentSize

  val name: String = render.trim match {
    case x if x.forall(_ == '_') => "_"
    case x => x
  }

}

class RichString(string: String) {

  import RichString._

  def indentSize: Int = indentRegex.findFirstIn(string).get.size

}

object RichString {

  val indentRegex = " *".r

  implicit def enrichString(x: String): RichString = new RichString(x)

}
