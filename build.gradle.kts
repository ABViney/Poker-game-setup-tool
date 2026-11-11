plugins {
    id("java")
    id("org.openapi.generator") version "7.25.0"
    application
}

val generateApiBahmsClient = tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("generateApiBahmsClient") {
    description = "Generate openapi classes for Jane's poker server"
    generatorName.set("java")
    inputSpec.set("$projectDir/specs/bahms-poker-openapi.json")
    outputDir.set(layout.buildDirectory.dir("generated/bahms_api"))
    apiPackage.set("buzz.blaize.bahms_poker.api")
    modelPackage.set("buzz.blaize.bahms_poker.model")
    invokerPackage.set("buzz.blaize.bahms_poker.invoker")
    library.set("native")
    configOptions.set(mapOf(
        "dateLibrary" to "java8",
        "serializationLibrary" to "jackson"
    ))
}

val generateApiWwsClient = tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("generateApiWwsClient") {
    description = "Generate openapi classes for WolfWithSword's pokerbot API"
    generatorName.set("java")
    inputSpec.set("$projectDir/specs/wws-poker-openapi.json")
    outputDir.set(layout.buildDirectory.dir("generated/wws_api"))
    apiPackage.set("buzz.blaize.wws_poker.api")
    modelPackage.set("buzz.blaize.wws_poker.model")
    invokerPackage.set("buzz.blaize.wws_poker.invoker")
    library.set("native")
    configOptions.set(mapOf(
        "dateLibrary" to "java8",
        "serializationLibrary" to "jackson"
    ))
}

sourceSets {
    main {
        java {
            srcDir(layout.buildDirectory.dir("generated/bahms_api/src/main/java"))
            srcDir(layout.buildDirectory.dir("generated/wws_api/src/main/java"))
        }
    }
}

group = "buzz.blaize"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("buzz.blaize.Main")
}

dependencies {
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("com.fasterxml.jackson.core:jackson-core:2.21.5")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.21")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.21.5")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.21.5")
    implementation("org.openapitools:jackson-databind-nullable:0.2.11")
    implementation("jakarta.annotation:jakarta.annotation-api:1.3.5")

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

tasks.named("compileJava") {
    dependsOn(generateApiBahmsClient, generateApiWwsClient)
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "buzz.blaize.Main"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}