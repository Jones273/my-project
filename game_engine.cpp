#include <iostream>
#include <vector>
#include <memory>
#include <algorithm>
#include <chrono>
#include <cmath>
#include <windows.h>
#include <conio.h>
#include <map>
#include <string>
#include <random>

#define SCREEN_WIDTH 120
#define SCREEN_HEIGHT 40
#define PI 3.14159265359f

// Forward declarations
class GameObject;
class Renderer;
class Scene;

// 3D Vector class with operator overloading
class Vector3 {
public:
    float x, y, z;
    
    Vector3() : x(0), y(0), z(0) {}
    Vector3(float x, float y, float z) : x(x), y(y), z(z) {}
    
    // Operator overloading
    Vector3 operator+(const Vector3& other) const {
        return Vector3(x + other.x, y + other.y, z + other.z);
    }
    
    Vector3 operator-(const Vector3& other) const {
        return Vector3(x - other.x, y - other.y, z - other.z);
    }
    
    Vector3 operator*(float scalar) const {
        return Vector3(x * scalar, y * scalar, z * scalar);
    }
    
    Vector3& operator+=(const Vector3& other) {
        x += other.x; y += other.y; z += other.z;
        return *this;
    }
    
    float magnitude() const {
        return sqrt(x*x + y*y + z*z);
    }
    
    Vector3 normalized() const {
        float mag = magnitude();
        if (mag > 0) return Vector3(x/mag, y/mag, z/mag);
        return Vector3(0, 0, 0);
    }
    
    float dot(const Vector3& other) const {
        return x*other.x + y*other.y + z*other.z;
    }
    
    Vector3 cross(const Vector3& other) const {
        return Vector3(
            y*other.z - z*other.y,
            z*other.x - x*other.z,
            x*other.y - y*other.x
        );
    }
};

// Matrix4x4 class for transformations
class Matrix4x4 {
public:
    float m[4][4];
    
    Matrix4x4() {
        identity();
    }
    
    void identity() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                m[i][j] = (i == j) ? 1.0f : 0.0f;
            }
        }
    }
    
    static Matrix4x4 rotationX(float angle) {
        Matrix4x4 result;
        float c = cos(angle), s = sin(angle);
        result.m[1][1] = c; result.m[1][2] = -s;
        result.m[2][1] = s; result.m[2][2] = c;
        return result;
    }
    
    static Matrix4x4 rotationY(float angle) {
        Matrix4x4 result;
        float c = cos(angle), s = sin(angle);
        result.m[0][0] = c; result.m[0][2] = s;
        result.m[2][0] = -s; result.m[2][2] = c;
        return result;
    }
    
    static Matrix4x4 rotationZ(float angle) {
        Matrix4x4 result;
        float c = cos(angle), s = sin(angle);
        result.m[0][0] = c; result.m[0][1] = -s;
        result.m[1][0] = s; result.m[1][1] = c;
        return result;
    }
    
    static Matrix4x4 translation(const Vector3& pos) {
        Matrix4x4 result;
        result.m[0][3] = pos.x;
        result.m[1][3] = pos.y;
        result.m[2][3] = pos.z;
        return result;
    }
    
    Vector3 transformPoint(const Vector3& point) const {
        float w = m[3][0]*point.x + m[3][1]*point.y + m[3][2]*point.z + m[3][3];
        return Vector3(
            (m[0][0]*point.x + m[0][1]*point.y + m[0][2]*point.z + m[0][3]) / w,
            (m[1][0]*point.x + m[1][1]*point.y + m[1][2]*point.z + m[1][3]) / w,
            (m[2][0]*point.x + m[2][1]*point.y + m[2][2]*point.z + m[2][3]) / w
        );
    }
};

// Transform component
class Transform {
public:
    Vector3 position;
    Vector3 rotation;
    Vector3 scale;
    
    Transform() : position(0,0,0), rotation(0,0,0), scale(1,1,1) {}
    
    Matrix4x4 getMatrix() const {
        Matrix4x4 t = Matrix4x4::translation(position);
        Matrix4x4 rx = Matrix4x4::rotationX(rotation.x);
        Matrix4x4 ry = Matrix4x4::rotationY(rotation.y);
        Matrix4x4 rz = Matrix4x4::rotationZ(rotation.z);
        
        // Combine transformations (simplified)
        Matrix4x4 result = t;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i < 3 && j < 3) {
                    result.m[i][j] *= scale.x; // Simplified scaling
                }
            }
        }
        return result;
    }
};

// Mesh class for 3D geometry
class Mesh {
public:
    std::vector<Vector3> vertices;
    std::vector<std::vector<int>> faces;
    char symbol;
    int color;
    
    Mesh(char sym = '#', int col = 15) : symbol(sym), color(col) {}
    
    static std::shared_ptr<Mesh> createCube() {
        auto mesh = std::make_shared<Mesh>('#', 14);
        
        // Cube vertices
        mesh->vertices = {
            {-1, -1, -1}, {1, -1, -1}, {1, 1, -1}, {-1, 1, -1},  // Back face
            {-1, -1, 1},  {1, -1, 1},  {1, 1, 1},  {-1, 1, 1}   // Front face
        };
        
        // Cube faces (triangles)
        mesh->faces = {
            {0,1,2}, {0,2,3}, // Back
            {4,7,6}, {4,6,5}, // Front
            {0,4,5}, {0,5,1}, // Bottom
            {2,6,7}, {2,7,3}, // Top
            {0,3,7}, {0,7,4}, // Left
            {1,5,6}, {1,6,2}  // Right
        };
        
        return mesh;
    }
    
