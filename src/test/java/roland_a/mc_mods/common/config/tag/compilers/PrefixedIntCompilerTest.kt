package roland_a.mc_mods.common.config.tag.compilers

import org.junit.jupiter.api.Test
import roland_a.mc_mods.common.assertEquals
import roland_a.mc_mods.common.config.tag.CompiledEntry.Companion.isEmpty
import roland_a.mc_mods.common.config.tag.TaggedEntry.Companion.toTaggedEntry

private class PrefixedIntCompilerTest {
	val compiler = prefixedIntCompiler("prefix_")

	@Test
	fun `tag doesn't belong to compiler`() {
		compiler
		.compile(
			"wrong_prefix_1".toTaggedEntry(),
		)
		.isEmpty()
		.assertEquals(true)
	}

	@Test
	fun `value matches tag`() {
		compiler
		.compile(
			"prefix_1".toTaggedEntry(),
		)
		.matches(1)
		.assertEquals(true)
	}

	@Test
	fun `value doesn't match tag`() {
		compiler
		.compile(
			"prefix_2".toTaggedEntry(),
		)
		.matches(1)
		.assertEquals(false)
	}
}
