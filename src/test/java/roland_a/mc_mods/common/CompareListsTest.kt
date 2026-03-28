package roland_a.mc_mods.common

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private class CompareListsTest {
	@Nested
	inner class Equals{
		@Test
		fun `equal singleton`(){
			listOf(true)
			.assertEquals(
				listOf(true),
				lexicographicalComparator()
			)
		}

		@Test
		fun `equal sized-two list`(){
			listOf(true, false)
			.assertEquals(
				listOf(true, false),
				lexicographicalComparator()
			)
		}
	}

	@Nested
	inner class NotEquals{
		@Test
		fun singleton(){
			listOf(true)
			.assertGreaterThan(
				listOf(false),
				lexicographicalComparator()
			)
		}

		@Test
		fun `use second index if first index is the same`(){
			listOf(true, true)
			.assertGreaterThan(
				listOf(true, false),
				lexicographicalComparator()
			)
		}

		@Test
		fun `ignore second index if first index are different`(){
			listOf(true, false)
			.assertGreaterThan(
				listOf(false, true),
				lexicographicalComparator()
			)
		}

		@Test
		fun `use size if all index are the same`(){
			listOf(true, false, false)
			.assertGreaterThan(
				listOf(true, false),
				lexicographicalComparator()
			)
		}
	}
}
