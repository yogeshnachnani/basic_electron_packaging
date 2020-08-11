import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
val kotlinVersion = "1.3.72"
val ktorVersion = "1.3.2"

plugins {
    java
    kotlin("multiplatform") version "1.3.72"
    kotlin("plugin.serialization") version "1.3.72"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://kotlin.bintray.com/kotlin-js-wrappers/")
    mavenCentral()
    jcenter()
}

kotlin {
    /* Targets configuration omitted. 
    *  To find out how to configure the targets, please follow the link:
    *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */
    js {
        browser {
            testTask {
                useKarma() {
                    useChromeHeadless()
                }
            }
        }
        nodejs {
            useCommonJs()
        }
    }

    jvm {
        val main by compilations.getting {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
        tasks {
            /** Run compilations + tests and copy all built artifacts into build/electron directory */
            register<Copy>("buildAllArtifacts") {
                dependsOn(
                    "clean",
                    "compileJava",
                    "processResources",
                    "classes",
                    "jar",
                    "jsPackageJson",
                    "jsTestPackageJson",
                    "kotlinNodeJsSetup",
                    "kotlinNpmInstall",
                    "compileKotlinJs",
                    "jsProcessResources",
                    "jsBrowserDistributeResources",
                    "processDceJsKotlinJs",
                    "jsBrowserDevelopmentWebpack",
                    "jsMainClasses",
                    "jsJar",
                    "compileKotlinJvm",
                    "jvmProcessResources",
                    "jvmMainClasses",
                    "jvmJar",
                    "compileKotlinMetadata",
                    "metadataMainClasses",
                    "metadataJar",
                    "assemble",
                    "compileTestKotlinJs",
                    "jsTestProcessResources",
                    "jsTestClasses",
                    "jsBrowserTest",
                    "jsNodeTest",
                    "jsTest",
                    "compileTestKotlinJvm",
                    "jvmTestProcessResources",
                    "jvmTestClasses",
                    "jvmTest",
                    "allTests",
                    "compileTestJava",
                    "processTestResources",
                    "testClasses",
                    "test",
                    "check"
                )
                /** Copy electron files */
                from("$projectDir/start")
                into("$projectDir/electron_out")

                /** Copy main jar file */
                getByName("jvmJar").outputs.files.map {
                    from(it.absolutePath) {
                        into("java")
                    }
                }
                /** Copy java dependencies */
                main.runtimeDependencyFiles.asFileTree.map {
                    from(it.absolutePath) {
                        into("java")
                    }
                }
                /** Copy javascript */
                from("$buildDir/distributions") {
                    into("js")
                }
            }
            register<Exec>("electronPackage") {
                dependsOn("buildAllArtifacts")
                val locationOfElectronPackager = "$projectDir/electron_out"
                println("Will run electron packager now")
                workingDir = File(locationOfElectronPackager)
                commandLine("node_modules/electron-packager/bin/electron-packager.js", ".", "--platform", "darwin")
                commandLine("node_modules/electron-packager/bin/electron-packager.js", ".", "--platform", "linux", "x64")
            }
        }
    }
    tasks.named("compileKotlinJs") {
        this as KotlinJsCompile
        kotlinOptions.moduleKind = "commonjs"
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.1")
                implementation("org.jetbrains:kotlin-styled:1.0.0-pre.110-kotlin-$kotlinVersion")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
            }
        }
    }
}