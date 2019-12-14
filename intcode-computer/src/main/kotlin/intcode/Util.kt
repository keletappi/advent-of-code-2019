package intcode

infix fun Int.pow(n: Int): Int {
    assert (n >= 0)
    if (n == 0) return 1

    var result = this;
    for (i in 1 until n) result *= this
    return result
}

fun <T> List<T>.permute(): List<List<T>> {
    if (this.size == 1) return listOf(this)
    val perms = mutableListOf<List<T>>()
    val toInsert = this[0]
    for (perm in this.drop(1).permute()) {
        for (i in 0..perm.size) {
            val newPerm = perm.toMutableList()
            newPerm.add(i, toInsert)
            perms.add(newPerm)
        }
    }
    return perms
}