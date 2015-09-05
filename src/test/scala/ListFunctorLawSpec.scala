import fr.xebia.xke.fp2.{Cons, Nil, List}
import org.scalacheck.Gen
import org.scalacheck.Gen.{alphaLowerChar, oneOf}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSpec, Matchers, PropSpec}

class ListFunctorSpec extends FunSpec with Matchers {

  val _1_2_3_as_string = List("1", "2", "3")

  val _1_2_3_as_int = List(1, 2, 3)


  describe("Functor instance for list") {
    it("should map a function over a list") {
      val result_of_map = List.foncteur.map(_1_2_3_as_string)(Integer.parseInt)

      result_of_map shouldBe _1_2_3_as_int
    }

    it("should pair the input and output of a function over a list with fproduct") {
      val result_of_fproduct = List.foncteur.fproduct(_1_2_3_as_string)(Integer.parseInt)

      result_of_fproduct shouldBe List(
        ("1", 1),
        ("2", 2),
        ("3", 3)
      )
    }

    it("should be able to apply a list of functions over an element with mapply") {
      val result_of_mapply = List.foncteur.mapply("heLLo")(List(
        (s: String) => s.toUpperCase,
        (s: String) => s.toLowerCase
      ))

      result_of_mapply shouldBe List(
        "HELLO",
        "hello"
      )
    }
  }
}

class ListFunctorLawSpec extends PropSpec with GeneratorDrivenPropertyChecks with Matchers {

  import ListFunctorLawSpec._

  import List.foncteur._

  property("identity law of List Functor instance") {

    def id[T]: (T => T) = c => c

    forAll(listsOf(alphaLowerChar)) { list =>
      map(list)(id[Char]) should equal(list)
    }

  }

  property("associativity law of List Functor instance") {

    forAll(listsOf(alphaLowerChar)) { list =>
      val f: (Char) => Int = (c: Char) => c.toInt
      val g: (Int) => String = (i: Int) => i.toString
      val h: (Char) => String = g compose f

      val h_over_list = map(list)(h)
      val f_over_list = map(list)(f)
      val g_over_f_over_list = map(f_over_list)(g)

      h_over_list should equal(g_over_f_over_list)
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
