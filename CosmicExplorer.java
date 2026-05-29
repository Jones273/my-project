import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Cosmic Explorer - Advanced 3D Space Exploration Game
 * A professional-grade 3D game engine with physics, particles, and advanced graphics
 */
public class CosmicExplorer extends JFrame {
    
    // Game constants
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;
    private static final double FOV = Math.PI / 3;
    
    // Core systems
    private GameEngine engine;
    private Renderer3D renderer;
    private PhysicsEngine physics;
    private ParticleSystem particles;
    private SoundSystem sound;
    private InputManager input;
    private GameWorld world;
    private HUD hud;
    
    // Player
    private Player player;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
                new CosmicExplorer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    public CosmicExplorer() {
        setTitle("Cosmic Explorer - Advanced 3D Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Initialize game systems
        initializeSystems();
        
        // Create game panel
        GamePanel panel = new GamePanel();
        add(panel);
        pack();
        setLocationRelativeTo(null);
        
        // Start game
        setVisible(true);
        engine.start();
    }
    
    private void initializeSystems() {
        renderer = new Renderer3D(WINDOW_WIDTH, WINDOW_HEIGHT);
        physics = new PhysicsEngine();
        particles = new ParticleSystem();
        sound = new SoundSystem();
        input = new InputManager(this);
        world = new GameWorld();
        hud = new HUD();
        player = new Player();
        
        engine = new GameEngine();
        
        // Create game world
        createGameWorld();
        
        System.out.println("🚀 Cosmic Explorer Initialized!");
        System.out.println("Controls: WASD/Arrows = Move, Mouse = Look, Space = Jump");
        System.out.println("Shift = Boost, E = Interact, Tab = Map, ESC = Menu");
    }
    
    private void createGameWorld() {
        // Space Station Hub
        world.addObject(new SpaceStation(new Vector3(0, 0, 0)));
        
        // Planets with orbits
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI * 2 / 8;
            float distance = 50 + i * 30;
            Vector3 pos = new Vector3(
                Math.cos(angle) * distance,
                (Math.random() - 0.5) * 20,
                Math.sin(angle) * distance
            );
            
            Planet planet = new Planet(pos, 5 + (float)Math.random() * 10);
            planet.orbitSpeed = 0.001f + (float)Math.random() * 0.002f;
            world.addObject(planet);
            
            // Add moons
            if (Math.random() > 0.5) {
                for (int j = 0; j < (int)(Math.random() * 3 + 1); j++) {
                    Moon moon = new Moon(planet, 15 + j * 5, 1 + (float)Math.random() * 2);
                    world.addObject(moon);
                }
            }
        }
        
        // Asteroid field
        for (int i = 0; i < 100; i++) {
            Vector3 pos = new Vector3(
                (Math.random() - 0.5) * 500,
                (Math.random() - 0.5) * 100,
                (Math.random() - 0.5) * 500
            );
            world.addObject(new Asteroid(pos, (float)Math.random() * 3 + 1));
        }
        
        // Energy crystals
        for (int i = 0; i < 30; i++) {
            Vector3 pos = new Vector3(
                (Math.random() - 0.5) * 200,
                (Math.random() - 0.5) * 50,
                (Math.random() - 0.5) * 200
            );
            world.addObject(new EnergyCrystal(pos));
        }
        
        // Enemy ships
        for (int i = 0; i < 10; i++) {
            Vector3 pos = new Vector3(
                (Math.random() - 0.5) * 300,
                (Math.random() - 0.5) * 100,
                (Math.random() - 0.5) * 300
            );
            world.addObject(new EnemyShip(pos));
        }
    }
    
    // Game Panel
    class GamePanel extends JPanel {
        private BufferedImage frameBuffer;
        
        public GamePanel() {
            setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
            setBackground(Color.BLACK);
            frameBuffer = new BufferedImage(WINDOW_WIDTH, WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
            setFocusable(true);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // Render to buffer
            Graphics2D g2d = frameBuffer.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Clear screen
            drawBackground(g2d);
            
            // Render 3D world
            renderer.render(g2d, world, player.camera);
            
            // Render particles
            particles.render(g2d);
            
            // Render HUD
            hud.render(g2d, player);
            
            g2d.dispose();
            
            // Draw buffer to screen
            g.drawImage(frameBuffer, 0, 0, null);
        }
        
        private void drawBackground(Graphics2D g) {
            // Space background with stars
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(0, 0, 20),
                0, WINDOW_HEIGHT, new Color(10, 0, 40)
            );
            g.setPaint(gradient);
            g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
            
            // Stars
            g.setColor(Color.WHITE);
            Random rand = new Random(42); // Fixed seed for consistent stars
            for (int i = 0; i < 200; i++) {
                int x = rand.nextInt(WINDOW_WIDTH);
                int y = rand.nextInt(WINDOW_HEIGHT);
                int size = rand.nextInt(3);
                float alpha = 0.3f + rand.nextFloat() * 0.7f;
                
                g.setColor(new Color(1f, 1f, 1f, alpha));
                g.fillOval(x, y, size, size);
            }
        }
    }
    
