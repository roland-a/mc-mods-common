package roland_a.mc_mods.common.config.tag.compilers

import roland_a.mc_mods.common.config.tag.CompiledEntry
import roland_a.mc_mods.common.config.tag.EntryCompiler
import roland_a.mc_mods.common.config.tag.Tag
import roland_a.mc_mods.common.config.tag.Tag.Companion.toTag
import kotlin.enums.enumEntries

inline fun <reified T: Enum<T>> compilerOf(): EntryCompiler<T> {
	return mapCompiler(enumEntries<T>().associateWith { it.name.toTag() })
}

fun <K> Map<K, String>.toCompiler(): EntryCompiler<K> {
	return (
		this
		.mapValues { (_, v) ->
			v.toTag()
		}
		.let {
			mapCompiler(it)
		}
	)
}

fun <K> mapCompiler(map: Map<K, Tag>): EntryCompiler<K> {
	return { entries ->
		val relevantTags =
			entries
			.values
			.filter {
				it in map.values
			}
			.toSet()

		CompiledEntry(
			relevantTags.size,
			relevantTags,
		) { value ->
			map[value] in relevantTags
		}
	}
}
