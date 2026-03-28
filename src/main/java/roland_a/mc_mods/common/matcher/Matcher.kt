@file:OptIn(InternalSerializationApi::class)

package roland_a.mc_mods.common.matcher

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import roland_a.mc_mods.common.lexicographicalComparator
import roland_a.mc_mods.common.matcher.ActiveLabel.Companion.matches
import roland_a.mc_mods.common.matcher.Candidate.Companion.toCandidate

@JvmInline
@Serializable
value class Matcher<T> private constructor(
	private val candidates: Map<Candidate, T>
): Map<Candidate, T> by candidates {
	constructor(
		vararg entries: Pair<String, T>
	): this(
		entries
		.toMap()
		.mapKeys {
			(k, _) -> k.toCandidate()
		}
	)

	fun getWithChosenCandidate(vararg activeLabels: ActiveLabel<*>): Pair<Collection<Label>, T>? {
		fun Collection<Label>.activatedGroupList(): List<Boolean> {
			return activeLabels.map { a -> this.any { a.matches(it) } }
		}

		return (
			this
			.candidates
			.filter {
				(k, _) -> k.matches(activeLabels.toList())
			}
			.maxWithOrNull(
				compareBy<Map.Entry<Candidate, T>> {
					it.key.size
				}
				.then(
					compareBy<Map.Entry<Collection<Label>, T>, List<Boolean>>(
						lexicographicalComparator()
					) {
						it.key.activatedGroupList()
					}
				)
			)
			?.let {
				it.key to it.value
			}
		)
	}

	operator fun get(vararg activeLabels: ActiveLabel<*>): T? {
		return getWithChosenCandidate(*activeLabels)?.second
	}
}
