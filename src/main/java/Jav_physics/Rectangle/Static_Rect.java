package Jav_physics.Rectangle;
import com.raylib.Raylib.Vector2;

import resources.console;

public class Static_Rect extends Rect {
    public Static_Rect(int width, int height, int x, int y){
        super(width, height, x, y);
    }

    @Override
    public void impulseip(float dx, float dy) {
        errorimpulse();
    }

    @Override
    public void impulseip(Vector2 d) {
        errorimpulse();
    }

    @Override
    public void accelerateip(float dx, float dy){
        erroraccelerate();
        }

    @Override
    public void accelerateip(Vector2 d) {
        erroraccelerate();
    }

    private void errorimpulse() {
        System.out.println(console.RED + "Static rect cannot access velocity:" + console.YELLOW + " change rect to Kinematic or Dynamic" + console.RESET);
    }

    private void erroraccelerate() {
        System.out.println(console.RED + "Static rect cannot access acceleration:" + console.YELLOW + " change rect to Kinematic or Dynamic" + console.RESET);
    }
}
