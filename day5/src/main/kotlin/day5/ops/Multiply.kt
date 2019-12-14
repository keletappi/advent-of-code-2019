package day5.ops

import day5.Param

class Multiply(private val a: Param,
               private val b: Param,
               private val result: Param) : Operation {
    override fun eval(): (Int) -> Int {
        result.write(a.read() * b.read())
        return { it + 4 }
    }

    override fun toString(): String {
        return "MUL(a=$a, b=$b, result=$result)"
    }


}