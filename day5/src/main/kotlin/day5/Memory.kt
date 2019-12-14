package day5

import java.lang.UnsupportedOperationException

fun Array<Int>.reference(n: Int): Param = Reference(this[n], this)
fun Array<Int>.immediate(n: Int): Param = Immediate(this[n])

interface Param {
    fun read(): Int
    fun write(newValue: Int)
}

class Reference(val addr: Int, val memory: Array<Int>) : Param {
    override fun read(): Int = memory[addr]
    override fun write(newValue: Int) {
        // println("         Write $addr<-$newValue")
        memory[addr] = newValue
    }

    override fun toString(): String {
        return "ref($addr->${read()})"
    }

}

class Immediate(val value: Int) : Param {
    override fun read(): Int = value

    override fun write(newValue: Int) {
        throw UnsupportedOperationException("Writing to immediate parameter")
    }

    override fun toString(): String {
        return "val($value)"
    }

}
