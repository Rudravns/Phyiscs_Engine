package Jav_physics.Rectangle;

import java.util.List;
import java.util.Map;

import Jav_physics.Utils.Force;
import resources.console;
import Jav_physics.Jav_physics;

public class Kinematic_Rect extends Rect {

    public Kinematic_Rect(float x, float y, float width, float height, float mass) {
        super(x, y, width, height, mass);
        super.type = Jav_physics.Rect_types.Kinematic;
        recalculateMassAndInertia();
        registerRect(this);
    }

    public Kinematic_Rect(float x, float y, float width, float height) {
        this(x, y, width, height, 1.0f);
    }

    @Override
    public void step(Map<String, List<Object>> rect_list) {
        pos.x(pos.x() + vel.x());
        pos.y(pos.y() + vel.y());

      

        if (Math.abs(angularVelocity) > 0.1f) {
            rot += angularVelocity;
            rot = rot % 360f;
            if (rot < 0f) rot += 360f;
        }
    
        
    }

    @Override
    public void accelerateip(float dx, float dy) {
        errorAccelerate();
    }

    @Override
    public void accelerateip(Force d) {
        errorAccelerate();
    }

    @Override
    public void applyForce(float fx, float fy) {
        errorAccelerate();
    }

    @Override
    public void applyForce(Force f) {
        errorAccelerate();
    }

    private void errorAccelerate() {
        System.out.println(console.RED + "Kinematic rect cannot be accelerated:" + console.YELLOW + " use impulseip() to set velocity directly." + console.RESET);
    }
}
