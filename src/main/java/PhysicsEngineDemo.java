import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

import com.raylib.Raylib;
import com.raylib.Raylib.Camera2D;

import Jav_physics.Jav_physics;
import Jav_physics.Rectangle.Dynamic_Rect;
import Jav_physics.Rectangle.Kinematic_Rect;
import Jav_physics.Rectangle.Static_Rect;

/** Interactive example of the physics engine's rectangle types and controls. */
public class PhysicsEngineDemo {
    private static final int WORLD_WIDTH = 1000;
    private static final int WORLD_HEIGHT = 760;

    private static final Dynamic_Rect player = new Dynamic_Rect(220, 400, 70, 70, 2.0f);
    private static final Dynamic_Rect crate = new Dynamic_Rect(480, 300, 90, 55, 3.0f);
    private static final Kinematic_Rect platform = new Kinematic_Rect(650, 500, 180, 24);
    private static final Static_Rect floor = new Static_Rect(0, 650, WORLD_WIDTH, 50);
    private static boolean paused;

    public static void main(String[] args) {
        player.accelerateip(0f, 0.25f);
        player.linearDamping(0.002f);
        crate.accelerateip(0f, 0.25f);
        crate.restitution(0.35f);

        SetConfigFlags(FLAG_WINDOW_RESIZABLE);
        InitWindow(WORLD_WIDTH, WORLD_HEIGHT, "Jav_physics - Interactive Demo");
        SetTargetFPS(120);

        while (!WindowShouldClose()) {
            if (IsKeyPressed(KEY_F11)) ToggleFullscreen();
            if (IsKeyPressed(KEY_P)) paused = !paused;
            if (IsKeyPressed(KEY_R)) resetBodies();

            float scale = viewScale();
            Camera2D camera = new Camera2D()
                    .target(new Raylib.Vector2().x(WORLD_WIDTH / 2f).y(WORLD_HEIGHT / 2f))
                    .offset(new Raylib.Vector2().x(GetScreenWidth() / 2f).y(GetScreenHeight() / 2f))
                    .rotation(0f)
                    .zoom(scale);

            resizeFloor(scale);
            handleInput(scale);

            if (!paused) Jav_physics.step_all_rect();

            BeginDrawing();
            ClearBackground(RAYWHITE);
            BeginMode2D(camera);
            floor.draw(BLUE);
            platform.draw(ORANGE);
            player.draw(GREEN);
            crate.draw(MAROON);
            EndMode2D();
            drawHud();
            EndDrawing();
        }

        CloseWindow();
    }

    private static void handleInput(float scale) {
        if (IsKeyDown(KEY_LEFT)) player.applyForce(-0.25f, 0f);
        if (IsKeyDown(KEY_RIGHT)) player.applyForce(0.25f, 0f);
        if (IsKeyPressed(KEY_SPACE)) player.impulseip(0f, -7f);
        if (IsKeyDown(KEY_Q)) player.applyTorque(-8f);
        if (IsKeyDown(KEY_E)) player.applyTorque(8f);

        if (IsKeyPressed(KEY_Z)) resizePlayer(-10f, 0f);
        if (IsKeyPressed(KEY_X)) resizePlayer(10f, 0f);
        if (IsKeyPressed(KEY_C)) resizePlayer(0f, -10f);
        if (IsKeyPressed(KEY_V)) resizePlayer(0f, 10f);

        if (IsMouseButtonDown(MOUSE_BUTTON_LEFT)) {
            Raylib.Vector2 mouse = GetMousePosition();
            float worldX = (mouse.x() - GetScreenWidth() / 2f) / scale + WORLD_WIDTH / 2f;
            float worldY = (mouse.y() - GetScreenHeight() / 2f) / scale + WORLD_HEIGHT / 2f;
            platform.pos(new Raylib.Vector2()
                    .x(worldX - platform.width() / 2f)
                    .y(worldY - platform.height() / 2f));
        }
    }

    private static void resizePlayer(float deltaWidth, float deltaHeight) {
        float centerX = player.x() + player.width() / 2f;
        float centerY = player.y() + player.height() / 2f;
        float width = Math.max(20f, player.width() + deltaWidth);
        float height = Math.max(20f, player.height() + deltaHeight);
        player.width(width);
        player.height(height);
        player.pos(new Raylib.Vector2().x(centerX - width / 2f).y(centerY - height / 2f));
    }

    private static void resizeFloor(float scale) {
        float visibleWorldWidth = GetScreenWidth() / scale;
        floor.x(WORLD_WIDTH / 2f - visibleWorldWidth / 2f);
        floor.width(visibleWorldWidth);
    }

    private static void resetBodies() {
        player.pos(new Raylib.Vector2().x(220f).y(400f));
        player.vel().x(0f).y(0f);
        player.acc().x(0f).y(0.25f);
        player.rot(0f);
        player.angularVelocity(0f);
        player.width(70f);
        player.height(70f);

        crate.pos(new Raylib.Vector2().x(480f).y(300f));
        crate.vel().x(0f).y(0f);
        crate.rot(0f);
        crate.angularVelocity(0f);

        platform.pos(new Raylib.Vector2().x(650f).y(500f));
        platform.vel().x(0f).y(0f);
        paused = false;
    }

    private static float viewScale() {
        float scale = Math.min((float) GetScreenWidth() / WORLD_WIDTH,
                (float) GetScreenHeight() / WORLD_HEIGHT);
        return scale > 0f ? scale : 1f;
    }

    private static void drawHud() {
        String title = "Jav_physics interactive demo";
        String controls = "P pause  |  R reset  |  Arrows force  |  Space jump  |  Q/E torque  |  Z/X width  |  C/V height";
        String mouse = "Hold left mouse to move the kinematic platform  |  F11 fullscreen  |  "
                + (paused ? "PAUSED" : "RUNNING");
        DrawText(title, 20, 16, 22, DARKGRAY);
        DrawText(controls, 20, 46, 16, DARKGRAY);
        DrawText(mouse, 20, 68, 16, paused ? RED : DARKGRAY);
        DrawText(String.format("Player: %.0f x %.0f  |  mass %.1f kg  |  inertia %.1f",
                player.width(), player.height(), player.mass(), player.inertia()),
                20, GetScreenHeight() - 28, 16, DARKGRAY);
    }
}