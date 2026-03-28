package roland_a.mc_mods.common.config.tag.compilers

import roland_a.mc_mods.common.config.tag.CompiledEntry
import roland_a.mc_mods.common.config.tag.EntryCompiler
import roland_a.mc_mods.common.ext.equals
import roland_a.mc_mods.common.ext.notEquals
import roland_a.mc_mods.common.ext.substringOrNull

fun <T> prefixedCompiler(prefix: String, extractor: (String) -> T?): EntryCompiler<T> {
	return { entries ->
		val relevantTags =
			entries
			.values
			.filter {
				it
				.value
				.substringOrNull(0, prefix.length)
				.equals(prefix)
			}
			.filter {
				it
				.value
				.substringOrNull(prefix.length, it.value.length)
				?.let(extractor)
				.notEquals(null)
			}
			.toSet()

		val allAcceptedValues =
			relevantTags
			.map {
				it
				.value
				.substring(prefix.length, it.value.length)
				.let(extractor)!!
			}
			.toSet()

		CompiledEntry(
			relevantTags.size,
			relevantTags,
		) { value ->
			value in allAcceptedValues
		}
	}
}
