package roland_a.mc_mods.common

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private class CompareListsTest {
	@Nested
	inner class Equals {
		@Test
		fun singleton() {
			listOf(true)
			.assertEquals	(
				listOf(true),
				lexicographicalComparator(),
			)
		}

		@Test
		fun `sized-two list`() {
			listOf(true, false)
			.assertEquals(
				listOf(true, false),
				lexicographicalComparator(),
			)
		}
	}

	@Nested
	inner class NotEquals {
		@Test
		fun singleton() {
			listOf(true)
			.assertGreaterThan(
				listOf(false),
				lexicographicalComparator(),
			)
		}

		@Test
		fun `compare second indexes if first indexes are the same`() {
			listOf(true, true)
			.assertGreaterThan(
				listOf(true, false),
				lexicographicalComparator(),
			)
		}

		@Test
		fun `ignore second indexes if first indexes are different`() {
			listOf(true, false)
			.assertGreaterThan(
				listOf(false, true),
				lexicographicalComparator(),
			)
		}

		@Test
		fun `use size if all common indexes are the same`() {
			listOf(true, false, false)
			.assertGreaterThan(
				listOf(true, false),
				lexicographicalComparator(),
			)
		}
	}
}
