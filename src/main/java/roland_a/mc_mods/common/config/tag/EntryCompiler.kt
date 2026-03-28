package roland_a.mc_mods.common.config.tag

fun interface EntryCompiler<T> {
	fun compile(t: TaggedEntry): CompiledEntry<T>

	companion object {
		fun <K, V> EntryCompiler<K>.compileAll(
			entries: List<Pair<TaggedEntry, V>>,
			onUnknownTags: (Tag) -> Unit,
		): TaggedMap<K, V> {
			val fns: List<Triple<(K) -> Boolean, V, TaggedEntry>> =
				entries
				.map { (k, v) ->
					Triple(
						compile(k),
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

			return { key ->
				fns
				.firstOrNull {
					it.first(key)
				}
				?.let {
					it.second to it.third
				}
			}
		}
	}
}
