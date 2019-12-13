package intcode

infix fun Int.pow(n: Int): Int {
    assert (n >= 0)
    if (n == 0) return 1

    var result = this;
    for (i in 1 until n) result *= this
    return result
}