package benchmarks

import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations._
import sun.misc.IOUtils

object JsonBench {
  def main(args: Array[String]): Unit = {
    import org.openjdk.jmh.runner.Runner
    import org.openjdk.jmh.runner.options.OptionsBuilder

    val opts = new OptionsBuilder()
      .include("JsonBenchmark")
      .forks(1)
      .warmupIterations(20)
      .measurementIterations(20)
      .mode(Mode.Throughput)
      .timeUnit(TimeUnit.SECONDS)
      .threads(1)
      .build()
    new Runner(opts).run()

    //    val json = new TestState().json
    //    (0 to 10000).foreach(_ ⇒ benchmarks.json.array.ast.JsonParser(json))
  }
}

@State(Scope.Thread)
class TestState {
  // 744kb test JSON produced with http://www.json-generator.com/
  val jsonBytes: Array[Byte] = IOUtils.readFully(getClass.getResourceAsStream("/test.json"), -1, true)
  val jsonString: String = new String(jsonBytes, "UTF-8")
}

@State(Scope.Thread)
class JsonBenchmark {

  @Benchmark
  def sprayJsonFromBytes(state: TestState) =
    spray.json.JsonParser(state.jsonBytes)

  @Benchmark
  def sprayJsonFromString(state: TestState) =
    spray.json.JsonParser(state.jsonString)

  @Benchmark
  def json4SNativeFromByteArrayInputStream(state: TestState) =
    org.json4s.native.JsonMethods.parse(new ByteArrayInputStream(state.jsonBytes))

  @Benchmark
  def json4SNativeFromString(state: TestState) =
    org.json4s.native.JsonMethods.parse(state.jsonString)

  @Benchmark
  def json4SJacksonFromByteArrayInputStream(state: TestState) =
    org.json4s.jackson.JsonMethods.parse(new ByteArrayInputStream(state.jsonBytes))

  @Benchmark
  def json4SJacksonFromString(state: TestState) =
    org.json4s.jackson.JsonMethods.parse(state.jsonString)

  @Benchmark
  def argonautFromString(state: TestState) =
    argonaut.Parse.parseOption(state.jsonString).get

  @Benchmark
  def jawnFromByteBuffer(state: TestState) =
    jawn.support.spray.Parser.parseFromByteBuffer(ByteBuffer.wrap(state.jsonBytes))

  @Benchmark
  def jawnFromString(state: TestState) =
    jawn.support.spray.Parser.parseFromString(state.jsonString)

  @Benchmark
  def sprayJsonParserToArrayBasedBasicAstFromBytes(state: TestState) =
    benchmarks.json.array.ast.JsonParser(state.jsonBytes)

  @Benchmark
  def sprayJsonParserToVectorBasedBasicAstFromBytes(state: TestState) =
    benchmarks.json.vector.ast.JsonParser(state.jsonBytes)
}