package benchmarks

import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations._

import scala.util.Random
import scala.util.control.NoStackTrace

object ExceptionBench {
  def main(args: Array[String]): Unit = {
    import org.openjdk.jmh.runner.Runner
    import org.openjdk.jmh.runner.options.OptionsBuilder

    val opts = new OptionsBuilder()
      .include("ExceptionBenchmark")
      .forks(1)
      .warmupIterations(5)
      .measurementIterations(5)
      .mode(Mode.AverageTime)
      .timeUnit(TimeUnit.MICROSECONDS)
      .threads(1)
      .build()
    new Runner(opts).run()
  }
}

@State(Scope.Thread)
class ExceptionBenchmark {

  private[this] var randomSeed = 182642182642182642L

  // simple XORshift random number generator (see, e.g., http://en.wikipedia.org/wiki/Xorshift)
  private def nextRandomLong(): Long = {
    var x = randomSeed
    x ^= x << 21
    x ^= x >>> 35
    x ^= x << 4
    randomSeed = x
    x - 1
  }

  @Benchmark
  def returnWithException(): Long = {
    var ix = 0L
    while (ix < 10000) ix += bit0WithException()
    ix
  }

  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  def bit0WithException(): Long =
    try {
      val bit0 = nextRandomLong() & 1
      if (bit0 == 1) throw MyException
      bit0
    } catch {
      case _: ArithmeticException ⇒ 5
      case MyException            ⇒ 1
    }

  @Benchmark
  def returnWithoutException(): Long = {
    var ix = 0L
    while (ix < 10000) ix += bit0WithoutException()
    ix
  }

  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  def bit0WithoutException(): Long =
    try {
      val bit0 = nextRandomLong() & 1
      if (bit0 == 2) throw MyException
      bit0
    } catch {
      case _: ArithmeticException ⇒ 5
      case MyException            ⇒ 1
    }

  @Benchmark
  def returnWithoutTry(): Long = {
    var ix = 0L
    while (ix < 10000) ix += {
      val bit0 = nextRandomLong() & 1
      if (bit0 == 2) 5
      else bit0
    }
    ix
  }
}

object MyException extends RuntimeException with NoStackTrace