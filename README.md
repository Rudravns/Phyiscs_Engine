# Jav_physics

A small Java physics library built on Jaylib. The source remains in this project so it can be edited directly, tested, and run with the demo application.

## Build and run

Install JDK 26 and check that `java -version` reports version 26.

On macOS, run the demo from a terminal in a logged-in desktop session:

```sh
cd /path/to/Phyiscs_Engine
./gradlew run
```

Jaylib includes native libraries for both Apple Silicon and Intel Macs. The demo
opens a graphics window, so it cannot run in a headless or SSH-only session.
The Gradle run task supplies the macOS JVM options needed by the native graphics
library automatically.

On Windows, run the equivalent Gradle wrapper command in PowerShell:

```powershell
.\gradlew.bat run
```

## Build the library

On macOS:

```sh
./gradlew build
./gradlew publishToMavenLocal
```

On Windows:

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
