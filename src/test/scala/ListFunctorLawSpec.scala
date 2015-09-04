import fr.xebia.xke.fp2.{Cons, Nil, List}
import org.scalacheck.Gen
import org.scalacheck.Gen.{alphaLowerChar, oneOf}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSpec, Matchers, PropSpec}

class ListFunctorSpec extends FunSpec with Matchers {

  val _1_2_3_as_string = Cons(
    "1", Cons(
      "2", Cons(
        "3", Nil)
    )
  )

  val _1_2_3_as_int = Cons(
    1, Cons(
      2, Cons(
        3, Nil)
    )
  )

  describe("Functor instance for list") {
    it("should map a function over a list") {
      val result_of_map = List.foncteur.map(_1_2_3_as_string)(Integer.parseInt)

      result_of_map shouldBe _1_2_3_as_int
    }

    it("should pair the input and output of a function over a list with fproduct") {
      val result_of_fproduct = List.foncteur.fproduct(_1_2_3_as_string)(Integer.parseInt)

      result_of_fproduct shouldBe Cons(
        ("1", 1), Cons(
          ("2", 2), Cons(
            ("3", 3), Nil)
        )
      )
    }

    it("should be able to apply a list of functions over an element with mapply") {
      val result_of_mapply = List.foncteur.mapply("heLLo")(Cons(
        (s: String) => s.toUpperCase, Cons(
          (s: String) => s.toLowerCase, Nil)
      ))

      result_of_mapply shouldBe Cons(
        "HELLO", Cons(
          "hello", Nil)
      )
    }
  }
}

class ListFunctorLawSpec extends PropSpec with GeneratorDrivenPropertyChecks with Matchers {

  import ListFunctorLawSpec._

  property("identity law of List Functor instance") {

    forAll(listsOf(alphaLowerChar)) { list =>
      List.foncteur.map(list)(c => c) should equal(list)
    }

    forAll(listsOf(Gen.choose(Int.MinValue, Int.MaxValue))) { list =>
      List.foncteur.map(list)(c => c) should equal(list)
    }

  }

  property("associativity law of List Functor instance") {

    forAll(listsOf(alphaLowerChar)) { list =>
      val f: (Char) => Int = (c: Char) => c.toInt
      val g: (Int) => String = (i: Int) => i.toString
      val h: (Char) => String = g compose f

      List.foncteur.map(list)(h) should equal(List.foncteur.map(List.foncteur.map(list)(f))(g))
    }
  }
}

object ListFunctorLawSpec {

  def listsOf[A](as: Gen[A]): Gen[List[A]] = oneOf(Gen.const(Nil), consOf(as))

  def consOf[A](as: Gen[A]): Gen[Cons[A]] =
    for {
      a <- as
      tail <- listsOf(as)
    } yield Cons(a, tail)
}
