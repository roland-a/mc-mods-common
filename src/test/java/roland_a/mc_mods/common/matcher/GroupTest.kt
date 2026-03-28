package roland_a.mc_mods.common.matcher

import net.minecraft.resources.ResourceLocation.parse
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import roland_a.mc_mods.common.Either.Companion.wrapAsLeft
import roland_a.mc_mods.common.Either.Companion.wrapAsRight
import roland_a.mc_mods.common.assertEquals
import roland_a.mc_mods.common.matcher.Group.Companion.either
import roland_a.mc_mods.common.matcher.Group.Companion.enumGroup
import roland_a.mc_mods.common.matcher.Group.Companion.idGroup
import roland_a.mc_mods.common.matcher.Group.Companion.intGroup
import roland_a.mc_mods.common.matcher.Group.Companion.intRangeGroup
import roland_a.mc_mods.common.matcher.Group.Companion.mapGroup
import roland_a.mc_mods.common.matcher.Group.Result.IN_GROUP
import roland_a.mc_mods.common.matcher.Group.Result.MATCHES
import roland_a.mc_mods.common.matcher.Group.Result.NOT_IN_GROUP
import roland_a.mc_mods.common.matcher.Label.Companion.toLabel

private class GroupTest {
	@Nested
	inner class IntGroupTest {
		val group = intGroup("prefix_")

		@Test
		fun `value matches label`() {
			group
			.eval(1, "prefix_1".toLabel())
			.assertEquals(
				MATCHES
			)
		}

		@Test
		fun `value doesn't match label`() {
			group
			.eval(1, "prefix_2".toLabel())
			.assertEquals(
				IN_GROUP
			)
		}

		@Test
		fun `null doesn't match label`() {
			group
			.eval(null, "prefix_2".toLabel())
			.assertEquals(
				IN_GROUP
			)
		}

		@Test
		fun `label doesn't belong to group`() {
			group
			.eval(1, "wrong_prefix_1".toLabel())
			.assertEquals(
				NOT_IN_GROUP
			)
		}
	}

	@Nested
	inner class IntRangeGroupTest {
		val group = intRangeGroup("prefix_")

		@Test
		fun `value matches start of range`() {
			group
			.eval(1, "prefix_1-3".toLabel())
			.assertEquals(
				MATCHES
			)
		}

		@Test
		fun `value matches middle of range`() {
			group
			.eval(2, "prefix_1-3".toLabel())
			.assertEquals(
				MATCHES
			)
		}

		@Test
		fun `value matches end of range`() {
			group
			.eval(3, "prefix_1-3".toLabel())
			.assertEquals(
				MATCHES
			)
		}

		@Test
		fun `value falls before range`() {
			group
			.eval(0, "prefix_1-3".toLabel())
			.assertEquals(
				IN_GROUP
			)
		}

		@Test
		fun `value falls after range`() {
			group
			.eval(4, "prefix_1-3".toLabel())
			.assertEquals(
				IN_GROUP
			)
		}

		@Test
		fun `value is null`() {
			group
			.eval(null, "prefix_1-3".toLabel())
			.assertEquals(
				IN_GROUP
			)
		}

		@Test
		fun `label does not belong to group`() {
			group
			.eval(1, "wrong_prefix_1-3".toLabel())
			.assertEquals(
				NOT_IN_GROUP
			)
		}
	}

	@Nested
	inner class ResourceLocationGroupTest {
		val group = idGroup

		@Test
		fun `value matches label`() {
			group
			.eval(
				parse("test:1"),
				"test:1".toLabel()
			)
			.assertEquals(
				MATCHES
			)
		}


		@Test
		fun `value doesn't match label`() {
			group
			.eval(
				parse("test:2"),
				"test:1".toLabel()
			)
			.assertEquals(
				IN_GROUP
			)
		}

		@Test
		fun `null doesn't match label`() {
			group
			.eval(
				null,
				"test:1".toLabel()
			)
			.assertEquals(
				IN_GROUP
			)
		}

		@Test
		fun `label doesn't belong to group`() {
			group
			.eval(
				parse("test:1"),
				"not_a_resource_location".toLabel()
			)
			.assertEquals(
				NOT_IN_GROUP
			)
		}
	}

