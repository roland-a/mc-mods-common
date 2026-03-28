package roland_a.mc_mods.common.config.tag.compilers

import net.minecraft.resources.ResourceLocation
import roland_a.mc_mods.common.config.tag.CompiledEntry
import roland_a.mc_mods.common.config.tag.EntryCompiler

val mcIdCompiler: EntryCompiler<ResourceLocation> = { entries ->
	val relevantTags =
		entries
		.values
		.filter {
			":" in it.value
		}
		.filter {
			runCatching {
				ResourceLocation.parse(it.value)
			}
			.isSuccess
		}
		.toSet()

	val allAcceptedValues =
		relevantTags
		.map {
			ResourceLocation.parse(it.value)
		}
		.toSet()

	CompiledEntry(
		relevantTags.size,
		relevantTags,
	) { value ->
		value in allAcceptedValues
	}
}
