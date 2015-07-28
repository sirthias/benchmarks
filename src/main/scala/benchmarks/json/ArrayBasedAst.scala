package benchmarks.json.array.ast

import scala.annotation.tailrec
import scala.collection.immutable.VectorBuilder

/**
 * Minimal AST for " The JavaScript Object Notation (JSON) Data Interchange Format"
 * RFC 7159 (https://tools.ietf.org/html/rfc7159)
 */
sealed abstract class JValue extends Serializable with Product

case object JNull extends JValue

sealed abstract class JBoolean extends JValue {
  def isEmpty = false
  def get: Boolean
}

object JBoolean {
  def apply(x: Boolean): JBoolean = if (x) JTrue else JFalse
  def unapply(x: JBoolean): JBoolean = x
}

case object JTrue extends JBoolean {
  def get = true
}

case object JFalse extends JBoolean {
  def get = false
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

sealed abstract class JContainer[T <: AnyRef] extends JValue {
  def value: Vector[T] = {
    val array = underlyingArray_unsafe
    val vb = new VectorBuilder[T]
    @tailrec def copyFrom(ix: Int): Vector[T] =
      if (ix < array.length) {
        vb += array(ix)
        copyFrom(ix + 1)
      } else vb.result()
    copyFrom(0)
  }
  def isEmpty = false
  def get: Vector[T] = value
  def underlyingArray_unsafe: Array[T]

  def productArity = 1
  def productElement(n: Int): Any =
    if (n == 0) value else throw new IndexOutOfBoundsException(s"Expected 0, was $n")

  override def equals(obj: Any): Boolean =
    obj match {
      case x: JContainer[_] ⇒
        canEqual(obj) && java.util.Arrays.equals(underlyingArray_unsafe.asInstanceOf[Array[Object]],
          x.underlyingArray_unsafe.asInstanceOf[Array[Object]])
      case _ ⇒ false
    }
  override def hashCode(): Int = java.util.Arrays.hashCode(underlyingArray_unsafe.asInstanceOf[Array[Object]])
}

sealed abstract class JObject extends JContainer[JField] {
  override def productPrefix = "JObject"
  def canEqual(that: Any): Boolean = that.isInstanceOf[JObject]
}

object JObject {
  val empty = JObject.create_unsafe(new Array[JField](0))

  def apply(fields: JField*): JObject = create_unsafe(fields.toArray)
  def apply(fields: Vector[JField]): JObject = create_unsafe(fields.toArray)
  def apply(fields: Array[JField]): JObject = JObject(fields, 0, fields.length)
  def apply(fields: Array[JField], start: Int, end: Int): JObject = {
    val len = end - start
    if (len < 0) throw new IllegalArgumentException(s"$start  > $end")
    val slice = new Array[JField](len)
    System.arraycopy(fields, start, slice, 0, math.min(fields.length - start, len))
    create_unsafe(slice)
  }
  def create_unsafe(_fields: Array[JField]): JObject =
    new JObject {
      def underlyingArray_unsafe = _fields
    }
  def unapply(obj: JObject): JObject = obj
}

final case class JField(key: String, value: JValue)

sealed abstract class JArray extends JContainer[JValue] {
  override def productPrefix = "JArray"
  def canEqual(that: Any): Boolean = that.isInstanceOf[JArray]
}

object JArray {
  val empty = JArray.create_unsafe(new Array[JValue](0))

  def apply(elements: JValue*): JArray = create_unsafe(elements.toArray)
  def apply(elements: Vector[JValue]): JArray = create_unsafe(elements.toArray)
  def apply(elements: Array[JValue]): JArray = JArray(elements, 0, elements.length)
  def apply(elements: Array[JValue], start: Int, end: Int): JArray = {
    val len = end - start
    if (len < 0) throw new IllegalArgumentException(s"$start  > $end")
    val slice = new Array[JValue](len)
    System.arraycopy(elements, start, slice, 0, math.min(elements.length - start, len))
    create_unsafe(slice)
  }
  def create_unsafe(_elements: Array[JValue]): JArray =
    new JArray {
      def underlyingArray_unsafe = _elements
    }
  def unapply(obj: JArray): JArray = obj
}
