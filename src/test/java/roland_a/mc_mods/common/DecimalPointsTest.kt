package roland_a.mc_mods.common

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import roland_a.mc_mods.common.DecimalPoints.Companion.decimalPoints
import roland_a.mc_mods.common.DecimalPoints.Companion.toStringWithDecimalPlaces

class DecimalPointsTest {
	@Nested
	inner class DoubleTest{
		@Test
		fun `zero decimal points`(){
			1.0
			.decimalPoints
			.assertEquals(
				DecimalPoints(0)
			)
		}

		@Test
		fun `one decimal points`(){
			1.1
			.decimalPoints
			.assertEquals(
				DecimalPoints(1)
			)
		}

		@Test
		fun `two decimal points`(){
			1.12
			.decimalPoints
			.assertEquals(
				DecimalPoints(2)
			)
		}

		@Test
		fun `negative decimal points coerce to zero`(){
			10.0
			.decimalPoints
			.assertEquals(
				DecimalPoints(0)
			)
		}

		@Test
		fun `can handle zeros between digits`(){
			1.01
			.decimalPoints
			.assertEquals(
				DecimalPoints(2)
			)
		}
	}

	@Nested
	inner class DoubleListTest{
		@Test
		fun pickHighestDecimalPoints(){
			listOf(
				1.0,
				1.2,
				1.23,
			)
			.decimalPoints
			.assertEquals(
				DecimalPoints(2)
			)
		}
	}

	@Nested
	inner class ToStringWithDecimalPointsTest{
		@Nested
		inner class Positive{
			@Test
			fun `zero decimal points`(){
				1.2
				.toStringWithDecimalPlaces(DecimalPoints(0))
				.assertEquals(
					"1"
				)
			}

			@Test
			fun `one decimal points`(){
				1.2
				.toStringWithDecimalPlaces(DecimalPoints(1))
				.assertEquals(
					"1.2"
				)
			}

			@Test
			fun `two decimal points`(){
				1.2
				.toStringWithDecimalPlaces(DecimalPoints(2))
				.assertEquals(
					"1.20"
				)
			}
		}

		@Nested
		inner class Negative{
			@Test
			fun `zero decimal points`(){
				(-1.2)
				.toStringWithDecimalPlaces(DecimalPoints(0))
				.assertEquals(
					"-1"
				)
			}

			@Test
			fun `one decimal points`(){
				(-1.2)
				.toStringWithDecimalPlaces(DecimalPoints(1))
				.assertEquals(
					"-1.2"
				)
			}

			@Test
			fun `two decimal points`(){
				(-1.2)
				.toStringWithDecimalPlaces(DecimalPoints(2))
				.assertEquals(
					"-1.20"
				)
			}
		}

		@Nested
		inner class Zero{
			@Test
			fun `zero decimal points`(){
				0.0
				.toStringWithDecimalPlaces(DecimalPoints(0))
				.assertEquals(
					"0"
				)
			}

			@Test
			fun `one decimal points`(){
				0.0
				.toStringWithDecimalPlaces(DecimalPoints(1))
				.assertEquals(
					"0.0"
				)
			}

			@Test
			fun `two decimal points`(){
				0.0
				.toStringWithDecimalPlaces(DecimalPoints(2))
				.assertEquals(
					"0.00"
				)
			}
		}

		@Test
		fun `handle NaNs`(){
			Double.NaN
			.toStringWithDecimalPlaces(DecimalPoints(3))
			.assertEquals(
				"nan"
			)
		}

		@Test
		fun `handle Positive Infinity`(){
			Double.POSITIVE_INFINITY
			.toStringWithDecimalPlaces(DecimalPoints(3))
			.assertEquals(
				"inf"
			)
		}

		@Test
		fun `handle Negative Infinity`(){
			Double.NEGATIVE_INFINITY
			.toStringWithDecimalPlaces(DecimalPoints(3))
			.assertEquals(
				"-inf"
			)
		}
	}
}
