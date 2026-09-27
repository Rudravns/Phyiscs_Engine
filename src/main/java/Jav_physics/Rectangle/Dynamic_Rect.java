package Jav_physics.Rectangle;

import java.util.List;
import java.util.Map;
import Jav_physics.Jav_physics;

public class Dynamic_Rect extends Rect {

    public Dynamic_Rect(float x, float y, float width, float height) {
        this(x, y, width, height, 1.0f);
    }

    public Dynamic_Rect(float x, float y, float width, float height, float mass) {
        super(x, y, width, height, mass);
        super.type = Jav_physics.Rect_types.Dynamic;
        recalculateMassAndInertia();
        registerRect(this);
    }

    @Override
    public void step(Map<String, List<Object>> rect_list) {
        integrate();

        // Resolve collisions against all other rects
        rect_list.forEach((key, list) -> {
            for (Object obj : list) {
                if (obj instanceof Rect) {
                    Rect other = (Rect) obj;
                    if (other != this) {
                        collide(other);
                    }
                }
            }
        });

        if (Math.abs(angularVelocity) <= 0.1f) {
            angularVelocity = 0f;
        }
    }

    /**
     * Integrates linear forces, persistent acceleration, torque, and velocities.
     */
    public void integrate() {
        // Integrate forces into linear acceleration
        float accFx = (invMass > 0f) ? (force.x() * invMass) : 0f;
        float accFy = (invMass > 0f) ? (force.y() * invMass) : 0f;

        vel.x(vel.x() + acc.x() + accFx);
        vel.y(vel.y() + acc.y() + accFy);

        // Linear damping (air resistance)
        if (linearDamping > 0f) {
            float factor = Math.max(0f, 1f - linearDamping);
            vel.x(vel.x() * factor);
            vel.y(vel.y() * factor);
        }

        // Positional movement
        pos.x(pos.x() + vel.x());
        pos.y(pos.y() + vel.y());

        // Rotational integration
        if (!fixedRotation && invInertia > 0f) {
            float alphaRad = torque * invInertia;
            float alphaDeg = (float) Math.toDegrees(alphaRad);
            angularVelocity += angularAcceleration + alphaDeg;

            // Angular damping
            if (angularDamping > 0f) {
                float factor = Math.max(0f, 1f - angularDamping);
                angularVelocity *= factor;
            }

            rot += angularVelocity;
            rot = rot % 360f;
            if (rot < 0f) rot += 360f;
        }

        // Reset transient forces and torque
        force.x(0f);
        force.y(0f);
        torque = 0f;
    }

    public void collide(Rect other) {
        if (!this.overlapping(other)) return;

        if (Math.abs(this.rot) < 0.001f && Math.abs(other.rot) < 0.001f) {
            collideAABB(other);
        } else {
            collideSAT(other);
        }
    }

    protected void collideAABB(Rect other) {
        float minAx = pos.x();
        float maxAx = pos.x() + width;
        float minAy = pos.y();
        float maxAy = pos.y() + height;

        float minBx = other.pos.x();
        float maxBx = other.pos.x() + other.width;
        float minBy = other.pos.y();
        float maxBy = other.pos.y() + other.height;

        float overlapX = Math.min(maxAx, maxBx) - Math.max(minAx, minBx);
        float overlapY = Math.min(maxAy, maxBy) - Math.max(minAy, minBy);

        if (overlapX <= 0f || overlapY <= 0f) return;

        float nx = 0f, ny = 0f;
        float penetration;

        float cAx = pos.x() + width / 2f;
        float cAy = pos.y() + height / 2f;
        float cBx = other.pos.x() + other.width / 2f;
        float cBy = other.pos.y() + other.height / 2f;

        float px, py;

        if (overlapX < overlapY) {
            penetration = overlapX;
            // Normal points from other (B) to this (A)
            nx = (cAx < cBx) ? -1f : 1f;
            ny = 0f;
            px = (cAx < cBx) ? maxAx : minAx;
            py = (Math.max(minAy, minBy) + Math.min(maxAy, maxBy)) / 2f;
        } else {
            penetration = overlapY;
            nx = 0f;
            // Normal points from other (B) to this (A)
            ny = (cAy < cBy) ? -1f : 1f;
            px = (Math.max(minAx, minBx) + Math.min(maxAx, maxBx)) / 2f;
            py = (cAy < cBy) ? maxAy : minAy;
        }

        resolveContact(other, nx, ny, penetration, px, py);
    }

