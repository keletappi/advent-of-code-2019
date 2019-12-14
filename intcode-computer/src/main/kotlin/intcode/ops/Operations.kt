package intcode.ops

import intcode.*
import java.lang.RuntimeException
import java.math.BigInteger


private enum class ParamMode {
    REFERENCE,
    IMMEDIATE,
    RELATIVE
}

class Opcode(private val opcode: Int) {
    private val op: Int by lazy { opcode % 100 }

    fun toOperation(mem: Array<BigInteger>,
                    ctr: BigInteger,
                    input: () -> BigInteger,
                    output: (BigInteger) -> Unit,
                    relativeBase: RelativeBase): Operation {
        return when (this.op) {
            1 -> Add(
                    accessParam(mem, ctr, 1, relativeBase.value),
                    accessParam(mem, ctr, 2, relativeBase.value),
                    accessParam(mem, ctr, 3, relativeBase.value)
            )
            2 -> Multiply(
                    accessParam(mem, ctr, 1, relativeBase.value),
                    accessParam(mem, ctr, 2, relativeBase.value),
                    accessParam(mem, ctr, 3, relativeBase.value)
            )
            3 -> InputToMemory(
                    accessParam(mem, ctr, 1, relativeBase.value),
                    input
            )
            4 -> MemoryToOutput(
                    accessParam(mem, ctr, 1, relativeBase.value),
                    output
            )
            5 -> JumpIfTrue(
                    accessParam(mem, ctr, 1, relativeBase.value),
                    accessParam(mem, ctr, 2, relativeBase.value)
            )
            6 -> JumpIfFalse(
                    accessParam(mem, ctr, 1, relativeBase.value),
                    accessParam(mem, ctr, 2, relativeBase.value)
            )
            7 -> LessThan(
                    accessParam(mem, ctr, 1, relativeBase.value),
                    accessParam(mem, ctr, 2, relativeBase.value),
                    accessParam(mem, ctr, 3, relativeBase.value)
            )
            8 -> Equals(
                    accessParam(mem, ctr, 1, relativeBase.value),
                    accessParam(mem, ctr, 2, relativeBase.value),
                    accessParam(mem, ctr, 3, relativeBase.value)
            )
            9 -> AdjustRelativeBase(
                    accessParam(mem, ctr, 1, relativeBase.value),
                    relativeBase
            )
            99 -> Halt()
            else -> throw RuntimeException("Unknown operation $this at $ctr")
        }
    }

    private fun accessParam(mem: Array<BigInteger>,
                            ctr: BigInteger,
                            n: Int,
                            base: BigInteger): Param =
            accessParam(mem, ctr, n.toBigInteger(), base)

    private fun accessParam(mem: Array<BigInteger>,
                            ctr: BigInteger,
                            n: BigInteger,
                            base: BigInteger): Param {
        return when (paramMode(n)) {
            ParamMode.REFERENCE -> mem.reference(ctr + n)
            ParamMode.IMMEDIATE -> mem.immediate(ctr + n)
            ParamMode.RELATIVE -> mem.relative(ctr + n, base)
        }
    }

    private fun paramMode(n: BigInteger): ParamMode {
        return when ((opcode / (10 pow (n.toInt() + 1))) % 10) {
            0 -> ParamMode.REFERENCE
            1 -> ParamMode.IMMEDIATE
            2 -> ParamMode.RELATIVE
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
    fun eval(): (BigInteger) -> BigInteger
}

class Halt : Operation, Throwable() {
    override fun eval(): (BigInteger) -> BigInteger {
        throw this
    }
}