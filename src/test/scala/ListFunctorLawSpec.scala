import fr.xebia.xke.fp2.{Cons, Nil, List}
import org.scalacheck.Gen
import org.scalacheck.Gen.{alphaLowerChar, oneOf}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSpec, Matchers, PropSpec}

class ListFunctorSpec extends FunSpec with Matchers {

  val _1_2_3_as_string = List("1", "2", "3")

  val _1_2_3_as_int = List(1, 2, 3)

  import List.foncteur._


  describe("Functor instance for list") {
    it("should map a function over a list") {
      /*
      val result_of_map = map(_1_2_3_as_string)(Integer.parseInt)

      result_of_map shouldBe _1_2_3_as_int
       */
      fail("prove me, I'm famous")
    }

    it("should pair the input and output of a function over a list with fproduct") {
      /*
      val result_of_fproduct = fproduct(_1_2_3_as_string)(Integer.parseInt)

      result_of_fproduct shouldBe List(
        ("1", 1),
        ("2", 2),
        ("3", 3)
      )
      */
      fail("prove me, I'm famous")
    }

    it("should be able to apply a list of functions over an element with mapply") {
      /*
      val result_of_mapply = mapply("heLLo")(List(
        (s: String) => s.toUpperCase,
      (s: String) => s.toLowerCase
      ))

      result_of_mapply shouldBe List(
      "HELLO",
      "hello"
      )
       */
      fail("prove me, I'm famous")
    }
  }
}

class ListFunctorLawSpec extends PropSpec with GeneratorDrivenPropertyChecks with Matchers {

  import ListFunctorLawSpec._

  import List.foncteur._

  property("identity law of List Functor instance") {

    fail("prove me, I'm famous")
  }

  property("associativity law of List Functor instance") {

    fail("prove me, I'm famous")
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
