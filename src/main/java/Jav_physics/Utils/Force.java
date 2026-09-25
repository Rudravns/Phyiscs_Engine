package Jav_physics.Utils;
import com.raylib.Raylib.Vector2;

// Darth Vadar uses the force to force you into movement like newtons law of force

public class Force {

    private float magnitude; private float angle;
    private Vector2 pos;

    private Force() {
        this.pos = new Vector2();
        recalc_force();
    }

    public static Force fromPolar(float magnitude, float angle) {
        Force force = new Force();
        force.magnitude = magnitude;
        force.angle = angle;
        
        float radians = (float) Math.toRadians(angle);
        force.pos.x((float) Math.cos(radians)).y((float) Math.sin(radians));
        
        return force;
    }

    public static Force fromXY(float x, float y) {
        Force force = new Force();
        force.pos.x(x).y(y);
        force.recalc_force(); 
        return force;
    }
    

    public void magnitude(float m){ this.magnitude = m;}

    public float magnitude(){return magnitude;}

    public void angle(float a){ this.angle = a;}

    public float angle(){return angle;}

    public Vector2 position(){
        return pos; 
    }

    public float x()
    {
        return pos.x();
    }

    public float y()
    {
        return pos.y();
    }

    public void x(float x)
    {
        pos.x(x);
        recalc_force();
    }

    public void y(float y)
    {
        pos.y(y);
        recalc_force();
    }

    public void moveip(float dx, float dy){
        pos.x(pos.x()+dx).y(pos.y()+dy);
        recalc_force();
    }

    private void recalc_force(){
        magnitude = (float)(Math.hypot(pos.x(), pos.y()));
        angle =  (float)Math.toDegrees(Math.atan2(pos.y(), pos.x()));
    }

    public Force add(Force other){
        return Force.fromXY(this.x()+other.x(), this.y()+other.y());
    }

    public Force subtract(Force other){
        return Force.fromXY(this.x()-other.x(), this.y()-other.y());
    }

    public void Multiply_by_scalar(float scalar){
        x(x()*scalar); y(y()*scalar);
    }
    
}
