import scala.language.higherKinds

trait Foncteur[F[_]] {
  def map[A, B](fa: F[A])(f: (A => B)): F[B]

  def fpair[A, B](fa: F[A])(f: (A => B)): F[(A, B)] = map(fa)(a => (a, f(a)))
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

  implicit class FoncteurOps[A](val list: List[A]) {
    def map[B](f: A => B): List[B] = foncteur.map(list)(f)

    def fpair[B](f: A => B): List[(A, B)] = foncteur.fpair(list)(f)
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
}