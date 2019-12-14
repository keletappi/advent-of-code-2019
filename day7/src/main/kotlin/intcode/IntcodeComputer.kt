package intcode

import intcode.ops.*
import java.lang.Exception
import java.lang.UnsupportedOperationException


class IntcodeComputer(
        val name: String = "",
        val memory: Array<Int> ) {
    constructor(vararg params: Int) : this("IntcodeComputer", params.toTypedArray())
    constructor(name: String, vararg params: Int) : this(name, params.toTypedArray())

    private lateinit var readInput: () -> Int
    private lateinit var writeOuput: (Int) -> Unit

    fun execute(input: () -> Int = { throw UnsupportedOperationException("$name :: No input defined") },
                output: (Int) -> Unit = { println(it) },
                debug: Boolean = false) {
        readInput = input
        writeOuput = output
        execute(memory.clone(), debug = debug)
    }

    private fun execute(mem: Array<Int>,
                        opCtrInit: Int = 0,
                        debug: Boolean = false) {
        var ctr = opCtrInit;
        try {
            while (true) {
                val opcode = Opcode(mem[ctr])
                val operation = opcode.toOperation(mem, ctr, readInput, writeOuput)
                if (debug) println("$name :: $ctr ::: ${runtimeDump(mem, ctr)} ::: $operation")
                val nextInstructionFunction = operation.eval()
                ctr = nextInstructionFunction(ctr)
            }
            //run(mem, operation.eval()(ctr))
        } catch (e: Halt) {
            if (debug) println("\"$name :: HALT")
        } catch (e: Exception) {
            println("$name :: Crash at op $ctr\nMemory content around op:\n${crashdump(mem, ctr)} \n$e");
            throw e
        }
    }

    private fun crashdump(mem: Array<Int>, ctr: Int): String {
        return (Math.max(0, ctr - 5) until Math.min(mem.size, ctr + 8))
                .map { "  $it :: ${mem[it]}" }
                .joinToString(separator = "\n")

    }

    private fun runtimeDump(mem: Array<Int>, ctr: Int): String {
        return (ctr until Math.min(mem.size, ctr + 6))
                .map { "${mem[it]}" }
                .joinToString(separator = ",")

    }


}

class InputToMemory(private val param: Param, private val input: () -> Int) : Operation {
    override fun eval(): (Int) -> Int {
        param.write(input())
        return { it + 2 }
    }

    override fun toString(): String {
        return "ITM(param=$param)"
    }
}

class MemoryToOutput(private val param: Param, private val output: (Int) -> Unit) : Operation {
    override fun eval(): (Int) -> Int {
        output(param.read())
        return { it + 2 }
    }

    override fun toString(): String {
        return "MTO(param=$param)"
    }
}
