package Jav_physics.Rectangle;

import java.util.List;
import java.util.Map;
import java.util.Collections;

import static com.raylib.Colors.DARKGRAY;
import static com.raylib.Colors.RED;
import static com.raylib.Raylib.DrawText;

import com.raylib.Raylib;
import com.raylib.Raylib.Vector2;

import Jav_physics.Utils.Force;
import Jav_physics.Jav_physics;

public class Rect extends Jav_physics {
    protected float width;
    protected float height;
    protected Vector2 pos;               // Top-left corner position (unrotated reference)
    protected Vector2 acc;               // Persistent acceleration vector
    protected Vector2 vel;               // Velocity vector
    protected Vector2 force;             // Transient accumulated force vector
    protected float mass = 1.0f;        // Mass in kg
    protected float invMass = 1.0f;     // 1 / mass (0 for static / kinematic)
    protected float inertia;            // Moment of inertia around center of gravity
    protected float invInertia;         // 1 / inertia (0 for static / kinematic / fixedRotation)
    protected double mu_s = 0.8;        // Static friction coefficient
    protected double mu_k = 0.7;        // Kinetic friction coefficient
    protected float restitution = 0.2f; // Bounciness / elasticity [0, 1]
    protected float rot = 0;             // Rotation angle in degrees
    protected float angularVelocity = 0; // Angular velocity in degrees/step
    protected float angularAcceleration = 0; // Angular acceleration in degrees/step^2
    protected float torque = 0;          // Accumulated torque
    protected float linearDamping = 0.0f;     // Linear drag / resistance
    protected float angularDamping = 0.005f;  // Rotational drag / air resistance
    protected boolean fixedRotation = false;  // Lock rotation if desired
    protected Vector2 center_of_gravity; // Local offset (w/2, h/2) used by DrawRectanglePro
    protected Jav_physics.Rect_types type = Jav_physics.Rect_types.Dynamic;
    
    public Rect(float x, float y, float width, float height) {
        this(x, y, width, height, 1.0f);
    }

    public Rect(float x, float y, float width, float height, float mass) {
        this.width  = width;
        this.height = height;
        this.mass   = mass;

        this.pos   = new Vector2().x(x).y(y);
        this.acc   = new Vector2().x(0).y(0);
        this.vel   = new Vector2().x(0).y(0);
        this.force = new Vector2().x(0).y(0);

        // Local center offset for DrawRectanglePro (relative to top-left corner)
        this.center_of_gravity = new Vector2().x(width / 2f).y(height / 2f);

        recalculateMassAndInertia();

        if (getClass() == Rect.class) {
            Jav_physics.registerRect(this);
        }
    }

    public Rect(float width, float height) {
        this(0f, 0f, width, height, 1.0f);
    }

    public void recalculateMassAndInertia() {
        if (type == Jav_physics.Rect_types.Static) {
            this.invMass = 0f;
            this.inertia = 0f;
            this.invInertia = 0f;
        } else if (type == Jav_physics.Rect_types.Kinematic) {
            this.invMass = 0f;
            this.inertia = (mass > 0f) ? (mass * (width * width + height * height) / 12f) : 0f;
            this.invInertia = 0f;
        } else {
            if (mass > 0f) {
                this.invMass = 1.0f / mass;
                this.inertia = mass * (width * width + height * height) / 12f;
                this.invInertia = (fixedRotation || inertia <= 0f) ? 0f : (1.0f / inertia);
            } else {
                this.invMass = 0f;
                this.inertia = 0f;
                this.invInertia = 0f;
            }
        }
    }

    public void step(Map<String, List<Object>> rect_list) {
    }

    public boolean overlapping(Rect other) {
        if (Math.abs(this.rot) < 0.001f && Math.abs(other.rot) < 0.001f) {
            return !(this.pos.x() >= other.pos.x() + other.width  ||
                     this.pos.x() + this.width  <= other.pos.x()  ||
                     this.pos.y() >= other.pos.y() + other.height ||
                     this.pos.y() + this.height <= other.pos.y());
        }
        return checkSATOverlap(this, other);
    }

    public static boolean checkSATOverlap(Rect a, Rect b) {
        float[][] cornersA = a.getCorners();
        float[][] cornersB = b.getCorners();

        float[][] axes = new float[][] {
            a.getAxisX(), a.getAxisY(),
            b.getAxisX(), b.getAxisY()
        };

        for (float[] axis : axes) {
            float len = (float) Math.hypot(axis[0], axis[1]);
            if (len < 1e-6f) continue;
            float ax = axis[0] / len;
            float ay = axis[1] / len;

            float minA = Float.MAX_VALUE, maxA = -Float.MAX_VALUE;
            for (float[] pt : cornersA) {
                float proj = pt[0] * ax + pt[1] * ay;
                if (proj < minA) minA = proj;
                if (proj > maxA) maxA = proj;
            }

            float minB = Float.MAX_VALUE, maxB = -Float.MAX_VALUE;
            for (float[] pt : cornersB) {
                float proj = pt[0] * ax + pt[1] * ay;
                if (proj < minB) minB = proj;
                if (proj > maxB) maxB = proj;
            }

            if (minA >= maxB || minB >= maxA) {
                return false;
            }
        }
        return true;
    }