    protected void collideSAT(Rect other) {
        float[][] cornersA = this.getCorners();
        float[][] cornersB = other.getCorners();

        float[][] axes = new float[][] {
            this.getAxisX(),
            this.getAxisY(),
            other.getAxisX(),
            other.getAxisY()
        };

        float minOverlap = Float.MAX_VALUE;
        float bestNx = 0f;
        float bestNy = 0f;

        for (float[] axis : axes) {
            float len = (float) Math.hypot(axis[0], axis[1]);
            if (len < 1e-6f) continue;
            float ax = axis[0] / len;
            float ay = axis[1] / len;

            float minA = Float.MAX_VALUE, maxA = -Float.MAX_VALUE;
            for (float[] pt : cornersA) {
                float proj = pt[0] * ax + pt[1] * ay;
                if (proj < minA) minA = proj;
                if (proj > maxA) maxA = proj;
            }

            float minB = Float.MAX_VALUE, maxB = -Float.MAX_VALUE;
            for (float[] pt : cornersB) {
                float proj = pt[0] * ax + pt[1] * ay;
                if (proj < minB) minB = proj;
                if (proj > maxB) maxB = proj;
            }

            float overlap = Math.min(maxA, maxB) - Math.max(minA, minB);
            if (overlap <= 0f) return;

            if (overlap < minOverlap) {
                minOverlap = overlap;
                bestNx = ax;
                bestNy = ay;
            }
        }

        // Ensure normal points from other (B) to this (A)
        float cAx = this.pos.x() + this.width / 2f;
        float cAy = this.pos.y() + this.height / 2f;
        float cBx = other.pos.x() + other.width / 2f;
        float cBy = other.pos.y() + other.height / 2f;

        float dirX = cAx - cBx;
        float dirY = cAy - cBy;

        if (dirX * bestNx + dirY * bestNy < 0f) {
            bestNx = -bestNx;
            bestNy = -bestNy;
        }

        // Find contact point: vertex of A deepest along -normal
        float minProjA = Float.MAX_VALUE;
        float[] deepA = cornersA[0];
        for (float[] pt : cornersA) {
            float proj = pt[0] * bestNx + pt[1] * bestNy;
            if (proj < minProjA) {
                minProjA = proj;
                deepA = pt;
            }
        }

        float px = deepA[0] - bestNx * (minOverlap * 0.5f);
        float py = deepA[1] - bestNy * (minOverlap * 0.5f);

        resolveContact(other, bestNx, bestNy, minOverlap, px, py);
    }

