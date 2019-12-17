import kotlinx.coroutines.*
import java.math.BigInteger
import java.math.BigInteger.ZERO
import kotlin.math.absoluteValue
import kotlin.math.sign
import kotlin.time.ExperimentalTime
import kotlin.time.nanoseconds

val SAMPLE = """
    <x=-1, y=0, z=2>
    <x=2, y=-10, z=-7>
    <x=4, y=-8, z=8>
    <x=3, y=5, z=-1>
""".trimIndent().trim()

val SAMPLE_2 = """
    <x=-8, y=-10, z=0>
    <x=5, y=5, z=10>
    <x=2, y=-7, z=3>
    <x=9, y=-8, z=-3>
""".trimIndent().trim()

val INPUT = """
    <x=-4, y=-9, z=-3>
    <x=-13, y=-11, z=0>
    <x=-17, y=-7, z=15>
    <x=-16, y=4, z=2>
""".trimIndent().trim()

private fun String.moons() = this.lines().map { Moon(Pos(it), Vel(0, 0, 0)) }

data class Moon(val position: Pos, val velocity: Vel) {
    val energy by lazy { position.energy * velocity.energy }
    override fun toString(): String = "Moon(P=$position, V=$velocity)"
}

data class Pos(val x: Int, val y: Int, val z: Int) {
    constructor(txt: String) : this(
            "x=(-?\\d+)".toRegex().find(txt)!!.groupValues[1].toInt(),
            "y=(-?\\d+)".toRegex().find(txt)!!.groupValues[1].toInt(),
            "z=(-?\\d+)".toRegex().find(txt)!!.groupValues[1].toInt()
    )

    val energy by lazy { x.absoluteValue + y.absoluteValue + z.absoluteValue }
    override fun toString(): String = "(x=$x, y=$y, z=$z)"
}

data class Vel(val x: Int, val y: Int, val z: Int) {
    val energy by lazy { x.absoluteValue + y.absoluteValue + z.absoluteValue }
    override fun toString(): String = "(x=$x, y=$y, z=$z)"
}

private const val USE_BRUTE_FORCE = false
@Suppress("ConstantConditionIf")
fun main() {

    SAMPLE.moons()
            .onEach { println("at start        :  $it") }
            .simulate(1)
            .onEach { println("after 1 tick    :  $it") }
            .simulate(99)
            .onEach { println("after 100 ticks :  $it") }
            .sumBy { it.energy }
            .apply { println("Sample :: total energy: $this") } // 293

    // Part1:
    INPUT.moons()
            .simulate(1000)
            .sumBy { it.energy }
            .apply { println("Day 12 part 1 :: total energy: $this") } // 6220

    SAMPLE.moons()
            .measuring { simulateUntilRepeat() }
            .apply { println("Sample 1      :: #ticks to repeat: $this") } // 2772

    SAMPLE_2.moons()
            .measuring {
                findAxisPeriodsAsync().run {
                    println("Sample 2 :: expected periodicity  4686774924")
                    println("Sample 2 :: periodicity by axis   $this")
                }
            }

    INPUT.moons()
            .measuring {
                findAxisPeriodsAsync().run {
                    println("Day 12 part 2 :: expected periodicity  4686774924")
                    println("Day 12 part 2 :: periodicity by axis   $this")
                }
            }

    // These take a _long_ time to complete: ETA for SAMPLE_2 on 3.1GHz i7 is 60-70 minutes;
    // actual INPUT would take days or weeks.
    if (USE_BRUTE_FORCE) {
        SAMPLE_2.moons()
                .measuring { simulateUntilRepeat() }
                .apply { println("Sample 2      :: #ticks to repeat: $this") } // 4686774924
    }

    if (USE_BRUTE_FORCE) {
        INPUT.moons()
                .measuring { simulateUntilRepeat() }
                .apply { println("Day 12 part 2 :: #ticks to repeat: $this") } // 548525804273976
    }
}

