package benchmarks

import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations._

import scala.util.Random

object ForwardBackwardArrayLoopBench {
  def main(args: Array[String]): Unit = {
    import org.openjdk.jmh.runner.Runner
    import org.openjdk.jmh.runner.options.OptionsBuilder

    val opts = new OptionsBuilder()
      .include("ForwardBackwardArrayLoopBenchmark")
      .forks(5)
      .warmupIterations(10)
      .measurementIterations(10)
      .mode(Mode.AverageTime)
      .timeUnit(TimeUnit.MICROSECONDS)
      .threads(1)
      .build()
    new Runner(opts).run()
  }
}

@State(Scope.Thread)
class ForwardBackwardArrayLoopBenchmark {

  private[this] val array = new Array[Byte](1000000)
  ForwardBackwardArrayLoopBenchmark.random.nextBytes(array)

  @Benchmark
  def forward(): Int = {
    var ix = 0
    var x = 0
    while (ix < array.length) {
      x |= array(ix)
      ix += 1
    }
    x
  }

  @Benchmark
  def backward(): Int = {
    var ix = array.length - 1
    var x = 0
    while (ix >= 0) {
      x |= array(ix)
      ix -= 1
    }
    x
  }
}

object ForwardBackwardArrayLoopBenchmark {
  val random = new Random(182642)
}