	enum class E { A, B }

	@Nested
	inner class EnumGroupTest {
		val group = enumGroup<E>()

		@Test
		fun `value matches label`() {
			group
			.eval(
				E.A,
				"a".toLabel()
			)
			.assertEquals(
				MATCHES
			)
		}

		@Test
		fun `value doesn't match label`() {
			group
			.eval(
				E.B,
				"a".toLabel()
			)
			.assertEquals(
				IN_GROUP
			)
		}

		@Test
		fun `null doesn't match label`() {
			group
			.eval(
				null,
				"a".toLabel()
			)
			.assertEquals(
				IN_GROUP
			)
		}

		@Test
		fun `label doesn't belong to group`() {
			group
			.eval(
				E.A,
				"c".toLabel()
			)
			.assertEquals(
				NOT_IN_GROUP
			)
		}
	}

	@Nested
	inner class MapGroupTest {
		val group = mapGroup(
			1 to "a".toLabel(),
			2 to "b".toLabel(),
		)

		@Test
		fun `value matches label`() {
			group
			.eval(
				1,
				"a".toLabel()
			)
			.assertEquals(
				MATCHES
			)
		}

		@Test
		fun `value doesn't match label`() {
			group
			.eval(
				1,
				"b".toLabel()
			)
			.assertEquals(
				IN_GROUP
			)
		}

		@Test
		fun `null doesn't match label`() {
			group
			.eval(
				null,
				"a".toLabel()
			)
			.assertEquals(
				IN_GROUP
			)
		}

		@Test
		fun `label doesn't belong to group`() {
			group
			.eval(
				1,
				"c".toLabel()
			)
			.assertEquals(
				NOT_IN_GROUP
			)
		}
	}

	@Nested
	inner class EitherGroup {
		val group = either(
			mapGroup(
				1 to "1".toLabel(),
				2 to "2".toLabel(),
			),
			mapGroup(
				false to "f".toLabel(),
				true to "t".toLabel(),
			)
		)

		@Nested
		inner class Left {
			@Test
			fun `value matches label`() {
				group
				.eval(
					1.wrapAsLeft(),
					"1".toLabel()
				)
				.assertEquals(
					MATCHES
				)
			}

			@Test
			fun `value doesn't match label`() {
				group
				.eval(
					1.wrapAsLeft(),
					"2".toLabel()
				)
				.assertEquals(
					IN_GROUP
				)
			}

			@Test
			fun `label belongs to opposite side`() {
				group
				.eval(
					1.wrapAsLeft(),
					"t".toLabel()
				)
				.assertEquals(
					IN_GROUP
				)
			}

			@Test
			fun `value is null`() {
				group
				.eval(
					null,
					"1".toLabel()
				)
				.assertEquals(
					IN_GROUP
				)
			}

			@Test
			fun `value is null and label belongs to opposite side`() {
				group
				.eval(
					null,
					"t".toLabel()
				)
				.assertEquals(
					IN_GROUP
				)
			}
		}

		@Nested
		inner class Right {
			@Test
			fun `value matches label`() {
				group
				.eval(
					false.wrapAsRight(),
					"f".toLabel()
				)
				.assertEquals(
					MATCHES
				)
			}

			@Test
			fun `value doesn't match label`() {
				group
				.eval(
					false.wrapAsRight(),
					"t".toLabel()
				)
				.assertEquals(
					IN_GROUP
				)
			}

			@Test
			fun `label belongs to opposite side`() {
				group
				.eval(
					false.wrapAsRight(),
					"1".toLabel()
				)
				.assertEquals(
					IN_GROUP
				)
			}

			@Test
			fun `value is null`() {
				group
				.eval(
					null,
					"t".toLabel()
				)
				.assertEquals(
					IN_GROUP
				)
			}

			@Test
			fun `value is null and label belongs to opposite side`() {
				group
				.eval(
					null,
					"1".toLabel()
				)
				.assertEquals(
					IN_GROUP
				)
			}
		}

		@Test
		fun notInGroup() {
			group
			.eval(
				1.wrapAsLeft(),
				"c".toLabel()
			)
			.assertEquals(
				NOT_IN_GROUP
			)
		}
	}
}