private fun List<Moon>.findAxisPeriodsAsync(): BigInteger {
    return runBlocking {
        listOf(Pos::x to Vel::x to "x",
                Pos::y to Vel::y to "y",
                Pos::z to Vel::z to "z")
                .map { GlobalScope.async { simulateUntilRepeat(it.second, it.first.first, it.first.second) } }
                .awaitAll()
                .reduce { acc, it -> lcm(acc, it) }
    }
}

fun List<Moon>.simulateUntilRepeat(label: String, axisPosition: Pos.() -> Int, axisVelocity: Vel.() -> Int): BigInteger {
    var n = ZERO
    var axisAtStart = this.map { it.position.axisPosition() to it.velocity.axisVelocity() }
    var state = this
    do {
        n = n.inc()
        state = state.tick()
    } while (state.map { it.position.axisPosition() to it.velocity.axisVelocity() } != axisAtStart)

    println("Found $label period $n :: $state")

    return n
}

@UseExperimental(ExperimentalTime::class)
private fun <T, V> V.measuring(block: V.() -> T): T {
    val start = System.nanoTime()
    val result = block.invoke(this)
    val end = System.nanoTime()
    println("took " + (end - start).nanoseconds)
    return result
}

@UseExperimental(ExperimentalTime::class)
private fun List<Moon>.simulateUntilRepeat(): BigInteger {

    // region Configure stats
    val printStats = true
    val expected = BigInteger.valueOf(4686774924)
    val statInterval = BigInteger.valueOf(1000000)
    var ts = System.nanoTime()
    var total: BigInteger = ZERO
    // endregion Configure stats

    var n = ZERO
    var state = this
    do {
        n = n.inc()
        state = state.tick()

        // region Print stats
        if (n % statInterval == ZERO && printStats) {
            val lastMil = System.nanoTime() - ts
            total += BigInteger.valueOf(lastMil)
            val eta = (expected - n) / statInterval * BigInteger.valueOf(lastMil)
            println("last 1000000 :: ${lastMil.nanoseconds}")
            println("total to $n :: ${total.toLong().nanoseconds}")
            println("ETA to   4 686 774 924 :: ${eta.toLong().nanoseconds}")

            ts = System.nanoTime()
        }
        // endregion

    } while (state != this)
    return n
}


private fun List<Moon>.simulate(ticksRemaining: Int): List<Moon> {
    return if (ticksRemaining > 0) this.tick().simulate(ticksRemaining - 1) else this
}

private fun List<Moon>.tick() = sequentialTick()

private fun List<Moon>.sequentialTick(): List<Moon> {
    return map { moon ->
        Moon(moon.position, moon.velocity +
                filter { it != moon }
                        .fold(Vel(0, 0, 0)) { acc, other ->
                            Vel(acc.x + (other.position.x - moon.position.x).sign,
                                    acc.y + (other.position.y - moon.position.y).sign,
                                    acc.z + (other.position.z - moon.position.z).sign)
                        })
    }.map { moon -> Moon(moon.position + moon.velocity, moon.velocity) }
}

// This is a _LOT_ slower than sequential version. No idea where this would become more efficient :/
private fun List<Moon>.asyncTick(): List<Moon> = runBlocking {
    map { moon ->
        GlobalScope.async {
            Moon(moon.position, moon.velocity +
                    filter { it != moon }
                            .fold(Vel(0, 0, 0)) { acc, other ->
                                Vel(acc.x + (other.position.x - moon.position.x).sign,
                                        acc.y + (other.position.y - moon.position.y).sign,
                                        acc.z + (other.position.z - moon.position.z).sign)
                            })
        }
    }.awaitAll().map { moon -> Moon(moon.position + moon.velocity, moon.velocity) }
}


private operator fun Vel.plus(other: Vel): Vel = Vel(x + other.x, y + other.y, z + other.z)
private operator fun Pos.plus(other: Vel): Pos = Pos(x + other.x, y + other.y, z + other.z)

fun gcd(a: BigInteger, b: BigInteger): BigInteger = if (b == BigInteger.ZERO) a else gcd(b, a % b)
fun lcm(a: BigInteger, b: BigInteger): BigInteger = a / gcd(a, b) * b

