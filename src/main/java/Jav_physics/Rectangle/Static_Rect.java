package Jav_physics.Rectangle;

import java.util.List;
import java.util.Map;

import Jav_physics.Jav_physics;
import Jav_physics.Utils.Force;
import resources.console;

public class Static_Rect extends Rect {

    public Static_Rect(float x, float y, float width, float height) {
        super(x, y, width, height);
        registerRect(this);
        super.type = Jav_physics.Rect_types.Static;
    }

    @Override
    public void step(Map<String, List<Object>> rect_list) {
    }

    @Override
    public void impulseip(float dx, float dy) {
        errorImpulse();
    }

    @Override
    public void impulseip(Force d) {
        errorImpulse();
    }

    @Override
    public void accelerateip(float dx, float dy) {
        errorAccelerate();
    }

    @Override
    public void accelerateip(Force d) {
        errorAccelerate();
    }

    private void errorImpulse() {
        System.out.println(console.RED + "Static rect cannot have velocity applied:" + console.YELLOW + " change rect type to Kinematic or Dynamic." + console.RESET);
    }

    private void errorAccelerate() {
        System.out.println(console.RED + "Static rect cannot be accelerated:" + console.YELLOW + " change rect type to Kinematic or Dynamic." + console.RESET);
    }
}
