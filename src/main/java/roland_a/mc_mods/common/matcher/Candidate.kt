@file:OptIn(InternalSerializationApi::class)

package roland_a.mc_mods.common.matcher

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import roland_a.mc_mods.common.matcher.Group.Companion.containsLabel
import roland_a.mc_mods.common.matcher.Label.Companion.toLabel
import java.util.function.IntFunction

@Serializable(with = Candidate.Serializer::class)
@JvmInline
value class Candidate(private val labels: Collection<Label>): Collection<Label> by labels{
	fun matches(activeLabels: List<ActiveLabel<*>>): Boolean {
		val anyUnsatisfied = activeLabels.any { !it.isSatisfied(this) }

		val anyUnknownLabel = this.any { l -> activeLabels.none { it.group.containsLabel(l) } }

		if (anyUnsatisfied){
			return false
		}
		if (anyUnknownLabel){
			return false
		}
		return true
	}

	override fun toString(): String {
		return labels.joinToString(separator = " ")
	}

	object Serializer: KSerializer<Candidate>{
		private val serializer = String::class.serializer()

		override val descriptor: SerialDescriptor = serializer.descriptor

		override fun serialize(encoder: Encoder, value: Candidate) {
			serializer.serialize(encoder, value.toString())
		}

		override fun deserialize(decoder: Decoder): Candidate {
			return serializer.deserialize(decoder).toCandidate()
		}
	}

	companion object{
		fun String.toCandidate(): Candidate{
			return (
				this
				.split(" ")
				.filter {
					it.isNotBlank()
				}
				.map {
					it.toLabel()
				}
				.let {
					Candidate(it)
				}
			)
		}
	}
}
