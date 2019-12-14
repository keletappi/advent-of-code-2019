package intcode.ops

import intcode.Param

class MemoryToOutput(private val param: Param, private val output: (Int) -> Unit) : Operation {
    override fun eval(): (Int) -> Int {
        output(param.read())
        return { it + 2 }
    }

    override fun toString(): String {
        return "MTO(param=$param)"
    }
}