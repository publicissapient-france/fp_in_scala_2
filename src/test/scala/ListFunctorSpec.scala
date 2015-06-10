import org.scalacheck.Gen
import org.scalacheck.Gen.{alphaLowerChar, oneOf}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Matchers, PropSpec}

class ListFunctorSpec extends PropSpec with GeneratorDrivenPropertyChecks with Matchers {

  import ListFunctorSpec._

  property("identity law of List Functor instance") {

    forAll(listsOf(alphaLowerChar)) { list =>
      list.map(c => c) should equal(list)
    }

    forAll(listsOf(Gen.choose(Int.MinValue, Int.MaxValue))) { list =>
      list.map(c => c) should equal(list)
    }

  }

  property("associativity law of List Functor instance") {

    forAll(listsOf(alphaLowerChar)) { list =>
      val f: (Char) => Int = (c: Char) => c.toInt
      val g: (Int) => String = (i: Int) => i.toString
      val h: (Char) => String = g compose f

      list.map(h) should equal(list.map(f).map(g))
    }
  }
}

object ListFunctorSpec {

  def listsOf[A](as: Gen[A]): Gen[List[A]] = oneOf(Gen.const(Nil), consOf(as))

  def consOf[A](as: Gen[A]): Gen[Cons[A]] =
    for {
      a <- as
      tail <- listsOf(as)
    } yield Cons(a, tail)
}
