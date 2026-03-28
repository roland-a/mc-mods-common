package roland_a.mc_mods.common.config

import kotlinx.serialization.Serializable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import roland_a.mc_mods.common.assertConditionIsTrue
import roland_a.mc_mods.common.assertContentEquals
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
	normalLogs: List<String>,
	errorLogs: List<String>
)->Unit

private class LoadConfigTest {
	@Suppress("PropertyName")
	@Serializable
	data class Config(
		val field_a: Int=123,
		val field_b: String="abc"
	){
		init {
			require(field_a >= 0){
				"NO NEGATIVES ALLOWED"
			}
		}
	}

	val basePath: Path = Paths.get(".").resolve("temp")
	val configPath: Path = basePath.resolve(SubFiles.CONFIG)
	val invalidConfigPath: Path = basePath.resolve(SubFiles.INVALID_CONFIG)

	inline fun <reified T: Any> String?.test(fn: Result<T>){
		val normalLogs = mutableListOf<String>()
		val errorLogs = mutableListOf<String>()

		if (this != null){
			basePath.createDirectories()
			configPath.writeText(this)
		}

		return (
			loadConfig<T> {
				folderPath = basePath

				withDefaultSerialization()

				logNormal = normalLogs::add
				logError = errorLogs::add
			}
			.let { result->
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

				fn(
					result,
					newConfigFileContent,
					invalidConfigFileContent,
					normalLogs,
					errorLogs,
				)
			}
		)
	}

	@AfterEach
	fun cleanup(){
		configPath.deleteIfExists()
		invalidConfigPath.deleteIfExists()
		basePath.deleteIfExists()
	}

	@Test
	fun `empty files loads clean config`(){
		null
		.test<Config> { configObj, newConfigFileContent, invalidConfigFileContent, normalLogs, errorLogs ->
			configObj
			.assertEquals(Config())

			newConfigFileContent
			.assertEquals(
				"""
				{
					"field_a": 123,
					"field_b": "abc"
				}
				""".trimIndent()
			)

			invalidConfigFileContent
			.assertEquals(null)

			normalLogs
			.assertContentEquals(
				Messages.readingConfig(configPath),
				Messages.done()
			)

			errorLogs
			.assertContentEquals()
		}
	}

	@Test
	fun `valid configs with all fields are accepted`(){
		val content = """
			{
				"field_a": 456,
				"field_b": "def"
			}
		""".trimIndent()

		content
		.test<Config> { configObj, newConfigFileContent, invalidConfigFileContent, normalLogs, errorLogs->
			configObj
			.assertEquals(
				Config(456, "def")
			)

			newConfigFileContent
			.assertEquals(content)

			invalidConfigFileContent
			.assertEquals(null)

			normalLogs
			.assertContentEquals(
				Messages.readingConfig(configPath),
				Messages.done()
			)

			errorLogs
			.assertContentEquals()
		}
	}

	@Test
	fun `configs with missing field are accepted and reloads clean config`(){
		val content = """
			{
				"field_b": "def"
			}
		""".trimIndent()

		content
		.test<Config> { configObj, newConfigFileContent, invalidConfigFileContent, normalLogs, errorLogs ->
			configObj
			.assertEquals(
				Config(field_b = "def")
			)

			newConfigFileContent
			.assertEquals(
				"""
				{
					"field_a": 123,
					"field_b": "def"
				}
				""".trimIndent()
			)

			invalidConfigFileContent
			.assertEquals(
				null
			)

			normalLogs
			.assertContentEquals(
				Messages.readingConfig(configPath),
				Messages.done()
			)

			errorLogs
			.assertContentEquals()
		}
	}

	@Test
	fun `config with invalid syntax are not accepted and reloads clean config`(){
		val content = """
			{
				"field_a": 123,
				"field_b": "abc
			}
		""".trimIndent()

		content
		.test<Config> { configObj, newConfigFileContent, invalidConfigFileContent, normalLogs, errorLogs->
			configObj
			.assertEquals(
				Config()
			)

			newConfigFileContent
			.assertEquals(
				"""
				{
					"field_a": 123,
					"field_b": "abc"
				}
				""".trimIndent()
			)

			invalidConfigFileContent
			.assertEquals(
				content
			)

			normalLogs
			.assertContentEquals(
				Messages.readingConfig(configPath),
				Messages.done()
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
					Messages.Errors.movingInvalidConfig(invalidConfigPath)
				)
			}
		}
	}

	@Test
	fun `configs with invalid arguments are not accepted and reloads clean config`(){
		val content = """
			{
				"field_a": -1,
				"field_b": "def"
			}
		""".trimIndent()

		content
		.test<Config> { configObj, newConfigFileContent, invalidConfigFileContent, normalLogs, errorLogs->
			configObj
			.assertEquals(
				Config()
			)

			newConfigFileContent
			.assertEquals(
				"""
				{
					"field_a": 123,
					"field_b": "abc"
				}
				""".trimIndent()
			)

			invalidConfigFileContent
			.assertEquals(
				content
			)

			normalLogs
			.assertContentEquals(
				Messages.readingConfig(configPath),
				Messages.done(),
			)

			errorLogs
			.assertContentEquals(
				Messages.Errors.invalidArgument("NO NEGATIVES ALLOWED"),
				Messages.Errors.movingInvalidConfig(invalidConfigPath),
			)
		}
	}

	@Test
	fun `configs with unknown arguments are not accepted and reloads clean config`(){
		val content = """
			{
				"field_a": 123,
				"field_b": "def",
				"field_c": 1.23
			}
		""".trimIndent()

		content
		.test<Config> { configObj, newConfigFileContent, invalidConfigFileContent, normalLogs, errorLogs->
			configObj
			.assertEquals(
				Config()
			)

			newConfigFileContent
			.assertEquals(
				"""
				{
					"field_a": 123,
					"field_b": "abc"
				}
				""".trimIndent()
			)

			invalidConfigFileContent
			.assertEquals(
				content
			)

			normalLogs
			.assertContentEquals(
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
					Messages.Errors.movingInvalidConfig(invalidConfigPath)
				)
			}
		}
	}

	@Test
	fun `invalid config file gets overwritten`(){
		val content = """
			{
				"field_a": 123,
				"field_b": "abc
			}
		""".trimIndent()

		basePath.createDirectories()
		invalidConfigPath.writeText("sdjskdj")

		content
		.test<Config> { configObj, newConfigFileContent, invalidConfigFileContent, normalLogs, errorLogs->
			configObj
			.assertEquals(
				Config()
			)

			newConfigFileContent
			.assertEquals(
				"""
				{
					"field_a": 123,
					"field_b": "abc"
				}
				""".trimIndent()
			)

			invalidConfigFileContent
			.assertEquals(
				content
			)

			normalLogs
			.assertContentEquals(
				Messages.readingConfig(configPath),
				Messages.done()
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
					Messages.Errors.movingInvalidConfig(invalidConfigPath)
				)
			}
		}
	}

	@Serializable
	class EmptyConfig{
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
	fun `empty config does nothing`(){
		null
		.test<EmptyConfig> { configObj, newConfigFileContent, invalidConfigFileContent, normalLogs, errorLogs ->
			configObj.assertEquals(EmptyConfig())

			newConfigFileContent.assertEquals(null)

			invalidConfigFileContent.assertEquals(null)

			normalLogs.assertContentEquals()

			errorLogs.assertContentEquals()
		}
	}
}
