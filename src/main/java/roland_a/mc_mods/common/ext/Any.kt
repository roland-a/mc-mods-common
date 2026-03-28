package roland_a.mc_mods.common.ext

fun <T: Any> T?.orDefault(default: T): T {
	return this ?: default
}

fun <T: Any> T?.equals(other: T?): Boolean {
	if (this == null) {
		return other == null
	}

	return this == other
}

fun <T: Any> T?.notEquals(other: T?): Boolean {
	return !this.equals(other)
}
