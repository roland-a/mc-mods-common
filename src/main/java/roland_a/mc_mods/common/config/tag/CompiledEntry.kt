package roland_a.mc_mods.common.config.tag

import roland_a.mc_mods.common.ext.indexOfFirstOrNull
import roland_a.mc_mods.common.ext.orDefault
import roland_a.mc_mods.common.lexicographicalComparator

data class CompiledEntry<T>(
	val ranking: List<Double>,
	val relevantTags: Set<Tag>,
	val matchesNonEmpty: (T) -> Boolean,
): Comparable<CompiledEntry<T>> {
	init {
		assert(ranking.all { it.isFinite() })
		assert(ranking.all { it >= 0.0 })
	}

	constructor(
		length: Number,
		relevantTags: Set<Tag>,
		matchesNonEmpty: (T) -> Boolean,
	): this(
		listOf(length.toDouble()),
		relevantTags,
		matchesNonEmpty,
	)

	fun matches(value: T): Boolean {
		return this.isEmpty() || this.matchesNonEmpty(value)
	}

	override fun compareTo(other: CompiledEntry<T>): Int {
		return cmp.compare(this, other)
	}

	companion object {
		fun <T> CompiledEntry<T>.isEmpty(): Boolean {
			return this.relevantTags.isEmpty()
		}

		private val cmp = run {
			val highestCategory =
				compareByDescending<CompiledEntry<*>> { c ->
					c
					.ranking
					.indexOfFirstOrNull {
						it != 0.0
					}
					.orDefault(Int.MAX_VALUE)
				}

			val numOfCategories =
				compareBy<CompiledEntry<*>> { c ->
					c.ranking.count { it != 0.0 }
				}

			val fewerTagsWithinSameCategory =
				compareByDescending<CompiledEntry<*>, List<Double>>(
					lexicographicalComparator(),
				) { c ->
					c.ranking
				}

			highestCategory.then(numOfCategories).then(fewerTagsWithinSameCategory)
		}
	}
}
