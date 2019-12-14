package intcode.ops

import intcode.Param
import java.math.BigInteger

class JumpIfTrue(private val condition: Param,
                 private val target: Param) : Operation {
    override fun eval(): (BigInteger) -> BigInteger {
        if (condition.read() == BigInteger.ONE) {
            return { target.read() }
        } else {
            return { it + 3.toBigInteger() }
        }
    }

    override fun toString(): String {
        return "JIT(condition=$condition, target=$target)"
    }

}