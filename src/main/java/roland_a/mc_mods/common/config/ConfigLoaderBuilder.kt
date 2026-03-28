@file:OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)

package roland_a.mc_mods.common.config

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy.Builtins.SnakeCase
import kotlinx.serialization.serializer
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.Logger
import roland_a.mc_mods.common.ext.useFallBackForMissingFields
import java.nio.file.Path
import kotlin.reflect.full.memberProperties

data class ConfigLoaderBuilder<T: Any>(
	var folderPath: Path? = null,

	var configToString: ((T) -> String)? = null,
	var stringToConfig: ((String) -> Result<T>)? = null,
	var default: T? = null,
	var inert: Boolean? = null,

	var logNormal: ((String) -> Unit)? = null,
	var logError: ((String) -> Unit)? = null,
) {
	companion object {
		fun <T: Any> ConfigLoaderBuilder<T>.withDefaultLocation(modId: String) {
			this.folderPath = FabricLoader.getInstance().configDir.resolve(modId)
		}

		fun <T: Any> ConfigLoaderBuilder<T>.withLogger(logger: Logger) {
			this.logNormal = logger::info
			this.logError = logger::error
		}

		inline fun <reified T: Any> ConfigLoaderBuilder<T>.withDefaultSerialization(default: T) {
			val json = Json {
				namingStrategy = SnakeCase

				encodeDefaults = true
				prettyPrint = true
				prettyPrintIndent = "\t"

				allowComments = true
				allowTrailingComma = true
				isLenient = true
			}

			this.inert =
				default::class
				.memberProperties
				.isEmpty()

			this.default = default

			this.configToString = { c ->
				json.encodeToString(c)
			}

			this.stringToConfig = { str ->
				runCatching {
					json
					.decodeFromString(
						T::class.serializer().useFallBackForMissingFields(default),
						str,
					)
				}
			}
		}
	}
}
