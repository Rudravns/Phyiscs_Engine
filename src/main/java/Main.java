import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;
import Jav_physics.Rectangle.Dynamic_Rect;
import Jav_physics.Rectangle.Kinematic_Rect;
import Jav_physics.Rectangle.Static_Rect;
import Jav_physics.Jav_physics;
/**
 * Jaylib starter application with full screen support and dynamic centering.

 *  *TO run cls $env:JAVA_HOME = 'C:\Users\kumar1272\.jdks\openjdk-26.0.2'
 .\gradlew.bat compileJava .\gradlew.bat run
 * 
 * for personal laptops
  clear
  .\gradlew.bat build 
 .\gradlew.bat run

  **/
public class Main {
    static String libraryStatus;

    static boolean pause_simulation = true;

    static Dynamic_Rect drect = new Dynamic_Rect(0, 0, 100, 100);
    static Kinematic_Rect krect = new Kinematic_Rect(300, 100, 100, 100, 1.0f);
    static Static_Rect srect = new Static_Rect(0, 800, 1000, 50);

    public static void main(String[] args) {
        drect.accelerateip(0, 0.2f);
        drect.impulseip(2, 0);

        double friction_static = Jav_physics.calulate_friction(drect.mu_s(), 10);
        double friction_kinetic = Jav_physics.calulate_friction(drect.mu_k(), 10);
        libraryStatus = String.format(
            "Jav_physics loaded: Dynamic_Rect, Kinematic_Rect, Static_Rect | friction %.1f / %.1f",
            friction_static, friction_kinetic);

        SetConfigFlags(FLAG_WINDOW_RESIZABLE);

        int initialWidth = 1000;
        int initialHeight = 850;
        InitWindow(initialWidth, initialHeight, "Jaylib - Physics Engine");

        SetTargetFPS(120);

        while (!WindowShouldClose()) {
            if (IsKeyPressed(KEY_F11)) {
                ToggleFullscreen();
            }
            if (IsKeyPressed(KEY_P))
            {
                pause_simulation = !pause_simulation;
            }
            
            if (!pause_simulation){update();}
            draw();
        }

        CloseWindow();
    }

    private static void update() {
        Jav_physics.step_all_rect();
    }

    public static void draw() {
        BeginDrawing();
        ClearBackground(RAYWHITE);

        drawmain();
        drect.draw(DARKGRAY);
        srect.draw(BLUE);
        krect.draw(GREEN);



        EndDrawing();        
    }

    private static void drawmain() {
        String message = "Congrats! Jaylib is working!";
        String subtext = "Press [F11] to toggle Fullscreen mode";
        String librarySubtext = libraryStatus;
        int mainFontSize = 24;
        int subFontSize = 16;

        int currentScreenWidth = GetScreenWidth();
        int currentScreenHeight = GetScreenHeight();

        int mainTextWidth = MeasureText(message, mainFontSize);
        int subTextWidth = MeasureText(subtext, subFontSize);

        int mainTextX = (currentScreenWidth - mainTextWidth) / 2;
        int mainTextY = (currentScreenHeight / 2) - 20;

        int subTextX = (currentScreenWidth - subTextWidth) / 2;
        int subTextY = mainTextY + 40;
        int libraryTextWidth = MeasureText(librarySubtext, subFontSize);
        int libraryTextX = (currentScreenWidth - libraryTextWidth) / 2;
        int libraryTextY = subTextY + 30;

        DrawText(message, mainTextX, mainTextY, mainFontSize, DARKGRAY);
        DrawText(subtext, subTextX, subTextY, subFontSize, LIGHTGRAY);
        DrawText(librarySubtext, libraryTextX, libraryTextY, subFontSize, DARKGRAY);
    }
}
