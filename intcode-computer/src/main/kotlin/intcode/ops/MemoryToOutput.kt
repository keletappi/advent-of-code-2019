package intcode.ops

import intcode.Param
import java.math.BigInteger

class MemoryToOutput(private val param: Param, private val output: (BigInteger) -> Unit) : Operation {
    override fun eval(): (BigInteger) -> BigInteger {
        output(param.read())
        return { it + 2.toBigInteger() }
    }

    override fun toString(): String {
        return "MTO(param=$param)"
    }
}