/*
$env:JAVA_HOME = 'C:\Users\moghadaszadeh8593\.jdks\openjdk-26.0.2'; $env:Path = "$env:JAVA_HOME\bin;$env:Path"; .\gradlew.bat run
*/
import static com.raylib.Colors.BLUE;
import static com.raylib.Colors.DARKGRAY;
import static com.raylib.Colors.GREEN;
import static com.raylib.Colors.LIGHTGRAY;
import static com.raylib.Colors.RAYWHITE;
import static com.raylib.Raylib.BeginDrawing;
import static com.raylib.Raylib.BeginMode2D;
import com.raylib.Raylib.Camera2D;
import static com.raylib.Raylib.ClearBackground;
import static com.raylib.Raylib.CloseWindow;
import static com.raylib.Raylib.DrawText;
import static com.raylib.Raylib.EndDrawing;
import static com.raylib.Raylib.EndMode2D;
import static com.raylib.Raylib.FLAG_WINDOW_RESIZABLE;
import static com.raylib.Raylib.GetMousePosition;
import static com.raylib.Raylib.GetScreenHeight;
import static com.raylib.Raylib.GetScreenWidth;
import static com.raylib.Raylib.InitWindow;
import static com.raylib.Raylib.IsKeyDown;
import static com.raylib.Raylib.IsKeyPressed;
import static com.raylib.Raylib.IsMouseButtonDown;
import static com.raylib.Raylib.KEY_A;
import static com.raylib.Raylib.KEY_D;
import static com.raylib.Raylib.KEY_E;
import static com.raylib.Raylib.KEY_F11;
import static com.raylib.Raylib.KEY_LEFT;
import static com.raylib.Raylib.KEY_P;
import static com.raylib.Raylib.KEY_Q;
import static com.raylib.Raylib.KEY_R;
import static com.raylib.Raylib.KEY_RIGHT;
import static com.raylib.Raylib.KEY_S;
import static com.raylib.Raylib.KEY_SPACE;
import static com.raylib.Raylib.KEY_W;
import static com.raylib.Raylib.MOUSE_BUTTON_LEFT;
import static com.raylib.Raylib.MeasureText;
import static com.raylib.Raylib.SetConfigFlags;
import static com.raylib.Raylib.SetTargetFPS;
import static com.raylib.Raylib.ToggleFullscreen;
import static com.raylib.Raylib.WindowShouldClose;

import Jav_physics.Jav_physics;
import Jav_physics.Rectangle.Dynamic_Rect;
import Jav_physics.Rectangle.Kinematic_Rect;
import Jav_physics.Rectangle.Static_Rect;
import Jav_physics.Utils.Force;

/**
 * Jaylib starter application with full screen support and dynamic centering.
 */
public class Main {

    static String libraryStatus;
    static boolean pause_simulation = true;

    static Dynamic_Rect drect = new Dynamic_Rect(0, 0, 100, 100);
    static Kinematic_Rect krect = new Kinematic_Rect(300, 100, 100, 100, 1.0f);
    static Static_Rect srect = new Static_Rect(-100, 800, 1200, 50);

    // 1. Define virtual/internal application coordinates
    static final int VIRTUAL_WIDTH = 1000;
    static final int VIRTUAL_HEIGHT = 850;

