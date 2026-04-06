package roland_a.mc_mods.common.gradle


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion

class DepsPlugin implements Plugin<Project> {
	void apply(Project project) {
		project.pluginManager.apply("java")
		project.pluginManager.apply(project.libs.plugins.fabric.loom.get().getPluginId());
		project.pluginManager.apply(project.libs.plugins.kotlin.jvm.get().getPluginId());
		project.pluginManager.apply(project.libs.plugins.kotlin.serialization.get().getPluginId());

		def javaVersion = Integer.parseInt(project.libs.versions.java.get())

		project.dependencies {
			minecraft(project.libs.minecraft)

			implementation(project.libs.fabric.loader)
			implementation(project.libs.fabric.kotlin)

			testRuntimeOnly("org.junit.platform:junit-platform-launcher")
			testImplementation(project.libs.junit)
			testImplementation(project.libs.mockito)
		}

		project.java{
			toolchain {
				languageVersion = JavaLanguageVersion.of(javaVersion)
			}
		}

		project.kotlin {
			jvmToolchain(javaVersion)
		}

		project.test {
			useJUnitPlatform()
		}
	}
}
