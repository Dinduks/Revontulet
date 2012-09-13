package revontulet

import scala.util.matching.Regex

import java.util.regex.Pattern

object Util {
  def trim(string: String, char: Char) = {
    val pattern = new Regex("^" +  Pattern.quote(char.toString)  +  "+|" + Pattern.quote(char.toString) + "+$", "trimmedString")
    pattern.replaceAllIn(string, "")
  }
}

