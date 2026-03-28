package roland_a.mc_mods.common.config.tag.compilers

import org.junit.jupiter.api.Test
import roland_a.mc_mods.common.assertEquals
import roland_a.mc_mods.common.config.tag.TaggedEntry.Companion.toTaggedEntry
import roland_a.mc_mods.common.config.tag.compilers.NullableTest.Group.A
import roland_a.mc_mods.common.config.tag.compilers.NullableTest.Group.B

private class NullableTest {
	enum class Group {
		A, B
	}

	val compiler = compilerOf<Group>().allowNull()

	@Test
	fun `succeed if base succeeds normally`() {
		compiler
		.compile(
			"a".toTaggedEntry(),
		)
		.matches(A)
		.assertEquals(true)
	}

	@Test
	fun `fails if base fails normally`() {
		compiler
		.compile(
			"a".toTaggedEntry(),
		)
		.matches(B)
		.assertEquals(false)
	}

	@Test
	fun `succeed if null and empty`() {
		compiler
		.compile(
			"".toTaggedEntry(),
		)
		.matches(null)
		.assertEquals(true)
	}

	@Test
	fun `fails if null and not empty`() {
		compiler
		.compile(
			"a".toTaggedEntry(),
		)
		.matches(null)
		.assertEquals(false)
	}
}
