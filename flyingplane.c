#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <windows.h>
#include <conio.h>
#include <time.h>

#define SCREEN_WIDTH 120
#define SCREEN_HEIGHT 40
#define PI 3.14159265359

// 3D Point structure
typedef struct {
    float x, y, z;
} Point3D;

// Plane structure
typedef struct {
    Point3D position;
    Point3D rotation;
    Point3D velocity;
    float speed;
    float fuel;
    int altitude;
} Plane;

// Cloud structure
typedef struct {
    Point3D position;
    float size;
    char symbol;
} Cloud;

// Screen buffer
char screen[SCREEN_HEIGHT][SCREEN_WIDTH + 1];
float depthBuffer[SCREEN_HEIGHT][SCREEN_WIDTH];

// Global variables
Plane plane;
Cloud clouds[20];
int cloudCount = 20;
Point3D camera;
float time_elapsed = 0.0f;

// Function prototypes
void initializeGame();
void clearScreen();
void drawPixel(int x, int y, char c, float depth);
void render3DPoint(Point3D point, char c);
void updatePlane();
void drawPlane();
void drawClouds();
void drawHUD();
void drawTerrain();
void rotatePoint(Point3D* point, Point3D rotation);
Point3D projectTo2D(Point3D point3D);
void handleInput();
void gameLoop();

void gotoxy(int x, int y) {
    COORD coord;
    coord.X = x;
    coord.Y = y;
    SetConsoleCursorPosition(GetStdHandle(STD_OUTPUT_HANDLE), coord);
}

void hideCursor() {
    CONSOLE_CURSOR_INFO cursorInfo;
    GetConsoleCursorInfo(GetStdHandle(STD_OUTPUT_HANDLE), &cursorInfo);
    cursorInfo.bVisible = FALSE;
    SetConsoleCursorInfo(GetStdHandle(STD_OUTPUT_HANDLE), &cursorInfo);
}

void setColor(int color) {
    SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), color);
}

void initializeGame() {
    // Initialize plane
    plane.position.x = 0.0f;
    plane.position.y = 0.0f;
    plane.position.z = 0.0f;
    plane.rotation.x = 0.0f;
    plane.rotation.y = 0.0f;
    plane.rotation.z = 0.0f;
    plane.velocity.x = 0.0f;
    plane.velocity.y = 0.0f;
    plane.velocity.z = 1.0f;
    plane.speed = 2.0f;
    plane.fuel = 100.0f;
    plane.altitude = 1000;
    
    // Initialize camera
    camera.x = 0.0f;
    camera.y = -5.0f;
    camera.z = -10.0f;
    
    // Initialize clouds
    srand((unsigned int)time(NULL));
    for (int i = 0; i < cloudCount; i++) {
        clouds[i].position.x = (float)(rand() % 200 - 100);
        clouds[i].position.y = (float)(rand() % 50 - 25);
        clouds[i].position.z = (float)(rand() % 100 + 20);
        clouds[i].size = (float)(rand() % 5 + 2);
        clouds[i].symbol = (rand() % 2) ? '*' : 'o';
    }
    
    hideCursor();
    printf("🛩️  3D FLYING PLANE SIMULATOR  🛩️\n");
    printf("=====================================\n");
    printf("CONTROLS:\n");
    printf("W/S - Pitch Up/Down\n");
    printf("A/D - Roll Left/Right\n");
    printf("Q/E - Yaw Left/Right\n");
    printf("SPACE - Increase Speed\n");
    printf("SHIFT - Decrease Speed\n");
    printf("ESC - Exit\n");
    printf("=====================================\n");
    printf("Press any key to start flying...\n");
    _getch();
}

void clearScreen() {
    // Clear screen buffer
    for (int y = 0; y < SCREEN_HEIGHT; y++) {
        for (int x = 0; x < SCREEN_WIDTH; x++) {
            screen[y][x] = ' ';
            depthBuffer[y][x] = 1000.0f;
        }
        screen[y][SCREEN_WIDTH] = '\0';
    }
}

void drawPixel(int x, int y, char c, float depth) {
    if (x >= 0 && x < SCREEN_WIDTH && y >= 0 && y < SCREEN_HEIGHT) {
        if (depth < depthBuffer[y][x]) {
            screen[y][x] = c;
            depthBuffer[y][x] = depth;
        }
    }
}