    public float[][] getCorners() {
        float cx = pos.x() + center_of_gravity.x();
        float cy = pos.y() + center_of_gravity.y();
        float hx = width / 2f;
        float hy = height / 2f;

        float rad = (float) Math.toRadians(rot);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);

        float uxx = cos, uxy = sin;
        float uyx = -sin, uyy = cos;

        return new float[][] {
            { cx - uxx * hx - uyx * hy, cy - uxy * hx - uyy * hy },
            { cx + uxx * hx - uyx * hy, cy + uxy * hx - uyy * hy },
            { cx + uxx * hx + uyx * hy, cy + uxy * hx + uyy * hy },
            { cx - uxx * hx + uyx * hy, cy - uxy * hx + uyy * hy }
        };
    }

    public float[] getAxisX() {
        float rad = (float) Math.toRadians(rot);
        return new float[] { (float) Math.cos(rad), (float) Math.sin(rad) };
    }

    public float[] getAxisY() {
        float rad = (float) Math.toRadians(rot);
        return new float[] { (float) -Math.sin(rad), (float) Math.cos(rad) };
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

    public void applyForce(float fx, float fy) {
        force.x(force.x() + fx);
        force.y(force.y() + fy);
    }

    public void applyForce(Force f) {
        applyForce(f.x(), f.y());
    }

    public void applyTorque(float t) {
        this.torque += t;
    }

    public void applyAngularImpulse(float impulse) {
        if (!fixedRotation && invInertia > 0f) {
            float deltaOmegaRad = impulse * invInertia;
            this.angularVelocity += (float) Math.toDegrees(deltaOmegaRad);
            if (Math.abs(this.angularVelocity) <= 0.2f) {
                this.angularVelocity = 0f;
                this.angularAcceleration = 0f;
                this.rot = findNearestStraight(this.rot);
            }
        }
    }

    private int findNearestStraight(float rot) {
        float[] straightAngles = {0f, 90f, 180f, 270f};
        float[] diffs = new float[4];
        for (int i = 0; i < straightAngles.length; i++) {
            float diff = Math.abs(rot - straightAngles[i]);
            diffs[i] = Math.min(diff, 360f - diff);
        }

        return (int) Collections.min(
            List.of(0, 1, 2, 3), 
            (i, j) -> Float.compare(diffs[i], diffs[j])).floatValue();
    }

    public void applyForceAtPoint(float fx, float fy, float px, float py) {
        applyForce(fx, fy);
        float cx = pos.x() + center_of_gravity.x();
        float cy = pos.y() + center_of_gravity.y();
        float rx = px - cx;
        float ry = py - cy;
        // Torque tau = rx * fy - ry * fx
        float t = rx * fy - ry * fx;
        applyTorque(t);
    }

    public void applyForceAtPoint(Force f, Vector2 point) {
        applyForceAtPoint(f.x(), f.y(), point.x(), point.y());
    }

    public void applyImpulseAtPoint(float jx, float jy, float px, float py) {
        float dvx = (invMass > 0f) ? (jx * invMass) : jx;
        float dvy = (invMass > 0f) ? (jy * invMass) : jy;
        impulseip(dvx, dvy);
        float cx = pos.x() + center_of_gravity.x();
        float cy = pos.y() + center_of_gravity.y();
        float rx = px - cx;
        float ry = py - cy;
        float angImpulse = rx * jy - ry * jx;
        applyAngularImpulse(angImpulse);
    }

    public void applyImpulseAtPoint(Force j, Vector2 point) {
        applyImpulseAtPoint(j.x(), j.y(), point.x(), point.y());
    }

    // Getters and Setters
    public float mass() { return this.mass; }
    public void mass(float m) {
        this.mass = m;
        recalculateMassAndInertia();
    }

    public float invMass() { return this.invMass; }
    public float inertia() { return this.inertia; }
    public float momentOfInertia() { return this.inertia; }
    public float invInertia() { return this.invInertia; }

    public float angularVelocity() { return this.angularVelocity; }
    public void angularVelocity(float w) { this.angularVelocity = w; }

    public float angularAcceleration() { return this.angularAcceleration; }
    public void angularAcceleration(float a) { this.angularAcceleration = a; }

    public float torque() { return this.torque; }
    public void torque(float t) { this.torque = t; }

    public float angularMomentum() {
        float omegaRad = (float) Math.toRadians(angularVelocity);
        return inertia * omegaRad;
    }

    public void angularMomentum(float L) {
        if (inertia > 0f) {
            float omegaRad = L / inertia;
            this.angularVelocity = (float) Math.toDegrees(omegaRad);
        }
    }

    public Vector2 linearMomentum() {
        return new Vector2().x(mass * vel.x()).y(mass * vel.y());
    }

    public float linearMomentumMagnitude() {
        return (float) Math.hypot(mass * vel.x(), mass * vel.y());
    }

    public float restitution() { return this.restitution; }
    public void restitution(float r) { this.restitution = r; }

    public double mu_s() { return this.mu_s; }
    public void mu_s(double mu) { this.mu_s = mu; }

    public double mu_k() { return this.mu_k; }
    public void mu_k(double mu) { this.mu_k = mu; }

    public float linearDamping() { return this.linearDamping; }
    public void linearDamping(float d) { this.linearDamping = d; }

    public float angularDamping() { return this.angularDamping; }
    public void angularDamping(float d) { this.angularDamping = d; }

    public boolean fixedRotation() { return this.fixedRotation; }
    public void fixedRotation(boolean fixed) {
        this.fixedRotation = fixed;
        recalculateMassAndInertia();
    }

    public Vector2 pos() { return this.pos; }
    public Vector2 vel() { return this.vel; }
    public Vector2 acc() { return this.acc; }
    public Vector2 force() { return this.force; }

    public Vector2 center() {
        return new Vector2().x(pos.x() + center_of_gravity.x()).y(pos.y() + center_of_gravity.y());
    }

    public Vector2 center_of_gravity() { return this.center_of_gravity; }

    public void draw(Raylib.Color color) {
        Raylib.DrawRectanglePro(
            new Raylib.Rectangle()
                .x(pos.x() + center_of_gravity.x())
                .y(pos.y() + center_of_gravity.y())
                .width(width)
                .height(height),
            center_of_gravity,
            rot,
            color
        );

        // Draw a small orientation indicator line from center to top edge
        float cx = pos.x() + center_of_gravity.x();
        float cy = pos.y() + center_of_gravity.y();
        float rad = (float) Math.toRadians(rot);
        float endX = cx + (float) -Math.sin(rad) * (height / 2f);
        float endY = cy + (float) Math.cos(rad) * (height / 2f);
        Raylib.DrawLine((int) cx, (int) cy, (int) endX, (int) endY, RED);

        drawAttributes();
    }

    private void drawAttributes() {
        int textX = (int) (pos.x() + width + 5);
        int textY = (int) (pos.y());
        if (Raylib.GetScreenWidth() > 0 && textX + 170 > Raylib.GetScreenWidth()) {
            textX = (int) (pos.x() - 175);
        }
        if (textY < 10) textY = 10;

        int lineHeight = 14;
        int fontSize = 12;
        String[] attributes = {
            String.format("pos: (%.1f, %.1f)", pos.x(), pos.y()),
            String.format("vel: (%.1f, %.1f)", vel.x(), vel.y()),
            String.format("acc: (%.1f, %.1f)", acc.x(), acc.y()),
            String.format("rot: %.1f deg", rot),
            String.format("ang vel: %.1f deg/s", angularVelocity),
            String.format("ang mom: %.1f", angularMomentum()),
            String.format("friction: s %.2f, k %.2f", mu_s, mu_k),
            String.format("mass: %.1f kg", mass)
        };

        for (int i = 0; i < attributes.length; i++) {
            DrawText(attributes[i], textX, textY + i * lineHeight, fontSize, DARKGRAY);
        }
    }

    public Object[] get_attributes() {
        return new Object[]{pos.toString(), pos.x(), pos.y() + 2 * center_of_gravity.y()};
    }

    public float height() { return height; }
    public void height(float h) {
        height = h;
        center_of_gravity.y(h / 2f);
        recalculateMassAndInertia();
    }

    public float width() { return width; }
    public void width(float w) {
        width = w;
        center_of_gravity.x(w / 2f);
        recalculateMassAndInertia();
    }

    public float angle() { return rot; }
    public void angle(float a) { rot = a; }

    public float rot() { return rot; }
    public void rot(float r) { rot = r; }

    public float x() { return pos.x(); }
    public void x(float x) { pos.x(x); }

    public float y() { return pos.y(); }
    public void y(float y) { pos.y(y); }

    public void pos(Vector2 position) {
        pos.x(position.x()).y(position.y());
    }

   
}
