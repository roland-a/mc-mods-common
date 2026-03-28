package roland_a.mc_mods.common.config.tag

@JvmInline
value class Tag private constructor(val value: String) {
	init {
		require(this.value.isNotEmpty())
		require(this.value.all { !it.isWhitespace() })
		require(this.value.all { !it.isUpperCase() })
	}

	companion object {
		fun String.toTag(): Tag {
			return Tag(this.lowercase())
		}
	}
}
