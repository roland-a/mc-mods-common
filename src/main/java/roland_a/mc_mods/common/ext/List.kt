package roland_a.mc_mods.common.ext

fun <T> List<T>.permutations(): List<List<T>> {
	val results = mutableListOf<List<T>>()
	val current = this.toMutableList()

	fun <T> MutableList<T>.swap(i: Int, j: Int) {
		val temp = this[i]
		this[i] = this[j]
		this[j] = temp
	}

	fun backtrack(start: Int) {
		if (start == current.size) {
			results.add(current.toList())
			return
		}
		for (i in start until current.size) {
			current.swap(start, i)
			backtrack(start + 1)
			current.swap(start, i)
		}
	}

	backtrack(0)
	return results
}

fun <T> List<T>.indexOfFirstOrNull(fn: (T) -> Boolean): Int? {
	val result = this.indexOfFirst(fn)
	if (result == -1) {
		return null
	}
	return result
}
