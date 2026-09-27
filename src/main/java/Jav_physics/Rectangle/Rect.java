
package Jav_physics.Rectangle;

import java.util.List;
import java.util.Map;

import static com.raylib.Colors.DARKGRAY;
import static com.raylib.Raylib.DrawText;

import com.raylib.Raylib;
import com.raylib.Raylib.Vector2;

import Jav_physics.Utils.Force;
import Jav_physics.Jav_physics;
public class Rect extends Jav_physics {
    protected float width;
    protected float height;
    protected Vector2 pos;               // Top-left corner position
    protected Vector2 acc;               // Acceleration vector
    protected Vector2 vel;               // Velocity vector
    protected double mu_s = 0.8;        // Static friction coefficient
    protected double mu_k = 0.7;        // Kinetic friction coefficient
    protected float rot = 0;             // Rotation angle in degrees
    protected Vector2 center_of_gravity; // Local offset (w/2, h/2) used by DrawRectanglePro
    protected Jav_physics.Rect_types type;
    
    public Rect(float x, float y, float width, float height) {
        this.width  = width;
        this.height = height;

        this.pos = new Vector2().x(x).y(y);
        this.acc = new Vector2().x(0).y(0);
        this.vel = new Vector2().x(0).y(0);

        // Local center offset for DrawRectanglePro (relative to top-left corner)
        this.center_of_gravity = new Vector2().x(width / 2f).y(height / 2f);

        if (getClass() == Rect.class) {
            Jav_physics.registerRect(this);
        }
    }

    public Rect(float width, float height) {
        this(0f, 0f, width, height);
    }

    public void step(Map<String, List<Object>> rect_list) {
    }

    public boolean overlapping(Rect other) {
        if (this.pos.x()  >= other.pos.x() + other.width  ||
            this.pos.x()  + this.width  <= other.pos.x()  ||
            this.pos.y()  >= other.pos.y() + other.height ||
            this.pos.y()  + this.height <= other.pos.y()) {
            return false;
        }
        return true;
    }

    public void moveip(float dx, float dy) {
        pos.x(pos.x() + dx);
        pos.y(pos.y() + dy);
    }

    public void moveip(Force d) {
        pos.x(pos.x() + d.x());
        pos.y(pos.y() + d.y());
    }

    public void accelerateip(float dx, float dy) {
        acc.x(acc.x() + dx);
        acc.y(acc.y() + dy);
    }

    public void accelerateip(Force d) {
        acc.x(acc.x() + d.x());
        acc.y(acc.y() + d.y());
    }

    public void impulseip(float dx, float dy) {
        vel.x(vel.x() + dx);
        vel.y(vel.y() + dy);
    }

    public void impulseip(Force d) {
        vel.x(vel.x() + d.x());
        vel.y(vel.y() + d.y());
    }

    public double mu_s() { return this.mu_s; }
    public void mu_s(double mu) { this.mu_s = mu; }

    public double mu_k() { return this.mu_k; }
    public void mu_k(double mu) { this.mu_k = mu; }

    public void draw(Raylib.Color color) {
        Raylib.DrawRectanglePro(
            new Raylib.Rectangle()
                .x(pos.x()+center_of_gravity.x())
                .y(pos.y()+center_of_gravity.y())
                .width(width)
                .height(height),
            center_of_gravity,
            rot,
            color
        );
        drawAttributes();
    }

    private void drawAttributes() {
        int textX = (int) (pos.x()+width+5);
        int textY = (int) (pos.y());
        int lineHeight = 14;
        int fontSize = 12;
        String[] attributes = {
            String.format("pos: (%.1f, %.1f)", pos.x(), pos.y()),
            String.format("acc: (%.1f, %.1f)", acc.x(), acc.y()),
            String.format("vel: (%.1f, %.1f)", vel.x(), vel.y()),
            String.format("friction: static %.2f, kinetic %.2f", mu_s, mu_k),
            String.format("size: %.1f x %.1f", width, height),
            String.format("rotation: %.1f", rot)
        };

        for (int i = 0; i < attributes.length; i++) {
            DrawText(attributes[i], textX, textY + i * lineHeight, fontSize, DARKGRAY);
        }
    }

    public Object[] get_attributes() {
        return new Object[]{pos.toString(), pos.x(), pos.y() + 2 * center_of_gravity.y()};
    }


    public float height() { return height; }
    public void height(float h) { height = h; }

    public float width() { return width; }
    public void width(float w) { width = w; }

    public float angle() { return rot; }
    public void angle(float a) { rot = a; }

    public float x() { return pos.x(); }
    public float y() { return pos.y(); }
}
