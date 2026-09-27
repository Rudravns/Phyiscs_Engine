package Jav_physics.Rectangle;

import java.util.List;
import java.util.Map;

import Jav_physics.Utils.Force;
import resources.console;
import Jav_physics.Jav_physics;

public class Kinematic_Rect extends Rect {

    private float mass;

    public Kinematic_Rect(float x, float y, float width, float height, float mass) {
        super(x, y, width, height);
        this.mass = mass;
        registerRect(this);
        super.type = Jav_physics.Rect_types.Kinematic;
    }

    @Override
    public void step(Map<String, List<Object>> rect_list) {
        pos.x(pos.x() + vel.x());
        pos.y(pos.y() + vel.y());
    }

    @Override
    public void accelerateip(float dx, float dy) {
        errorAccelerate();
    }

    @Override
    public void accelerateip(Force d) {
        errorAccelerate();
    }

    public float mass() { return mass; }
    public void mass(float m) { this.mass = m; }

    private void errorAccelerate() {
        System.out.println(console.RED + "Kinematic rect cannot be accelerated:" + console.YELLOW + " use impulseip() to set velocity directly." + console.RESET);
    }
}
