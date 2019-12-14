package intcode

import intcode.ops.Halt
import intcode.ops.Opcode


class IntcodeComputer(
        val name: String = "",
        val source: Array<Int>,
        val input: () -> Int = { throw UnsupportedOperationException("$name :: No input defined") },
        val output: (Int) -> Unit = { println(it) }
        ) {
    constructor(vararg params: Int) : this("IntcodeComputer", params.toTypedArray())
    constructor(name: String, vararg params: Int) : this(name, params.toTypedArray())

    val memory = source.copyOf()

    fun reset() = source.copyInto(memory)

    fun execute(initialInstruction: Int = 0,
                        debug: Boolean = false) {
        var instructionCounter = initialInstruction;
        try {
            while (true) {
                val opcode = Opcode(memory[instructionCounter])
                val operation = opcode.toOperation(memory, instructionCounter, input, output)
                if (debug) println("$name :: $instructionCounter ::: ${runtimeDump(memory, instructionCounter)} ::: $operation")
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

    private fun crashdump(ctr: Int): String {
        return (Math.max(0, ctr - 5) until Math.min(memory.size, ctr + 8))
                .map { "  $it :: ${memory[it]}" }
                .joinToString(separator = "\n")
    }

    private fun runtimeDump(mem: Array<Int>, ctr: Int): String {
        return (ctr until Math.min(mem.size, ctr + 6))
                .map { "${mem[it]}" }
                .joinToString(separator = ",")
    }
}

