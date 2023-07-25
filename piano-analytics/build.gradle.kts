plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.moshiIR)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.mavenRelease)
    `maven-publish`
}

val GROUP: String by project
val VERSION_NAME: String by project
group = GROUP
version = VERSION_NAME

android {
    defaultConfig {
        minSdk = 21
        compileSdk = 34
        buildConfigField("String", "SDK_VERSION", """"${project.version}"""")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    namespace = "io.piano.android.analytics"
}

val sourcesJar = task<Jar>("androidSourcesJar") {
    archiveClassifier.set("sources")

    if (project.plugins.findPlugin("com.android.library") != null) {
        from(android.sourceSets["main"].java.srcDirs)
        from(android.sourceSets["main"].kotlin.srcDirs())

    } else {
        from(sourceSets["main"].java.srcDirs)
    }
}

artifacts {
    archives(sourcesJar)
}

kotlin {
    explicitApi()
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

ktlint {
    version.set("0.49.1")
    android.set(true)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = "io.piano"
                artifactId = "analytics"
                version = "3.3.1"

                pom {
                    name.set("analytics")
                    description.set("Piano Analytics SDK for Android")
                    url.set("https://github.com/digitalegarage/audiopilot-android/")
                    developers {
                        developer {
                            id.set("felix-ulmer")
                            name.set("Felix Ulmer")
                            email.set("felix.ulmer@andrena.de")
                        }
                    }
                }
                artifacts.apply {
                    add(artifact("$buildDir/outputs/aar/piano-analytics-release.aar"))
                }
            }
        }

        repositories {
            maven {
                name = "ATGithubPackages"
                url = uri("https://maven.pkg.github.com/digitalegarage/audiopilot-android")
                credentials {
                    username = System.getenv("GITHUB_ACTOR") ?: ""
                    password = System.getenv("GITHUB_PUBLISH_TOKEN") ?: ""
                }
            }
        }
    }
}

dependencies {
    compileOnly(libs.googleAdsId)
    compileOnly(libs.huaweiAdsId)
    implementation(libs.lifecycleProcess)
    implementation(libs.timber)
    implementation(libs.okhttp)
    implementation(libs.okhttpLogging)
    implementation(libs.moshi)

    testImplementation(libs.kotlinJunit)
    testImplementation(libs.mockitoKotlin)
    testImplementation(libs.mockitoCore)
    testImplementation(libs.junit)
    testImplementation(libs.androidxTestCore)
    testImplementation(libs.okhttpMockServer)
}