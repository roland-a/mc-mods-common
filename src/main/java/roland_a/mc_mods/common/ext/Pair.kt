package roland_a.mc_mods.common.ext

fun <L, R, LR> Pair<L, R>.mapLeft(fn: (L) -> LR): Pair<LR, R> {
	return fn(this.first) to this.second
}

fun <L, R, RR> Pair<L, R>.mapRight(fn: (R) -> RR): Pair<L, RR> {
	return this.first to fn(this.second)
}

fun <L, R, LR> List<Pair<L, R>>.mapLeft(fn: (L) -> LR): List<Pair<LR, R>> {
	return this.map { it.mapLeft(fn) }
}

fun <L, R, RR> List<Pair<L, R>>.mapRight(fn: (R) -> RR): List<Pair<L, RR>> {
	return this.map { it.mapRight(fn) }
}
