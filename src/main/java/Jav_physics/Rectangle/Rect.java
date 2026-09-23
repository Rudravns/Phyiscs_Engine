package Jav_physics.Rectangle; 

import com.raylib.Raylib; 
import static com.raylib.Raylib.DrawRectangle;
import com.raylib.Raylib.Vector2;

import Jav_physics.combine; 

public class Rect extends combine { 
    protected float width; 
    protected float height; 
    protected Vector2 pos; 
    protected Vector2 acc; 
    protected Vector2 vel; 
    protected double mu = 0.8; // Friction coefficient

    // Main constructor with positions
    public Rect(float width, float height, float x, float y) { 
        this.width = width; 
        this.height = height; 
        
        // Properly allocating separate native memory for each instance
        this.pos = new Vector2().x(x).y(y); 
        this.acc = new Vector2().x(0).y(0); 
        this.vel = new Vector2().x(0).y(0); 
    } 

    // Constructor without positions (defaults to 0,0)
    public Rect(float width, float height) { 
        this(width, height, 0f, 0f);
    } 

    // moveip method with floats
    public void moveip(float dx, float dy) { 
        pos.x(pos.x() + dx); 
        pos.y(pos.y() + dy); 
    } 

    // moveip method with Vector2
    public void moveip(Vector2 d) { 
        pos.x(pos.x() + d.x()); 
        pos.y(pos.y() + d.y()); 
    } 

    // accelerateip method with floats
    public void accelerateip(float dx, float dy) { 
        acc.x(acc.x() + dx); 
        acc.y(acc.y() + dy); 
    } 

    // accelerateip method with Vector2
    public void accelerateip(Vector2 d) { 
        acc.x(acc.x() + d.x()); 
        acc.y(acc.y() + d.y()); 
    } 

    // impulseip (velocity) method with floats
    public void impulseip(float dx, float dy) { 
        vel.x(vel.x() + dx); 
        vel.y(vel.y() + dy); 
    } 

    // impulseip (velocity) method with Vector2
    public void impulseip(Vector2 d) { 
        vel.x(vel.x() + d.x()); 
        vel.y(vel.y() + d.y()); 
    } 

    // Getters and Setters for friction
    public double mu() { 
        return this.mu; 
    } 

    public void mu(double mu) { 
        this.mu = mu; 
    } 

    // Render method
    public void draw(Raylib.Color color) { 
        DrawRectangle((int)pos.x(), (int)pos.y(), (int)width, (int)height, color); 
    } 
}
