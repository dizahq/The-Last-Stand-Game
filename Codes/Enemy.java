package Codes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.*;
import javax.swing.ImageIcon;

public class Enemy extends GameObject {
    private static final int SPEED             = 1;
    private static final int ATTACK_RANGE      = 50;
    private static final int CELL              = 16;
    private static final int PATH_REFRESH      = 60;
    private static final int MOVE_THRESHOLD    = 50;
    private static final int CONTACT_COOLDOWN_MAX = 90;

    private int contactCooldown = 0;
    private int lastTargetX     = -1;
    private int lastTargetY     = -1;
    private int stuckTimer      = 0;
    private int lastX, lastY;

    private enum State  { WALK, ATTACK }
    private enum Facing { UP, DOWN, LEFT, RIGHT }
    private State  state  = State.WALK;
    private Facing facing = Facing.DOWN;

    private final Deque<int[]> path = new ArrayDeque<>();
    private int pathCooldown = 0;

    private Image[] walkUp, walkDown, walkLeft, walkRight;
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
        lastX = x; lastY = y;
    }

    private Image[] loadStrip(String prefix, int count) {
        Image[] frames = new Image[count];
        for (int i = 0; i < count; i++)
            frames[i] = new ImageIcon(prefix + (i + 1) + ".png").getImage();
        return frames;
    }

    public boolean canContactDamage()  { return contactCooldown <= 0; }
    public void resetContactCooldown() { contactCooldown = CONTACT_COOLDOWN_MAX; }

    public void respawn() {
        int side = (int)(Math.random() * 4);
        switch (side) {
            case 0 -> { x = (int)(Math.random() * panelWidth);  y = 0; }
            case 1 -> { x = (int)(Math.random() * panelWidth);  y = panelHeight - height; }
            case 2 -> { x = 0; y = (int)(Math.random() * panelHeight); }
            case 3 -> { x = panelWidth - width; y = (int)(Math.random() * panelHeight); }
        }

        state           = State.WALK;
        attackLanded    = false;
        frameIndex      = 0;
        animationTick   = 0;
        contactCooldown = 0;
        lastTargetX     = -1;
        lastTargetY     = -1;
        stuckTimer      = 0;
        lastX = x; lastY = y;
        path.clear();
        pathCooldown = 0;
    }

    public boolean moveTowards(int targetX, int targetY, List<Obstacle> obstacles) {
        if (contactCooldown > 0) contactCooldown--;

        int centerX = x + width  / 2;
        int centerY = y + height / 2;
        double dist = Math.hypot(targetX - centerX, targetY - centerY);

        // State switch
        if (dist <= ATTACK_RANGE) {
            state = State.ATTACK;
        } else if (state == State.ATTACK && isAttackFinished()) {
            state         = State.WALK;
            attackLanded  = false;
            frameIndex    = 0;
            animationTick = 0;
        }

        if (state == State.ATTACK) {
            boolean dmg = tickAttackAnimation(getAttackStrip());
            return dmg && !attackLanded;
        }

        // ── FIX 1: Stuck detection now resets stuckTimer when not stuck,
        //    and forces a replan with a small random nudge so the enemy
        //    doesn't freeze against the same obstacle forever.
        if (Math.abs(x - lastX) < 1 && Math.abs(y - lastY) < 1) {
            stuckTimer++;
            if (stuckTimer > 30) {
                path.clear();
                pathCooldown = 0;
                stuckTimer   = 0;

                // Nudge the enemy slightly away from its current position
                // so A* doesn't immediately plan a path into the same wall.
                int nudgeX = (int)((Math.random() * 2 - 1) * CELL * 2);
                int nudgeY = (int)((Math.random() * 2 - 1) * CELL * 2);
                x = Math.max(0, Math.min(panelWidth  - width,  x + nudgeX));
                y = Math.max(0, Math.min(panelHeight - height, y + nudgeY));
            }
        } else {
            stuckTimer = 0;
        }
        lastX = x; lastY = y;

        // Recalculate center after possible nudge
        centerX = x + width  / 2;
        centerY = y + height / 2;

        boolean playerMoved = Math.hypot(targetX - lastTargetX, targetY - lastTargetY) > MOVE_THRESHOLD;

        if (pathCooldown <= 0 || path.isEmpty() || playerMoved) {
            List<int[]> newPath = astar(centerX, centerY, targetX, targetY, obstacles);
            path.clear();
            if (newPath != null) path.addAll(newPath);
            pathCooldown = PATH_REFRESH;
            lastTargetX  = targetX;
            lastTargetY  = targetY;
        }
        pathCooldown--;

        // Follow path
        if (!path.isEmpty()) {
            int[] next = path.peek();
            double dx  = next[0] - centerX;
            double dy  = next[1] - centerY;
            double len = Math.hypot(dx, dy);

            if (len <= CELL) {
                path.poll();
                updateAnimation(getWalkStrip(), WALK_ANIM_SPEED);
                return false;
            }

            double moveX = (dx / len) * SPEED;
            double moveY = (dy / len) * SPEED;

            int newX = Math.max(0, Math.min(panelWidth  - width,  (int) Math.round(x + moveX)));
            int newY = Math.max(0, Math.min(panelHeight - height, (int) Math.round(y + moveY)));

            // ── FIX 2: When fully blocked, increment stuckTimer immediately
            //    instead of forcing an instant replan. This prevents the
            //    path.clear() / pathCooldown = 0 thrash loop that caused
            //    the enemy to freeze (it was replanning and re-blocking
            //    every single frame). The stuckTimer block above will
            //    handle the replan cleanly after 30 frames.
            if (getBlockingObstacle(newX, newY, obstacles) == null) {
                x = newX;
                y = newY;
            } else if (getBlockingObstacle(newX, y, obstacles) == null) {
                x = newX;
            } else if (getBlockingObstacle(x, newY, obstacles) == null) {
                y = newY;
            } else {
                // Fully blocked: let stuckTimer handle the replan
                // (removed the instant path.clear() that caused freezing)
                stuckTimer += 5; // accelerate stuck detection when wall-blocked
            }

            double actualDx = (x + width  / 2.0) - centerX;
            double actualDy = (y + height / 2.0) - centerY;
            if (actualDx != 0 || actualDy != 0) updateFacing(actualDx, actualDy);
        }

        updateAnimation(getWalkStrip(), WALK_ANIM_SPEED);
        return false;
    }

    // A* pathfinder 
    private List<int[]> astar(int startX, int startY, int goalX, int goalY,
                               List<Obstacle> obstacles) {
        int cols = panelWidth  / CELL + 1;
        int rows = panelHeight / CELL + 1;

        int sc = Math.max(0, Math.min(cols - 1, startX / CELL));
        int sr = Math.max(0, Math.min(rows - 1, startY / CELL));
        int gc = Math.max(0, Math.min(cols - 1, goalX  / CELL));
        int gr = Math.max(0, Math.min(rows - 1, goalY  / CELL));

        int inflate = (Math.max(width, height) / 2) / CELL + 1;

        boolean[][] blocked = new boolean[rows][cols];
        for (Obstacle obs : obstacles) {
            Rectangle b  = obs.getBounds();
            int minC = Math.max(0, b.x / CELL - inflate);
            int maxC = Math.min(cols - 1, (b.x + b.width)  / CELL + inflate);
            int minR = Math.max(0, b.y / CELL - inflate);
            int maxR = Math.min(rows - 1, (b.y + b.height) / CELL + inflate);
            for (int r = minR; r <= maxR; r++)
                for (int c = minC; c <= maxC; c++)
                    blocked[r][c] = true;
        }

        int clearRadius = inflate; // same radius used to inflate
        for (int dr = -clearRadius; dr <= clearRadius; dr++) {
            for (int dc = -clearRadius; dc <= clearRadius; dc++) {
                int r = sr + dr, c = sc + dc;
                if (r >= 0 && r < rows && c >= 0 && c < cols) blocked[r][c] = false;
                r = gr + dr; c = gc + dc;
                if (r >= 0 && r < rows && c >= 0 && c < cols) blocked[r][c] = false;
            }
        }

        int[][] gCost  = new int[rows][cols];
        int[][] parent = new int[rows][cols];
        for (int[] row : gCost)  Arrays.fill(row, Integer.MAX_VALUE);
        for (int[] row : parent) Arrays.fill(row, -1);

        PriorityQueue<int[]> open = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        gCost[sr][sc] = 0;
        open.offer(new int[]{ heuristic(sc, sr, gc, gr), sc, sr });

        int[][] dirs = {
            {-1,-1},{0,-1},{1,-1},
            {-1, 0},       {1, 0},
            {-1, 1},{0, 1},{1, 1}
        };

        boolean found = false;
        while (!open.isEmpty()) {
            int[] cur = open.poll();
            int cc = cur[1], cr = cur[2];
            if (cc == gc && cr == gr) { found = true; break; }

            for (int[] d : dirs) {
                int nc = cc + d[0], nr = cr + d[1];
                if (nc < 0 || nc >= cols || nr < 0 || nr >= rows) continue;
                if (blocked[nr][nc]) continue;

                if (d[0] != 0 && d[1] != 0) {
                    if (blocked[cr][cc + d[0]] || blocked[cr + d[1]][cc]) continue;
                }

                int step = (d[0] != 0 && d[1] != 0) ? 14 : 10;
                int ng   = gCost[cr][cc] + step;

                if (ng < gCost[nr][nc]) {
                    gCost[nr][nc]  = ng;
                    parent[nr][nc] = cr * cols + cc;
                    open.offer(new int[]{ ng + heuristic(nc, nr, gc, gr), nc, nr });
                }
            }
        }

        if (!found) return null;

        List<int[]> waypoints = new ArrayList<>();
        int cc = gc, cr = gr;
        while (!(cc == sc && cr == sr)) {
            waypoints.add(0, new int[]{ cc * CELL + CELL / 2, cr * CELL + CELL / 2 });
            int p = parent[cr][cc];
            if (p < 0) break;
            int prevR = p / cols;
            int prevC = p % cols;
            cr = prevR;
            cc = prevC;
        }

        if (waypoints.isEmpty())
            waypoints.add(new int[]{ goalX, goalY });

        return waypoints;
    }

    private int heuristic(int c1, int r1, int c2, int r2) {
        int dx = Math.abs(c1 - c2);
        int dy = Math.abs(r1 - r2);
        return 10 * Math.max(dx, dy) + (dx + dy);
    }

    private Obstacle getBlockingObstacle(int nx, int ny, List<Obstacle> obstacles) {
        Rectangle test = new Rectangle(nx, ny, width, height);
        for (Obstacle obs : obstacles)
            if (test.intersects(obs.getBounds())) return obs;
        return null;
    }

    // ── Attack animation ──────────────────────────────────────────────────────
    private boolean tickAttackAnimation(Image[] frames) {
        animationTick++;
        boolean isDamageFrame = false;
        if (animationTick >= ATTACK_ANIM_SPEED) {
            animationTick = 0;
            if (frameIndex < frames.length - 1) frameIndex++;
            if (frameIndex == 2) { isDamageFrame = true; attackLanded = true; }
        }
        currentImage = frames[frameIndex];
        return isDamageFrame;
    }

    private boolean isAttackFinished() {
        return frameIndex >= getAttackStrip().length - 1;
    }

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
            frameIndex = (frameIndex + 1) % frames.length;
        }
        currentImage = frames[frameIndex];
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
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
