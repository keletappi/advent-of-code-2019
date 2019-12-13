package intcode.ops

import intcode.Param

class JumpIfTrue(private val condition: Param,
                 private val target: Param) : Operation {
    override fun eval(): (Int) -> Int {
        if (condition.read() != 0) {
            return { target.read() }
        } else {
            return { it + 3 }
        }
    }

    override fun toString(): String {
        return "JIT(condition=$condition, target=$target)"
    }

}