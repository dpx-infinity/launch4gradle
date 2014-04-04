package cc.cu.netvl.gradle.launch4gradle

import groovy.transform.EqualsAndHashCode
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

/**
 * Date: 03.04.2014
 * Time: 13:17
 */
@EqualsAndHashCode(includeFields = true, excludes = 'project')
class Launch4jConfig implements Serializable {
    transient final Project project

    File outputDir
    String configFileName = 'launch4j.xml'
    String libDirName = 'lib'
    String outputFileName
    boolean copyLibraries = true

    File getOutputFile() {
        project.file("$outputDir/$outputFileName")
    }

    File getConfigFile() {
        project.file("$outputDir/$configFileName")
    }

    File getLibDir() {
        project.file("$outputDir/$libDirName")
    }

    /**
     * Header type, either GUI or CONSOLE.
     */
    HeaderType headerType = HeaderType.GUI

    /**
     * Jar file to use by Launch4j.
     */
    String jar

    /**
     * Whether to wrap {@link #jar} with executable or leave it as is.
     */
    Boolean dontWrapJar

    /**
     * Error dialog boxes title.
     */
    String errTitle

    String downloadUrl
    String supportUrl

    /**
     * Command-line arguments to the executable.
     */
    String cmdLine

    /**
     * A working directory for the executable defined by a path relative to the executable.
     */
    String chdir

    /**
     * When true, the launcher will wait for Java application to finish and return its exit code.
     */
    Boolean stayAlive

    /**
     * A path to application icon in ICO format.
     */
    File icon

    /**
     * A list of environment variables in 'KEY=value' form.
     */
    List<String> variables = new ArrayList<>()

    /**
     * Single program instance configuration.
     */
    SingleInstanceConfig singleInstance

    /**
     * Classpath configuration.
     */
    ClassPathConfig classPath

    /**
     * JRE configuration.
     */
    JreConfig jre

    /**
     * Splash screen configuration.
     */
    SplashConfig splash

    /**
     * Version info configuration.
     */
    VersionInfoConfig versionInfo

    /**
     * Messages configuration.
     */
    MessagesConfig messages

    Launch4jConfig(Project project) {
        this.project = project
        initializeDefaults()
    }

    private void initializeDefaults() {
        this.outputDir = project.file("$project.buildDir/launch4j")
        this.outputFileName = project.name + '.exe'
        this.jar = libDirName + '/' + project.tasks[JavaPlugin.JAR_TASK_NAME].outputs.files.singleFile.name
        this.jre = new JreConfig()
    }

    void var(String name, String value) {
        this.variables.add("$name=$value");
    }

    private static <T> T invokeConfig(T object, Closure closure) {
        Closure config = (Closure) closure.clone()
        config.delegate = object
        config.resolveStrategy = Closure.DELEGATE_FIRST
        config()
        object
    }

    void singleInstance(Closure closure) {
        this.singleInstance = invokeConfig(new SingleInstanceConfig(), closure)
    }

    void classPath(boolean empty = false, Closure closure) {
        this.classPath = invokeConfig(new ClassPathConfig(empty), closure)
    }

    void jre(Closure closure) {
        this.jre = invokeConfig(new JreConfig(), closure)
    }

    void splash(Closure closure) {
        this.splash = invokeConfig(new SplashConfig(), closure)
    }

    void versionInfo(Closure closure) {
        this.versionInfo = invokeConfig(new VersionInfoConfig(), closure)
    }

    void messages(Closure closure) {
        this.messages = invokeConfig(new MessagesConfig(), closure)
    }

    static final HeaderType GUI = HeaderType.GUI
    static final HeaderType CONSOLE = HeaderType.CONSOLE

    static enum HeaderType {
        GUI('gui'),
        CONSOLE('console');

        final String value;

        HeaderType(String value) {
            this.value = value
        }
    }

    @EqualsAndHashCode(includeFields = true)
    class SingleInstanceConfig implements Serializable {
        String mutexName
        String windowTitle
    }

    @EqualsAndHashCode(includeFields = true)
    class ClassPathConfig implements Serializable {
        String mainClass
        List<String> classpathEntries = new ArrayList<>()

