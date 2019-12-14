package intcode.ops

import intcode.Param

class Equals(private val a: Param,
             private val b: Param,
             private val result: Param) : Operation {
    override fun eval(): (Int) -> Int {
        val aVal = a.read()
        val bVal = b.read()
        val output = if (aVal == bVal) 1 else 0
        result.write(output)
        return { it + 4 }
    }

    override fun toString(): String {
        return "EQ(a=$a, b=$b, result=$result)"
    }


}