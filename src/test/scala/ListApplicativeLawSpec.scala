import fr.xebia.xke.fp2.{Cons, Nil, List}
import org.scalacheck.Gen
import org.scalacheck.Gen.{alphaLowerChar, oneOf}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSpec, Matchers, PropSpec}

class ListApplicativeSpec extends FunSpec with Matchers {

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

  describe("An applicate instance for list") {
    it("should map a function over a list") {
      val result_of_map = List.applicative.map(_1_2_3_as_string)(Integer.parseInt)

      result_of_map shouldBe _1_2_3_as_int
    }

    it("should apply a list of functions on a list of elements") {
      val result_of_ap = List.applicative.ap(_1_2_3_as_int)(Cons(
        (i: Int) => i + 1, Cons(
          (i: Int) => i - 1, Nil
        )
      ))

      result_of_ap shouldBe Cons(
        2, Cons(
          3, Cons(
            4, Cons(
              0, Cons(
                1, Cons(
                  2, Nil)
              )
            )
          )
        )
      )
    }

    it("should apply a list of functions over pairs over a pair of list of elements") {
      val result_of_ap: List[Int] = List.applicative.ap2(_1_2_3_as_int, _1_2_3_as_int)(Cons(
        _ + _, Cons(
          _ * _, Nil)))

      result_of_ap shouldBe Cons(
        2, Cons(// SUM
          3, Cons(
            4, Cons(

              3, Cons(// SUM
                4, Cons(
                  5, Cons(

                    4, Cons(// SUM
                      5, Cons(
                        6, Cons(

                          1, Cons(// PRODUCT
                            2, Cons(
                              3, Cons(

                                2, Cons(// PRODUCT
                                  4, Cons(
                                    6, Cons(

                                      3, Cons(// PRODUCT
                                        6, Cons(
                                          9, Nil))))))))))))))))))
    }

  }

}

object ListApplicativeSpec {

  def listsOf[A](as: Gen[A]): Gen[List[A]] = oneOf(Gen.const(Nil), consOf(as))

  def consOf[A](as: Gen[A]): Gen[Cons[A]] =
    for {
      a <- as
      tail <- listsOf(as)
    } yield Cons(a, tail)
}
