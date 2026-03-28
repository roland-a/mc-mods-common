package roland_a.mc_mods.common.config.tag

import org.jetbrains.annotations.VisibleForTesting

fun requiresKnownTag(exceptions: List<String> = listOf()): (Tag) -> Unit {
	return { tag ->
		require(tag.value in exceptions) {
			tag.unknownTagMessage()
		}
	}
}

@VisibleForTesting
fun Tag.unknownTagMessage(): String {
	return "UNKNOWN TAG INSIDE CONFIG: ${this.value}"
}
