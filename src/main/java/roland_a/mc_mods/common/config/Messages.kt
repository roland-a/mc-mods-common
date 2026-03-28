package roland_a.mc_mods.common.config

import java.nio.file.Path

internal object Messages {
	fun readingConfig(path: Path) =
		"Attempting to read config at $path"

	fun done() =
		"Done reading or creating config"

	object Errors {
		fun invalidSyntax(message: String) =
			"CANNOT PARSE CONFIG DUE TO INVALID SYNTAX:\n$message"

		fun invalidArgument(message: String) =
			"CANNOT PARSE CONFIG DUE TO INVALID VALUE:\n$message"

		fun movingInvalidConfig(newPath: Path) =
			"MOVING INVALID CONFIG to $newPath"
	}
}
