package roland_a.mc_mods.common.config

import kotlinx.serialization.Serializable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import roland_a.mc_mods.common.assertConditionIsTrue
import roland_a.mc_mods.common.assertContentsAre
import roland_a.mc_mods.common.assertEquals
import roland_a.mc_mods.common.config.ConfigLoaderBuilder.Companion.withDefaultSerialization
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

private typealias Result<T> = (
	configObj: T,
	newConfigFileContent: String?,
	invalidConfigFileContent: String?,
	errorLogsFileContent: String?,
	normalLogs: List<String>,
	errorLogs: List<String>,
) -> Unit

private class LoadConfigTest {
	@Serializable
	data class Config(
		val fieldA: Int,
		val fieldB: String,
	) {
		init {
			require(fieldA >= 0) {
				"NO NEGATIVES ALLOWED"
			}
		}

		companion object {
			val default = Config(123, "abc")
		}
	}

	val basePath: Path = Paths.get(".").resolve("temp")
	val configPath: Path = basePath.resolve(SubFiles.CONFIG)
	val invalidConfigPath: Path = basePath.resolve(SubFiles.INVALID_CONFIG)
	val errorLogsPath: Path = basePath.resolve(SubFiles.ERROR_LOGS)

	inline fun <reified T: Any> String?.test(default: T, fn: Result<T>) {
		val normalLogs = mutableListOf<String>()
		val errorLogs = mutableListOf<String>()

		if (this != null) {
			basePath.createDirectories()
			configPath.writeText(this)
		}

		return (
			loadConfig {
				folderPath = basePath

				withDefaultSerialization(default)

				logNormal = normalLogs::add
				logError = errorLogs::add
			}
			.let { result ->
				val newConfigFileContent =
					configPath
					.takeIf {
						it.exists()
					}
					?.readText()

				val invalidConfigFileContent =
					invalidConfigPath
					.takeIf {
						it.exists()
					}
					?.readText()

				val errorsLogContent =
					errorLogsPath
					.takeIf {
						it.exists()
					}
					?.readText()

				fn(
					result,
					newConfigFileContent,
					invalidConfigFileContent,
					errorsLogContent,
					normalLogs,
					errorLogs,
				)
			}
		)
	}

	@AfterEach
	fun cleanup() {
		configPath.deleteIfExists()
		invalidConfigPath.deleteIfExists()
		errorLogsPath.deleteIfExists()
		basePath.deleteIfExists()
	}

	@Test
	fun `empty files loads clean config`() {
		null
		.test<Config>(
			Config.default,
		) { configObj, newConfigFileContent, invalidConfigFileContent, errorLogsFileContent, normalLogs, errorLogs ->
			configObj
			.assertEquals(Config.default)

			newConfigFileContent
			.assertEquals(
				"""
				{
					"field_a": 123,
					"field_b": "abc"
				}
				""".trimIndent(),
			)

			invalidConfigFileContent
			.assertEquals(null)

			errorLogsFileContent
			.assertEquals(null)

			normalLogs
			.assertContentsAre(
				Messages.readingConfig(configPath),
				Messages.done(),
			)

			errorLogs
			.assertContentsAre()
		}
	}

	@Test
	fun `valid configs with all fields are accepted`() {
		val content = """
			{
				"field_a": 456,
				"field_b": "def"
			}
		""".trimIndent()

		content
		.test<Config>(
			Config.default,
		) { configObj, newConfigFileContent, invalidConfigFileContent, errorLogsFileContent, normalLogs, errorLogs ->
			configObj
			.assertEquals(
				Config(456, "def"),
			)

			newConfigFileContent
			.assertEquals(content)

			invalidConfigFileContent
			.assertEquals(null)

			errorLogsFileContent
			.assertEquals(null)

			normalLogs
			.assertContentsAre(
				Messages.readingConfig(configPath),
				Messages.done(),
			)

			errorLogs
			.assertContentsAre()
		}
	}

	@Test
	fun `configs with missing field are accepted and reloads missing fields`() {
		val content = """
			{
				"field_b": "def"
			}
		""".trimIndent()

		content
		.test<Config>(
			Config.default,
		) { configObj, newConfigFileContent, invalidConfigFileContent, errorLogsFileContent, normalLogs, errorLogs ->
			configObj
			.assertEquals(
				Config.default.copy(fieldB = "def"),
			)

			newConfigFileContent
			.assertEquals(
				"""
				{
					"field_a": 123,
					"field_b": "def"
				}
				""".trimIndent(),
			)

			invalidConfigFileContent
			.assertEquals(null)

			errorLogsFileContent
			.assertEquals(null)

			normalLogs
			.assertContentsAre(
				Messages.readingConfig(configPath),
				Messages.done(),
			)

			errorLogs
			.assertContentsAre()
		}
	}

