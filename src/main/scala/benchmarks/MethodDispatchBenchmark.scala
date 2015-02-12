package benchmarks

import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations._

object MethodDispatchBench extends App {
  import org.openjdk.jmh.runner.Runner
  import org.openjdk.jmh.runner.options.OptionsBuilder

  val opts = new OptionsBuilder()
    .include("MethodDispatchBenchmark")
    .forks(1)
    .warmupIterations(10)
    .measurementIterations(10)
    .mode(Mode.AverageTime)
    .timeUnit(TimeUnit.NANOSECONDS)
    .threads(1)
    .build()
  new Runner(opts).run()
}

@State(Scope.Thread)
class MethodDispatchBenchmark {

  var x: Long = _

  val a = new TestClassA
  val b = new TestClassB
  val c = new TestClassC
  val d = new TestClassD

  @Benchmark
  def baseline(): Unit = {
    var y = 0
    nextCounter2Bits() match {
      case 0 ⇒ y = 1
      case 1 ⇒ y = 2
      case 2 ⇒ y = 3
      case 3 ⇒ y = 4
    }
    x = x + y + 1
  }
  @Benchmark
  def staticDispatch(): Unit = {
    var y = 0
    nextCounter2Bits() match {
      case 0 ⇒ y = 1
      case 1 ⇒ y = 2
      case 2 ⇒ y = 3
      case 3 ⇒ y = 4
    }
    x = StaticDispatch.inc(x + y)
  }

  @Benchmark
  def directObjectDispatch(): Unit = {
    var y = 0
    nextCounter2Bits() match {
      case 0 ⇒ y = 1
      case 1 ⇒ y = 2
      case 2 ⇒ y = 3
      case 3 ⇒ y = 4
    }
    x = TestObject.inc(x + y)
  }

  @Benchmark
  def indirectObjectDispatch(): Unit = {
    var y = 0
    val obj = nextCounter2Bits() match {
      case 0 ⇒ { y = 1; TestObject }
      case 1 ⇒ { y = 2; TestObject }
      case 2 ⇒ { y = 3; TestObject }
      case 3 ⇒ { y = 4; TestObject }
    }
    x = obj.inc(x + y)
  }

  @Benchmark
  def monomorphicVirtualDispatch(): Unit = {
    var y = 0
    val obj: TestClass = nextCounter2Bits() match {
      case 0 ⇒ { y = 1; a }
      case 1 ⇒ { y = 2; a }
      case 2 ⇒ { y = 3; a }
      case 3 ⇒ { y = 4; a }
    }
    x = obj.inc(x + y)
  }

  @Benchmark
  def bimorphicVirtualDispatch(): Unit = {
    var y = 0
    val obj: TestClass = nextCounter2Bits() match {
      case 0 ⇒ { y = 1; a }
      case 1 ⇒ { y = 2; b }
      case 2 ⇒ { y = 3; a }
      case 3 ⇒ { y = 4; b }
    }
    x = obj.inc(x + y)
  }

  @Benchmark
  def megamorphicVirtualDispatch(): Unit = {
    var y = 0
    val obj: TestClass = nextCounter2Bits() match {
      case 0 ⇒ { y = 1; a }
      case 1 ⇒ { y = 2; b }
      case 2 ⇒ { y = 3; c }
      case 3 ⇒ { y = 4; d }
    }
    x = obj.inc(x + y)
  }

  private var counter: Int = _
  private def nextCounter2Bits(): Int = {
    counter += 1
    counter & 0x3
  }
}

object TestObject {
  def inc(i: Long): Long = i + 1
}

trait TestClass {
  def inc(i: Long): Long
}

class TestClassA extends TestClass {
  def inc(i: Long): Long = i + 1
}

class TestClassB extends TestClass {
  def inc(i: Long): Long = i + 1
}

class TestClassC extends TestClass {
  def inc(i: Long): Long = i + 1
}

class TestClassD extends TestClass {
  def inc(i: Long): Long = i + 1
}