        ClassPathConfig(boolean empty = false) {
            if (!empty) {
                initializeDefaults()
            }
        }

        void cp(String entry) {
            classpathEntries.add(entry)
        }

        private void initializeDefaults() {
            // add all dependencies in runtime configuration to the classpath
            classpathEntries.addAll(project.configurations.runtime.collect { "${libDirName}/${it.name}" })
        }
    }

    @EqualsAndHashCode(includeFields = true)
    class JreConfig implements Serializable {
        String path
        Boolean bundledJre64Bit
        String minVersion
        String maxVersion
        JdkPreference jdkPreference
        RuntimeBits runtimeBits
        Integer initialHeapSize
        Integer initialHeapPercent
        Integer maxHeapSize
        Integer maxHeapPercent
        List<String> options = new ArrayList<>();

        JreConfig() {
            minVersion = jreVersion(project.targetCompatibility.toString())
        }

        String jreVersion(String version) {
            if (version ==~ /\d(\.\d){3}(_\d\d)?/) {
                version
            } else if (version ==~ /\d/) {  // special case - 5 or 6 or 7 or 8
                "1.$version.0"
            } else if (version ==~ /\d(\.\d){0,2}/) {
                while (version.count('.') < 2) {
                    version += '.0'
                }
                version
            } else {
                throw new IllegalArgumentException("Invalid JRE version: $version, should be 'x', 'x.y' " +
                                                   "or 'x.y.z[_wv]'")
            }
        }

        void opt(String option) {
            options.add(option)
        }
        
        static final RuntimeBits BITS_64 = RuntimeBits.BITS_64
        static final RuntimeBits BITS_64_OR_32 = RuntimeBits.BITS_64_OR_32
        static final RuntimeBits BITS_32 = RuntimeBits.BITS_32

        static enum RuntimeBits {
            BITS_64('64'),
            BITS_64_OR_32('64/32'),
            BITS_32('32');

            final String value;

            RuntimeBits(String value) {
                this.value = value
            }
        }
        
        static final JdkPreference JRE_ONLY = JdkPreference.JRE_ONLY
        static final JdkPreference PREFER_JRE = JdkPreference.PREFER_JRE
        static final JdkPreference PREFER_JDK = JdkPreference.PREFER_JDK
        static final JdkPreference JDK_ONLY = JdkPreference.JDK_ONLY

        static enum JdkPreference {
            JRE_ONLY('jreOnly'),
            PREFER_JRE('preferJre'),
            PREFER_JDK('preferJdk'),
            JDK_ONLY('jdkOnly');

            final String value;

            JdkPreference(String value) {
                this.value = value
            }
        }
    }

    @EqualsAndHashCode(includeFields = true)
    class SplashConfig implements Serializable {
        File file
        Boolean waitForWindow
        Integer timeout
        Boolean timeoutErr
    }

    @EqualsAndHashCode(includeFields = true)
    class VersionInfoConfig implements Serializable {
        String fileVersion
        String txtFileVersion
        String fileDescription
        String copyright
        String productVersion
        String txtProductVersion
        String productName
        String companyName
        String internalName
        String originalFilename

        VersionInfoConfig() {
            this.productName = project.name
            this.internalName = project.name
            this.fileDescription = project.name
            this.originalFilename = outputFileName

            String version = project.version.toString()
            this.txtFileVersion = version
            this.txtProductVersion = version
            if (acceptableVersion(version)) {
                this.fileVersion = this.version(version)
                this.productVersion = this.version(version)
            }
        }

        private static boolean acceptableVersion(String version) {
            version ==~ /\d+(\.\d+){0,3}/
        }

        String version(String version) {
            if (!acceptableVersion(version)) {
                throw new IllegalArgumentException(
                    "Invalid version argument: '$version', should be 'x', 'x.y', 'x.y.z' or 'x.y.z.w', where " +
                    "'x', 'y', 'z' and 'w' are numbers"
                )
            }

            while (version.count('.') < 3) {
                version += '.0'
            }
            version
        }
    }

    @EqualsAndHashCode(includeFields = true)
    class MessagesConfig implements Serializable {
        String startupErr
        String bundledJreErr
        String jreVersionErr
        String launcherErr
    }
}

