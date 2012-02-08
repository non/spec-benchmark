package org.example

import annotation.tailrec
import com.google.caliper.Param

import scala.{specialized => spec}
import scala.util.Random

trait HasPlus[@spec(Int) A] {
  def plus(x:A, y:A):A
  class Ops(lhs:A) {
    def +(rhs:A) = plus(lhs, rhs)
  }
}

trait IntHasPlus extends HasPlus[Int] {
  def plus(x:Int, y:Int) = x + y
}

object HasPlus {
  implicit object IntHasPlus extends IntHasPlus
}

final class HasPlusOps[@spec(Int) A](lhs:A)(implicit ev:HasPlus[A]) {
  def +(rhs:A) = ev.plus(lhs, rhs)
}

object Implicits {
  implicit def infix1[A](lhs:A)(implicit n:HasPlus[A]): HasPlus[A]#Ops = new n.Ops(lhs)
  implicit def infix2[@spec(Int) A:HasPlus](lhs:A) = new HasPlusOps(lhs)
}

object Nested {
  import Implicits.infix1
  def add[@spec(Int) A:HasPlus](x:A, y:A) = x + y
}

object External {
  import Implicits.infix2
  def add[@spec(Int) A:HasPlus](x:A, y:A) = x + y
}

class Benchmark extends SimpleScalaBenchmark {
  
  @Param(Array("100", "1000", "10000", "100000"))
  val length: Int = 0
  
  var array: Array[Int] = _
  
  override def setUp() {
    array = Array.ofDim[Int](length).map(i => Random.nextInt())
  }

  // first benchmark
  def timeNested(reps: Int) = repeat(reps) {
    var result = 0
    var i = 0
    while (i < length) {
      result = Nested.add(result, array(i))
      i += 1
    }
    result
  }
  
  // a second benchmarking code snippet
  def timeExternal(reps: Int) = repeat(reps) {
    var result = 0
    var i = 0
    while (i < length) {
      result = External.add(result, array(i))
      i += 1
    }
    result
  }

  override def tearDown() {
    // clean up after yourself if required
  }
}

