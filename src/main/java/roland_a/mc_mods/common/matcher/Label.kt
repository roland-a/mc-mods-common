package roland_a.mc_mods.common.matcher

@JvmInline
value class Label private constructor(val value: String){
	override fun toString(): String {
		return value
	}

	companion object{
		fun String.toLabel(): Label{
			return Label(this.lowercase())
		}

		fun Label.toIntOrNull(): Int? {
			return value.toIntOrNull()
		}

		fun Label.splitIntoTwo(delimiter: String): Pair<Label, Label>? {
			return (
				this
				.value
				.split(delimiter, limit = 2)
				.takeIf {
					it.size == 2
				}
				?.let { (l, r) ->
					l.toLabel() to r.toLabel()
				}
			)
		}

		fun Label.substring(startIndex: Int, endIndex: Int): Label? {
			return (
				if (startIndex in 0..value.length && endIndex in startIndex..value.length) {
					value.substring(startIndex, endIndex).toLabel()
				} else {
					null
				}
			)
		}

		fun Label.contains(s: String): Boolean {
			return this.value.contains(s)
		}

		val Label.length: Int
			get() = this.value.length
	}
}
