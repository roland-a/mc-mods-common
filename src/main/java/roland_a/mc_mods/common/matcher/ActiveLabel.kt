package roland_a.mc_mods.common.matcher

import roland_a.mc_mods.common.matcher.Group.Result.MATCHES
import roland_a.mc_mods.common.matcher.Group.Result.NOT_IN_GROUP

data class ActiveLabel<T>(private val value: T?, val group: Group<T>){
	internal fun isSatisfied(candidate: Candidate): Boolean {
		val hasNoMemberOfGroup = value != null && candidate.all { group.eval(value, it) == NOT_IN_GROUP }

		val containsActiveLabel = candidate.any { group.eval(value, it) == MATCHES }

		return hasNoMemberOfGroup || containsActiveLabel
	}

	companion object{
		infix fun <T> Group<T>.withActive(value: T?): ActiveLabel<T> {
			return ActiveLabel(
				value,
				this
			)
		}

		fun <T> ActiveLabel<T>.matches(label: Label): Boolean{
			return this.group.eval(value, label) == MATCHES
		}
	}
}
