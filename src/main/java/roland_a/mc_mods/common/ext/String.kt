package roland_a.mc_mods.common.ext

fun String.substringOrNull(startIndex: Int, endIndex: Int): String? {
	return (
		if (startIndex in 0..this.length && endIndex in startIndex..this.length) {
			this.substring(startIndex, endIndex)
		}
		else {
			null
		}
	)
}

fun String.splitOrNull(delimiter: String): Pair<String, String>? {
	return (
		this
		.split(delimiter, limit = 2)
		.takeIf {
			it.size == 2
		}
		?.let { (l, r) ->
			l to r
		}
	)
}
