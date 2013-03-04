package org.codeswarm.setec.crypto

import scala.util.Random
import scala.collection.immutable

class SaltedPassword(password: Password, salt: Salt) {

  import SaltedPassword._

  def asSecretKey(nrOfBits: Int): Array[Byte] =
    pbkdFunction.generateSecret(
      new javax.crypto.spec.PBEKeySpec(
        password.charArray,
        salt.byteArray,
        pbkdIterationCount,
        nrOfBits / 16
      )
    ).getEncoded

}
object SaltedPassword {

  lazy val pbkdFunction =
    javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")

  val pbkdIterationCount = 53197

}

class Password(string: String) {

  def charArray: Array[Char] = string.toCharArray

  def salted(implicit random: Random): SaltedPassword =
    new SaltedPassword(this, new SaltRandom().nextSalt())

}

class Salt(seq: immutable.Seq[Byte]) {

  def this(array: Array[Byte]) = this(immutable.Seq(array:_*))

  def byteArray: Array[Byte] = seq.toArray

}

class SaltRandom(implicit random: Random) {

  def nextSalt(): Salt = new Salt({
    val bytes = new Array[Byte](32)
    random.nextBytes(bytes)
    bytes
  })

}
