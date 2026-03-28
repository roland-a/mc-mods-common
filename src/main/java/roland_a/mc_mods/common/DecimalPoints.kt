package roland_a.mc_mods.common

import java.math.RoundingMode

@JvmInline
value class DecimalPoints(private val value: Int) {
	companion object{
		val Double.decimalPoints: DecimalPoints
			get() = (
				this
				.toString()
				.split(".")
				.last()
				.dropLastWhile { digit -> digit == '0' }.length
				.let {
					DecimalPoints(it)
				}
			)

		val Collection<Double>.decimalPoints: DecimalPoints
			get() = (
				this
				.map {
					it.decimalPoints
				}
				.maxByOrNull {
					it.value
				}
				?: DecimalPoints(0)
			)

		fun Double.toStringWithDecimalPlaces(decimalPlaces: DecimalPoints): String {
			if (this == Double.POSITIVE_INFINITY){
				return "inf"
			}
			if (this == Double.NEGATIVE_INFINITY){
				return "-inf"
			}
			if (this.isNaN()){
				return "nan"
			}

			return (
				this
				.toBigDecimal()
				.setScale(decimalPlaces.value, RoundingMode.DOWN)
				.toString()
			)
		}
	}
}