Point3D projectTo2D(Point3D point3D) {
    Point3D result;
    
    // Translate relative to camera
    point3D.x -= camera.x;
    point3D.y -= camera.y;
    point3D.z -= camera.z;
    
    // Simple perspective projection
    float distance = 50.0f;
    if (point3D.z > 0.1f) {
        result.x = (point3D.x * distance / point3D.z) + SCREEN_WIDTH / 2;
        result.y = (point3D.y * distance / point3D.z) + SCREEN_HEIGHT / 2;
        result.z = point3D.z;
    } else {
        result.x = -1;
        result.y = -1;
        result.z = -1;
    }
    
    return result;
}

void render3DPoint(Point3D point, char c) {
    Point3D projected = projectTo2D(point);
    
    if (projected.x >= 0 && projected.y >= 0 && projected.z > 0) {
        drawPixel((int)projected.x, (int)projected.y, c, projected.z);
    }
}

void rotatePoint(Point3D* point, Point3D rotation) {
    float x = point->x, y = point->y, z = point->z;
    
    // Rotate around X axis (pitch)
    float cosX = cos(rotation.x), sinX = sin(rotation.x);
    float newY = y * cosX - z * sinX;
    float newZ = y * sinX + z * cosX;
    y = newY;
    z = newZ;
    
    // Rotate around Y axis (yaw)
    float cosY = cos(rotation.y), sinY = sin(rotation.y);
    float newX = x * cosY + z * sinY;
    newZ = -x * sinY + z * cosY;
    x = newX;
    z = newZ;
    
    // Rotate around Z axis (roll)
    float cosZ = cos(rotation.z), sinZ = sin(rotation.z);
    newX = x * cosZ - y * sinZ;
    newY = x * sinZ + y * cosZ;
    
    point->x = newX;
    point->y = newY;
    point->z = z;
}

void drawPlane() {
    // Define plane vertices relative to plane position
    Point3D vertices[] = {
        // Fuselage
        {0, 0, 2},   {0, 0, -2},  {0, 0.5f, 0}, {0, -0.5f, 0},
        // Wings
        {-3, 0, 0},  {3, 0, 0},   {-2, 0, -1},  {2, 0, -1},
        // Tail
        {0, 1, -2},  {0, -1, -2}, {-1, 0, -2},  {1, 0, -2}
    };
    
    int vertexCount = sizeof(vertices) / sizeof(Point3D);
    
    // Transform vertices
    for (int i = 0; i < vertexCount; i++) {
        // Rotate vertex
        rotatePoint(&vertices[i], plane.rotation);
        
        // Translate to plane position
        vertices[i].x += plane.position.x;
        vertices[i].y += plane.position.y;
        vertices[i].z += plane.position.z;
        
        // Render
        char symbol = (i < 4) ? '#' : (i < 8) ? '=' : '+';
        render3DPoint(vertices[i], symbol);
    }
}

void drawClouds() {
    for (int i = 0; i < cloudCount; i++) {
        Cloud* cloud = &clouds[i];
        
        // Create cloud particles
        for (int j = 0; j < (int)cloud->size; j++) {
            Point3D cloudPoint;
            cloudPoint.x = cloud->position.x + (rand() % 6 - 3);
            cloudPoint.y = cloud->position.y + (rand() % 3 - 1);
            cloudPoint.z = cloud->position.z + (rand() % 4 - 2);
            
            render3DPoint(cloudPoint, cloud->symbol);
        }
        
        // Move clouds
        cloud->position.z -= 0.5f;
        if (cloud->position.z < camera.z - 20) {
            cloud->position.z = camera.z + 100;
            cloud->position.x = (float)(rand() % 200 - 100);
            cloud->position.y = (float)(rand() % 50 - 25);
        }
    }
}

void drawTerrain() {
    // Draw simple terrain grid
    for (int x = -50; x <= 50; x += 10) {
        for (int z = 0; z <= 100; z += 10) {
            Point3D terrainPoint;
            terrainPoint.x = (float)x;
            terrainPoint.y = -20.0f + sin(x * 0.1f + time_elapsed) * 2;
            terrainPoint.z = (float)z + camera.z;
            
            render3DPoint(terrainPoint, '.');
        }
    }
}

