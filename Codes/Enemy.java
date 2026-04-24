package Codes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.List;
import javax.swing.ImageIcon;

public class Enemy extends GameObject {
    private static final int   SPEED           = 1;
    private static final int   ATTACK_RANGE    = 80;

    // How many pixels to stand off from an obstacle edge when picking a waypoint
    private static final int   STANDOFF        = 20;

    // State
    private enum State  { WALK, ATTACK }
    private enum Facing { UP, DOWN, LEFT, RIGHT }
    private State  state  = State.WALK;
    private Facing facing = Facing.DOWN;

    // Waypoint: when non-null the enemy walks to this point before re-targeting the player
    private int[] waypoint = null;

    // Cooldown prevents hammering a new waypoint every frame while still blocked
    private int waypointCooldown = 0;
    private static final int WAYPOINT_COOLDOWN_MAX = 30; // frames

    // Walk sprites
    private Image[] walkUp, walkDown, walkLeft, walkRight;

    // Attack sprites
    private Image[] attackUp, attackDown, attackLeft, attackRight;

    private Image currentImage;
    private int panelWidth, panelHeight;
    private int frameIndex    = 0;
    private int animationTick = 0;
    private static final int WALK_ANIM_SPEED   = 5;
    private static final int ATTACK_ANIM_SPEED = 4;
    private boolean attackLanded = false;

    public Enemy(int x, int y, int panelWidth, int panelHeight) {
        super(x, y, 60, 60);
        this.panelWidth  = panelWidth;
        this.panelHeight = panelHeight;

        walkUp    = loadStrip("Entities/Enemy/up",    4);
        walkDown  = loadStrip("Entities/Enemy/down",  4);
        walkLeft  = loadStrip("Entities/Enemy/left",  4);
        walkRight = loadStrip("Entities/Enemy/right", 4);

        attackUp    = loadStrip("Entities/Enemy/attack_up",    4);
        attackDown  = loadStrip("Entities/Enemy/attack_down",  4);
        attackLeft  = loadStrip("Entities/Enemy/attack_left",  4);
        attackRight = loadStrip("Entities/Enemy/attack_right", 4);

        currentImage = walkDown[0];
    }

    // ── Utility: load numbered sprite strip ───────────────────────────────────
    private Image[] loadStrip(String prefix, int count) {
        Image[] frames = new Image[count];
        for (int i = 0; i < count; i++) {
            frames[i] = new ImageIcon(prefix + (i + 1) + ".png").getImage();
        }
        return frames;
    }

    // ── Respawn ───────────────────────────────────────────────────────────────
    public void respawn() {
        int side = (int)(Math.random() * 4);
        switch (side) {
            case 0 -> { x = (int)(Math.random() * panelWidth);  y = 0; }
            case 1 -> { x = (int)(Math.random() * panelWidth);  y = panelHeight - height; }
            case 2 -> { x = 0;                                   y = (int)(Math.random() * panelHeight); }
            case 3 -> { x = panelWidth - width;                  y = (int)(Math.random() * panelHeight); }
        }
        state         = State.WALK;
        waypoint      = null;
        attackLanded  = false;
        frameIndex    = 0;
        animationTick = 0;
    }

