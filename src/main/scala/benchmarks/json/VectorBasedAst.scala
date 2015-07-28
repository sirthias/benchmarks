package benchmarks.json.vector.ast

/**
 * Minimal AST for " The JavaScript Object Notation (JSON) Data Interchange Format"
 * RFC 7159 (https://tools.ietf.org/html/rfc7159)
 */
sealed abstract class JValue extends Serializable with Product

case object JNull extends JValue

sealed abstract class JBoolean extends JValue {
  def isEmpty = false
  def get: Boolean = value
  def value: Boolean
}

object JBoolean {
  def apply(x: Boolean): JBoolean = if (x) JTrue else JFalse
  def unapply(x: JBoolean): JBoolean = x
}

case object JTrue extends JBoolean {
  def value = true
}

case object JFalse extends JBoolean {
  def value = false
}

final case class JString(value: String) extends JValue

object JString {
  val empty = JString("")
}

/**
 * Contract: `value` *must* be formatted according to https://tools.ietf.org/html/rfc7159#section-6
 */
final case class JNumber(value: String) extends JValue

object JNumber {
  def apply(value: Byte): JNumber = JNumber(value.toString)
  def apply(value: Short): JNumber = JNumber(value.toString)
  def apply(value: Int): JNumber = JNumber(value.toString)
  def apply(value: Long): JNumber = JNumber(value.toString)
  def apply(value: Float): JNumber = JNumber(value.toString)
  def apply(value: Double): JNumber = JNumber(value.toString)
  def apply(value: BigInt): JNumber = JNumber(value.toString)
  def apply(value: BigDecimal): JNumber = JNumber(value.toString)
}

final case class JField(key: String, value: JValue)

final case class JObject(fields: Vector[JField]) extends JValue

object JObject {
  val empty = JObject(Vector.empty)

  def apply(fields: JField*): JObject = JObject(fields.toVector)
}

final case class JArray(elements: Vector[JValue]) extends JValue

object JArray {
  val empty = JArray(Vector.empty)

  def apply(elements: JValue*): JArray = JArray(elements.toVector)
}
