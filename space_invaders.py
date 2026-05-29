import pygame
import random
import math
import sys

# Initialize Pygame
pygame.init()

# Game Constants
SCREEN_WIDTH = 1000
SCREEN_HEIGHT = 700
FPS = 60

# Colors
BLACK = (0, 0, 0)
WHITE = (255, 255, 255)
RED = (255, 0, 0)
GREEN = (0, 255, 0)
BLUE = (0, 0, 255)
YELLOW = (255, 255, 0)
PURPLE = (128, 0, 128)
CYAN = (0, 255, 255)
ORANGE = (255, 165, 0)

# Player settings
PLAYER_SPEED = 7
BULLET_SPEED = 10
ENEMY_SPEED = 2
ENEMY_DROP_SPEED = 20

class Player:
    def __init__(self, x, y):
        self.x = x
        self.y = y
        self.width = 60
        self.height = 40
        self.speed = PLAYER_SPEED
        self.color = CYAN
        self.health = 100
        self.max_health = 100
        
    def move_left(self):
        if self.x > 0:
            self.x -= self.speed
            
    def move_right(self):
        if self.x < SCREEN_WIDTH - self.width:
            self.x += self.speed
            
    def draw(self, screen):
        # Draw player ship
        # Main body
        pygame.draw.rect(screen, self.color, (self.x, self.y, self.width, self.height))
        # Cockpit
        pygame.draw.rect(screen, WHITE, (self.x + 20, self.y + 5, 20, 15))
        # Wings
        pygame.draw.polygon(screen, BLUE, [
            (self.x, self.y + self.height),
            (self.x + 15, self.y + 25),
            (self.x + 15, self.y + self.height)
        ])
        pygame.draw.polygon(screen, BLUE, [
            (self.x + self.width, self.y + self.height),
            (self.x + self.width - 15, self.y + 25),
            (self.x + self.width - 15, self.y + self.height)
        ])
        # Engines
        pygame.draw.circle(screen, ORANGE, (self.x + 10, self.y + self.height), 5)
        pygame.draw.circle(screen, ORANGE, (self.x + self.width - 10, self.y + self.height), 5)
        
    def get_rect(self):
        return pygame.Rect(self.x, self.y, self.width, self.height)

class Bullet:
    def __init__(self, x, y, direction=1, color=YELLOW, speed=BULLET_SPEED):
        self.x = x
        self.y = y
        self.width = 4
        self.height = 10
        self.speed = speed * direction  # direction: 1 for up, -1 for down
        self.color = color
        
    def update(self):
        self.y -= self.speed
        
    def draw(self, screen):
        pygame.draw.rect(screen, self.color, (self.x, self.y, self.width, self.height))
        # Add glow effect
        pygame.draw.rect(screen, WHITE, (self.x + 1, self.y + 1, 2, 8))
        
    def get_rect(self):
        return pygame.Rect(self.x, self.y, self.width, self.height)
        
    def is_off_screen(self):
        return self.y < 0 or self.y > SCREEN_HEIGHT

