package intcode.ops

import intcode.Param
import java.math.BigInteger

class InputToMemory(private val param: Param, private val input: () -> BigInteger) : Operation {
    override fun eval(): (BigInteger) -> BigInteger {
        param.write(input())
        return { it + 2.toBigInteger() }
    }

    override fun toString(): String {
        return "ITM(param=$param)"
    }
}