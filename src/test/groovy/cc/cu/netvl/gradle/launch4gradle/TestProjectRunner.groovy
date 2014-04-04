package cc.cu.netvl.gradle.launch4gradle

import org.gradle.tooling.GradleConnector
import org.junit.Test

/**
 * Date: 04.04.2014
 * Time: 9:53
 */
class TestProjectRunner {
    @Test
    public void runTask() throws Exception {
        GradleConnector.newConnector()
            .useInstallation(new File('D:/programs/gradle/1.10'))
            .forProjectDirectory(new File('test'))
            .connect().newBuild().forTasks("launch4j").run()
    }
}
