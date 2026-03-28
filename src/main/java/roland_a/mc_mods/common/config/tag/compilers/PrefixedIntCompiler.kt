package roland_a.mc_mods.common.config.tag.compilers

import roland_a.mc_mods.common.config.tag.EntryCompiler

fun prefixedIntCompiler(prefix: String): EntryCompiler<Int> {
	return prefixedCompiler(prefix) {
		it.toIntOrNull()
	}
}
