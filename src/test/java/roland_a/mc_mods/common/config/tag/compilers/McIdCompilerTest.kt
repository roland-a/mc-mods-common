package roland_a.mc_mods.common.config.tag.compilers

import net.minecraft.resources.ResourceLocation.parse
import org.junit.jupiter.api.Test
import roland_a.mc_mods.common.assertEquals
import roland_a.mc_mods.common.config.tag.CompiledEntry.Companion.isEmpty
import roland_a.mc_mods.common.config.tag.TaggedEntry.Companion.toTaggedEntry

private class McIdCompilerTest {
	val compiler = mcIdCompiler

	@Test
	fun `value matches tag`() {
		compiler
		.compile(
			"test:1".toTaggedEntry(),
		)
		.matches(
			parse("test:1"),
		)
		.assertEquals(true)
	}

	@Test
	fun `value doesn't match tag`() {
		compiler
		.compile(
			"test:1".toTaggedEntry(),
		)
		.matches(
			parse("test:2"),
		)
		.assertEquals(false)
	}

	@Test
	fun `tag is not a mcId`() {
		compiler
		.compile(
			"test_1".toTaggedEntry(),
		)
		.isEmpty()
		.assertEquals(true)
	}
}
