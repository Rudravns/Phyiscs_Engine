
package Jav_physics.Rectangle;

public class Dynamic_Rect extends Rect {

    private float restitution = 0.2f;

    public Dynamic_Rect(int width, int height, int x, int y) {
        super(width, height, x, y);
    }

    public void collition(Rect other_rect) {

        // 1. Check for overlap
        if (!super.overlapping(other_rect)) return;

        // 2. Calculate overlap on each axis
        float leftA = pos.x();
        float rightA = pos.x() + width;
        float topA = pos.y();
        float bottomA = pos.y() + height;

        float leftB = other_rect.pos.x();
        float rightB = other_rect.pos.x() + other_rect.width;
        float topB = other_rect.pos.y();
        float bottomB = other_rect.pos.y() + other_rect.height;

        float overlapX =
            Math.min(rightA, rightB) - Math.max(leftA, leftB);

        float overlapY =
            Math.min(bottomA, bottomB) - Math.max(topA, topB);

        // 3. Choose the axis with the smallest overlap
        float nx = 0;
        float ny = 0;
        float penetration;

        if (overlapX < overlapY) {
            penetration = overlapX;

            // Normal points from the other rectangle toward this one
            nx = center_of_gravity.x() < 
                 other_rect.center_of_gravity.x() ? -1 : 1;

        } else {
            penetration = overlapY;

            ny = center_of_gravity.y() <
                 other_rect.center_of_gravity.y() ? -1 : 1;
        }

        // 4. Correct position
        pos.x(pos.x() + nx * penetration);
        pos.y(pos.y() + ny * penetration);

        center_of_gravity.x(pos.x() + width / 2f);
        center_of_gravity.y(pos.y() + height / 2f);

        // 5. Calculate relative velocity along the normal
        float rvx = vel.x() - other_rect.vel.x();
        float rvy = vel.y() - other_rect.vel.y();

        float velocityAlongNormal = rvx * nx + rvy * ny;

        // 6. Only apply impulse if objects are approaching
        if (velocityAlongNormal >= 0) return;

        // 7. Calculate impulse (other rectangle is stationary)
        float j = -(1 + restitution) * velocityAlongNormal;

        // 8. Apply impulse to this dynamic rectangle
        vel.x(vel.x() + j * nx);
        vel.y(vel.y() + j * ny);
    }
}