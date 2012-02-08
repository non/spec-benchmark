Some benchmarks of various specialized Scala code

Currently, it shows the difference in performance between using inner classes
and regular classes, e.g. between defining Numeric[A]#Ops and NumericOps[A].

Benchmarking is done via Caliper. This project was based on the template from
https://github.com/sirthias/scala-benchmarking-template.
