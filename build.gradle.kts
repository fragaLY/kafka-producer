import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.util.parseSpaceSeparatedArgs

plugins {
	application
	id("org.springframework.boot") version "2.3.1.RELEASE"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
	kotlin("jvm") version "1.3.72"
	kotlin("plugin.spring") version "1.3.72"
	id("com.google.cloud.tools.jib") version "2.4.0"
}

springBoot {
	buildInfo()
}

group = "com.kafka.producer"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_11

application {
	mainClassName = "com.kafka.producer.demo.NotificationProducerKt"
	applicationName = "kafka-producer"
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web") {
		exclude("org.springframework.boot", "spring-boot-starter-tomcat")
	}
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.springframework.boot:spring-boot-starter-jetty")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.springframework.kafka:spring-kafka")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("org.springframework.kafka:spring-kafka-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

kotlin {
	sourceSets["test"].apply {
		kotlin.srcDirs("src/test/kotlin/unit", "src/test/kotlin/integration")
	}
}

object DockerProps {
	const val BASE_IMAGE = "gcr.io/distroless/java:11"
	const val APP_PORT = "8080"
}

jib {
	from {
		image = DockerProps.BASE_IMAGE
	}
	to {
		tags = setOf("$version", "latest")
	}
	container {
		jvmFlags = parseSpaceSeparatedArgs("-noverify -Dspring.profiles.active=dev -Djava.rmi.server.hostname=localhost")
		ports = listOf(DockerProps.APP_PORT)
		labels = mapOf("app-name" to application.applicationName, "service-version" to version.toString())
	}
}