    /**
     * Resolves contact dynamics: positional separation, normal restitution impulse,
     * Coulomb friction (static & kinetic), and rotational torque/momentum transfer.
     */
    protected void resolveContact(Rect other, float nx, float ny, float penetration, float px, float py) {
        float wA = this.invMass;
        float wB = other.invMass;
        float totalInvMass = wA + wB;

        if (totalInvMass <= 0f) return;

        // 1. Positional correction (prevents sinking/tunneling)
        float percent = 1.0f;
        float slop = 0.0f;
        float correction = Math.max(penetration - slop, 0.0f) / totalInvMass * percent;

        if (correction > 0f) {
            if (this.type == Jav_physics.Rect_types.Dynamic) {
                this.pos.x(this.pos.x() + nx * correction * wA);
                this.pos.y(this.pos.y() + ny * correction * wA);
            }
            if (other.type == Jav_physics.Rect_types.Dynamic) {
                other.pos.x(other.pos.x() - nx * correction * wB);
                other.pos.y(other.pos.y() - ny * correction * wB);
            }
        }

        // 2. Relative contact velocity calculation
        float cAx = this.pos.x() + this.width / 2f;
        float cAy = this.pos.y() + this.height / 2f;
        float cBx = other.pos.x() + other.width / 2f;
        float cBy = other.pos.y() + other.height / 2f;

        float rAx = px - cAx;
        float rAy = py - cAy;
        float rBx = px - cBx;
        float rBy = py - cBy;

        float omegaA = (float) Math.toRadians(this.angularVelocity);
        float omegaB = (float) Math.toRadians(other.angularVelocity);

        // Contact velocity = v + omega x r = (v.x - omega * r.y, v.y + omega * r.x)
        float uAx = this.vel.x() - omegaA * rAy;
        float uAy = this.vel.y() + omegaA * rAx;

        float uBx = other.vel.x() - omegaB * rBy;
        float uBy = other.vel.y() + omegaB * rBx;

        float rvx = uAx - uBx;
        float rvy = uAy - uBy;

        // Velocity along normal
        float vn = rvx * nx + rvy * ny;

        // If separating, no normal impulse required
        if (vn >= 0f) return;

        float iA = (!this.fixedRotation) ? this.invInertia : 0f;
        float iB = (!other.fixedRotation) ? other.invInertia : 0f;

        // 2D cross product: r x n = rx * ny - ry * nx
        float rnA = rAx * ny - rAy * nx;
        float rnB = rBx * ny - rBy * nx;

        float kn = totalInvMass + (rnA * rnA) * iA + (rnB * rnB) * iB;
        if (kn <= 0f) return;

        // Restitution with resting velocity threshold to eliminate jitter
        float e = Math.min(this.restitution, other.restitution);
        if (Math.abs(vn) < 0.25f) {
            e = 0.0f;
        }

        // Normal impulse scalar
        float jn = -(1.0f + e) * vn / kn;
        if (jn < 0f) jn = 0f;

        // 3. Friction impulse along tangent vector t = (-ny, nx)
        float tx = -ny;
        float ty = nx;

        float vt = rvx * tx + rvy * ty;

        // 2D cross product: r x t = rx * ty - ry * tx
        float rtA = rAx * ty - rAy * tx;
        float rtB = rBx * ty - rBy * tx;

        float kt = totalInvMass + (rtA * rtA) * iA + (rtB * rtB) * iB;

        float mu_s_combined = (float) Math.sqrt(this.mu_s * other.mu_s);
        float mu_k_combined = (float) Math.sqrt(this.mu_k * other.mu_k);

        float jt = 0f;
        if (kt > 0f) {
            float desiredJt = -vt / kt;
            if (Math.abs(desiredJt) <= mu_s_combined * jn) {
                // Static friction: stick without sliding
                jt = desiredJt;
            } else {
                // Kinetic friction: slide with resisting force
                jt = -Math.signum(vt) * mu_k_combined * jn;
            }
        }

        // Total contact impulse: J = jn * n + jt * t
        float jx = jn * nx + jt * tx;
        float jy = jn * ny + jt * ty;

        // Apply impulse to Body A
        if (this.type == Jav_physics.Rect_types.Dynamic) {
            this.vel.x(this.vel.x() + jx * wA);
            this.vel.y(this.vel.y() + jy * wA);

            if (!this.fixedRotation && iA > 0f) {
                float torqueImpulseA = rAx * jy - rAy * jx;

                // Contact surface normal moment stability:
                // Only when contacting with a flat edge does distributed normal pressure resist tipping.
                // When tilted on a corner, torque rotates the body freely.
                boolean isEdgeContactA = (Math.abs(this.rot % 90f) < 1.0f || Math.abs(this.rot % 90f) > 89.0f);
                if (isEdgeContactA) {
                    if (Math.abs(ny) > 0.7f) {
                        float maxStabilizingTorque = (width / 2f) * jn;
                        if (Math.abs(torqueImpulseA) <= maxStabilizingTorque) {
                            torqueImpulseA = 0f;
                        } else {
                            torqueImpulseA -= Math.signum(torqueImpulseA) * maxStabilizingTorque;
                        }
                    } else if (Math.abs(nx) > 0.7f) {
                        float maxStabilizingTorque = (height / 2f) * jn;
                        if (Math.abs(torqueImpulseA) <= maxStabilizingTorque) {
                            torqueImpulseA = 0f;
                        } else {
                            torqueImpulseA -= Math.signum(torqueImpulseA) * maxStabilizingTorque;
                        }
                    }
                }

                float deltaOmegaRadA = torqueImpulseA * iA;
                this.angularVelocity += (float) Math.toDegrees(deltaOmegaRadA);
            }
        }

        // Apply negative impulse to Body B
        if (other.type == Jav_physics.Rect_types.Dynamic) {
            other.vel.x(other.vel.x() - jx * wB);
            other.vel.y(other.vel.y() - jy * wB);

            if (!other.fixedRotation && iB > 0f) {
                float torqueImpulseB = rBx * jy - rBy * jx;

                boolean isEdgeContactB = (Math.abs(other.rot % 90f) < 1.0f || Math.abs(other.rot % 90f) > 89.0f);
                if (isEdgeContactB) {
                    if (Math.abs(ny) > 0.7f) {
                        float maxStabilizingTorque = (other.width / 2f) * jn;
                        if (Math.abs(torqueImpulseB) <= maxStabilizingTorque) {
                            torqueImpulseB = 0f;
                        } else {
                            torqueImpulseB -= Math.signum(torqueImpulseB) * maxStabilizingTorque;
                        }
                    } else if (Math.abs(nx) > 0.7f) {
                        float maxStabilizingTorque = (other.height / 2f) * jn;
                        if (Math.abs(torqueImpulseB) <= maxStabilizingTorque) {
                            torqueImpulseB = 0f;
                        } else {
                            torqueImpulseB -= Math.signum(torqueImpulseB) * maxStabilizingTorque;
                        }
                    }
                }

                float deltaOmegaRadB = torqueImpulseB * iB;
                other.angularVelocity -= (float) Math.toDegrees(deltaOmegaRadB);
            }
        }

        float angleFromUpright = Math.abs(this.rot % 360f);
        angleFromUpright = Math.min(angleFromUpright, 360f - angleFromUpright);
        if (other.type == Jav_physics.Rect_types.Static
                && ny < -0.9f
                && Math.abs(vn) < 0.25f
                && angleFromUpright < 3f
                && Math.abs(this.angularVelocity) < 0.5f) {
            this.vel.y(0f);
            this.angularVelocity = 0f;
        }
    }
}
