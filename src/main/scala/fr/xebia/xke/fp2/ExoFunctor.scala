package fr.xebia.xke.fp2

import scala.language.higherKinds

trait Foncteur[F[_]] {
  def map[A, B](fa: F[A])(f: (A => B)): F[B]

  def fproduct[A, B](fa: F[A])(f: A => B): F[(A, B)] = map(fa)(a => (a, f(a)))


  def fpair[A, B](fa: F[A]): F[(A, A)] = map(fa)(a => (a, a))


  def mapply[A, B](a: A)(f: F[A => B]): F[B] = map(f) { (g: (A => B)) => g(a)}


  def lift[A, B](f: A => B): F[A] => F[B] = (fa: F[A]) => map(fa)(f)


}

sealed trait List[+A]

case object Nil extends List[Nothing]

case class Cons[A](a: A, t: List[A]) extends List[A]

object List {

  val foncteur = new Foncteur[List] {
    def map[A, B](fa: List[A])(f: (A => B)): List[B] = fa match {
      case Nil => Nil
      case Cons(x, t) => Cons(f(x), map(t)(f))
    }
  }

  val applicative = new Applicative[List] {

    override def point[A](a: A): List[A] = Cons(a, Nil)

    override def ap[A, B](fa: List[A])(f: List[(A) => B]): List[B] = {

      def loop(itFA: List[A])(itF: List[A => B]): List[B] = itF match {
        case Cons(func, tailF) =>
          itFA match {
            case Cons(a, tailA) =>
              Cons(func(a), loop(tailA)(itF))

            case Nil =>
              loop(fa)(tailF)

          }

        case Nil =>
          Nil
      }

      loop(fa)(f)
    }

  }

}

//OPTION
sealed trait Option[+A]

case object None extends Option[Nothing]

case class Some[A](a: A) extends Option[A]

object Option {

  val foncteur = new Foncteur[Option] {
    def map[A, B](fa: Option[A])(f: (A => B)): Option[B] = fa match {
      case None => None
      case Some(x) => Some(f(x))
    }
  }

  val applicative = new Applicative[Option] {

    override def point[A](a: A): Option[A] = Some(a)

    override def ap[A, B](fa: Option[A])(f: Option[(A) => B]): Option[B] = fa match {
      case Some(a) =>
        f match {
          case Some(func) =>
            Some(func(a))
          case None =>
            None
        }

      case None =>
        None
    }

  }
}