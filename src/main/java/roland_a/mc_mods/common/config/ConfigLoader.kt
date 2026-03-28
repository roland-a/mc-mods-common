package roland_a.mc_mods.common.config

import kotlinx.serialization.SerializationException
import roland_a.mc_mods.common.ext.orDefault
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.moveTo
import kotlin.io.path.readText
import kotlin.io.path.writeText

fun <T: Any> loadConfig(fn: ConfigLoaderBuilder<T>.() -> Unit): T {
	val (folderPath, configToString, stringToConfig, default, isInert, logNormal, logError) =
		ConfigLoaderBuilder<T>()
		.apply(fn)

	require(folderPath != null)
	require(configToString != null)
	require(stringToConfig != null)
	require(default != null)
	require(isInert != null)
	require(logNormal != null)
	require(logError != null)

	if (isInert) {
		return default
	}

	val configPath = folderPath.resolve(SubFiles.CONFIG)
	val invalidPath = folderPath.resolve(SubFiles.INVALID_CONFIG)
	val errorLogsPath = folderPath.resolve(SubFiles.ERROR_LOGS)

	val errorLogsFileContent = mutableListOf<String>()

	fun logExceptions(e: Throwable) {
		when (e) {
			is SerializationException -> {
				val message = Messages.Errors.invalidSyntax(e.message.orDefault(""))

				logError(message)
				errorLogsFileContent += message
			}

			is IllegalArgumentException -> {
				val message = Messages.Errors.invalidArgument(e.message.orDefault(""))

				logError(message)
				errorLogsFileContent += message
			}

			else -> {
				throw e
			}
		}
	}

	fun moveInvalidConfig() {
		logError(Messages.Errors.movingInvalidConfig(invalidPath))

		configPath.moveTo(invalidPath, overwrite = true)
	}

	logNormal(Messages.readingConfig(configPath))

	folderPath.createDirectories()

	//read from config
	val result =
		configPath
		.takeIf {
			it.exists()
		}
		?.readText()
		?.let(stringToConfig)
		?.getOrElse { e ->
			logExceptions(e)
			moveInvalidConfig()

			null
		}
		.orDefault(default)

	//write to config
	result
	.let(configToString)
	.also {
		configPath.writeText(it)
	}

	if (errorLogsFileContent.isNotEmpty()) {
		errorLogsPath
		.toFile()
		.writeText(
			errorLogsFileContent.joinToString("\n"),
		)
	}

	logNormal(Messages.done())

	return result
}
