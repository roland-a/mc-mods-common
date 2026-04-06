package roland_a.mc_mods.common.matcher

import net.minecraft.resources.Identifier
import roland_a.mc_mods.common.Either
import roland_a.mc_mods.common.Either.Companion.map
import roland_a.mc_mods.common.matcher.Label.Companion.contains
import roland_a.mc_mods.common.matcher.Label.Companion.length
import roland_a.mc_mods.common.matcher.Label.Companion.splitIntoTwo
import roland_a.mc_mods.common.matcher.Label.Companion.substring
import roland_a.mc_mods.common.matcher.Label.Companion.toIntOrNull
import roland_a.mc_mods.common.matcher.Label.Companion.toLabel

fun interface Group<T> {
	fun eval(value: T?, label: Label): Result

	enum class Result{
		MATCHES,
		IN_GROUP,
		NOT_IN_GROUP;
	}

	companion object{
		fun <T> Group<T>.containsLabel(label: Label): Boolean{
			return this.eval(null, label) == Result.IN_GROUP
		}

		val idGroup = Group<Identifier> { value, label->
			if (value.toString().toLabel() == label){
				Result.MATCHES
			}
			else if (label.contains(":")){
				Result.IN_GROUP
			}
			else {
				Result.NOT_IN_GROUP
			}
		}

		fun <T> extractedGroup(prefix: String, extract: (Label)->T?): Group<T>{
			return { value, label ->
				val left =
					label
					.substring(0, prefix.length)

				val right =
					label
					.substring(prefix.length, label.length)
					?.let(extract)

				if (left != prefix.toLabel()){
					Result.NOT_IN_GROUP
				}
				else if (right == null) {
					Result.NOT_IN_GROUP
				}
				else if (right != value){
					Result.IN_GROUP
				}
				else {
					Result.MATCHES
				}
			}
		}

		fun <T> extractedRangeGroup(prefix: String, extract: (Label)->T?, inRange: (T,Pair<T, T>)->Boolean): Group<T>{
			return { value, label ->
				val left = label.substring(0, prefix.length)

				val (start, end) =
					label
					.substring(prefix.length, label.length)
					?.splitIntoTwo(
						"-"
					)
					?.let { (l, r) ->
						extract(l) to extract(r)
					}
					?: (null to null)

				if (left != prefix.toLabel()){
					Result.NOT_IN_GROUP
				}
				else if (start == null) {
					Result.NOT_IN_GROUP
				}
				else if (end == null) {
					Result.NOT_IN_GROUP
				}
				else if (value != null && inRange(value, Pair(start, end))){
					Result.MATCHES
				}
				else {
					Result.IN_GROUP
				}
			}
		}

		fun intGroup(prefix: String): Group<Int> {
			return extractedGroup(prefix){
				it.toIntOrNull()
			}
		}

		fun intRangeGroup(prefix: String): Group<Int> {
			return extractedRangeGroup(
				prefix,
				{
					it.toIntOrNull()
				},
				{ v, (s, e) ->
					v in s..e
				}
			)
		}

		inline fun <reified T: Enum<T>> enumGroup(): Group<T> {
			return { value, label ->
				if (value?.name?.toLabel() == label){
					Result.MATCHES
				}
				else if (label in enumValues<T>().map { it.name.toLabel() }){
					Result.IN_GROUP
				}
				else {
					Result.NOT_IN_GROUP
				}
			}
		}

		fun <T> mapGroup(entries: Map<T, Label>): Group<T> {
			return { value, label ->
				if (entries[value] == label){
					Result.MATCHES
				}
				else if (label in entries.values){
					Result.IN_GROUP
				}
				else {
					Result.NOT_IN_GROUP
				}
			}
		}

		fun <T> mapGroup(vararg entries: Pair<T, Label>): Group<T> {
			return mapGroup(entries.toMap())
		}

		fun <L, R> either(left: Group<L>, right: Group<R>): Group<Either<L, R>>{
			return { value, label ->
				val matches =
					value
					?.map(
						{
							left.eval(it, label)
						},
						{
							right.eval(it, label)
						}
					)
					.let {
						it == Result.MATCHES
					}

				val inGroup = left.eval(null, label) == Result.IN_GROUP || right.eval(null, label) == Result.IN_GROUP

				if (matches){
					Result.MATCHES
				}
				else if (inGroup){
					Result.IN_GROUP
				}
				else {
					Result.NOT_IN_GROUP
				}
			}
		}
	}
}
