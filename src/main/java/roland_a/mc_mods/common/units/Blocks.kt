package roland_a.mc_mods.common.units

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Blocks<N: Comparable<N>>(val value: N): Comparable<Blocks<N>> {
    override fun compareTo(other: Blocks<N>): Int {
        return this.value.compareTo(other.value)
    }

    companion object{
        val <N: Comparable<N>> N.blocks: Blocks<N>
            get() = Blocks(this)
    }
}
