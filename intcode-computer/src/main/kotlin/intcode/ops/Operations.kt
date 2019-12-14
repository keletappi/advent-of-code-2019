package intcode.ops

import intcode.Param
import intcode.immediate
import intcode.pow
import intcode.reference
import java.lang.RuntimeException


private enum class ParamMode {
    REFERENCE,
    IMMEDIATE
}

class Opcode(val opcode: Int) {
    val op: Int by lazy { opcode % 100 }

    fun toOperation(mem: Array<Int>, ctr: Int, input: () -> Int, output: (Int) -> Unit): Operation {
        return when (this.op) {
            1 -> Add(
                    accessParam(mem, ctr, 1),
                    accessParam(mem, ctr, 2),
                    accessParam(mem, ctr, 3)
            )
            2 -> Multiply(
                    accessParam(mem, ctr, 1),
                    accessParam(mem, ctr, 2),
                    accessParam(mem, ctr, 3)
            )
            3 -> InputToMemory(
                    accessParam(mem, ctr, 1),
                    input
            )
            4 -> MemoryToOutput(
                    accessParam(mem, ctr, 1),
                    output
            )
            5 -> JumpIfTrue(
                    accessParam(mem, ctr, 1),
                    accessParam(mem, ctr, 2)
            )
            6 -> JumpIfFalse(
                    accessParam(mem, ctr, 1),
                    accessParam(mem, ctr, 2)
            )
            7 -> LessThan(
                    accessParam(mem, ctr, 1),
                    accessParam(mem, ctr, 2),
                    accessParam(mem, ctr, 3)
            )
            8 -> Equals(
                    accessParam(mem, ctr, 1),
                    accessParam(mem, ctr, 2),
                    accessParam(mem, ctr, 3)
            )
            99 -> Halt()
            else -> throw RuntimeException("Unknown operation $this at $ctr")
        }
    }

    private fun accessParam(mem: Array<Int>, ctr: Int, n: Int): Param = when (paramMode(n)) {
        ParamMode.REFERENCE -> mem.reference(ctr + n)
        ParamMode.IMMEDIATE -> mem.immediate(ctr + n)
    }

    private fun paramMode(n: Int): ParamMode {
        return when ((opcode / (10 pow (1 + n))) % 10) {
            0 -> ParamMode.REFERENCE
            1 -> ParamMode.IMMEDIATE
            else -> throw RuntimeException("Bad param mode for param $n in $opcode")
        }
    }

    override fun toString(): String {
        return "Opcode($opcode)"
    }
}

interface Operation {
    /**
     * Evaluates operation, and returns a function which updates
     * given instruction pointer argument to the instruction pointer
     * of next operation
     */
    fun eval(): (Int) -> Int
}

class Halt : Operation, Throwable() {
    override fun eval(): (Int) -> Int {
        throw this
    }
}