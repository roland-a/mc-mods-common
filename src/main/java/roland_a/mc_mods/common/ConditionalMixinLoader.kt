package roland_a.mc_mods.common

import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

fun conditionalMixinLoader(fn: ConditionalMixinLoaderBuilder.() -> Unit): IMixinConfigPlugin {
	val builder =
		ConditionalMixinLoaderBuilder()
		.apply(fn)

	return object: IMixinConfigPlugin {
		override fun shouldApplyMixin(targetClassName: String?, mixinClassName: String?): Boolean {
			for ((`package`, condition) in builder.packages) {
				val mixinPackageName =
					mixinClassName
					?.split(".")
					?.dropLast(1)
					?.joinToString(".")

				if (`package`.name == mixinPackageName) {
					if (!condition()) {
						return false
					}
				}
			}

			for ((`class`, condition) in builder.classes) {
				if (`class`.jvmName == mixinClassName) {
					if (!condition()) {
						return false
					}
				}
			}

			return true
		}

		//region boilerplate
		override fun onLoad(mixinPackage: String?) {}

		override fun getRefMapperConfig(): String? {
			return null
		}

		override fun acceptTargets(
			myTargets: Set<String?>?,
			otherTargets: Set<String?>?,
		) {
		}

		override fun getMixins(): List<String?>? {
			return null
		}

		override fun preApply(
			targetClassName: String?,
			targetClass: ClassNode?,
			mixinClassName: String?,
			mixinInfo: IMixinInfo?,
		) {
		}

		override fun postApply(
			targetClassName: String?,
			targetClass: ClassNode?,
			mixinClassName: String?,
			mixinInfo: IMixinInfo?,
		) {
		}
		//endregion
	}
}

data class ConditionalMixinLoaderBuilder(
	val packages: MutableMap<Package, () -> Boolean> = mutableMapOf(),
	val classes: MutableMap<KClass<*>, () -> Boolean> = mutableMapOf(),
) {
	companion object {
		fun ConditionalMixinLoaderBuilder.add(vararg classes: KClass<*>, condition: () -> Boolean) {
			classes.forEach {
				this.classes += it to condition
			}
		}

		fun ConditionalMixinLoaderBuilder.add(vararg packages: Package, condition: () -> Boolean) {
			packages.forEach {
				this.packages += it to condition
			}
		}
	}
}
