package fr.xebia.xke.fp2

import scala.language.higherKinds

trait Applicative[F[_]] extends Foncteur[F] {

  def point[A](a: A): F[A]

  def ap[A, B](fa: F[A])(f: F[A => B]): F[B]

  override def map[A, B](fa: F[A])(f: (A => B)): F[B] = ap(fa)(point(f))

  def ap2[A, B, C](fa: F[A], fb: F[B])(f: F[(A, B) => C]): F[C] = {
    val curriedF: F[A => B => C] = map(f)(_.curried)

    val fAppliedOnFA: F[(B) => C] = ap(fa)(curriedF)
    
    ap(fb)(fAppliedOnFA)
  }

  
  
  def apply2[A, B, C](fa: F[A], fb: F[B])(f: (A, B) => C): F[C] = ap2(fa, fb)(point(f))

  def mapply2[A, B, C](a: A, b: B)(f: F[(A, B) => C]): F[C] = ap2(point(a), point(b))(f)


}