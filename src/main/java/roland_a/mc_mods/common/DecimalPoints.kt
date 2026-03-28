package roland_a.mc_mods.common

import roland_a.mc_mods.common.ext.orDefault
import java.math.RoundingMode

@JvmInline
value class DecimalPoints internal constructor(private val value: Int) {
	companion object {
		fun Double.decimalPoints(): DecimalPoints {
			return (
				this
				.toString()
				.split(".")
				.last()
				.dropLastWhile { digit ->
					digit == '0'
				}
				.length
				.let {
					DecimalPoints(it)
				}
			)
		}

		fun Collection<Double>.decimalPoints(): DecimalPoints {
			return (
				this
				.map {
					it.decimalPoints()
				}
				.maxByOrNull {
					it.value
				}
				.orDefault(
					DecimalPoints(0),
				)
			)
		}

		fun Double.toStringWithDecimalPlaces(decimalPlaces: DecimalPoints): String {
			if (this == Double.POSITIVE_INFINITY) {
				return "inf"
			}
			if (this == Double.NEGATIVE_INFINITY) {
				return "-inf"
			}
			if (this.isNaN()) {
				return "nan"
			}

			return (
				this
				.toBigDecimal()
				.setScale(
					decimalPlaces.value,
					RoundingMode.DOWN,
				)
				.toString()
			)
		}
	}
}
