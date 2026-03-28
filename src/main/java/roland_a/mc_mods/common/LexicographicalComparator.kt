package roland_a.mc_mods.common

import kotlin.math.min

fun <T: Comparable<T>> lexicographicalComparator(): Comparator<List<T>>{
	return Comparator<List<T>> { o1, o2 ->
		for (i in 0..<min(o1.size, o2.size)) {
			val elem1 = o1[i]
			val elem2 = o2[i]

			compareValues(elem1, elem2)
			.let {
				if (it != 0) return@Comparator it
			}
		}

		compareValues(o1.size, o2.size)
	}
}
