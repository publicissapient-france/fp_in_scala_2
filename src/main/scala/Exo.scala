sealed trait List[+A]{
  def map[B](f: (A => B)): List[B]
}

case object Nil extends List[Nothing]{
  def map[B](f: (Nothing => B)): List[B] = Nil
}

case class Cons[A](a: A, t: List[A]) extends List[A]{
  def map[B](f: (A => B)): List[B] = Cons(f(a),t.map(f))
}


//OPTION
sealed trait Option[+A]{
  def map[B](f: (A => B)): Option[B]
}

case object None extends Option[Nothing]{
  def map[B](f: (Nothing => B)): Option[B] = None
}

case class Some[A](a:A) extends Option[A]{
  def map[B](f: (A => B)): Option[B] = Some(f(a))
}