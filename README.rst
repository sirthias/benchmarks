Benchmarks
==========

A collection of potentially interesting benchmarks on the JVM with a specific focus on the current Scala compiler,
produced with JMH (and `@ktoso`_'s excellent `sbt-jmh`_ plugin).
Apart from simply showing benchmark results this project is also meant as a quickstart basis for your own benchmarking
endeavors. If you think you have something that might be interesting to a wider audience
you are more than welcome to `Contribute`_.


Interpreting Results
--------------------

As always benchmarking results need to be taken with rather chunky grain of salt.
The ones shown here are no exception.
Especially the absolute numbers have no meaning whatsoever. They merely serve as a basis for comparing
different logic snippets. As such they can give an indication of which of several competing implementations
can be expected to perform better.

However, even if alternative A shows 1000 ops/sec and alternative B shows 2000 ops/sec that doesn't mean that
B is twice as fast as A. Because the results for A and B both contain a hopefully similar amount of test overhead,
this result could also mean that B is in fact 100 times as fast as A!
Compare with the "baseline" entry of each benchmark to get an indication of how large this common test overhead for
the specific test roughly is.


ObjectDispatchBenchmark
-----------------------

...



Test Machine
------------

All results shown here are produced on a MacBook Pro with a
*Intel(R) Core(TM) i7-4960HQ CPU @ 2.60GHz* running *OS/X 10.9.5* and
*Java(TM) SE Runtime Environment (build 1.8.0_31-b13)* on
*Java HotSpot(TM) 64-Bit Server VM (build 25.31-b07, mixed mode)*.


Contribute
----------

Additions, corrections, improvements, comments, etc. are welcome anytime.


License
-------

Licensed under `APL 2.0`_.

.. _sbt-jmh: https://github.com/ktoso/sbt-jmh/
.. _ktoso: https://github.com/ktoso
.. _APL 2.0: http://www.apache.org/licenses/LICENSE-2.0