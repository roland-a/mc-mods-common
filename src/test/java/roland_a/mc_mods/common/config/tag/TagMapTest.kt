package roland_a.mc_mods.common.config.tag

import org.junit.jupiter.api.Test
import roland_a.mc_mods.common.assertContentsAre
import roland_a.mc_mods.common.assertEquals
import roland_a.mc_mods.common.config.tag.EntryCompiler.Companion.compileAll
import roland_a.mc_mods.common.config.tag.Tag.Companion.toTag
import roland_a.mc_mods.common.config.tag.TaggedEntry.Companion.toTaggedEntry
import roland_a.mc_mods.common.config.tag.TaggedMap.Companion.computeValue
import roland_a.mc_mods.common.config.tag.compilers.categories
import roland_a.mc_mods.common.config.tag.compilers.map
import roland_a.mc_mods.common.config.tag.compilers.prefixedIntCompiler
import roland_a.mc_mods.common.ext.mapLeft
import roland_a.mc_mods.common.ext.permutations

private class TagMapTest {
	data class Input(
		val a: Int,
		val b: Int,
		val c: Int,
	)

	val compiler = categories<Input>(
		prefixedIntCompiler("a")
		.map {
			it.a
		},

		prefixedIntCompiler("b")
		.map {
			it.b
		},

		prefixedIntCompiler("c")
		.map {
			it.c
		},
	)

	fun permutatedMatcher(vararg entries: Pair<String, String>, fn: (TaggedMap<Input, String>, List<Tag>) -> Unit) {
		entries
		.toList()
		.mapLeft {
			it.toTaggedEntry()
		}
		.permutations()
		.map {
			val unknownTags = mutableListOf<Tag>()

			compiler
			.compileAll(it) { t ->
				unknownTags.add(t)
			} to
			unknownTags
		}
		.forEach { (map, unknownTags) ->
			fn(map, unknownTags)
		}
	}

	@Test
	fun `succeeds when all categories matches`() {
		permutatedMatcher(
			"a1 b1 c1" to "winner",
		) { map, _ ->
			map
			.computeValue(
				Input(
					a = 1,
					b = 1,
					c = 1,
				),
			)
			.assertEquals("winner")
		}
	}

	@Test
	fun `fails when not all categories matches`() {
		permutatedMatcher(
			"a1, b1, c1" to "loser",
		) { map, _ ->
			map
			.computeValue(
				Input(
					a = 1,
					b = 1,
					c = 2,
				),
			)
			.assertEquals(null)
		}
	}

	@Test
	fun `fails with unknown tag`() {
		permutatedMatcher(
			"a1 b1 c1 unknown_label" to "loser",
		) { map, unknownTags ->
			map
			.computeValue(
				Input(
					a = 1,
					b = 1,
					c = 1,
				),
			)
			.assertEquals(null)

			unknownTags
			.assertContentsAre(
				"unknown_label".toTag(),
			)
		}
	}

	// case 1
	@Test
	fun `entries with the highest category wins`() {
		permutatedMatcher(
			"a1" to "winner",
			"b1" to "loser",
		) { map, _ ->
			map
			.computeValue(
				Input(
					a = 1,
					b = 1,
					c = 1,
				),
			)
			.assertEquals("winner")
		}
	}

	// case 2
	@Test
	fun `entries with more categories win`() {
		permutatedMatcher(
			"a1 b1" to "winner",
			"a1" to "loser",
		) { map, _ ->
			map
			.computeValue(
				Input(
					a = 1,
					b = 1,
					c = 1,
				),
			)
			.assertEquals("winner")
		}
	}

	//case 3
	@Test
	fun `entries with fewer tags within same category wins`() {
		permutatedMatcher(
			"a1" to "winner",
			"a1 a2" to "loser",
		) { map, _ ->
			map
			.computeValue(
				Input(
					a = 1,
					b = 1,
					c = 1,
				),
			)
			.assertEquals("winner")
		}
	}

	@Test
	fun `case 1 beats case 2`() {
		permutatedMatcher(
			"a1" to "winner",
			"b1 c1" to "loser",
		) { map, _ ->
			map
			.computeValue(
				Input(
					a = 1,
					b = 1,
					c = 1,
				),
			)
			.assertEquals("winner")
		}
	}

	@Test
	fun `case 1 beats case 3`() {
		permutatedMatcher(
			"a1 b1 b2" to "winner",
			"b1" to "loser",
		) { map, _ ->
			map
			.computeValue(
				Input(
					a = 1,
					b = 1,
					c = 1,
				),
			)
			.assertEquals("winner")
		}
	}

	@Test
	fun `case 2 beats case 3`() {
		permutatedMatcher(
			"a1 a2 b1" to "winner",
			"a1" to "loser",
		) { map, _ ->
			map
			.computeValue(
				Input(
					a = 1,
					b = 1,
					c = 1,
				),
			)
			.assertEquals("winner")
		}
	}
}
