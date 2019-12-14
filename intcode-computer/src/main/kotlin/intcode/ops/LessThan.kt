package intcode.ops

import intcode.Param
import java.math.BigInteger

class LessThan(private val a: Param,
               private val b: Param,
               private val result: Param) : Operation {
    override fun eval(): (BigInteger) -> BigInteger {
        val aVal = a.read()
        val bVal = b.read()

        result.write(if (aVal < bVal) BigInteger.ONE else BigInteger.ZERO)
        return { it + 4.toBigInteger() }
    }

    override fun toString(): String {
        return "LT(a=$a, b=$b, result=$result)"
    }
}