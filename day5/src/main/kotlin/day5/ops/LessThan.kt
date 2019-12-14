package day5.ops

import day5.Param

class LessThan(private val a: Param,
               private val b: Param,
               private val result: Param) : Operation {
    override fun eval(): (Int) -> Int {
        result.write(if (a.read() < b.read()) 1 else 0)
        return { it + 4 }
    }

    override fun toString(): String {
        return "LT(a=$a, b=$b, result=$result)"
    }
}