package roland_a.mc_mods.common.config.tag.compilers

import org.junit.jupiter.api.Test
import roland_a.mc_mods.common.assertEquals
import roland_a.mc_mods.common.config.tag.CompiledEntry.Companion.isEmpty
import roland_a.mc_mods.common.config.tag.Tag.Companion.toTag
import roland_a.mc_mods.common.config.tag.TaggedEntry.Companion.toTaggedEntry

private class MapCompilerTest {
	object X
	object Y

	val compiler = mapCompiler(
		mapOf(
			X to "x".toTag(),
			Y to "y".toTag(),
		),
	)

	@Test
	fun `value matches tag`() {
		compiler
		.compile(
			"x".toTaggedEntry(),
		)
		.matches(X)
		.assertEquals(true)
	}

	@Test
	fun `value doesn't match tag`() {
		compiler
		.compile(
			"x".toTaggedEntry(),
		)
		.matches(Y)
		.assertEquals(false)
	}

	@Test
	fun `tag is not a mcId`() {
		compiler
		.compile(
			"z".toTaggedEntry(),
		)
		.isEmpty()
		.assertEquals(true)
	}
}
