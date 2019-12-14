package intcode.ops

import intcode.Param

class JumpIfFalse(private val condition: Param,
                  private val target: Param) : Operation {
    override fun eval(): (Int) -> Int {
        if (condition.read() == 0) {
            return { target.read() }
        } else {
            return { it + 3 }
        }
    }

    override fun toString(): String {
        return "JIF(condition=$condition, target=$target)"
    }


}