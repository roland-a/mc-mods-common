package roland_a.mc_mods.common

import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

class ConditionalMixinLoader private constructor(
	private val packages: Map<Package, ()->Boolean> = mapOf(),
	private val classes: Map<KClass<*>, ()->Boolean> = mapOf(),
): IMixinConfigPlugin {
	constructor(): this(mapOf(), mapOf())

	fun add(vararg classes: KClass<*>, condition: () -> Boolean): ConditionalMixinLoader {
		return ConditionalMixinLoader(
			this.packages,

			this
			.classes
			.toMutableMap()
			.also {
				for (`class` in classes) {
					it[`class`] = condition
				}
			}
		)
	}

	fun add(vararg packages: Package, condition: () -> Boolean): ConditionalMixinLoader {
		return ConditionalMixinLoader(
			this.packages
			.toMutableMap()
			.also {
				for (`package` in packages) {
					it[`package`] = condition
				}
			},

			this.classes
		)
	}

	override fun shouldApplyMixin(targetClassName: String?, mixinClassName: String?): Boolean {
		for ((`package`, condition) in packages){
			val mixinPackageName =
				mixinClassName
				?.split(".")
				?.dropLast(1)
				?.joinToString(".")

			if (`package`.name == mixinPackageName){
				if (!condition()){
					return false
				}
			}
		}

		for ((`class`, condition) in classes){
			if (`class`.jvmName == mixinClassName){
				if (!condition()){
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
	) {}

	override fun getMixins(): List<String?>? {
		return null
	}

	override fun preApply(
		targetClassName: String?,
		targetClass: ClassNode?,
		mixinClassName: String?,
		mixinInfo: IMixinInfo?,
	) {}

	override fun postApply(
		targetClassName: String?,
		targetClass: ClassNode?,
		mixinClassName: String?,
		mixinInfo: IMixinInfo?,
	) {}
	//endregion
}
