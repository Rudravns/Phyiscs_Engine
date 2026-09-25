package Jav_physics.Rectangle; 

import com.raylib.Raylib;
import com.raylib.Raylib.Vector2;

import Jav_physics.Utils.Force;
import Jav_physics.combine;

import java.util.Map;
import java.util.List;

public class Rect extends combine { 
    protected float width; 
    protected float height; 
    protected Vector2 pos; 
    protected Vector2 acc; 
    protected Vector2 vel; 
    protected double mu_s = 0.8; // Friction static coefficient
    protected double mu_k = 0.7; // Friction kinetic coefficient
    protected float rot = 0;
    protected Vector2 center_of_gravity;
    
    // Main constructor with positions
    public Rect(float width, float height, float x, float y) { 
        this.width = width; 
        this.height = height; 
        
        // Properly allocating separate native memory for each instance
        this.pos = new Vector2().x(x).y(y); 
        this.acc = new Vector2().x(0).y(0); 
        this.vel = new Vector2().x(0).y(0); 
        center_of_gravity =new Vector2().x(width / 2f).y(height / 2f);
    } 

    // Constructor without positions (defaults to 0,0)
    public Rect(float width, float height) { 
        this(width, height, 0f, 0f);
    } 

    public Rect(float angle) {
        rot = angle;
    }

    // Update every tick
    public void step(Map<String, List<Object>> rect_list) {
        rect_list.forEach((key, value) -> {
            for (int i=0; i < value.length(); i++) {

            }
        });
    }
      
    public boolean overlapping(Rect other) {
        // If any of these are true, they absolutely do not overlap
        if (this.pos.x() >= other.pos.x() + other.width() ||  // This is too far right
            this.pos.x() + this.width() <= other.pos.x()  ||  // This is too far left
            this.pos.y() >= other.pos.y() + other.height() ||  // This is too far down
            this.pos.y() + this.height() <= other.pos.y()) {   // This is too far up
            return false;
        }
    return true;
    }


    
    
    // moveip method with floats
    public void moveip(float dx, float dy) { 
        pos.x(pos.x() + dx); 
        pos.y(pos.y() + dy); 
        center_of_gravity.x(pos.x() + width / 2f).y(pos.y() + height / 2f);
       } 

    // moveip method with Vector2
    public void moveip(Force d) { 
        pos.x(pos.x() + d.x()); 
        pos.y(pos.y() + d.y()); 
        center_of_gravity.x(pos.x() + width / 2f).y(pos.y() + height / 2f);
    } 

    // accelerateip method with floats
    public void accelerateip(float dx, float dy) { 
        acc.x(acc.x() + dx); 
        acc.y(acc.y() + dy); 
    } 

    // accelerateip method with Vector2
    public void accelerateip(Force d) { 
        acc.x(acc.x() + d.x()); 
        acc.y(acc.y() + d.y()); 
    } 

    // impulseip (velocity) method with floats
    public void impulseip(float dx, float dy) { 
        vel.x(vel.x() + dx); 
        vel.y(vel.y() + dy); 
    } 

    // impulseip (velocity) method with Vector2
    public void impulseip(Force d) { 
        vel.x(vel.x() + d.x()); 
        vel.y(vel.y() + d.y()); 
    } 

    // Getters and Setters for static friction
    public double mu_s() { 
        return this.mu_s; 
    } 

    public void mu_s(double mu) { 
        this.mu_s = mu; 
    } 

    // Getters and Setters for kinetic friction
    public double mu_k() { 
        return this.mu_k; 
    } 

    public void mu_k(double mu) { 
        this.mu_k = mu; 
    } 

    // Render method
    public void draw(Raylib.Color color) {
    Raylib.DrawRectanglePro(
        new Raylib.Rectangle()
            .x(pos.x())
            .y(pos.y())
            .width(width)
            .height(height),
        center_of_gravity,
        rot,
        color
    );
    }

    public float height(){
        return height;  
    }
    
    public void height(float h){
        height = h;
    }
    
    public float width(){
        return width; 
    }
    
    public void width(float w){
        width = w;
    }
    
    public float angle(){
        return rot;
    }

    public void angle(float a){
        rot = a;
    }
}
