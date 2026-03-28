@file:OptIn(ExperimentalSerializationApi::class)

package roland_a.mc_mods.common.config

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy.Builtins.SnakeCase
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.Logger
import java.nio.file.Path
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.typeOf

data class ConfigLoaderBuilder<T: Any>(
	var folderPath: Path? = null,

	var configToString: ((T)->String)? = null,
	var stringToConfig: ((String)-> Result<T>)? = null,
	var default: T? = null,
	var inert: Boolean? = null,

	var logNormal: ((String)->Unit)? = null,
	var logError: ((String)->Unit)? = null,
){
	companion object{
		fun <T: Any> ConfigLoaderBuilder<T>.withDefaultConfigLocation(modId: String) {
			folderPath = FabricLoader.getInstance().configDir.resolve(modId)
		}

		fun <T: Any> ConfigLoaderBuilder<T>.withLogger(logger: Logger){
			logNormal = logger::info
			logError = logger::error
		}

		inline fun <reified T: Any> ConfigLoaderBuilder<T>.withDefaultSerialization(){
			val json = Json {
				namingStrategy = SnakeCase

				encodeDefaults = true
				prettyPrint = true
				prettyPrintIndent = "\t"

				allowComments = true
				allowTrailingComma = true
				isLenient = true
			}

			inert =
				typeOf<T>()
				.classifier
				.let {
					@Suppress("UNCHECKED_CAST")
					it as KClass<T>
				}
				.memberProperties
				.isEmpty()

			default =
				typeOf<T>()
				.classifier
				.let {
					@Suppress("UNCHECKED_CAST")
					it as? KClass<T> ?: throw IllegalArgumentException()
				}
				.let {
					it.primaryConstructor ?: throw IllegalArgumentException()
				}
				.callBy(
					mapOf()
				)

			configToString = { c ->
				json.encodeToString(c)
			}

			stringToConfig = { str ->
				runCatching {
					json.decodeFromString<T>(str)
				}
			}
		}
	}
}
