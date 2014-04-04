package cc.cu.netvl.gradle.launch4gradle

import groovy.transform.EqualsAndHashCode
import org.gradle.api.Project

/**
 * Date: 03.04.2014
 * Time: 13:17
 */
@EqualsAndHashCode(includeFields = true, excludes = 'project')
class Launch4jPluginExtension implements Serializable {
    transient final Project project

    List<Launch4jConfig> configurations = new ArrayList<>()
    String launch4jProgram = 'launch4j'

    Launch4jPluginExtension(Project project) {
        this.project = project
    }

    void executable(Closure closure) {
        def config = new Launch4jConfig(project)

        Closure builderExec = (Closure) closure.clone()
        builderExec.delegate = config
        builderExec.resolveStrategy = Closure.DELEGATE_FIRST
        builderExec()

        configurations.add(config)
    }
}