    // ── Main update ───────────────────────────────────────────────────────────
    public boolean moveTowards(int targetX, int targetY, List<Obstacle> obstacles) {
        int centerX = x + width  / 2;
        int centerY = y + height / 2;

        double dx   = targetX - centerX;
        double dy   = targetY - centerY;
        double dist = Math.sqrt(dx * dx + dy * dy);

        // ── State switch ──────────────────────────────────────────────────────
        if (dist <= ATTACK_RANGE) {
            state = State.ATTACK;
        } else if (state == State.ATTACK && isAttackFinished()) {
            state        = State.WALK;
            attackLanded = false;
            frameIndex   = 0;
            animationTick = 0;
        }

        if (state == State.ATTACK) {
            boolean damageFrame = tickAttackAnimation(getAttackStrip());
            return damageFrame && !attackLanded;
        }

        // ── Determine move target (waypoint or player) ────────────────────────
        int goalX, goalY;
        if (waypoint != null) {
            // Check if we've arrived at the waypoint (within SPEED radius)
            double wpDx = waypoint[0] - centerX;
            double wpDy = waypoint[1] - centerY;
            if (Math.sqrt(wpDx * wpDx + wpDy * wpDy) < SPEED + 2) {
                waypoint = null; // reached it, go back to chasing player
            }
        }

        if (waypoint != null) {
            goalX = waypoint[0];
            goalY = waypoint[1];
        } else {
            goalX = targetX;
            goalY = targetY;
        }

        // ── Build move vector toward goal ──────────────────────────────────────
        double gDx  = goalX - centerX;
        double gDy  = goalY - centerY;
        double gLen = Math.sqrt(gDx * gDx + gDy * gDy);
        if (gLen == 0) return false;

        double moveX = (gDx / gLen) * SPEED;
        double moveY = (gDy / gLen) * SPEED;

        // ── Try to move; if blocked, pick a waypoint around the obstacle ───────
        int newX = Math.max(0, Math.min(panelWidth  - width,  (int) Math.round(x + moveX)));
        int newY = Math.max(0, Math.min(panelHeight - height, (int) Math.round(y + moveY)));

        Obstacle blocking = getBlockingObstacle(newX, newY, obstacles);

        if (blocking == null) {
            // Clear path — move normally
            x = newX;
            y = newY;
            waypointCooldown = 0;
        } else {
            // Blocked — try axis-separated slides first
            boolean movedX = false, movedY = false;

            int slideXx = Math.max(0, Math.min(panelWidth  - width,  (int) Math.round(x + moveX)));
            if (getBlockingObstacle(slideXx, y, obstacles) == null) {
                x = slideXx;
                movedX = true;
            }

            int slideYy = Math.max(0, Math.min(panelHeight - height, (int) Math.round(y + moveY)));
            if (getBlockingObstacle(x, slideYy, obstacles) == null) {
                y = slideYy;
                movedY = true;
            }

            // Still fully blocked on the dominant axis — pick a waypoint to go around
            if (!movedX && !movedY && waypointCooldown <= 0) {
                waypoint = pickWaypoint(blocking, targetX, targetY, centerX, centerY);
                waypointCooldown = WAYPOINT_COOLDOWN_MAX;
            }

            if (waypointCooldown > 0) waypointCooldown--;
        }

        // ── Facing & animation ─────────────────────────────────────────────────
        double actualMoveX = (x + width  / 2.0) - centerX;
        double actualMoveY = (y + height / 2.0) - centerY;
        if (actualMoveX != 0 || actualMoveY != 0) {
            updateFacing(actualMoveX, actualMoveY);
        }
        updateAnimation(getWalkStrip(), WALK_ANIM_SPEED);
        return false;
    }

