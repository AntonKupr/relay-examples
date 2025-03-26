import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

version = "2019.2"

project {
    description = "Relay Examples - A collection of example applications using Relay"

    // Use the main VCS root as specified in the issue description
    val mainVcsRoot = DslContext.settingsRoot

    // Common build template for all examples
    val relayExampleTemplate = template {
        id("RelayExampleTemplate")
        name = "Relay Example Template"

        vcs {
            root(mainVcsRoot)
        }

        steps {
            // Install Watchman
            script {
                name = "Install Watchman"
                scriptContent = """
                    mkdir -p watchman
                    cd watchman
                    curl -L -O 'https://github.com/facebook/watchman/releases/download/v2021.05.24.00/watchman-v2021.05.24.00-linux.zip'
                    unzip watchman-v2021.05.24.00-linux.zip
                    cd watchman-v2021.05.24.00-linux
                    mkdir -p /usr/local/{bin,lib} /usr/local/var/run/watchman
                    cp bin/* /usr/local/bin
                    cp lib/* /usr/local/lib
                    chmod 755 /usr/local/bin/watchman
                    chmod 2777 /usr/local/var/run/watchman
                """.trimIndent()
            }
        }

        features {
            perfmon {}
        }

        triggers {
            vcs {
                branchFilter = "+:*"
            }
        }
    }

    // Todo Example Build Configuration
    val todoExample = buildType {
        id("TodoExample")
        name = "Todo Example"

        templates(relayExampleTemplate)

        steps {
            script {
                name = "Yarn Install"
                workingDir = "todo"
                scriptContent = "yarn install --frozen-lockfile"
            }
            script {
                name = "Lint"
                workingDir = "todo"
                scriptContent = "yarn run lint"
            }
            script {
                name = "Update Schema"
                workingDir = "todo"
                scriptContent = "yarn run update-schema"
            }
            script {
                name = "Build"
                workingDir = "todo"
                scriptContent = "yarn run build"
            }
            script {
                name = "Flow Type Check"
                workingDir = "todo"
                scriptContent = "yarn run flow"
            }
        }
    }

    // Issue Tracker Example Build Configuration
    val issueTrackerExample = buildType {
        id("IssueTrackerExample")
        name = "Issue Tracker Example"

        templates(relayExampleTemplate)

        steps {
            script {
                name = "Yarn Install"
                workingDir = "issue-tracker"
                scriptContent = "yarn install --frozen-lockfile"
            }
            script {
                name = "Build"
                workingDir = "issue-tracker"
                scriptContent = "yarn run build"
            }
        }
    }

    // Issue Tracker Next v13 Example Build Configuration
    val issueTrackerNextV13Example = buildType {
        id("IssueTrackerNextV13Example")
        name = "Issue Tracker Next v13 Example"

        templates(relayExampleTemplate)

        steps {
            script {
                name = "Yarn Install"
                workingDir = "issue-tracker-next-v13"
                scriptContent = "yarn install --frozen-lockfile"
            }
            script {
                name = "Build"
                workingDir = "issue-tracker-next-v13"
                scriptContent = "yarn run build"
            }
        }
    }

    // Newsfeed Example Build Configuration
    val newsfeedExample = buildType {
        id("NewsfeedExample")
        name = "Newsfeed Example"

        templates(relayExampleTemplate)

        steps {
            script {
                name = "Yarn Install"
                workingDir = "newsfeed"
                scriptContent = "yarn install --frozen-lockfile"
            }
            script {
                name = "Build"
                workingDir = "newsfeed"
                scriptContent = "yarn run build"
            }
        }
    }

    // Data Driven Dependencies Example Build Configuration
    val dataDrivenDependenciesExample = buildType {
        id("DataDrivenDependenciesExample")
        name = "Data Driven Dependencies Example"

        templates(relayExampleTemplate)

        steps {
            script {
                name = "Yarn Install"
                workingDir = "data-driven-dependencies"
                scriptContent = "yarn install --frozen-lockfile"
            }
            script {
                name = "Build"
                workingDir = "data-driven-dependencies"
                scriptContent = "yarn run build"
            }
        }
    }

    // Deploy Configuration
    buildType {
        id("DeployRelayExamples")
        name = "Deploy Relay Examples"

        vcs {
            root(mainVcsRoot)
        }

        steps {
            script {
                name = "Deploy to Production"
                scriptContent = """
                    echo "Deploying Relay Examples to production environment"
                    # Add actual deployment commands here
                """.trimIndent()
            }
        }

        dependencies {
            snapshot(todoExample) {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }
            snapshot(issueTrackerExample) {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }
            snapshot(issueTrackerNextV13Example) {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }
            snapshot(newsfeedExample) {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }
            snapshot(dataDrivenDependenciesExample) {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }
        }

        features {
            perfmon {}
        }
    }
}
