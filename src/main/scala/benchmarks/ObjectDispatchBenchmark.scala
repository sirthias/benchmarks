package benchmarks

import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations._

object ObjectDispatchBench extends App {
  import org.openjdk.jmh.runner.Runner
  import org.openjdk.jmh.runner.options.OptionsBuilder

  val opts = new OptionsBuilder()
    .include("ObjectDispatchBenchmark")
    .forks(1)
    .warmupIterations(10)
    .measurementIterations(10)
    .mode(Mode.AverageTime)
    .timeUnit(TimeUnit.MICROSECONDS)
    .threads(1)
    .build()
  new Runner(opts).run()
}

@State(Scope.Thread)
class ObjectDispatchBenchmark {

  @Benchmark
  def staticDispatch(): Int = {
    var i = 100000
    var x = 0
    while (i != 0) {
      i & 3 match {
        case 0 ⇒ x = doStaticDispatch(x)
        case 1 ⇒ x = doStaticDispatch(x)
        case 2 ⇒ x = doStaticDispatch(x)
        case 3 ⇒ x = doStaticDispatch(x)
      }
      i -= 1
    }
    x
  }

  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  def doStaticDispatch(x: Int) = StaticDispatch.inc(x)

  @Benchmark
  def objectDispatch(): Int = {
    var i = 100000
    var x = 0
    while (i != 0) {
      i & 3 match {
        case 0 ⇒ x = doObjectDispatch(x)
        case 1 ⇒ x = doObjectDispatch(x)
        case 2 ⇒ x = doObjectDispatch(x)
        case 3 ⇒ x = doObjectDispatch(x)
      }
      i -= 1
    }
    x
  }

  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  def doObjectDispatch(x: Int) = ObjectDispatch.inc(x)
}

object ObjectDispatch {
  def inc(i: Int): Int = i + 1
}