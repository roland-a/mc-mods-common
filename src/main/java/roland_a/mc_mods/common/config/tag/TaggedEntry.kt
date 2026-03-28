@file:OptIn(InternalSerializationApi::class)

package roland_a.mc_mods.common.config.tag

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import roland_a.mc_mods.common.config.tag.Tag.Companion.toTag
import roland_a.mc_mods.common.ext.useAsSurrogate
import kotlinx.serialization.serializer as serializerOf

@Serializable(with = TaggedEntry.Serializer::class)
@JvmInline
value class TaggedEntry private constructor(val values: Collection<Tag>) {
	override fun toString(): String {
		return this.values.joinToString(separator = " ")
	}

	class Serializer: KSerializer<TaggedEntry> by (
		serializerOf<String>()
		.useAsSurrogate(
			{
				it.toString()
			},
			{
				it.toTaggedEntry()
			},
		)
	)

	companion object {
		fun String.toTaggedEntry(): TaggedEntry {
			return (
				this
				.split(
					"\\s+".toRegex(),
				)
				.filter {
					it.isNotBlank()
				}
				.map {
					it.toTag()
				}
				.let {
					TaggedEntry(it)
				}
			)
		}
	}
}
