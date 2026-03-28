package roland_a.mc_mods.common.ext

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject

fun <T, S> KSerializer<S>.useAsSurrogate(fromT: (T) -> S, toT: (S) -> T): KSerializer<T> {
	val base = this

	return object: KSerializer<T> {
		override val descriptor: SerialDescriptor = base.descriptor

		override fun serialize(encoder: Encoder, value: T) {
			base.serialize(encoder, fromT(value))
		}

		override fun deserialize(decoder: Decoder): T {
			return base.deserialize(decoder).let(toT)
		}
	}
}

fun <T> KSerializer<T>.useFallBackForMissingFields(default: T): DeserializationStrategy<T> {
	val base = this

	return object: DeserializationStrategy<T> by base {
		override fun deserialize(decoder: Decoder): T {
			require(decoder is JsonDecoder)

			val json = decoder.decodeJsonElement().jsonObject

			val defaultJson = decoder.json.encodeToJsonElement(base, default).jsonObject

			val merged = buildJsonObject {
				// Copy all keys from default first
				for ((key, value) in defaultJson) {
					put(key, value)
				}
				// Override with incoming values
				for ((key, value) in json) {
					put(key, value)
				}
			}

			return decoder.json.decodeFromJsonElement(base, merged)
		}
	}
}
