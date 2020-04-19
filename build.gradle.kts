import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.2.6.RELEASE"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
	kotlin("jvm") version "1.3.71"
	kotlin("plugin.spring") version "1.3.71"
	scala
}

group = "org.brookstevens"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("org.scala-lang:scala-library:2.12.10")
	testImplementation("io.gatling.highcharts:gatling-charts-highcharts:3.3.1")
}


tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

tasks.create<JavaExec>("testLoad") {
	val baseUrl = if(project.properties["gatling-date-time-base-url"] != null) {
		project.properties["gatling-date-time-base-url"]
	} else {
		"http://localhost:8080"
	}

	description = "Test load the Spring Boot web service with Gatling"
	group = "Load Test"
	classpath = sourceSets.test.get().runtimeClasspath
	jvmArgs = listOf("-Dlogback.configurationFile=${logbackGatlingConfig()}")
	environment("gatling-date-time-base-url", baseUrl!!)
	main = "io.gatling.app.Gatling"
	args = listOf(
		"--simulation", "webservice.gatling.simulation.DateTimeWebServiceLoadTestSimulation",
		"--results-folder", "${buildDir}/gatling-results",
		"--binaries-folder", sourceSets.test.get().output.classesDirs.toString()
	)
	doFirst {
		// gatling needs java 8, make it obvious to user to switch
		if(JavaVersion.current() != JavaVersion.VERSION_1_8){
			throw GradleException("\n\n\n" +
					"********************************** \n " +
					"This build must be run with java 8 \n"+
					"**********************************\n\n\n")
		}
	}
}

fun logbackGatlingConfig(): File {
	return sourceSets.test.get().resources.find { it.name == "logback-gatling.xml"}!!
}
