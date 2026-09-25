package Jav_physics.Rectangle;

public class Kinematic_Rect extends Rect{

    private float mass;


    public Kinematic_Rect(int width, int height, int x, int y, float mass){
        super(width, height, x, y);
        this.mass = mass;
    }

    public void collide(Rect other) {
        // Y collision
        if (overlapping(other)) {
            pos.y(-vel.y());
            vel.y(0f);
        }

        // X collision
        if (overlapping(other)) {
            pos.x(-vel.x());
            vel.x(0f);
        }
    }
}