class Enemy:
    def __init__(self, x, y, enemy_type=1):
        self.x = x
        self.y = y
        self.width = 40
        self.height = 30
        self.speed = ENEMY_SPEED
        self.type = enemy_type
        self.direction = 1
        self.drop_timer = 0
        
        # Different enemy types
        if enemy_type == 1:
            self.color = RED
            self.points = 10
            self.health = 1
        elif enemy_type == 2:
            self.color = PURPLE
            self.points = 20
            self.health = 2
            self.width = 45
        elif enemy_type == 3:
            self.color = GREEN
            self.points = 30
            self.health = 3
            self.width = 50
            
    def update(self):
        self.x += self.speed * self.direction
        
    def drop_down(self):
        self.y += ENEMY_DROP_SPEED
        self.direction *= -1
        
    def draw(self, screen):
        # Draw enemy based on type
        if self.type == 1:
            # Basic enemy
            pygame.draw.rect(screen, self.color, (self.x, self.y, self.width, self.height))
            pygame.draw.circle(screen, WHITE, (self.x + 10, self.y + 10), 3)
            pygame.draw.circle(screen, WHITE, (self.x + 30, self.y + 10), 3)
        elif self.type == 2:
            # Medium enemy
            pygame.draw.ellipse(screen, self.color, (self.x, self.y, self.width, self.height))
            pygame.draw.circle(screen, WHITE, (self.x + 12, self.y + 10), 4)
            pygame.draw.circle(screen, WHITE, (self.x + 33, self.y + 10), 4)
            pygame.draw.rect(screen, RED, (self.x + 15, self.y + 20, 15, 5))
        elif self.type == 3:
            # Boss enemy
            pygame.draw.polygon(screen, self.color, [
                (self.x + self.width//2, self.y),
                (self.x, self.y + self.height),
                (self.x + self.width, self.y + self.height)
            ])
            pygame.draw.circle(screen, WHITE, (self.x + 15, self.y + 15), 5)
            pygame.draw.circle(screen, WHITE, (self.x + 35, self.y + 15), 5)
            
    def get_rect(self):
        return pygame.Rect(self.x, self.y, self.width, self.height)

class PowerUp:
    def __init__(self, x, y, power_type):
        self.x = x
        self.y = y
        self.width = 25
        self.height = 25
        self.speed = 3
        self.type = power_type  # 'health', 'rapid_fire', 'shield'
        self.rotation = 0
        
        if power_type == 'health':
            self.color = GREEN
        elif power_type == 'rapid_fire':
            self.color = ORANGE
        elif power_type == 'shield':
            self.color = BLUE
            
    def update(self):
        self.y += self.speed
        self.rotation += 5
        
    def draw(self, screen):
        # Rotate the power-up
        center_x = self.x + self.width // 2
        center_y = self.y + self.height // 2
        
        if self.type == 'health':
            # Health cross
            pygame.draw.rect(screen, self.color, (self.x + 8, self.y + 3, 9, 19))
            pygame.draw.rect(screen, self.color, (self.x + 3, self.y + 8, 19, 9))
        elif self.type == 'rapid_fire':
            # Lightning bolt
            points = [
                (center_x - 8, center_y - 10),
                (center_x + 2, center_y - 10),
                (center_x - 5, center_y),
                (center_x + 8, center_y),
                (center_x - 2, center_y + 10),
                (center_x + 5, center_y)
            ]
            pygame.draw.polygon(screen, self.color, points)
        elif self.type == 'shield':
            # Shield
            pygame.draw.circle(screen, self.color, (center_x, center_y), 12)
            pygame.draw.circle(screen, WHITE, (center_x, center_y), 8)
            
    def get_rect(self):
        return pygame.Rect(self.x, self.y, self.width, self.height)
        
    def is_off_screen(self):
        return self.y > SCREEN_HEIGHT

class Particle:
    def __init__(self, x, y, color):
        self.x = x
        self.y = y
        self.vx = random.uniform(-5, 5)
        self.vy = random.uniform(-5, 5)
        self.color = color
        self.life = 30
        self.max_life = 30
        
    def update(self):
        self.x += self.vx
        self.y += self.vy
        self.life -= 1
        self.vy += 0.1  # Gravity
        
    def draw(self, screen):
        alpha = int(255 * (self.life / self.max_life))
        color_with_alpha = (*self.color, alpha)
        size = int(3 * (self.life / self.max_life))
        if size > 0:
            pygame.draw.circle(screen, self.color, (int(self.x), int(self.y)), size)
            
    def is_alive(self):
        return self.life > 0

class Game:
    def __init__(self):
        self.screen = pygame.display.set_mode((SCREEN_WIDTH, SCREEN_HEIGHT))
        pygame.display.set_caption("Space Invaders - Professional Edition")
        self.clock = pygame.time.Clock()
        
        # Game objects
        self.player = Player(SCREEN_WIDTH // 2 - 30, SCREEN_HEIGHT - 100)
        self.bullets = []
        self.enemy_bullets = []
        self.enemies = []
        self.power_ups = []
        self.particles = []
        
        # Game state
        self.score = 0
        self.level = 1
        self.game_over = False
        self.paused = False
        self.shooting_timer = 0
        self.enemy_shoot_timer = 0
        self.rapid_fire = False
        self.rapid_fire_timer = 0
        self.shield_active = False
        self.shield_timer = 0
        
        # Background stars
        self.stars = []
        for _ in range(100):
            self.stars.append({
                'x': random.randint(0, SCREEN_WIDTH),
                'y': random.randint(0, SCREEN_HEIGHT),
                'speed': random.uniform(1, 3),
                'size': random.randint(1, 3)
            })
            
        self.create_enemy_formation()
        
    def create_enemy_formation(self):
        self.enemies.clear()
        rows = 5
        cols = 10
        
        for row in range(rows):
            for col in range(cols):
                x = 100 + col * 70
                y = 50 + row * 60
                
                # Different enemy types based on row
                if row == 0:
                    enemy_type = 3  # Boss enemies
                elif row <= 2:
                    enemy_type = 2  # Medium enemies
                else:
                    enemy_type = 1  # Basic enemies
                    
                self.enemies.append(Enemy(x, y, enemy_type))
                
    def handle_events(self):
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                return False
            elif event.type == pygame.KEYDOWN:
                if event.key == pygame.K_SPACE and not self.game_over:
                    self.shoot()
                elif event.key == pygame.K_p:
                    self.paused = not self.paused
                elif event.key == pygame.K_r and self.game_over:
                    self.restart_game()
                elif event.key == pygame.K_ESCAPE:
                    return False
                    
        return True
        
    def shoot(self):
        if self.shooting_timer <= 0:
            bullet_x = self.player.x + self.player.width // 2 - 2
            bullet_y = self.player.y
            self.bullets.append(Bullet(bullet_x, bullet_y))
            
            if self.rapid_fire:
                self.shooting_timer = 5  # Faster shooting
            else:
                self.shooting_timer = 15
                
    def update_game(self):
        if self.paused or self.game_over:
            return
            
        # Update timers
        if self.shooting_timer > 0:
            self.shooting_timer -= 1
        if self.enemy_shoot_timer > 0:
            self.enemy_shoot_timer -= 1
        if self.rapid_fire_timer > 0:
            self.rapid_fire_timer -= 1
        else:
            self.rapid_fire = False
        if self.shield_timer > 0:
            self.shield_timer -= 1
        else:
            self.shield_active = False
            
        # Handle continuous input
        keys = pygame.key.get_pressed()
        if keys[pygame.K_LEFT] or keys[pygame.K_a]:
            self.player.move_left()
        if keys[pygame.K_RIGHT] or keys[pygame.K_d]:
            self.player.move_right()
        if keys[pygame.K_SPACE]:
            self.shoot()
            
        # Update background stars
        for star in self.stars:
            star['y'] += star['speed']
            if star['y'] > SCREEN_HEIGHT:
                star['y'] = 0
                star['x'] = random.randint(0, SCREEN_WIDTH)
                
        # Update bullets
        for bullet in self.bullets[:]:
            bullet.update()
            if bullet.is_off_screen():
                self.bullets.remove(bullet)
                
        # Update enemy bullets
        for bullet in self.enemy_bullets[:]:
            bullet.update()
            if bullet.is_off_screen():
                self.enemy_bullets.remove(bullet)
                
        # Update enemies
        should_drop = False
        for enemy in self.enemies:
            enemy.update()
            if enemy.x <= 0 or enemy.x >= SCREEN_WIDTH - enemy.width:
                should_drop = True
                
        if should_drop:
            for enemy in self.enemies:
                enemy.drop_down()
                
        # Enemy shooting
        if self.enemy_shoot_timer <= 0 and self.enemies:
            shooting_enemy = random.choice(self.enemies)
            bullet_x = shooting_enemy.x + shooting_enemy.width // 2 - 2
            bullet_y = shooting_enemy.y + shooting_enemy.height
            self.enemy_bullets.append(Bullet(bullet_x, bullet_y, -1, RED, 5))
            self.enemy_shoot_timer = random.randint(30, 120)
            
        # Update power-ups
        for power_up in self.power_ups[:]:
            power_up.update()
            if power_up.is_off_screen():
                self.power_ups.remove(power_up)
                
        # Update particles
        for particle in self.particles[:]:
            particle.update()
            if not particle.is_alive():
                self.particles.remove(particle)
                
        self.check_collisions()
        
        # Check win condition
        if not self.enemies:
            self.level += 1
            self.create_enemy_formation()
            
        # Check lose condition
        for enemy in self.enemies:
            if enemy.y + enemy.height >= self.player.y:
                self.game_over = True
                
    def check_collisions(self):
        # Player bullets vs enemies
        for bullet in self.bullets[:]:
            for enemy in self.enemies[:]:
                if bullet.get_rect().colliderect(enemy.get_rect()):
                    self.bullets.remove(bullet)
                    enemy.health -= 1
                    
                    # Create explosion particles
                    for _ in range(10):
                        self.particles.append(Particle(
                            enemy.x + enemy.width // 2,
                            enemy.y + enemy.height // 2,
                            enemy.color
                        ))
                    
                    if enemy.health <= 0:
                        self.enemies.remove(enemy)
                        self.score += enemy.points
                        
                        # Random power-up drop
                        if random.random() < 0.1:  # 10% chance
                            power_type = random.choice(['health', 'rapid_fire', 'shield'])
                            self.power_ups.append(PowerUp(
                                enemy.x + enemy.width // 2,
                                enemy.y + enemy.height // 2,
                                power_type
                            ))
                    break
                    
        # Enemy bullets vs player
        if not self.shield_active:
            for bullet in self.enemy_bullets[:]:
                if bullet.get_rect().colliderect(self.player.get_rect()):
                    self.enemy_bullets.remove(bullet)
                    self.player.health -= 10
                    
                    # Create damage particles
                    for _ in range(5):
                        self.particles.append(Particle(
                            self.player.x + self.player.width // 2,
                            self.player.y + self.player.height // 2,
                            RED
                        ))
                    
                    if self.player.health <= 0:
                        self.game_over = True
                        
        # Power-ups vs player
        for power_up in self.power_ups[:]:
            if power_up.get_rect().colliderect(self.player.get_rect()):
                self.power_ups.remove(power_up)
                
                if power_up.type == 'health':
                    self.player.health = min(self.player.max_health, self.player.health + 30)
                elif power_up.type == 'rapid_fire':
                    self.rapid_fire = True
                    self.rapid_fire_timer = 300  # 5 seconds at 60 FPS
                elif power_up.type == 'shield':
                    self.shield_active = True
                    self.shield_timer = 180  # 3 seconds at 60 FPS
                    
    def draw(self):
        # Clear screen
        self.screen.fill(BLACK)
        
        # Draw stars
        for star in self.stars:
            pygame.draw.circle(self.screen, WHITE, 
                             (int(star['x']), int(star['y'])), star['size'])
            
        # Draw game objects
        self.player.draw(self.screen)
        
        # Draw shield effect
        if self.shield_active:
            pygame.draw.circle(self.screen, BLUE, 
                             (self.player.x + self.player.width // 2,
                              self.player.y + self.player.height // 2), 
                             40, 3)
            
        for bullet in self.bullets:
            bullet.draw(self.screen)
            
        for bullet in self.enemy_bullets:
            bullet.draw(self.screen)
            
        for enemy in self.enemies:
            enemy.draw(self.screen)
            
        for power_up in self.power_ups:
            power_up.draw(self.screen)
            
        for particle in self.particles:
            particle.draw(self.screen)
            
        self.draw_ui()
        
        if self.paused:
            self.draw_pause_screen()
        elif self.game_over:
            self.draw_game_over_screen()
            
        pygame.display.flip()
        
    def draw_ui(self):
        font = pygame.font.Font(None, 36)
        
        # Score
        score_text = font.render(f"Score: {self.score}", True, WHITE)
        self.screen.blit(score_text, (10, 10))
        
        # Level
        level_text = font.render(f"Level: {self.level}", True, WHITE)
        self.screen.blit(level_text, (10, 50))
        
        # Health bar
        health_percentage = self.player.health / self.player.max_health
        bar_width = 200
        bar_height = 20
        bar_x = SCREEN_WIDTH - bar_width - 10
        bar_y = 10
        
        pygame.draw.rect(self.screen, RED, (bar_x, bar_y, bar_width, bar_height))
        pygame.draw.rect(self.screen, GREEN, (bar_x, bar_y, bar_width * health_percentage, bar_height))
        pygame.draw.rect(self.screen, WHITE, (bar_x, bar_y, bar_width, bar_height), 2)
        
        health_text = font.render(f"Health: {self.player.health}", True, WHITE)
        self.screen.blit(health_text, (bar_x, bar_y + 25))
        
        # Power-up indicators
        y_offset = 90
        if self.rapid_fire:
            rapid_text = font.render("RAPID FIRE!", True, ORANGE)
            self.screen.blit(rapid_text, (SCREEN_WIDTH - 200, y_offset))
            y_offset += 30
            
        if self.shield_active:
            shield_text = font.render("SHIELD ACTIVE!", True, BLUE)
            self.screen.blit(shield_text, (SCREEN_WIDTH - 200, y_offset))
            
    def draw_pause_screen(self):
        overlay = pygame.Surface((SCREEN_WIDTH, SCREEN_HEIGHT))
        overlay.set_alpha(128)
        overlay.fill(BLACK)
        self.screen.blit(overlay, (0, 0))
        
        font = pygame.font.Font(None, 72)
        pause_text = font.render("PAUSED", True, WHITE)
        text_rect = pause_text.get_rect(center=(SCREEN_WIDTH // 2, SCREEN_HEIGHT // 2))
        self.screen.blit(pause_text, text_rect)
        
        font_small = pygame.font.Font(None, 36)
        instruction_text = font_small.render("Press P to resume", True, WHITE)
        instruction_rect = instruction_text.get_rect(center=(SCREEN_WIDTH // 2, SCREEN_HEIGHT // 2 + 60))
        self.screen.blit(instruction_text, instruction_rect)
        
    def draw_game_over_screen(self):
        overlay = pygame.Surface((SCREEN_WIDTH, SCREEN_HEIGHT))
        overlay.set_alpha(180)
        overlay.fill(BLACK)
        self.screen.blit(overlay, (0, 0))
        
        font = pygame.font.Font(None, 72)
        game_over_text = font.render("GAME OVER", True, RED)
        text_rect = game_over_text.get_rect(center=(SCREEN_WIDTH // 2, SCREEN_HEIGHT // 2 - 50))
        self.screen.blit(game_over_text, text_rect)
        
        font_medium = pygame.font.Font(None, 48)
        score_text = font_medium.render(f"Final Score: {self.score}", True, WHITE)
        score_rect = score_text.get_rect(center=(SCREEN_WIDTH // 2, SCREEN_HEIGHT // 2 + 10))
        self.screen.blit(score_text, score_rect)
        
        font_small = pygame.font.Font(None, 36)
        instruction_text = font_small.render("Press R to restart or ESC to quit", True, WHITE)
        instruction_rect = instruction_text.get_rect(center=(SCREEN_WIDTH // 2, SCREEN_HEIGHT // 2 + 70))
        self.screen.blit(instruction_text, instruction_rect)
        
    def restart_game(self):
        self.__init__()
        
    def run(self):
        print("🚀 Space Invaders - Professional Edition")
        print("=" * 50)
        print("CONTROLS:")
        print("• Arrow Keys / A,D - Move left/right")
        print("• Spacebar - Shoot")
        print("• P - Pause/Resume")
        print("• R - Restart (when game over)")
        print("• ESC - Quit")
        print("=" * 50)
        print("POWER-UPS:")
        print("• Green Cross - Health boost")
        print("• Orange Lightning - Rapid fire")
        print("• Blue Circle - Shield protection")
        print("=" * 50)
        print("Starting game... Good luck! 🎮")
        
        running = True
        while running:
            running = self.handle_events()
            self.update_game()
            self.draw()
            self.clock.tick(FPS)
            
        pygame.quit()
        sys.exit()

if __name__ == "__main__":
    try:
        game = Game()
        game.run()
    except Exception as e:
        print(f"Error starting game: {e}")
        print("Make sure you have pygame installed: pip install pygame")
        input("Press Enter to exit...")