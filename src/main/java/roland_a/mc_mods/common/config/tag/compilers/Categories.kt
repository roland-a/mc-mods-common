package roland_a.mc_mods.common.config.tag.compilers

import roland_a.mc_mods.common.config.tag.CompiledEntry
import roland_a.mc_mods.common.config.tag.EntryCompiler

fun <T> categories(vararg compilers: EntryCompiler<T>): EntryCompiler<T> {
	return { entries ->
		val compiled =
			compilers
			.map {
				it.compile(entries)
			}

		val ranking =
			compiled
			.flatMap {
				it.ranking
			}

		val relevantTags = compiled.flatMap { it.relevantTags }.toSet()

		CompiledEntry(
			ranking,
			relevantTags,
		) { value ->
			compiled.all { it.matches(value) }
		}
	}
}
