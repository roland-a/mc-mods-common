package roland_a.mc_mods.common.config.tag.compilers

import roland_a.mc_mods.common.config.tag.CompiledEntry
import roland_a.mc_mods.common.config.tag.EntryCompiler

fun <T, R> EntryCompiler<T>.map(selector: (R) -> T): EntryCompiler<R> {
	val original = this

	return { entries ->
		val compiledOriginal = original.compile(entries)

		CompiledEntry(
			compiledOriginal.ranking,
			compiledOriginal.relevantTags,
		) {
			compiledOriginal.matchesNonEmpty(selector(it))
		}
	}
}