    public static void main(String[] args) {
        drect.accelerateip(0, 0.2f);
        drect.impulseip(2, 0);

        double friction_static = Jav_physics.calulate_friction(drect.mu_s(), 10);
        double friction_kinetic = Jav_physics.calulate_friction(drect.mu_k(), 10);
        libraryStatus = String.format(
                "Jav_physics loaded: Dynamic_Rect, Kinematic_Rect, Static_Rect | friction %.1f / %.1f",
                friction_static, friction_kinetic);

        SetConfigFlags(FLAG_WINDOW_RESIZABLE);
        InitWindow(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, "Jaylib - Physics Engine");

        SetTargetFPS(120);

        while (!WindowShouldClose()) {
            if (IsKeyPressed(KEY_F11)) {
                ToggleFullscreen();
            }

            float zoom = getViewScale();
            Camera2D camera = new Camera2D()
                .target(new com.raylib.Raylib.Vector2()
                    .x(VIRTUAL_WIDTH / 2f)
                    .y(VIRTUAL_HEIGHT / 2f))
                .offset(new com.raylib.Raylib.Vector2()
                    .x(GetScreenWidth() / 2f)
                    .y(GetScreenHeight() / 2f))
                .rotation(0f)
                .zoom(zoom);

            float visibleWorldWidth = GetScreenWidth() / zoom;
            srect.x(VIRTUAL_WIDTH / 2f - visibleWorldWidth / 2f);
            srect.width(visibleWorldWidth);

            if (IsKeyPressed(KEY_P)) {
                pause_simulation = !pause_simulation;
            }
            if (IsKeyPressed(KEY_R)) {
                drect.pos().x(0).y(0);
                drect.vel().x(0).y(0);
                drect.rot(0);
                drect.angularVelocity(0);
            }
            if (IsKeyPressed(KEY_SPACE)) {
                drect.impulseip(0, -5f); // Jump
            }
            if (IsKeyDown(KEY_LEFT)) {
                drect.applyForce(-0.2f, 0); // Push left
            }
            if (IsKeyDown(KEY_RIGHT)) {
                drect.applyForce(0.2f, 0); // Push right
            }
            if (IsKeyDown(KEY_Q)) {
                drect.applyTorque(-10f); // Spin CCW
            }
            if (IsKeyDown(KEY_E)) {
                drect.applyTorque(10f); // Spin CW
            }

            if (IsKeyDown(KEY_W)) {
                krect.impulseip(Force.fromPolar(50, 90));   // Up
            }
            if (IsKeyDown(KEY_S)) {
                krect.impulseip(Force.fromPolar(50, 270));  // Down
            }
            if (IsKeyDown(KEY_A)) {
                krect.impulseip(Force.fromPolar(50, 180));  // Left
            }
            if (IsKeyDown(KEY_D)) {
                krect.impulseip(Force.fromPolar(50, 0));    // Right
            }


            // 3. Convert window mouse coordinates back into your virtual world space
            if (IsMouseButtonDown(MOUSE_BUTTON_LEFT)) {
                com.raylib.Raylib.Vector2 realMouse = GetMousePosition();
                com.raylib.Raylib.Vector2 worldMouse = new com.raylib.Raylib.Vector2()
                        .x((realMouse.x() - GetScreenWidth() / 2f) / zoom + VIRTUAL_WIDTH / 2f)
                        .y((realMouse.y() - GetScreenHeight() / 2f) / zoom + VIRTUAL_HEIGHT / 2f);
                krect.pos(worldMouse);
            }

            if (!pause_simulation) {
                update();
            }

            BeginDrawing();
            ClearBackground(RAYWHITE);

            BeginMode2D(camera);
            drect.draw(DARKGRAY);
            srect.draw(BLUE);
            krect.draw(GREEN);
            EndMode2D();
            drawmain();
            EndDrawing();
        }

        CloseWindow();
    }

    private static void update() {
        Jav_physics.step_all_rect();
    }

    private static float getViewScale() {
        float scale = Math.min(
                (float) GetScreenWidth() / VIRTUAL_WIDTH,
                (float) GetScreenHeight() / VIRTUAL_HEIGHT);
        return scale > 0f ? scale : 1f;
    }

    private static void drawmain() {
        String message = "Physics Engine Running! Controls:";
        String subtext = "[P] Pause/Resume | [R] Reset | [Space] Jump | [Left/Right] Move | [Q/E] Spin";
        String librarySubtext = libraryStatus;
        int mainFontSize = 24;
        int subFontSize = 20;

        int mainTextWidth = MeasureText(message, mainFontSize);
        int subTextWidth = MeasureText(subtext, subFontSize);

        int screenWidth = GetScreenWidth();
        int mainTextX = (screenWidth - mainTextWidth) / 2;
        int mainTextY = 20;

        int subTextX = (screenWidth - subTextWidth) / 2;
        int subTextY = mainTextY + 30;

        int libraryTextWidth = MeasureText(librarySubtext, subFontSize);
        int libraryTextX = (screenWidth - libraryTextWidth) / 2;
        int libraryTextY = subTextY + 30;

        DrawText(message, mainTextX, mainTextY, mainFontSize, DARKGRAY);
        DrawText(subtext, subTextX, subTextY, subFontSize, LIGHTGRAY);
        DrawText(librarySubtext, libraryTextX, libraryTextY, subFontSize, DARKGRAY);
    }
}