	@Test
	fun `config with invalid syntax are not accepted and reloads clean config`() {
		val content = """
			{
				"field_a": 123,
				"field_b": "abc
			}
		""".trimIndent()

		content
		.test<Config>(
			Config.default,
		) { configObj, newConfigFileContent, invalidConfigFileContent, errorLogsFileContent, normalLogs, errorLogs ->
			configObj
			.assertEquals(
				Config.default,
			)

			newConfigFileContent
			.assertEquals(
				"""
					{
						"field_a": 123,
						"field_b": "abc"
					}
				""".trimIndent(),
			)

			invalidConfigFileContent
			.assertEquals(
				content,
			)

			normalLogs
			.assertContentsAre(
				Messages.readingConfig(configPath),
				Messages.done(),
			)

			errorLogsFileContent
			.assertConditionIsTrue {
				Messages.Errors.invalidSyntax("") in it!!
			}

			errorLogs
			.also {
				it
				.size
				.assertEquals(2)
			}
			.also { (first, second) ->
				first
				.assertConditionIsTrue {
					Messages.Errors.invalidSyntax("") in it
				}

				second
				.assertEquals(
					Messages.Errors.movingInvalidConfig(invalidConfigPath),
				)
			}
		}
	}

	@Test
	fun `configs with invalid arguments are not accepted and reloads clean config`() {
		val content = """
			{
				"field_a": -1,
				"field_b": "def"
			}
		""".trimIndent()

		content
		.test<Config>(
			Config.default,
		) { configObj, newConfigFileContent, invalidConfigFileContent, errorLogsFileContent, normalLogs, errorLogs ->
			configObj
			.assertEquals(
				Config.default,
			)

			newConfigFileContent
			.assertEquals(
				"""
					{
						"field_a": 123,
						"field_b": "abc"
					}
				""".trimIndent(),
			)

			invalidConfigFileContent
			.assertEquals(
				content,
			)

			errorLogsFileContent
			.assertEquals(
				Messages.Errors.invalidArgument("NO NEGATIVES ALLOWED"),
			)

			normalLogs
			.assertContentsAre(
				Messages.readingConfig(configPath),
				Messages.done(),
			)

			errorLogs
			.assertContentsAre(
				Messages.Errors.invalidArgument("NO NEGATIVES ALLOWED"),
				Messages.Errors.movingInvalidConfig(invalidConfigPath),
			)
		}
	}

	@Test
	fun `configs with unknown arguments are not accepted and reloads clean config`() {
		val content = """
			{
				"field_a": 123,
				"field_b": "def",
				"field_c": 1.23
			}
		""".trimIndent()

		content
		.test<Config>(
			Config.default,
		) { configObj, newConfigFileContent, invalidConfigFileContent, errorLogsFileContent, normalLogs, errorLogs ->
			configObj
			.assertEquals(
				Config.default,
			)

			newConfigFileContent
			.assertEquals(
				"""
				{
					"field_a": 123,
					"field_b": "abc"
				}
				""".trimIndent(),
			)

			errorLogsFileContent
			.assertConditionIsTrue {
				Messages.Errors.invalidSyntax("") in it!!
			}

			invalidConfigFileContent
			.assertEquals(
				content,
			)

			normalLogs
			.assertContentsAre(
				Messages.readingConfig(configPath),
				Messages.done(),
			)

			//TODO create custom error messages for unknown keys
			errorLogs
			.also {
				it
				.size
				.assertEquals(2)
			}
			.also { (first, second) ->
				first
				.assertConditionIsTrue {
					Messages.Errors.invalidSyntax("") in it
				}

				second
				.assertEquals(
					Messages.Errors.movingInvalidConfig(invalidConfigPath),
				)
			}
		}
	}

	@Test
	fun `invalid config file gets overwritten`() {
		val content = """
			{
				"field_a": 123,
				"field_b": "abc
			}
		""".trimIndent()

		basePath.createDirectories()
		invalidConfigPath.writeText("overwritten text")

		content
		.test<Config>(
			Config.default,
		) { configObj, newConfigFileContent, invalidConfigFileContent, errorLogsFileContent, normalLogs, errorLogs ->
			configObj
			.assertEquals(
				Config.default,
			)

			newConfigFileContent
			.assertEquals(
				"""
				{
					"field_a": 123,
					"field_b": "abc"
				}
				""".trimIndent(),
			)

			invalidConfigFileContent
			.assertEquals(
				content,
			)

			errorLogsFileContent
			.assertConditionIsTrue {
				Messages.Errors.invalidSyntax("") in it!!
			}

			normalLogs
			.assertContentsAre(
				Messages.readingConfig(configPath),
				Messages.done(),
			)

			errorLogs
			.also {
				it
				.size
				.assertEquals(2)
			}
			.also { (first, second) ->
				first
				.assertConditionIsTrue {
					Messages.Errors.invalidSyntax("") in it
				}

				second
				.assertEquals(
					Messages.Errors.movingInvalidConfig(invalidConfigPath),
				)
			}
		}
	}

	@Serializable
	class EmptyConfig {
		override fun equals(other: Any?): Boolean {
			if (this === other) return true
			if (javaClass != other?.javaClass) return false
			return true
		}

		override fun hashCode(): Int {
			return javaClass.hashCode()
		}

	}

	@Test
	fun `empty config does nothing`() {
		null
		.test<EmptyConfig>(
			EmptyConfig(),
		) { configObj, newConfigFileContent, invalidConfigFileContent, errorLogsContentFile, normalLogs, errorLogs ->
			configObj
			.assertEquals(EmptyConfig())

			newConfigFileContent
			.assertEquals(null)

			invalidConfigFileContent
			.assertEquals(null)

			errorLogsContentFile
			.assertEquals(null)

			normalLogs
			.assertContentsAre()

			errorLogs
			.assertContentsAre()
		}
	}
}
