package roland_a.mc_mods.common.units

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Ticks<T: Comparable<T>>(val value: T): Comparable<Ticks<T>>{
    override fun compareTo(other: Ticks<T>): Int {
        return this.value.compareTo(other.value)
    }

    companion object {
        val <T: Comparable<T>> T.ticks: Ticks<T>
            get() = Ticks(this)

        val Seconds<Double>.ticks: Ticks<Double>
            get() = (this.value * 20).ticks
    }
}
