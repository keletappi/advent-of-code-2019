import intcode.IntcodeComputer
import java.math.BigInteger.ZERO
import java.math.BigInteger as BInt


const val EMPTY = 0
const val WALL = 1
const val BLOCK = 2
const val PADDLE = 3
const val BALL = 4

fun main() {
    part1()
    part2()
}


private fun part1() {
    val screen = mutableMapOf<Pair<BInt, BInt>, BInt>()
    val cmd = mutableListOf<BInt>()
    IntcodeComputer(
            source = SOURCE_P1,
            output = {
                cmd.add(it)
                if (cmd.size == 3) {
                    screen[cmd[0] to cmd[1]] = cmd[2]
                }
            }
    ).execute()

    println("Day 13 part 1 :: End of game screen:")
    screen.draw()
    println("Day 13 part 1 :: End of game blocks: " + screen.values.count { it == 2.toBigInteger() })
}


fun part2() {
    val screen = mutableMapOf<Pair<BInt, BInt>, BInt>()
    val cmd = mutableListOf<BInt>()
    var score = ZERO
    var joystick = ZERO
    var paddleX: BInt? = null
    var ballX: BInt? = null

    IntcodeComputer(
            source = SOURCE_P2,
            input = { joystick },
            output = {
                cmd.add(it)
                if (cmd.size == 3) {
                    // Coordinates [-1, 0] -> update score
                    if (cmd[0] == (-1).toBigInteger() && cmd[1] == 0.toBigInteger()) {
                        score = cmd[2]
                    } else {
                        screen[cmd[0] to cmd[1]] = cmd[2]

                        if (cmd[2] == PADDLE.toBigInteger()) {
                            paddleX = cmd[0]
                        }

                        if (cmd[2] == BALL.toBigInteger()) {
                            ballX = cmd[0]
                            println("\n\n     $score\n")
                            println()
                            screen.draw()
                        }

                        if (paddleX != null && ballX != null) {
                            joystick = (ballX!! - paddleX!!).signum().toBigInteger()
                        }
                    }

                    cmd.clear()
                }
            }
    ).execute()

    println()
    if (screen.values.count { it.toInt() == BLOCK } == 0) {
        println("Day 13 part 2  :: final score $score")
    } else {
        println("Day 13 part 2  :: ALL BLOCKS NOT BROKEN! -- $score")
    }
}

private fun Map<Pair<BInt, BInt>, BInt>.draw() {
    val maxX = keys.map { it.first.toInt() }.max()!!
    val minX = keys.map { it.first.toInt() }.min()!!
    val maxY = keys.map { it.second.toInt() }.max()!!
    val minY = keys.map { it.second.toInt() }.min()!!

    for (y in minY..maxY) {
        for (x in minX..maxX) {
            print(getOrDefault(x.toBigInteger() to y.toBigInteger(), ZERO).toInt().render())
        }
        println()
    }
}
private fun Int.render(): Char {
    return when (this) {
        EMPTY -> ' '
        WALL -> 'X'
        BLOCK -> 'o'
        PADDLE -> '-'
        BALL -> '*'
        else -> throw RuntimeException("Bad block type $this")
    }
}