    static std::shared_ptr<Mesh> createSphere(int segments = 8) {
        auto mesh = std::make_shared<Mesh>('O', 11);
        
        // Generate sphere vertices
        for (int i = 0; i <= segments; i++) {
            float theta = i * PI / segments;
            for (int j = 0; j <= segments; j++) {
                float phi = j * 2 * PI / segments;
                float x = sin(theta) * cos(phi);
                float y = cos(theta);
                float z = sin(theta) * sin(phi);
                mesh->vertices.push_back(Vector3(x, y, z));
            }
        }
        
        // Generate sphere faces
        for (int i = 0; i < segments; i++) {
            for (int j = 0; j < segments; j++) {
                int first = i * (segments + 1) + j;
                int second = first + segments + 1;
                
                mesh->faces.push_back({first, second, first + 1});
                mesh->faces.push_back({second, second + 1, first + 1});
            }
        }
        
        return mesh;
    }
    
    static std::shared_ptr<Mesh> createPlane() {
        auto mesh = std::make_shared<Mesh>('=', 10);
        
        // Simple plane (aircraft shape)
        mesh->vertices = {
            {0, 0, 2}, {0, 0, -2},           // Fuselage
            {-3, 0, 0}, {3, 0, 0},           // Wings
            {0, 1, -2}, {0, -1, -2},         // Tail vertical
            {-1, 0, -2}, {1, 0, -2},         // Tail horizontal
            {0, 0.5f, 1}, {0, -0.5f, 1}     // Nose
        };
        
        mesh->faces = {
            {0, 2, 8}, {0, 8, 3}, // Wings
            {1, 4, 6}, {1, 6, 5}, // Tail
            {0, 8, 9}, {0, 9, 1}  // Fuselage
        };
        
        return mesh;
    }
};

// Base GameObject class
class GameObject {
public:
    Transform transform;
    std::shared_ptr<Mesh> mesh;
    bool active;
    std::string name;
    
    GameObject(const std::string& objectName = "GameObject") 
        : active(true), name(objectName) {}
    
    virtual ~GameObject() = default;
    virtual void update(float deltaTime) {}
    virtual void render(Renderer& renderer) {}
    
    void setMesh(std::shared_ptr<Mesh> newMesh) {
        mesh = newMesh;
    }
};

// Specialized game objects
class RotatingCube : public GameObject {
private:
    float rotationSpeed;
    
public:
    RotatingCube(float speed = 1.0f) : GameObject("RotatingCube"), rotationSpeed(speed) {
        setMesh(Mesh::createCube());
    }
    
    void update(float deltaTime) override {
        transform.rotation.x += rotationSpeed * deltaTime;
        transform.rotation.y += rotationSpeed * deltaTime * 0.7f;
        transform.rotation.z += rotationSpeed * deltaTime * 0.5f;
    }
};

class FlyingPlane : public GameObject {
private:
    float speed;
    float time;
    Vector3 targetPosition;
    
public:
    FlyingPlane() : GameObject("FlyingPlane"), speed(2.0f), time(0) {
        setMesh(Mesh::createPlane());
        transform.position = Vector3(0, 0, 0);
        targetPosition = Vector3(10, 5, 10);
    }
    
    void update(float deltaTime) override {
        time += deltaTime;
        
        // Smooth flight path
        transform.position.x = sin(time * 0.5f) * 15;
        transform.position.y = sin(time * 0.3f) * 5;
        transform.position.z = cos(time * 0.2f) * 10;
        
        // Banking and pitching
        transform.rotation.z = sin(time * 0.5f) * 0.3f; // Banking
        transform.rotation.x = sin(time * 0.3f) * 0.2f; // Pitching
        transform.rotation.y += deltaTime * 0.5f;       // Continuous turn
    }
};

class OrbitingSphere : public GameObject {
private:
    float orbitRadius;
    float orbitSpeed;
    float time;
    Vector3 center;
    
public:
    OrbitingSphere(Vector3 orbitCenter, float radius, float speed) 
        : GameObject("OrbitingSphere"), center(orbitCenter), 
          orbitRadius(radius), orbitSpeed(speed), time(0) {
        setMesh(Mesh::createSphere(6));
    }
    
    void update(float deltaTime) override {
        time += deltaTime * orbitSpeed;
        transform.position.x = center.x + cos(time) * orbitRadius;
        transform.position.y = center.y + sin(time * 0.5f) * 2;
        transform.position.z = center.z + sin(time) * orbitRadius;
        
        // Self rotation
        transform.rotation.y += deltaTime * 2.0f;
    }
};

// Camera class
class Camera {
public:
    Vector3 position;
    Vector3 rotation;
    float fov;
    
    Camera() : position(0, 0, -10), rotation(0, 0, 0), fov(60.0f) {}
    
    Vector3 projectPoint(const Vector3& worldPoint) const {
        // Simple perspective projection
        Vector3 relative = worldPoint - position;
        
        // Apply camera rotation (simplified)
        float distance = 30.0f;
        if (relative.z > 0.1f) {
            float x = (relative.x * distance / relative.z) + SCREEN_WIDTH / 2;
            float y = (relative.y * distance / relative.z) + SCREEN_HEIGHT / 2;
            return Vector3(x, y, relative.z);
        }
        return Vector3(-1, -1, -1); // Behind camera
    }
};

// Renderer class
class Renderer {
private:
    char screen[SCREEN_HEIGHT][SCREEN_WIDTH + 1];
    float depthBuffer[SCREEN_HEIGHT][SCREEN_WIDTH];
    int colorBuffer[SCREEN_HEIGHT][SCREEN_WIDTH];
    
public:
    Camera camera;
    
    Renderer() {
        clearBuffers();
    }
    
    void clearBuffers() {
        for (int y = 0