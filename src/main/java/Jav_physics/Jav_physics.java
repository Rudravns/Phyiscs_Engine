package Jav_physics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import com.raylib.Raylib;
import Jav_physics.Rectangle.Rect;

/*
update the lib


$env:JAVA_HOME = 'C:\Users\kumar1272\.jdks\openjdk-26.0.2'
.\gradlew.bat clean build publishToMavenLocal
.\gradlew.bat build --refresh-dependencies

 */
public class Jav_physics {

    protected static double Gravity = -9.8;

    public enum Rect_types {
        Static, Dynamic, Kinematic
    }

    // Private so NO ONE outside this class can touch the list directly
    private static final Set<Rect> rect_objects
            = Collections.newSetFromMap(new WeakHashMap<>());

    // Protected so only children (and package neighbors) can call it
    protected static void registerRect(Rect rect) {
        synchronized (rect_objects) {
            rect_objects.add(rect);
        }
    }

    public static void step_all_rect() {
        List<Rect> rectSnapshot;
        synchronized (rect_objects) {
            rectSnapshot = new ArrayList<>(rect_objects);
        }

        List<Object> allRects = new ArrayList<>(rectSnapshot);
        Map<String, List<Object>> rectList = new HashMap<>();
        rectList.put("All", allRects);

        for (Rect rect : rectSnapshot) {
            rect.step(rectList);
        }
    }

    public static double Gravity() {
        return Gravity;
    }

    public static void Gravity(double g) {
        Gravity = g;
    }

    public static double calulate_friction(double mu, double Fn) {
        return mu * Fn;
    }

    public static double calculate_friction(double mu, double Fn) {
        return mu * Fn;
    }

    public static double calculate_static_friction_max(double mu_s, double Fn) {
        return mu_s * Fn;
    }

    public static double calculate_kinetic_friction(double mu_k, double Fn) {
        return mu_k * Fn;
    }

    public static double calculate_mu(double Ff, double Fn) {
        return Ff / Fn;
    }

    public static double calculate_normal(double Ff, double mu) {
        return Ff / mu;
    }

    public static double calculate_normal(double weight, double Ft, double angle) {
        return weight + (Ft * Math.sin(Math.toRadians(angle)));
    }

    public static double calculate_normal(double weight, Raylib.Vector2 Ft) {
        return weight + (Ft.x() * Math.sin(Math.toRadians(Ft.y())));
    }

    public static double calculate_force(double mass, double acceleration) {
        return mass * acceleration;
    }

    public static double calculate_torque(double rx, double ry, double fx, double fy) {
        return rx * fy - ry * fx;
    }

    public static double calculate_moment_of_inertia_rect(double mass, double width, double height) {
        return (mass * (width * width + height * height)) / 12.0;
    }

    public static double calculate_angular_momentum(double inertia, double angularVelocityRad) {
        return inertia * angularVelocityRad;
    }

    public static double calculate_angular_momentum_deg(double inertia, double angularVelocityDeg) {
        return inertia * Math.toRadians(angularVelocityDeg);
    }
}
}
