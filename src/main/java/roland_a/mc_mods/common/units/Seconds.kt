package roland_a.mc_mods.common.units

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Seconds<N: Comparable<N>>(val value: N): Comparable<Seconds<N>>{
    override fun compareTo(other: Seconds<N>): Int {
        return this.value.compareTo(other.value)
    }

    companion object{
        val <N: Comparable<N>> N.seconds: Seconds<N>
            get() = Seconds(this)
    }
}
