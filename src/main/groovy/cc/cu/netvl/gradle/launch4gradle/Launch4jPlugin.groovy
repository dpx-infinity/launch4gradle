package cc.cu.netvl.gradle.launch4gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin

/**
 * Date: 03.04.2014
 * Time: 11:59
 */
class Launch4jPlugin implements Plugin<Project> {
    static final String PLUGIN_GROUP = 'launch4j'

    @Override
    void apply(Project project) {
        project.plugins.apply(JavaPlugin)

        Launch4jPluginExtension extension = new Launch4jPluginExtension(project)
        project.extensions.launch4j = extension

        Task generateConfig = createGenerateConfigTask(project, extension)
        Task copyLibraries = createCopyLibrariesTask(project, extension)

        Task runLaunch4j = createRunLaunch4jTask(project, extension)
        runLaunch4j.dependsOn(generateConfig, copyLibraries)

        Task launch4jTask = createLaunch4jTask(project)
        launch4jTask.dependsOn(runLaunch4j)
    }

    private Task createGenerateConfigTask(Project project, Launch4jPluginExtension extension) {
        Task task = project.tasks.create('l4jGenerateConfig', GenerateLaunch4jConfigTask)
        task.description = 'Generates launch4j configuration files for this project.'
        task.group = PLUGIN_GROUP
        task.extension = extension
        task
    }

    private Task createCopyLibrariesTask(Project project, Launch4jPluginExtension extension) {
        Task task = project.tasks.create('l4jCopyLibraries')
        task.description = 'Copies dependencies of this project to launch4j output libraries directories.'
        task.group = PLUGIN_GROUP
        task.inputs.file(project.tasks[JavaPlugin.JAR_TASK_NAME])
        task.inputs.file(project.configurations.runtime)
        extension.configurations.each { task.outputs.dir(it.libDir) }
        task << {
            extension.configurations.each { config ->
                if (config.copyLibraries) {
                    project.copy {
                        from project.tasks[JavaPlugin.JAR_TASK_NAME]
                        from project.configurations.runtime
                        into config.libDir
                    }
                }
            }
        }
        task
    }

    private Task createRunLaunch4jTask(Project project, Launch4jPluginExtension extension) {
        Task task = project.tasks.create('l4jRunLaunch4j')
        task.description = 'Executes launch4j binary to generate executable files.'
        task.group = PLUGIN_GROUP
        extension.configurations.each {
            task.inputs.file(it.configFile)
            task.outputs.file(project.file(it.outputFile))
        }
        task << {
            extension.configurations.each { config ->
                project.exec {
                    executable = extension.launch4jProgram
                    args = [config.configFile.toString()]
                    workingDir = config.outputDir
                }
            }
        }
        task
    }

    private Task createLaunch4jTask(Project project) {
        Task task = project.tasks.create('launch4j')
        task.description = 'Main task, runs all launch4gradle tasks.'
        task.group = PLUGIN_GROUP
        task
    }
}

