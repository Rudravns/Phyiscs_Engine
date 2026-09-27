package Jav_physics;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import Jav_physics.Rectangle.Dynamic_Rect;
import Jav_physics.Rectangle.Kinematic_Rect;
import Jav_physics.Rectangle.Static_Rect;
import Jav_physics.Utils.Force;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhysicsTest {

    @Test
    public void testLinearMovementAndAcceleration() {
        Dynamic_Rect rect = new Dynamic_Rect(0, 0, 50, 50, 2.0f);
        rect.accelerateip(0, 1.0f);
        rect.impulseip(5.0f, 0);

        Map<String, List<Object>> emptyList = new HashMap<>();
        emptyList.put("All", Collections.emptyList());

        rect.step(emptyList);

        // vel.x should remain 5.0, vel.y should become 1.0
        assertEquals(5.0f, rect.vel().x(), 0.001f);
        assertEquals(1.0f, rect.vel().y(), 0.001f);
        // pos should have updated by vel
        assertEquals(5.0f, rect.x(), 0.001f);
        assertEquals(1.0f, rect.y(), 0.001f);
    }

    @Test
    public void testTorqueAndAngularMomentum() {
        Dynamic_Rect rect = new Dynamic_Rect(0, 0, 100, 100, 2.0f);
        float expectedInertia = (2.0f * (100 * 100 + 100 * 100)) / 12f;
        assertEquals(expectedInertia, rect.inertia(), 0.01f);

        // Apply impulse at bottom right corner (x=100, y=100), center is at (50, 50)
        // r = (50, 50). Impulse j = (-10, 0)
        // r x j = 50 * 0 - 50 * (-10) = 500
        rect.applyImpulseAtPoint(-10f, 0f, 100f, 100f);

        // Linear velocity x should decrease by 10 / 2 = 5
        assertEquals(-5.0f, rect.vel().x(), 0.001f);

        // Angular velocity should be non-zero and positive
        assertTrue(rect.angularVelocity() > 0f);
        assertTrue(rect.angularMomentum() > 0f);

        // Step once and verify rotation increases
        Map<String, List<Object>> emptyList = new HashMap<>();
        emptyList.put("All", Collections.emptyList());
        rect.step(emptyList);

        assertTrue(rect.rot() > 0f);
    }

    @Test
    public void testFrictionSlowsAndStopsSliding() {
        Dynamic_Rect drect = new Dynamic_Rect(0, 700, 100, 100, 1.0f);
        Static_Rect floor = new Static_Rect(0, 800, 1000, 50);

        // Downward acceleration (gravity) pushing onto floor, horizontal initial velocity
        drect.accelerateip(0, 0.5f);
        drect.impulseip(5.0f, 0);

        Map<String, List<Object>> rectList = new HashMap<>();
        rectList.put("All", List.of(drect, floor));

        float initialVx = drect.vel().x();

        // Step simulation 5 times
        for (int i = 0; i < 5; i++) {
            drect.step(rectList);
        }

        // Horizontal velocity should have reduced due to friction
        assertTrue(drect.vel().x() < initialVx, "Velocity should decrease due to friction");

        // Step simulation enough times for sliding to come to a complete stop
        for (int i = 0; i < 200; i++) {
            drect.step(rectList);
        }

        // Object should have stopped completely due to static/kinetic friction without moving backwards
        assertEquals(0.0f, drect.vel().x(), 0.01f, "Box should come to rest due to friction");
        // Vertical position should be resting on the floor (y = 700)
        assertEquals(700.0f, drect.y(), 0.5f, "Box should rest on the surface");
    }

    @Test
    public void testDynamicToDynamicCollision() {
        // Two identical dynamic rects moving toward each other
        Dynamic_Rect a = new Dynamic_Rect(0, 0, 50, 50, 1.0f);
        Dynamic_Rect b = new Dynamic_Rect(40, 0, 50, 50, 1.0f); // overlapping by 10 in x

        a.impulseip(2.0f, 0f);
        b.impulseip(-2.0f, 0f);

        a.restitution(1.0f);
        b.restitution(1.0f);

        a.collide(b);

        // Velocities should reverse symmetrically in an elastic collision
        assertTrue(a.vel().x() < 0f, "Rect A should bounce backwards");
        assertTrue(b.vel().x() > 0f, "Rect B should bounce backwards");
        // Positions should have separated
        assertFalse(a.overlapping(b), "Rects should no longer overlap after resolution");
    }

    @Test
    public void testKinematicRectUnaffectedByCollision() {
        Dynamic_Rect dynamic = new Dynamic_Rect(40, 0, 50, 50, 1.0f);
        Kinematic_Rect kinematic = new Kinematic_Rect(0, 0, 50, 50, 5.0f);

        kinematic.impulseip(1.0f, 0f);
        float kinVxBefore = kinematic.vel().x();

        dynamic.collide(kinematic);

        // Kinematic rect velocity must remain strictly unchanged
        assertEquals(kinVxBefore, kinematic.vel().x(), 0.0001f);
        // Dynamic rect should be pushed away
        assertTrue(dynamic.x() >= 50f, "Dynamic rect should be displaced by kinematic rect");
    }

    @Test
    public void testRotatedCollisionWithSAT() {
        // Box A rotated by 30 degrees (asymmetric corner), positioned above a static floor
        Dynamic_Rect box = new Dynamic_Rect(100, 680, 50, 50, 1.0f);
        box.angle(30f);
        box.impulseip(0f, 5.0f); // moving downwards into the floor
        Static_Rect floor = new Static_Rect(0, 700, 500, 50);

        assertTrue(box.overlapping(floor), "Rotated box should detect SAT overlap with floor");

        box.collide(floor);
        assertTrue(box.vel().y() < 0.1f, "Normal impulse should oppose penetration velocity");
        // And rotation should have been induced from corner impact
        assertNotEquals(0f, box.angularVelocity(), 0.0001f, "Off-center corner impact induces rotation");
    }

    @Test
    public void testPhysicsCalculationHelpers() {
        double mu = 0.5;
        double fn = 20.0;
        assertEquals(10.0, Jav_physics.calculate_friction(mu, fn), 1e-6);
        assertEquals(10.0, Jav_physics.calulate_friction(mu, fn), 1e-6);

        // Inertia of rect: m * (w^2 + h^2) / 12
        double inertia = Jav_physics.calculate_moment_of_inertia_rect(3.0, 10.0, 20.0);
        assertEquals(3.0 * (100.0 + 400.0) / 12.0, inertia, 1e-6);

        // Torque = rx * fy - ry * fx
        double torque = Jav_physics.calculate_torque(2.0, 3.0, 4.0, 5.0);
        // 2*5 - 3*4 = 10 - 12 = -2
        assertEquals(-2.0, torque, 1e-6);

        // Angular momentum = I * omega
        double L = Jav_physics.calculate_angular_momentum(inertia, 2.0);
        assertEquals(inertia * 2.0, L, 1e-6);
    }
}
