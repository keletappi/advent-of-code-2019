package intcode.ops

import intcode.Param
import intcode.RelativeBase
import java.math.BigInteger

class AdjustRelativeBase(private val adjustment: Param,
                         private val relativeBase: RelativeBase) : Operation {
    override fun eval(): (BigInteger) -> BigInteger {
        relativeBase.value += adjustment.read()
        return { it + 2.toBigInteger() }
    }

}
