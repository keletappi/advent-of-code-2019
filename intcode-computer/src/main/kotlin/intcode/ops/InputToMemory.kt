package intcode.ops

import intcode.Param

class InputToMemory(private val param: Param, private val input: () -> Int) : Operation {
    override fun eval(): (Int) -> Int {
        param.write(input())
        return { it + 2 }
    }

    override fun toString(): String {
        return "ITM(param=$param)"
    }
}