package roland_a.mc_mods.common.matcher

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import roland_a.mc_mods.common.assertEquals
import roland_a.mc_mods.common.matcher.ActiveLabel.Companion.withActive
import roland_a.mc_mods.common.matcher.Group.Companion.enumGroup
import roland_a.mc_mods.common.matcher.MatcherTest.Companion.Group1.TEST_A
import roland_a.mc_mods.common.matcher.MatcherTest.Companion.Group2.TEST_X
import roland_a.mc_mods.common.matcher.MatcherTest.Companion.Group2.TEST_Y
import roland_a.mc_mods.common.matcher.MatcherTest.Companion.Group2.TEST_Z

private class MatcherTest {
	companion object{
		val json = Json {
			prettyPrint = true
			prettyPrintIndent = "\t"
		}

		enum class Group1{
			TEST_A,
			TEST_B,;

			companion object{
				val group = enumGroup<Group1>()
			}
		}

		enum class Group2{
			TEST_X,
			TEST_Y,
			TEST_Z,;

			companion object{
				val group = enumGroup<Group2>()
			}
		}
	}

	@Test
	fun succeeds() {
		Matcher(
			"test_a" to "X",
		)
		.get(
			Group1.group withActive TEST_A,
		)
		.assertEquals(
			"X"
		)
	}

	@Test
	fun `fails if one required group does not have required label`() {
		Matcher(
			"test_a test_x" to "X"
		)
		.get(
			Group1.group withActive TEST_A,
			Group2.group withActive TEST_Y,
		)
		.assertEquals(null)
	}

	@Test
	fun `succeeds if at least one active label is present in required group`() {
		Matcher(
			"test_x test_y" to "X"
		)
		.get(
			Group2.group withActive TEST_Y
		)
		.assertEquals("X")
	}

	@Test
	fun `succeeds if group is not required`() {
		Matcher(
			"test_a" to "A"
		)
		.get(
			Group1.group withActive TEST_A,
			Group2.group withActive TEST_Z,
		)
		.assertEquals("A")
	}

	@Test
	fun `fails if contains label not part of any group`() {
		Matcher(
			"test_a unknown_label" to "x",
		)
		.get(
			Group1.group withActive TEST_A,
		)
		.assertEquals(
			null
		)
	}

	@Test
	fun moreLabelsWin() {
		Matcher(
			"test_a" to "broad",
			"test_a test_x" to "specific"
		)
		.get(
			Group1.group withActive TEST_A,
			Group2.group withActive TEST_X,
		)
		.assertEquals(
			"specific"
		)

		Matcher(
			"test_a test_x" to "specific",
			"test_a" to "broad",
		)
		.get(
			Group1.group withActive TEST_A,
			Group2.group withActive TEST_X,
		)
		.assertEquals(
			"specific"
		)
	}

	@Test
	fun higherLabelsWin() {
		Matcher(
			"test_a" to "higher",
			"test_x" to "lower",
		)
		.get(
			Group1.group withActive TEST_A,
			Group2.group withActive TEST_X,
		)
		.assertEquals(
			"higher"
		)

		Matcher(
			"test_x" to "lower",
			"test_a" to "higher",
		)
		.get(
			Group1.group withActive TEST_A,
			Group2.group withActive TEST_X,
		)
		.assertEquals(
			"higher"
		)
	}

	@Test
	fun `properly serializes`(){
		Matcher(
			"test_a" to "x",
			"test_a test_b" to "y",
		)
		.let {
			json
			.encodeToString(it)
		}
		.assertEquals(
			"""
			{
				"test_a": "x",
				"test_a test_b": "y"
			}
			""".trimIndent()
		)
	}

	@Test
	fun `properly deserializes`(){
		"""
		{
			"test_a": "x",
			"test_a test_b": "y"
		}
		"""
		.trimIndent()
		.let {
			json
			.decodeFromString<Matcher<String>>(it)
		}
		.assertEquals(
			Matcher(
				"test_a" to "x",
				"test_a test_b" to "y",
			)
		)
	}
}