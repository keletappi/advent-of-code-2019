package intcode

import intcode.ops.Halt
import intcode.ops.Opcode
import java.math.BigInteger
import java.math.BigInteger.ZERO
import kotlin.math.min
import kotlin.math.max


class IntcodeComputer(
        val name: String = "intcode-computer",
        val source: Array<BigInteger>,
        val input: () -> BigInteger = { throw UnsupportedOperationException("$name :: No input defined") },
        val output: (BigInteger) -> Unit = { println(it) }
) {
    constructor(vararg params: Int) : this(source = params.map { it.toBigInteger() }.toTypedArray())
    constructor(name: String = "intcode-computer",
                source: Array<Int>,
                input: () -> Int = { throw UnsupportedOperationException("$name :: No input defined") },
                output: (Int) -> Unit = { println(it) }
    ) : this(name,
            source.map(Int::toBigInteger).toTypedArray(),
            input = { input().toBigInteger() },
            output = { output(it.toInt()) }
    )

    private var relativeBase = RelativeBase()
    private val memory: Array<BigInteger> = Array(64000) { ZERO }

    init {
        source.copyInto(memory)
        relativeBase.value = ZERO
    }

    fun execute(initialInstruction: BigInteger = ZERO,
                debug: Boolean = false) {
        var instructionCounter = initialInstruction;
        try {
            while (true) {
                val opcode = Opcode(memory[instructionCounter.toInt()].toInt())
                val operation = opcode.toOperation(memory, instructionCounter, input, output, relativeBase)
                if (debug) println("$name :: $instructionCounter ::: ${runtimeDump(instructionCounter)} ::: $operation")
                val instructionCounterUpdater = operation.eval()
                instructionCounter = instructionCounterUpdater(instructionCounter)
            }
        } catch (e: Halt) {
            if (debug) println("\"$name :: HALT")
        } catch (e: Exception) {
            println("$name :: Crash at op $instructionCounter\nMemory content around op:\n${crashdump(instructionCounter)} \n$e");
            throw e
        }
    }

    private fun crashdump(instruction: BigInteger): String {
        return (ZERO.max(instruction - 5) until memory.size.toBigInteger().min(instruction + 8))
                .map { "  $it :: ${memory[it.toInt()]}" }
                .joinToString(separator = "\n")
    }

    private fun runtimeDump(ctr: BigInteger): String {
        return (ctr until memory.size.toBigInteger().min(ctr + 6))
                .map { "${memory[it.toInt()]}" }
                .joinToString(separator = ",")
    }
}

class RelativeBase(initial: BigInteger = ZERO) {
    var value = initial
    override fun toString(): String {
        return "RelativeBase(value=$value)"
    }
}