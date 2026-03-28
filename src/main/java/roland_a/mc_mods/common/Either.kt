package roland_a.mc_mods.common

sealed interface Either<out L, out R> {
	val left: L?
	val right: R?

	data class Left<L>(override val left: L): Either<L, Nothing> {
		override val right: Nothing? = null
	}

	data class Right<R>(override val right: R): Either<Nothing, R> {
		override val left: Nothing? = null
	}

	companion object {
		fun <L, R, RR> Either<L, R>.map(left: (L) -> RR, right: (R) -> RR): RR {
			return when (this) {
				is Left<L> -> left(this.left)
				is Right<R> -> right(this.right)
			}
		}

		fun <T> T.asLeft(): Left<T> {
			return Left(this)
		}

		fun <T> T.asRight(): Right<T> {
			return Right(this)
		}
	}
}
