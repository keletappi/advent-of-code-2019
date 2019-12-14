package intcode

import java.math.BigInteger

fun Array<BigInteger>.reference(addr: BigInteger): Param = Reference(this[addr.toInt()], this)
fun Array<BigInteger>.immediate(addr: BigInteger): Param = Immediate(this[addr.toInt()])
fun Array<BigInteger>.relative(addr: BigInteger, relativeBase: BigInteger): Param = Relative(this[addr.toInt()], relativeBase, this)

interface Param {
    fun read(): BigInteger
    fun write(newValue: BigInteger)
}

class Reference(val paramValue: BigInteger, val memory: Array<BigInteger>) : Param {
    override fun read(): BigInteger = memory[paramValue.toInt()]
    override fun write(newValue: BigInteger) {
        memory[paramValue.toInt()] = newValue
    }

    override fun toString(): String {
        return "ref($paramValue->${read()})"
    }
}

class Relative(val paramValue: BigInteger, val relativeBase: BigInteger, val memory: Array<BigInteger>) : Param {
    override fun read(): BigInteger = memory[(paramValue + relativeBase).toInt()]

    override fun write(newValue: BigInteger) {
        memory[(paramValue + relativeBase).toInt()] = newValue
    }

    override fun toString(): String {
        return "rel(addr=$paramValue, rel=$relativeBase, ${paramValue + relativeBase}->${memory[(paramValue + relativeBase).toInt()]})"
    }
}

class Immediate(val paramValue: BigInteger) : Param {
    override fun read(): BigInteger = paramValue

    override fun write(newValue: BigInteger) {
        throw UnsupportedOperationException("Writing to immediate parameter")
    }

    override fun toString(): String {
        return "val($paramValue)"
    }

}
