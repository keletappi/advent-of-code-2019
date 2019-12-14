package intcode.ops

import intcode.Param

class Add(private val a: Param,
          private val b: Param,
          private val result: Param) : Operation {
    override fun eval(): (Int) -> Int {
        result.write(a.read() + b.read())
        return { it + 4 }
    }

    override fun toString(): String {
        return "ADD(a=$a, b=$b, result=$result)"
    }


}