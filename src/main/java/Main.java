/** Save without refactor
 *  Mac - Cmnd + k, s
 *  Windows - ctrl + k, s
 */

import static com.raylib.Colors.BLUE;
import static com.raylib.Colors.DARKGRAY;
import static com.raylib.Colors.GREEN;
import static com.raylib.Colors.LIGHTGRAY;
import static com.raylib.Colors.RAYWHITE;
import static com.raylib.Raylib.BeginDrawing;
import static com.raylib.Raylib.ClearBackground;
import static com.raylib.Raylib.CloseWindow;
import static com.raylib.Raylib.DrawText;
import static com.raylib.Raylib.EndDrawing;
import static com.raylib.Raylib.FLAG_WINDOW_RESIZABLE;
import static com.raylib.Raylib.GetScreenHeight;
import static com.raylib.Raylib.GetScreenWidth;
import static com.raylib.Raylib.InitWindow;
import static com.raylib.Raylib.IsKeyPressed;
import static com.raylib.Raylib.KEY_F11;
import static com.raylib.Raylib.MeasureText;
import static com.raylib.Raylib.SetConfigFlags;
import static com.raylib.Raylib.SetTargetFPS;
import static com.raylib.Raylib.ToggleFullscreen;
import static com.raylib.Raylib.WindowShouldClose;

import Jav_physics.Rectangle.Dynamic_Rect;
import Jav_physics.Rectangle.Kinematic_Rect;
import Jav_physics.Rectangle.Static_Rect;
import Jav_physics.combine;

/**
 * Jaylib starter application with full screen support and dynamic centering.

TO run
cls
$env:JAVA_HOME = 'C:\Users\kumar1272\.jdks\openjdk-26.0.2'
.\gradlew.bat compileJava
.\gradlew.bat run

*/

public class Main {
    static String libraryStatus;
    static Dynamic_Rect drect = new Dynamic_Rect(40, 20, 100, 100);
    static Kinematic_Rect krect = new Kinematic_Rect(40, 20, 200, 100, 1.0f);
    static Static_Rect srect = new Static_Rect(1000, 50, 0, 800);

    public static void main(String[] args) {
        drect.moveip(5, 0);
        drect.accelerateip(0, 9.8f);
        drect.impulseip(2, 0);
        double friction = combine.calulate_friction(drect.mu(), 10);
        libraryStatus = String.format(
            "Jav_physics loaded: Dynamic_Rect, Kinematic_Rect, Static_Rect | friction %.1f",
            friction);

        // Allow the window to be resizable when not in fullscreen mode
        SetConfigFlags(FLAG_WINDOW_RESIZABLE);

        int initialWidth = 1000;
        int initialHeight = 850;
        InitWindow(initialWidth, initialHeight, "Jaylib - Fullscreen Demo");


        // Set the target frames per second
        SetTargetFPS(120);

        while (!WindowShouldClose()) {
            // Toggle fullscreen when F11 key is pressed
            if (IsKeyPressed(KEY_F11)) {
                ToggleFullscreen();
            }
            
            update();
            draw();
        }

        CloseWindow();
    }

    private static void update() {
        drect.accelerateip(1.0f, 0.0f);
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

        // Get current viewport width and height (handles screen size changes)
        int currentScreenWidth = GetScreenWidth();
        int currentScreenHeight = GetScreenHeight();

        // Calculate text width to center horizontally
        int mainTextWidth = MeasureText(message, mainFontSize);
        int subTextWidth = MeasureText(subtext, subFontSize);

        int mainTextX = (currentScreenWidth - mainTextWidth) / 2;
        int mainTextY = (currentScreenHeight / 2) - 20;

        int subTextX = (currentScreenWidth - subTextWidth) / 2;
        int subTextY = mainTextY + 40;
        int libraryTextWidth = MeasureText(librarySubtext, subFontSize);
        int libraryTextX = (currentScreenWidth - libraryTextWidth) / 2;
        int libraryTextY = subTextY + 30;

        // Draw centered instructions and message
        DrawText(message, mainTextX, mainTextY, mainFontSize, DARKGRAY);
        DrawText(subtext, subTextX, subTextY, subFontSize, LIGHTGRAY);
        DrawText(librarySubtext, libraryTextX, libraryTextY, subFontSize, DARKGRAY);
    }

    
}