void drawHUD() {
    // Draw HUD at bottom of screen
    gotoxy(0, SCREEN_HEIGHT + 1);
    setColor(15); // White
    printf("┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐\n");
    printf("│ SPEED: %6.1f | ALTITUDE: %5d ft | FUEL: %5.1f%% | PITCH: %6.1f° | YAW: %6.1f° | ROLL: %6.1f° │\n", 
           plane.speed * 100, plane.altitude, plane.fuel, 
           plane.rotation.x * 180 / PI, plane.rotation.y * 180 / PI, plane.rotation.z * 180 / PI);
    printf("└─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘\n");
    
    // Flight instruments (simplified)
    printf("🛩️ FLIGHT INSTRUMENTS:\n");
    printf("Horizon: ");
    for (int i = 0; i < 20; i++) {
        if (i == 10) printf("|");
        else if (abs(i - 10) < abs(plane.rotation.z * 10)) printf("-");
        else printf(" ");
    }
    printf("\n");
    
    printf("Compass: ");
    float heading = plane.rotation.y * 180 / PI;
    while (heading < 0) heading += 360;
    while (heading >= 360) heading -= 360;
    printf("%.0f°", heading);
    
    // Direction indicator
    const char* directions[] = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
    int dir_index = (int)((heading + 22.5f) / 45.0f) % 8;
    printf(" [%s]\n", directions[dir_index]);
}

void updatePlane() {
    // Update physics
    plane.position.x += plane.velocity.x * plane.speed;
    plane.position.y += plane.velocity.y * plane.speed;
    plane.position.z += plane.velocity.z * plane.speed;
    
    // Update velocity based on rotation
    plane.velocity.x = sin(plane.rotation.y) * cos(plane.rotation.x);
    plane.velocity.y = sin(plane.rotation.x);
    plane.velocity.z = cos(plane.rotation.y) * cos(plane.rotation.x);
    
    // Update camera to follow plane
    camera.x = plane.position.x;
    camera.y = plane.position.y - 5.0f;
    camera.z = plane.position.z - 10.0f;
    
    // Calculate altitude
    plane.altitude = (int)(plane.position.y * 100) + 1000;
    
    // Consume fuel
    plane.fuel -= 0.02f;
    if (plane.fuel < 0) plane.fuel = 0;
    
    // Apply some damping to rotation
    plane.rotation.x *= 0.98f;
    plane.rotation.z *= 0.95f;
    
    time_elapsed += 0.1f;
}

void handleInput() {
    if (_kbhit()) {
        char key = _getch();
        
        switch (key) {
            case 'w': case 'W':
                plane.rotation.x += 0.1f; // Pitch up
                break;
            case 's': case 'S':
                plane.rotation.x -= 0.1f; // Pitch down
                break;
            case 'a': case 'A':
                plane.rotation.z += 0.1f; // Roll left
                break;
            case 'd': case 'D':
                plane.rotation.z -= 0.1f; // Roll right
                break;
            case 'q': case 'Q':
                plane.rotation.y -= 0.1f; // Yaw left
                break;
            case 'e': case 'E':
                plane.rotation.y += 0.1f; // Yaw right
                break;
            case ' ':
                if (plane.speed < 5.0f) plane.speed += 0.2f;
                break;
            case 'x': case 'X':
                if (plane.speed > 0.5f) plane.speed -= 0.2f;
                break;
            case 27: // ESC
                exit(0);
                break;
        }
        
        // Limit rotation angles
        if (plane.rotation.x > PI/3) plane.rotation.x = PI/3;
        if (plane.rotation.x < -PI/3) plane.rotation.x = -PI/3;
        if (plane.rotation.z > PI/4) plane.rotation.z = PI/4;
        if (plane.rotation.z < -PI/4) plane.rotation.z = -PI/4;
    }
}

void gameLoop() {
    while (1) {
        clearScreen();
        handleInput();
        updatePlane();
        
        // Render 3D scene
        drawTerrain();
        drawClouds();
        drawPlane();
        
        // Display screen buffer
        gotoxy(0, 0);
        setColor(11); // Light blue for sky
        for (int y = 0; y < SCREEN_HEIGHT; y++) {
            printf("%s\n", screen[y]);
        }
        
        drawHUD();
        
        // Control frame rate
        Sleep(50); // ~20 FPS
    }
}

int main() {
    printf("Initializing 3D Flight Simulator...\n");
    initializeGame();
    gameLoop();
    return 0;
}