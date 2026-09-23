package Jav_physics;

import com.raylib.Raylib;

/*
update the lib


$env:JAVA_HOME = 'C:\Users\kumar1272\.jdks\openjdk-26.0.2'
.\gradlew.bat clean build publishToMavenLocal
.\gradlew.bat build --refresh-dependencies

*/
public class combine {
    protected static double Gravity = -9.8;

    public static double Gravity() {
        return Gravity;
    }

    public static void Gravity(double g) {
        Gravity = g;
    }


    public static double calulate_friction(double mu, double Fn)
    {
        return mu*Fn;
    }

    public static double calculate_mu(double Ff, double Fn)
    {
        return Ff/Fn;
    }

    public static double calculate_normal(double Ff, double mu)
    {
        return Ff/mu;
    }

    public static double calculate_normal(double weight, double Ft, double angle){
        return weight +(Ft*Math.sin(Math.toRadians(angle)));
    }

    public static double calculate_normal(double weight, Raylib.Vector2 Ft){
        return weight +(Ft.x()*Math.sin(Math.toRadians(Ft.y())));
    }
    
    public static double calculate_force(double mass, double acceleration)
    {
        return mass*acceleration;
    }
}