    // ── Waypoint picker ───────────────────────────────────────────────────────
    /**
     * Picks one of the 4 corners of the blocking obstacle's bounds (expanded by
     * STANDOFF), choosing whichever corner is closest to the player while still
     * being on the same side as the enemy.
     */
    private int[] pickWaypoint(Obstacle obs, int targetX, int targetY,
                                int enemyCX, int enemyCY) {
        Rectangle b = obs.getBounds();

        // Expand bounds by STANDOFF so the waypoint clears the obstacle edge
        int left   = b.x - STANDOFF;
        int right  = b.x + b.width  + STANDOFF;
        int top    = b.y - STANDOFF;
        int bottom = b.y + b.height + STANDOFF;

        // Four candidate corner waypoints
        int[][] corners = {
            { left,  top    },
            { right, top    },
            { left,  bottom },
            { right, bottom }
        };

        // Score each corner: prefer ones that are (a) closer to the player and
        // (b) not behind us (dot product with desired direction > 0)
        double desiredDx = targetX - enemyCX;
        double desiredDy = targetY - enemyCY;
        double desiredLen = Math.sqrt(desiredDx * desiredDx + desiredDy * desiredDy);
        if (desiredLen > 0) { desiredDx /= desiredLen; desiredDy /= desiredLen; }

        int[]  best      = corners[0];
        double bestScore = Double.MAX_VALUE;

        for (int[] corner : corners) {
            double toDx   = corner[0] - enemyCX;
            double toDy   = corner[1] - enemyCY;
            double toLen  = Math.sqrt(toDx * toDx + toDy * toDy);
            double dot    = (toLen > 0) ? (toDx / toLen * desiredDx + toDy / toLen * desiredDy) : 0;

            // Distance from corner to final target (player)
            double fromCornerToTarget = Math.sqrt(
                Math.pow(corner[0] - targetX, 2) + Math.pow(corner[1] - targetY, 2)
            );

            // Only consider corners roughly in front of us (dot > -0.3)
            if (dot > -0.3 && fromCornerToTarget < bestScore) {
                bestScore = fromCornerToTarget;
                best      = corner;
            }
        }

        // Clamp waypoint to panel bounds
        best[0] = Math.max(width  / 2, Math.min(panelWidth  - width  / 2, best[0]));
        best[1] = Math.max(height / 2, Math.min(panelHeight - height / 2, best[1]));
        return best;
    }

    // ── Collision helper 
    /** Returns the first obstacle blocking position (nx, ny), or null if clear. */
    private Obstacle getBlockingObstacle(int nx, int ny, List<Obstacle> obstacles) {
        Rectangle test = new Rectangle(nx + 8, ny + (height - 12), width - 16, 12);
        for (Obstacle obs : obstacles) {
            if (test.intersects(obs.getBounds())) return obs;
        }
        return null;
    }

    // ── Attack animation ──────────────────────────────────────────────────────
    private boolean tickAttackAnimation(Image[] frames) {
        animationTick++;
        boolean isDamageFrame = false;
        if (animationTick >= ATTACK_ANIM_SPEED) {
            animationTick = 0;
            if (frameIndex < frames.length - 1) frameIndex++;
            if (frameIndex == 2) {
                isDamageFrame = true;
                attackLanded  = true;
            }
        }
        currentImage = frames[frameIndex];
        return isDamageFrame;
    }

    private boolean isAttackFinished() {
        return frameIndex >= getAttackStrip().length - 1;
    }

    // Direction helpers 
    private void updateFacing(double moveX, double moveY) {
        if (Math.abs(moveX) > Math.abs(moveY))
            facing = (moveX > 0) ? Facing.RIGHT : Facing.LEFT;
        else
            facing = (moveY > 0) ? Facing.DOWN : Facing.UP;
    }

    private Image[] getWalkStrip() {
        return switch (facing) {
            case UP    -> walkUp;
            case DOWN  -> walkDown;
            case LEFT  -> walkLeft;
            case RIGHT -> walkRight;
        };
    }

    private Image[] getAttackStrip() {
        return switch (facing) {
            case UP    -> attackUp;
            case DOWN  -> attackDown;
            case LEFT  -> attackLeft;
            case RIGHT -> attackRight;
        };
    }

    private void updateAnimation(Image[] frames, int speed) {
        animationTick++;
        if (animationTick >= speed) {
            animationTick = 0;
            frameIndex    = (frameIndex + 1) % frames.length;
        }
        currentImage = frames[frameIndex];
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x + 8, y + (height - 12), width - 16, 12);
    }

    @Override
    public void draw(Graphics g) {
        if (currentImage != null && currentImage.getWidth(null) != -1) {
            g.drawImage(currentImage, x, y, width, height, null);
        } else {
            g.setColor(Color.RED);
            g.fillOval(x, y, width, height);
        }
    }

    public boolean isAttacking() { return state == State.ATTACK; }
}