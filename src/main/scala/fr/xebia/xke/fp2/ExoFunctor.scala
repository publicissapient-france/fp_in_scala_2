package fr.xebia.xke.fp2

import scala.language.higherKinds

sealed trait List[+A]

case object Nil extends List[Nothing]

case class Cons[A](a: A, t: List[A]) extends List[A]

object List {

  def apply[A](elts: A*): List[A] = if (elts.isEmpty) Nil else Cons(elts.head, List.apply(elts.tail: _*))

  //TODO. Exo 4  functor instance here as an anonymous class
  lazy val foncteur = ???

  //TODO. Exo 7 applicative instance here as an anonymous class
  lazy val applicative = ???

}

//OPTION
sealed trait Option[+A]

case object None extends Option[Nothing]

case class Some[A](a: A) extends Option[A]

object Option {

  //TODO. Exo 4  functor instance here as an anonymous class
  lazy val foncteur = ???

  //TODO. Exo 7 applicative instance here as an anonymous class
  lazy val applicative = ???
}