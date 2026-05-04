package Codes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.*;

public abstract class Enemy extends Entity {

    //Movement and behavior constants 
    private static final int ATTACK_RANGE = 55; //distnace in pixels to trigger attack
    private static final int CELL = 16; // A* grid cell size in pixels 
    private static final int PATH_REFRESH = 60; // frames between path recalculations
    private static final int MOVE_THRESHOLD = 45; //how far player must move to force a replan
    private static final int CONTACT_COOLDOWN_MAX = 90; //frames before enemy can deal contact damage again
    private int strikeCount = 0; //tracks how many swings have happened in current attack cycle

    private int contactCooldown = 0; //countdown timer for contact damage cooldown
    private int lastTargetX = -1; //last known player X for move threshold check
    private int lastTargetY = -1; // last known player Y for move threshold check
    private int stuckTimer = 0; //counts frames thep enemy hasn't moved
    private int lastX, lastY; //position last frame, used for stuck detection

    // Attributes:
    private int speed = 1; //movement speed
    private int health = 1; //number of hits to kill

    //Enemy behavior states
    private enum State  { WALK, ATTACK }
    // Direction the enemy is facing, determines which sprite strip to use
    private enum Facing { UP, DOWN, LEFT, RIGHT }

    private State  state  = State.WALK; //current behavior state
    private State  lastState  = State.WALK; //state from previous frame, used to detect transitions
    private Facing facing = Facing.DOWN; //current facing direction

    //A* pathfinding queue of waypoints to follow
    private final Deque<int[]> path = new ArrayDeque<>();
    private int pathCooldown = 0;//countdown before next path recalculation
    
    //Static sprite strips - shared across all Enemy instances (loaded once from disk)
    private static Image[] walkUp, walkDown, walkLeft, walkRight;
    private static Image[] attackUp, attackDown, attackLeft, attackRight;

    protected int panelWidth, panelHeight; //game panel bound for clamping position

    private static final int WALK_ANIM_SPEED   = 5;
    private static final int ATTACK_ANIM_SPEED = 10;
    private boolean attackLanded = false; // whether the current swing has already dealt damage

    private static final Random missRate = new Random(); //new (missrate)
    private static final int MISS_CHANCE = 50; //new (missRate)

    public Enemy(int x, int y, int panelWidth, int panelHeight) {
        super(x, y, 80, 80);
        this.panelWidth  = panelWidth;
        this.panelHeight = panelHeight;

        // Load sprites only once — all Enemy instances share the same static arrays
        if (walkDown == null){
            walkUp    = loadStrip("Entities/Enemy/Walk/up",8);
            walkDown  = loadStrip("Entities/Enemy/Walk/down",8);
            walkLeft  = loadStrip("Entities/Enemy/Walk/left",  8);
            walkRight = loadStrip("Entities/Enemy/Walk/right", 8);

            attackUp    = loadStrip("Entities/Enemy/Attack/attack_up",6);
            attackDown  = loadStrip("Entities/Enemy/Attack/attack_down",6);
            attackLeft  = loadStrip("Entities/Enemy/Attack/attack_left",6);
            attackRight = loadStrip("Entities/Enemy/Attack/attack_right",6);
        }

        currentImage = walkDown[0]; // default idle frame facing down
        lastX = x; lastY = y;
    }

    // Returns true if enough time has passed since last contact damage
    public boolean canContactDamage()  { 
        return contactCooldown <= 0; 
    }

    // Resets the cooldown timer after contact damage is dealt
    public void resetContactCooldown() { 
        contactCooldown = CONTACT_COOLDOWN_MAX; 
    }

    // Repositions the enemy at a random edge of the screen and resets all state
    public void respawn() {
        int side = (int)(Math.random() * 4); // pick a random screen edge
        strikeCount = 0;

        switch (side) {
            case 0: //top
                x = (int)(Math.random() * panelWidth);  
                y = 0; 
            break;
            case 1: //bottom
                x = (int)(Math.random() * panelWidth);  
                y = panelHeight - height; 
            break;
            case 2: 
                x = 0; //left
                y = (int)(Math.random() * panelHeight); 
            break;
            case 3: //right
                x = panelWidth - width; 
                y = (int)(Math.random() * panelHeight); 
            break;
        }

        // Reset all state to defaults
        state = State.WALK;
        attackLanded = false;
        frameIndex = 0;
        animationTick = 0;
        contactCooldown = 0;
        lastTargetX = -1;
        lastTargetY = -1;
        stuckTimer = 0;
        lastX = x; 
        lastY = y;
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
            // just switched from WALK to ATTACK — always reset
            if (lastState == State.WALK) {
                attackLanded  = false;
                frameIndex    = 0;
                animationTick = 0;
            }
            // loop attack after finishing a full swing
            else if (isAttackFinished() && attackLanded) {
                attackLanded  = false;
                frameIndex    = 0;
                animationTick = 0;
            }
        } else if (state == State.ATTACK && isAttackFinished()) {
            state         = State.WALK;
            attackLanded  = false;
            frameIndex    = 0;
            animationTick = 0;
            strikeCount = 0;
        }
        lastState = state;

        if (state == State.ATTACK) {
            boolean dmg = tickAttackAnimation(getAttackStrip());
            return dmg;
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

            double moveX = (dx / len) * speed;
            double moveY = (dy / len) * speed;

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
    private List<int[]> astar(int startX, int startY, int goalX, int goalY, List<Obstacle> obstacles) {
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
        for (Obstacle obs : obstacles){
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
            if (frameIndex == 2 && !attackLanded) {
                attackLanded = true;
                strikeCount++;
                // only deal damage on every 1st strike
                if(strikeCount >= 1 && missRate.nextInt(100) >= MISS_CHANCE){
                    isDamageFrame = true;
                    strikeCount = 0; // reset for next 1-strike cycle
                }else{
                    System.out.println("MISS");        //to be replaced by actual miss sound effect and visual cue
                }
            }
        }
        currentImage = frames[frameIndex];
        return isDamageFrame;
    }

    private boolean isAttackFinished() {
        return frameIndex >= getAttackStrip().length - 1;
    }

    private void updateFacing(double moveX, double moveY) {
        if (Math.abs(moveX) > Math.abs(moveY)) facing = (moveX > 0) ? Facing.RIGHT : Facing.LEFT;
        else facing = (moveY > 0) ? Facing.DOWN : Facing.UP;
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

    public void deductHealth(){
        this.health--;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
    public int getHealth() {
        return health;
    }
    public void setHealth(int health) {
        this.health = health;
    }
}
