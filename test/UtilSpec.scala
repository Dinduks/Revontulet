import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import revontulet.Util

class UtilSpec extends Specification {
  "trim" should {
    "trim a character of a specific string" in {
      Util.trim(".hello.", '.') must beEqualTo("hello")
      Util.trim("!!hello!!!!!!", '!') must beEqualTo("hello")
    }
  }
}