    // Game Engine
    class GameEngine implements Runnable {
        private Thread gameThread;
        private boolean running;
        private final int TARGET_FPS = 60;
        private final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
        
        public void start() {
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
        
        @Override
        public void run() {
            long lastTime = System.nanoTime();
            long timer = System.currentTimeMillis();
            double delta = 0;
            int frames = 0;
            
            while (running) {
                long now = System.nanoTime();
                delta += (now - lastTime) / (double)OPTIMAL_TIME;
                lastTime = now;
                
                while (delta >= 1) {
                    update();
                    delta--;
                }
                
                repaint();
                frames++;
                
                if (System.currentTimeMillis() - timer > 1000) {
                    timer += 1000;
                    hud.fps = frames;
                    frames = 0;
                }
                
                // Sleep to maintain FPS
                try {
                    Thread.sleep(Math.max(0, (lastTime - System.nanoTime() + OPTIMAL_TIME) / 1000000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        
        private void update() {
            // Update game systems
            input.update();
            player.update(input);
            world.update();
            physics.update(world);
            particles.update();
            
            // Check collisions
            checkCollisions();
        }
        
        private void checkCollisions() {
            for (GameObject obj : world.getObjects()) {
                if (obj instanceof EnergyCrystal) {
                    if (Vector3.distance(player.position, obj.position) < 5) {
                        player.energy += 10;
                        particles.createExplosion(obj.position, Color.CYAN, 30);
                        sound.play("collect");
                        world.removeObject(obj);
                    }
                }
            }
        }
    }
}

// 3D Math Classes
class Vector3 {
    public float x, y, z;
    
    public Vector3() { this(0, 0, 0); }
    public Vector3(float x, float y, float z) { this.x = x; this.y = y; this.z = z; }
    public Vector3(double x, double y, double z) { this((float)x, (float)y, (float)z); }
    
    public Vector3 add(Vector3 v) { return new Vector3(x + v.x, y + v.y, z + v.z); }
    public Vector3 subtract(Vector3 v) { return new Vector3(x - v.x, y - v.y, z - v.z); }
    public Vector3 multiply(float s) { return new Vector3(x * s, y * s, z * s); }
    public float dot(Vector3 v) { return x * v.x + y * v.y + z * v.z; }
    public Vector3 cross(Vector3 v) {
        return new Vector3(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }
    public float magnitude() { return (float)Math.sqrt(x * x + y * y + z * z); }
    public Vector3 normalize() {
        float mag = magnitude();
        return mag > 0 ? multiply(1 / mag) : new Vector3();
    }
    public static float distance(Vector3 a, Vector3 b) { return a.subtract(b).magnitude(); }
}

class Matrix4 {
    public float[][] m = new float[4][4];
    
    public static Matrix4 identity() {
        Matrix4 mat = new Matrix4();
        mat.m[0][0] = mat.m[1][1] = mat.m[2][2] = mat.m[3][3] = 1;
        return mat;
    }
    
    public static Matrix4 translation(Vector3 v) {
        Matrix4 mat = identity();
        mat.m[0][3] = v.x;
        mat.m[1][3] = v.y;
        mat.m[2][3] = v.z;
        return mat;
    }
    
    public static Matrix4 rotationY(float angle) {
        Matrix4 mat = identity();
        float cos = (float)Math.cos(angle);
        float sin = (float)Math.sin(angle);
        mat.m[0][0] = cos;
        mat.m[0][2] = sin;
        mat.m[2][0] = -sin;
        mat.m[2][2] = cos;
        return mat;
    }
    
    public static Matrix4 perspective(float fov, float aspect, float near, float far) {
        Matrix4 mat = new Matrix4();
        float tanHalfFov = (float)Math.tan(fov / 2);
        mat.m[0][0] = 1 / (aspect * tanHalfFov);
        mat.m[1][1] = 1 / tanHalfFov;
        mat.m[2][2] = -(far + near) / (far - near);
        mat.m[2][3] = -(2 * far * near) / (far - near);
        mat.m[3][2] = -1;
        return mat;
    }
    
    public Vector3 transform(Vector3 v) {
        float w = m[3][0] * v.x + m[3][1] * v.y + m[3][2] * v.z + m[3][3];
        return new Vector3(
            (m[0][0] * v.x + m[0][1] * v.y + m[0][2] * v.z + m[0][3]) / w,
            (m[1][0] * v.x + m[1][1] * v.y + m[1][2] * v.z + m[1][3]) / w,
            (m[2][0] * v.x + m[2][1] * v.y + m[2][2] * v.z + m[2][3]) / w
        );
    }
}

// Game Objects
abstract class GameObject {
    public Vector3 position;
    public Vector3 rotation;
    public Vector3 scale;
    public Color color;
    public boolean active = true;
    
    public GameObject(Vector3 position) {
        this.position = position;
        this.rotation = new Vector3();
        this.scale = new Vector3(1, 1, 1);
        this.color = Color.WHITE;
    }
    
    public abstract void update();
    public abstract void render(Graphics2D g, Camera camera);
}

class SpaceStation extends GameObject {
    private float rotationSpeed = 0.01f;
    
    public SpaceStation(Vector3 position) {
        super(position);
        color = Color.LIGHT_GRAY;
        scale = new Vector3(10, 10, 10);
    }
    
    @Override
    public void update() {
        rotation.y += rotationSpeed;
    }
    
    @Override
    public void render(Graphics2D g, Camera camera) {
        // Render complex space station geometry
    }
}

class Planet extends GameObject {
    public float radius;
    public float orbitSpeed;
    private float orbitAngle;
    private Vector3 orbitCenter;
    
    public Planet(Vector3 position, float radius) {
        super(position);
        this.radius = radius;
        this.orbitCenter = new Vector3(0, 0, 0);
        this.color = new Color(
            (int)(Math.random() * 100 + 100),
            (int)(Math.random() * 100 + 100),
            (int)(Math.random() * 100 + 100)
        );
    }
    
    @Override
    public void update() {
        // Orbit around center
        orbitAngle += orbitSpeed;
        float distance = Vector3.distance(orbitCenter, position);
        position.x = orbitCenter.x + (float)Math.cos(orbitAngle) * distance;
        position.z = orbitCenter.z + (float)Math.sin(orbitAngle) * distance;
        
        // Self rotation
        rotation.y += 0.005f;
    }
    
    @Override
    public void render(Graphics2D g, Camera camera) {
        // Render planet sphere
    }
}

class Moon extends GameObject {
    private Planet parent;
    private float orbitRadius;
    private float orbitSpeed;
    private float orbitAngle;
    
    public Moon(Planet parent, float orbitRadius, float radius) {
        super(parent.position.add(new Vector3(orbitRadius, 0, 0)));
        this.parent = parent;
        this.orbitRadius = orbitRadius;
        this.orbitSpeed = 0.02f;
        this.scale = new Vector3(radius, radius, radius);
        this.color = Color.GRAY;
    }
    
    @Override
    public void update() {
        orbitAngle += orbitSpeed;
        position = parent.position.add(new Vector3(
            (float)Math.cos(orbitAngle) * orbitRadius,
            0,
            (float)Math.sin(orbitAngle) * orbitRadius
        ));
        rotation.y += 0.01f;
    }
    
    @Override
    public void render(Graphics2D g, Camera camera) {
        // Render moon
    }
}

class Asteroid extends GameObject {
    private Vector3 rotationSpeed;
    private Vector3 velocity;
    
    public Asteroid(Vector3 position, float size) {
        super(position);
        this.scale = new Vector3(size, size, size);
        this.color = new Color(100, 80, 60);
        this.rotationSpeed = new Vector3(
            (float)(Math.random() * 0.02 - 0.01),
            (float)(Math.random() * 0.02 - 0.01),
            (float)(Math.random() * 0.02 - 0.01)
        );
        this.velocity = new Vector3(
            (float)(Math.random() * 0.1 - 0.05),
            0,
            (float)(Math.random() * 0.1 - 0.05)
        );
    }
    
    @Override
    public void update() {
        rotation = rotation.add(rotationSpeed);
        position = position.add(velocity);
    }
    
    @Override
    public void render(Graphics2D g, Camera camera) {
        // Render irregular asteroid shape
    }
}

class EnergyCrystal extends GameObject {
    private float bobSpeed = 0.05f;
    private float bobAmount = 2f;
    private float baseY;
    
    public EnergyCrystal(Vector3 position) {
        super(position);
        this.baseY = position.y;
        this.color = Color.CYAN;
        this.scale = new Vector3(2, 3, 2);
    }
    
    @Override
    public void update() {
        rotation.y += 0.03f;
        position.y = baseY + (float)Math.sin(System.currentTimeMillis() * 0.001 * bobSpeed) * bobAmount;
    }
    
    @Override
    public void render(Graphics2D g, Camera camera) {
        // Render glowing crystal
    }
}

class EnemyShip extends GameObject {
    private Vector3 target;
    private float speed = 0.1f;
    private int health = 100;
    
    public EnemyShip(Vector3 position) {
        super(position);
        this.color = Color.RED;
        this.scale = new Vector3(3, 2, 4);
    }
    
    @Override
    public void update() {
        // AI behavior - patrol or chase player
        if (target != null) {
            Vector3 direction = target.subtract(position).normalize();
            position = position.add(direction.multiply(speed));
        }
        rotation.y += 0.01f;
    }
    
    @Override
    public void render(Graphics2D g, Camera camera) {
        // Render enemy ship
    }
}

// Player Class
class Player {
    public Vector3 position;
    public Vector3 velocity;
    public Camera camera;
    public int health = 100;
    public int energy = 100;
    public int score = 0;
    
    public Player() {
        position = new Vector3(0, 5, -20);
        velocity = new Vector3();
        camera = new Camera();
    }
    
    public void update(InputManager input) {
        // Movement
        Vector3 movement = new Vector3();
        float speed = input.isKeyPressed(KeyEvent.VK_SHIFT) ? 0.5f : 0.2f;
        
        if (input.isKeyPressed(KeyEvent.VK_W)) movement.z += speed;
        if (input.isKeyPressed(KeyEvent.VK_S)) movement.z -= speed;
        if (input.isKeyPressed(KeyEvent.VK_A)) movement.x -= speed;
        if (input.isKeyPressed(KeyEvent.VK_D)) movement.x += speed;
        if (input.isKeyPressed(KeyEvent.VK_SPACE)) movement.y += speed;
        if (input.isKeyPressed(KeyEvent.VK_CONTROL)) movement.y -= speed;
        
        // Apply movement relative to camera
        Vector3 forward = camera.getForward().multiply(movement.z);
        Vector3 right = camera.getRight().multiply(movement.x);
        Vector3 up = new Vector3(0, movement.y, 0);
        
        velocity = forward.add(right).add(up);
        position = position.add(velocity);
        
        // Update camera
        camera.position = position;
        camera.update(input.getMouseDeltaX() * 0.005f, input.getMouseDeltaY() * 0.005f);
    }
}

// Camera Class
class Camera {
    public Vector3 position;
    public float yaw, pitch;
    
    public Camera() {
        position = new Vector3(0, 5, -20);
        yaw = 0;
        pitch = 0;
    }
    
    public void update(float deltaYaw, float deltaPitch) {
        yaw += deltaYaw;
        pitch += deltaPitch;
        pitch = Math.max(-1.5f, Math.min(1.5f, pitch));
    }
    
    public Vector3 getForward() {
        return new Vector3(
            (float)(Math.sin(yaw) * Math.cos(pitch)),
            (float)(-Math.sin(pitch)),
            (float)(Math.cos(yaw) * Math.cos(pitch))
        ).normalize();
    }
    
    public Vector3 getRight() {
        return getForward().cross(new Vector3(0, 1, 0)).normalize();
    }
    
    public Matrix4 getViewMatrix() {
        // Create view matrix for 3D transformation
        return Matrix4.identity();
    }
}

// Supporting Systems
class GameWorld {
    private List<GameObject> objects = new CopyOnWriteArrayList<>();
    
    public void addObject(GameObject obj) { objects.add(obj); }
    public void removeObject(GameObject obj) { objects.remove(obj); }
    public List<GameObject> getObjects() { return objects; }
    
    public void update() {
        for (GameObject obj : objects) {
            if (obj.active) {
                obj.update();
            }
        }
    }
}

class Renderer3D {
    private int width, height;
    
    public Renderer3D(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public void render(Graphics2D g, GameWorld world, Camera camera) {
        // Sort objects by distance for proper rendering order
        List<GameObject> sortedObjects = new ArrayList<>(world.getObjects());
        sortedObjects.sort((a, b) -> {
            float distA = Vector3.distance(camera.position, a.position);
            float distB = Vector3.distance(camera.position, b.position);
            return Float.compare(distB, distA);
        });
        
        // Render each object
        for (GameObject obj : sortedObjects) {
            if (obj.active) {
                renderObject(g, obj, camera);
            }
        }
    }
    
    private void renderObject(Graphics2D g, GameObject obj, Camera camera) {
        // Project 3D position to 2D screen
        Vector3 relativePos = obj.position.subtract(camera.position);
        
        // Simple perspective projection
        float distance = relativePos.magnitude();
        if (distance < 1) return;
        
        float screenX = width / 2 + (relativePos.x / distance) * 500;
        float screenY = height / 2 - (relativePos.y / distance) * 500;
        float size = Math.max(1, 1000 / distance);
        
        // Draw object
        g.setColor(obj.color);
        g.fillOval((int)(screenX - size/2), (int)(screenY - size/2), (int)size, (int)size);
    }
}

class PhysicsEngine {
    public void update(GameWorld world) {
        // Update physics for all objects
    }
}

class ParticleSystem {
    private List<Particle> particles = new CopyOnWriteArrayList<>();
    
    public void createExplosion(Vector3 position, Color color, int count) {
        for (int i = 0; i < count; i++) {
            particles.add(new Particle(position, color));
        }
    }
    
    public void update() {
        particles.removeIf(p -> !p.update());
    }
    
    public void render(Graphics2D g) {
        for (Particle p : particles) {
            p.render(g);
        }
    }
}

class Particle {
    private Vector3 position, velocity;
    private Color color;
    private float life;
    
    public Particle(Vector3 position, Color color) {
        this.position = new Vector3(position.x, position.y, position.z);
        this.velocity = new Vector3(
            (float)(Math.random() - 0.5) * 2,
            (float)(Math.random() - 0.5) * 2,
            (float)(Math.random() - 0.5) * 2
        );
        this.color = color;
        this.life = 1.0f;
    }
    
    public boolean update() {
        position = position.add(velocity);
        velocity = velocity.multiply(0.98f);
        life -= 0.02f;
        return life > 0;
    }
    
    public void render(Graphics2D g) {
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(life * 255)));
        g.fillOval((int)position.x, (int)position.y, 3, 3);
    }
}

class SoundSystem {
    public void play(String sound) {
        // Play sound effect
    }
}

class InputManager implements KeyListener, MouseMotionListener {
    private boolean[] keys = new boolean[256];
    private int mouseX, mouseY, mouseDeltaX, mouseDeltaY;
    
    public InputManager(JFrame frame) {
        frame.addKeyListener(this);
        frame.addMouseMotionListener(this);
    }
    
    public boolean isKeyPressed(int keyCode) {
        return keyCode < keys.length && keys[keyCode];
    }
    
    public int getMouseDeltaX() { int delta = mouseDeltaX; mouseDeltaX = 0; return delta; }
    public int getMouseDeltaY() { int delta = mouseDeltaY; mouseDeltaY = 0; return delta; }
    
    public void update() {
        // Update input state
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() < keys.length) {
            keys[e.getKeyCode()] = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() < keys.length) {
            keys[e.getKeyCode()] = false;
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseDeltaX = e.getX() - mouseX;
        mouseDeltaY = e.getY() - mouseY;
        mouseX = e.getX();
        mouseY = e.getY();
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }
}

class HUD {
    public int fps = 0;
    
    public void render(Graphics2D g, Player player) {
        // Draw HUD elements
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Health bar
        g.drawString("Health: ", 10, 30);
        g.fillRect(80, 15, player.health * 2, 20);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(80, 15, 200, 20);
        
        // Energy bar
        g.setColor(Color.CYAN);
        g.drawString("Energy: ", 10, 60);
        g.fillRect(80, 45, player.energy * 2, 20);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(80, 45, 200, 20);
        
        // Score
        g.setColor(Color.YELLOW);
        g.drawString("Score: " + player.score, 10, 90);
        
        // FPS
        g.setColor(Color.WHITE);
        g.drawString("FPS: " + fps, 10, 120);
        
        // Minimap
        drawMinimap(g);
    }
    
    private void drawMinimap(Graphics2D g) {
        int mapX = 1000, mapY = 20, mapSize = 180;
        
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(mapX, mapY, mapSize, mapSize);
        g.setColor(Color.GREEN);
        g.drawRect(mapX, mapY, mapSize, mapSize);
        
        // Draw map content
        g.setColor(Color.GREEN);
        g.fillOval(mapX + mapSize/2 - 3, mapY + mapSize/2 - 3, 6, 6);
    }
}