# Jav_physics

A small Java physics library built on Jaylib. The source remains in this project so it can be edited directly, tested, and run with the demo application.

## Build and run

Set `JAVA_HOME` to JDK 26, then run:

```powershell
.\gradlew.bat run
```

## Build the library

```powershell
.\gradlew.bat build
.\gradlew.bat publishToMavenLocal
```

The library coordinates are `org.example:Phyiscs_Engine:1.0-SNAPSHOT`.

Add the local Maven repository and dependency to another Gradle project:

```groovy
repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation 'org.example:Phyiscs_Engine:1.0-SNAPSHOT'
}
```

Example:

```java
import Jav_physics.Rectangle.Dynamic_Rect;

Dynamic_Rect body = new Dynamic_Rect(40, 20, 100, 100);
body.moveip(5, 0);
body.accelerateip(0, 9.8f);
```

The library sources are under `src/main/java/Jav_physics`, so changes can be made there and republished with `publishToMavenLocal`.
