package roland_a.mc_mods.common.config.tag

fun interface TaggedMap<K, V> {
	fun compute(key: K): Pair<V, TaggedEntry>?

	companion object {
		fun <K, V> TaggedMap<K, V>.computeValue(key: K): V? {
			return this.compute(key)?.first
		}
	}
}
