package roland_a.mc_mods.common.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class SetupPlugin implements Plugin<Project> {
	void apply(Project project) {
		project.pluginManager.apply("roland_a.mc_mods.common.gradle.deps")
		project.pluginManager.apply(project.libs.plugins.shadow.get().getPluginId())

		project.ext {
			mc_version = project.libs.versions.minecraft.get()
			java_version = project.libs.versions.java.get()
			file_name = "${project.mod_id}-${project.mod_version}+mc${mc_version}"
			fabric_kotlin_version = project.libs.versions.fabric.kotlin.get()
		}

		//TODO replace this with declaring mc-mods-common as a dependency
		project.sourceSets{
			main {
				java {
					srcDirs += "./mc-mods-common/src/main/java"
				}
				resources {
					srcDirs += "./mc-mods-common/src/main/resources"
				}
			}

			test {
				java {
					srcDirs += "./mc-mods-common/src/test/java"
				}
				resources {
					srcDirs += "./mc-mods-common/src/test/resources"
				}
			}
		}

		project.processResources {
			includeEmptyDirs = false

			eachFile {
				name = name.replace("\${mod_id}", project.mod_id)
				path = path.replace("\${mod_id}", project.mod_id)

				if (name.endsWith(".json")) {
					expand(project.properties)
				}
			}
		}

		project.loom{
			accessWidenerPath = new File("./src/main/resources/\${mod_id}.accesswidener")
		}

		project.shadowJar {
			configurations = []

			archiveClassifier.set("")
			from(project.sourceSets.main.output)
			minimize()
			relocate("roland_a.mc_mods.common", "roland_a.mc_mods.${project.mod_id}.common")
		}

		project.base.archivesName = project.file_name
	}
}
