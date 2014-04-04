package cc.cu.netvl.gradle.launch4gradle

import groovy.xml.MarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction

/**
 * Date: 03.04.2014
 * Time: 15:36
 */
class GenerateLaunch4jConfigTask extends DefaultTask {
    @Input
    Launch4jPluginExtension extension

    @OutputFiles
    List<File> getLaunch4jConfigFiles() {
        extension.configurations.collect { it.configFile }
    }

    @TaskAction
    void generateConfigFiles() {
        extension.configurations.each { configuration ->
            File configFile = configuration.configFile
            configFile.parentFile.mkdirs()
            configFile.withWriter("UTF-8") { writer ->
                MarkupBuilder document = new MarkupBuilder(writer)
                document.launch4jConfig() {
                    headerType(configuration.headerType.value)
                    outfile(configuration.outputFileName)
                    jar(configuration.jar)
                    if (configuration.dontWrapJar != null)
                        dontWrapJar(configuration.dontWrapJar)
                    if (configuration.errTitle != null)
                        errTitle(configuration.errTitle)
                    if (configuration.downloadUrl != null)
                        downloadUrl(configuration.downloadUrl)
                    if (configuration.supportUrl != null)
                        supportUrl(configuration.supportUrl)
                    if (configuration.cmdLine != null)
                        cmdLine(configuration.cmdLine)
                    if (configuration.chdir != null)
                        chdir(configuration.chdir)
                    if (configuration.stayAlive != null)
                        stayAlive(configuration.stayAlive)
                    if (configuration.icon != null)
                        icon(configuration.icon)
                    configuration.variables.each { var(it) }

                    if (configuration.classPath != null) {
                        classPath() {
                            if (configuration.classPath.mainClass != null)
                                mainClass(configuration.classPath.mainClass)
                            configuration.classPath.classpathEntries.each { cp(it) }
                        }
                    }

                    if (configuration.singleInstance != null) {
                        def singleInstanceConfig = configuration.singleInstance
                        singleInstance() {
                            if (singleInstanceConfig.mutexName != null)
                                mutexName(singleInstanceConfig.mutexName)
                            if (singleInstanceConfig.windowTitle != null)
                                windowTitle(singleInstanceConfig.windowTitle)
                        }
                    }

                    if (configuration.jre != null) {
                        def jreConfig = configuration.jre
                        jre() {
                            if (jreConfig.path != null)
                                path(jreConfig.path)
                            if (jreConfig.bundledJre64Bit != null)
                                bundledJre64Bit(jreConfig.bundledJre64Bit)
                            if (jreConfig.minVersion != null)
                                minVersion(jreConfig.minVersion)
                            if (jreConfig.maxVersion != null)
                                maxVersion(jreConfig.maxVersion)
                            if (jreConfig.jdkPreference != null)
                                jdkPreference(jreConfig.jdkPreference.value)
                            if (jreConfig.runtimeBits != null)
                                runtimeBits(jreConfig.runtimeBits.value)
                            if (jreConfig.initialHeapSize != null)
                                initialHeapSize(jreConfig.initialHeapSize)
                            if (jreConfig.initialHeapPercent != null)
                                initialHeapPercent(jreConfig.initialHeapPercent)
                            if (jreConfig.maxHeapSize != null)
                                maxHeapSize(jreConfig.maxHeapSize)
                            if (jreConfig.maxHeapPercent != null)
                                maxHeapPercent(jreConfig.maxHeapPercent)
                            jreConfig.options.each { opt(it) }
                        }
                    }

                    if (configuration.splash != null) {
                        def splashConfig = configuration.splash
                        splash() {
                            if (splashConfig.file != null)
                                file(splashConfig.file)
                            if (splashConfig.waitForWindow != null)
                                waitForWindow(splashConfig.waitForWindow)
                            if (splashConfig.timeout != null)
                                timeout(splashConfig.timeout)
                            if (splashConfig.timeoutErr != null)
                                timeoutErr(splashConfig.timeoutErr)
                        }
                    }

                    if (configuration.versionInfo != null) {
                        def versionInfoConfig = configuration.versionInfo
                        versionInfo() {
                            if (versionInfoConfig.fileVersion != null)
                                fileVersion(versionInfoConfig.fileVersion)
                            if (versionInfoConfig.txtFileVersion != null)
                                txtFileVersion(versionInfoConfig.txtFileVersion)
                            if (versionInfoConfig.fileDescription != null)
                                fileDescription(versionInfoConfig.fileDescription)
                            if (versionInfoConfig.copyright != null)
                                copyright(versionInfoConfig.copyright)
                            if (versionInfoConfig.productVersion != null)
                                productVersion(versionInfoConfig.productVersion)
                            if (versionInfoConfig.txtProductVersion != null)
                                txtProductVersion(versionInfoConfig.txtProductVersion)
                            if (versionInfoConfig.productName != null)
                                productName(versionInfoConfig.productName)
                            if (versionInfoConfig.companyName != null)
                                companyName(versionInfoConfig.companyName)
                            if (versionInfoConfig.internalName != null)
                                internalName(versionInfoConfig.internalName)
                            if (versionInfoConfig.originalFilename != null)
                                originalFilename(versionInfoConfig.originalFilename)
                        }
                    }

                    if (configuration.messages != null) {
                        def messagesConfig = configuration.messages
                        messages() {
                            if (messagesConfig.startupErr != null)
                                startupErr(messagesConfig.startupErr)
                            if (messagesConfig.bundledJreErr != null)
                                bundledJreErr(messagesConfig.bundledJreErr)
                            if (messagesConfig.jreVersionErr != null)
                                jreVersionErr(messagesConfig.jreVersionErr)
                            if (messagesConfig.launcherErr != null)
                                launcherErr(messagesConfig.launcherErr)
                        }
                    }
                }
            }
        }
    }
}
