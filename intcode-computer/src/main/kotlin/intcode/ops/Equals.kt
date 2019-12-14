package intcode.ops

import intcode.Param
import java.math.BigInteger

class Equals(private val a: Param,
             private val b: Param,
             private val result: Param) : Operation {
    override fun eval(): (BigInteger) -> BigInteger {
        val aVal = a.read()
        val bVal = b.read()
        val output = if (aVal == bVal) BigInteger.ONE else BigInteger.ZERO
        result.write(output)
        return { it + 4.toBigInteger() }
    }

    override fun toString(): String {
        return "EQ(a=$a, b=$b, result=$result)"
    }


}