package roland_a.mc_mods.common.config.tag.compilers

import roland_a.mc_mods.common.config.tag.CompiledEntry
import roland_a.mc_mods.common.config.tag.CompiledEntry.Companion.isEmpty
import roland_a.mc_mods.common.config.tag.EntryCompiler

fun <T> EntryCompiler<T>.allowNull(): EntryCompiler<T?> {
	val original = this

	return { entries ->
		val originalCompiled = original.compile(entries)

		CompiledEntry(
			originalCompiled.ranking,
			originalCompiled.relevantTags,
		) { value ->
			if (value == null) {
				originalCompiled.isEmpty()
			}
			else {
				originalCompiled.matches(value)
			}
		}
	}
}
