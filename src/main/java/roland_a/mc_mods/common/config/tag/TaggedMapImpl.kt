package roland_a.mc_mods.common.config.tag

internal class TaggedMapImpl<K, V>(
	entries: List<Pair<TaggedEntry, V>>,
	compiler: EntryCompiler<K>,
	onUnknownTags: (Tag) -> Unit,
): TaggedMap<K, V> {
	private val fns: List<Triple<(K) -> Boolean, V, TaggedEntry>> =
		entries
		.map { (k, v) ->
			Triple(
				compiler.compile(k),
				v,
				k,
			)
		}
		//filter out entries with unknown tags
		.filter { (k, v, s) ->
			for (tag in s.values) {
				if (tag !in k.relevantTags) {
					onUnknownTags(tag)
					return@filter false
				}
			}
			true
		}
		.sortedByDescending {
			it.first
		}
		.map { (k, v, s) ->
			Triple(
				{ k.matches(it) },
				v,
				s,
			)
		}

	override fun compute(key: K): Pair<V, TaggedEntry>? {
		for ((fn, value, entry) in this.fns) {
			if (fn(key)) {
				return value to entry
			}
		}
		return null
	}
}
