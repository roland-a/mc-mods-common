@file:Suppress("SameParameterValue")

package roland_a.mc_mods.common

import org.junit.jupiter.api.Assertions
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

fun <T> T.assertEquals(expected: T): T {
	Assertions.assertEquals(expected, this)

	return this
}

fun <T> List<T>.assertContentsAre(vararg expectedContent: T): List<T> {
	Assertions.assertEquals(expectedContent.toList(), this)

	return this
}

fun <T> T.assertConditionIsTrue(condition: (T) -> Boolean): T {
	Assertions.assertEquals(true, condition(this), "CONDITION NOT TRUE WITH $this")

	return this
}

fun <T> T.assertGreaterThan(other: T, cmp: Comparator<T>) {
	cmp
	.compare(
		this,
		other,
	)
	.assertEquals(1)

	cmp
	.compare(
		other,
		this,
	)
	.assertEquals(-1)
}

fun <T> T.assertEquals(other: T, cmp: Comparator<T>) {
	cmp
	.compare(
		this,
		other,
	)
	.assertEquals(0)

	cmp
	.compare(
		other,
		this,
	)
	.assertEquals(0)
}

inline fun <reified T: Throwable> assertThrowsMessage(message: String, noinline fn: () -> Unit) {
	val clazz =
		typeOf<T>()
		.let {
			@Suppress("UNCHECKED_CAST")
			it.classifier as KClass<T>
		}
		.java

	Assertions.assertThrows(clazz, fn, message)
}
