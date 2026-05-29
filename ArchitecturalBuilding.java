import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class ArchitecturalBuilding extends JPanel {
    
    public ArchitecturalBuilding() {
        setPreferredSize(new Dimension(1000, 800));
        setBackground(new Color(135, 206, 250)); // Sky blue background
        System.out.println("Architectural Building initialized!");
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw landscape
        drawLandscape(g2d);
        
        // Draw the fence surrounding the property
        drawFence(g2d);
        
        // Draw the main building
        drawMainBuilding(g2d);
        
        // Draw architectural details
        drawArchitecturalDetails(g2d);
        
        // Draw environment (trees, garden)
        drawEnvironment(g2d);
        
        // Draw sky elements
        drawSkyElements(g2d);
        
        System.out.println("Beautiful architectural building drawn successfully!");
    }
    
    private void drawLandscape(Graphics2D g2d) {
        // Ground
        GradientPaint groundGradient = new GradientPaint(0, 600, new Color(34, 139, 34), 
                                                         0, 800, new Color(0, 100, 0));
        g2d.setPaint(groundGradient);
        g2d.fillRect(0, 600, getWidth(), 200);
        
        // Garden path
        g2d.setColor(new Color(139, 119, 101)); // Brown path
        int[] pathX = {400, 600, 620, 380};
        int[] pathY = {800, 800, 550, 550};
        g2d.fillPolygon(pathX, pathY, 4);
        
        // Path stones
        g2d.setColor(new Color(169, 169, 169)); // Gray stones
        for (int i = 0; i < 8; i++) {
            int x = 420 + i * 25;
            int y = 750 - i * 25;
            g2d.fillOval(x, y, 15, 10);
            g2d.fillOval(x + 30, y + 5, 12, 8);
        }
    }
    
    private void drawFence(Graphics2D g2d) {
        g2d.setColor(new Color(101, 67, 33)); // Brown fence
        g2d.setStroke(new BasicStroke(4));
        
        // Fence posts
        for (int x = 50; x <= 950; x += 80) {
            if (x < 350 || x > 650) { // Leave gate opening
                g2d.fillRect(x, 550, 8, 80);
                // Fence horizontal bars
                g2d.fillRect(x, 570, 80, 4);
                g2d.fillRect(x, 590, 80, 4);
                g2d.fillRect(x, 610, 80, 4);
            }
        }
        
        // Gate posts (taller and decorative)
        g2d.setColor(new Color(139, 69, 19)); // Darker brown
        g2d.fillRect(350, 540, 12, 90);
        g2d.fillRect(650, 540, 12, 90);
        
        // Gate caps
        g2d.setColor(new Color(184, 134, 11)); // Gold caps
        g2d.fillOval(348, 535, 16, 16);
        g2d.fillOval(648, 535, 16, 16);
        
        // Gate itself
        g2d.setColor(new Color(139, 69, 19));
        g2d.setStroke(new BasicStroke(3));
        // Left gate
        g2d.drawLine(362, 550, 485, 550);
        g2d.drawLine(362, 570, 485, 570);
        g2d.drawLine(362, 590, 485, 590);
        g2d.drawLine(375, 550, 375, 590);
        g2d.drawLine(410, 550, 410, 590);
        g2d.drawLine(445, 550, 445, 590);
        
        // Right gate
        g2d.drawLine(515, 550, 638, 550);
        g2d.drawLine(515, 570, 638, 570);
        g2d.drawLine(515, 590, 638, 590);
        g2d.drawLine(525, 550, 525, 590);
        g2d.drawLine(560, 550, 560, 590);
        g2d.drawLine(595, 550, 595, 590);
    }
    
    private void drawMainBuilding(Graphics2D g2d) {
        // Foundation
        g2d.setColor(new Color(105, 105, 105)); // Dark gray
        g2d.fillRect(350, 520, 300, 30);
        
        // Ground floor
        GradientPaint groundFloorGradient = new GradientPaint(350, 350, new Color(245, 245, 220), 
                                                              650, 350, new Color(222, 184, 135));
        g2d.setPaint(groundFloorGradient);
        g2d.fillRect(350, 350, 300, 170);
        
        // First floor
        GradientPaint firstFloorGradient = new GradientPaint(350, 200, new Color(255, 228, 196), 
                                                             650, 200, new Color(245, 222, 179));
        g2d.setPaint(firstFloorGradient);
        g2d.fillRect(350, 200, 300, 150);
        
        // Building outline
        g2d.setColor(new Color(139, 69, 19)); // Brown outline
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(350, 200, 300, 320);
        g2d.drawLine(350, 350, 650, 350); // Floor separator
        
        // Roof
        drawRoof(g2d);
        
        // Windows
        drawWindows(g2d);
        
        // Doors
        drawDoors(g2d);
        
        // Balcony
        drawBalcony(g2d);
    }
    
    private void drawRoof(Graphics2D g2d) {
        // Main roof
        int[] roofX = {330, 500, 670};
        int[] roofY = {200, 120, 200};
        
        GradientPaint roofGradient = new GradientPaint(330, 120, new Color(139, 69, 19), 
                                                      670, 160, new Color(160, 82, 45));
        g2d.setPaint(roofGradient);
        g2d.fillPolygon(roofX, roofY, 3);
        
        // Roof tiles effect
        g2d.setColor(new Color(101, 67, 33));
        g2d.setStroke(new BasicStroke(2));
        for (int y = 130; y < 200; y += 15) {
            for (int x = 340 + (y - 130) / 3; x < 660 - (y - 130) / 3; x += 30) {
                g2d.drawArc(x, y, 25, 10, 0, 180);
            }
        }
        
        // Chimney
        g2d.setColor(new Color(139, 69, 19));
        g2d.fillRect(580, 110, 25, 60);
        g2d.setColor(new Color(105, 105, 105));
        g2d.fillRect(577, 107, 31, 8);
        
        // Chimney smoke
        g2d.setColor(new Color(220, 220, 220, 150));
        for (int i = 0; i < 5; i++) {
            g2d.fillOval(585 + i * 3, 90 - i * 15, 8 + i, 12 + i);
        }
    }
    
    private void drawWindows(Graphics2D g2d) {
        // Ground floor windows
        drawWindow(g2d, 370, 380, 60, 80, true);  // Left window
        drawWindow(g2d, 560, 380, 60, 80, true);  // Right window
        
        // First floor windows
        drawWindow(g2d, 370, 230, 60, 80, false); // Left window
        drawWindow(g2d, 460, 230, 60, 80, false); // Center window
        drawWindow(g2d, 560, 230, 60, 80, false); // Right window
        
        // Small attic window
        drawWindow(g2d, 480, 160, 40, 30, false);
    }
    
    private void drawWindow(Graphics2D g2d, int x, int y, int width, int height, boolean hasFlowerBox) {
        // Window frame
        g2d.setColor(new Color(101, 67, 33)); // Brown frame
        g2d.fillRect(x - 5, y - 5, width + 10, height + 10);
        
        // Glass panes
        g2d.setColor(new Color(173, 216, 230, 180)); // Light blue glass
        g2d.fillRect(x, y, width / 2 - 2, height / 2 - 2);
        g2d.fillRect(x + width / 2 + 2, y, width / 2 - 2, height / 2 - 2);
        g2d.fillRect(x, y + height / 2 + 2, width / 2 - 2, height / 2 - 2);
        g2d.fillRect(x + width / 2 + 2, y + height / 2 + 2, width / 2 - 2, height / 2 - 2);
        
        // Window cross bars
        g2d.setColor(new Color(101, 67, 33));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(x + width / 2, y, x + width / 2, y + height);
        g2d.drawLine(x, y + height / 2, x + width, y + height / 2);
        
        // Window reflection
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.fillRect(x + 5, y + 5, 15, height - 10);
        
        // Flower box
        if (hasFlowerBox) {
            g2d.setColor(new Color(139, 69, 19));
            g2d.fillRect(x - 10, y + height, width + 20, 15);
            
            // Flowers
            g2d.setColor(Color.RED);
            g2d.fillOval(x, y + height + 5, 8, 8);
            g2d.setColor(Color.YELLOW);
            g2d.fillOval(x + 15, y + height + 3, 8, 8);
            g2d.setColor(Color.PINK);
            g2d.fillOval(x + 30, y + height + 5, 8, 8);
            g2d.setColor(Color.MAGENTA);
            g2d.fillOval(x + 45, y + height + 3, 8, 8);
        }
    }
    
    private void drawDoors(Graphics2D g2d) {
        // Main entrance door
        g2d.setColor(new Color(139, 69, 19)); // Brown door
        g2d.fillRect(480, 420, 40, 100);
        
        // Door frame
        g2d.setColor(new Color(101, 67, 33));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRect(476, 416, 48, 108);
        
        // Door panels
        g2d.setColor(new Color(160, 82, 45));
        g2d.fillRect(485, 430, 30, 35);
        g2d.fillRect(485, 475, 30, 35);
        
        // Door handle
        g2d.setColor(new Color(255, 215, 0)); // Gold handle
        g2d.fillOval(510, 465, 8, 8);
        
        // Door steps
        g2d.setColor(new Color(105, 105, 105));
        g2d.fillRect(470, 520, 60, 8);
        g2d.fillRect(475, 528, 50, 6);
        
        // Door arch
        g2d.setColor(new Color(101, 67, 33));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawArc(476, 380, 48, 72, 0, 180);
    }
    
    private void drawBalcony(Graphics2D g2d) {
        // Balcony floor
        g2d.setColor(new Color(105, 105, 105));
        g2d.fillRect(450, 310, 80, 10);
        
        // Balcony railings
        g2d.setColor(new Color(101, 67, 33));
        g2d.setStroke(new BasicStroke(2));
        
        // Vertical posts
        for (int x = 455; x <= 525; x += 10) {
            g2d.drawLine(x, 285, x, 310);
        }
        
        // Horizontal rails
        g2d.drawLine(450, 285, 530, 285);
        g2d.drawLine(450, 297, 530, 297);
        
        // Balcony door
        g2d.setColor(new Color(139, 69, 19));
        g2d.fillRect(465, 250, 30, 60);
        g2d.setColor(new Color(173, 216, 230, 180));
        g2d.fillRect(470, 255, 20, 50);
        
        // Door handle
        g2d.setColor(new Color(255, 215, 0));
        g2d.fillOval(488, 277, 4, 4);
    }
    
    private void drawArchitecturalDetails(Graphics2D g2d) {
        // Decorative corner stones
        g2d.setColor(new Color(105, 105, 105));
        int[] cornerX = {350, 350, 365};
        int[] cornerY = {200, 215, 200};
        g2d.fillPolygon(cornerX, cornerY, 3);
        
        int[] cornerX2 = {650, 650, 635};
        int[] cornerY2 = {200, 215, 200};
        g2d.fillPolygon(cornerX2, cornerY2, 3);
        
        // Building nameplate
        g2d.setColor(new Color(184, 134, 11)); // Gold nameplate
        g2d.fillRect(420, 180, 160, 25);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Serif", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        String buildingName = "XERXES BRIAN";
        int textX = 500 - fm.stringWidth(buildingName) / 2;
        g2d.drawString(buildingName, textX, 197);
        
        // Decorative pillars
        g2d.setColor(new Color(245, 245, 220));
        g2d.fillRect(440, 350, 15, 170);
        g2d.fillRect(545, 350, 15, 170);
        
        // Pillar capitals
        g2d.setColor(new Color(105, 105, 105));
        g2d.fillRect(435, 345, 25, 10);
        g2d.fillRect(540, 345, 25, 10);
    }
    
    private void drawEnvironment(Graphics2D g2d) {
        // Trees on the left
        drawTree(g2d, 150, 450, 40, 80);
        drawTree(g2d, 220, 430, 35, 70);
        
        // Trees on the right
        drawTree(g2d, 750, 440, 45, 85);
        drawTree(g2d, 820, 460, 38, 75);
        
        // Garden bushes
        g2d.setColor(new Color(0, 128, 0));
        g2d.fillOval(300, 480, 40, 30);
        g2d.fillOval(660, 490, 35, 25);
        g2d.fillOval(680, 470, 30, 30);
        
        // Flowers in garden
        g2d.setColor(Color.RED);
        g2d.fillOval(310, 485, 6, 6);
        g2d.fillOval(325, 490, 6, 6);
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(318, 495, 6, 6);
        g2d.setColor(Color.PINK);
        g2d.fillOval(670, 475, 6, 6);
        g2d.fillOval(685, 480, 6, 6);
    }
    
    private void drawTree(Graphics2D g2d, int x, int y, int width, int height) {
        // Tree trunk
        g2d.setColor(new Color(101, 67, 33));
        g2d.fillRect(x + width / 3, y + height - 30, width / 3, 30);
        
        // Tree canopy
        g2d.setColor(new Color(0, 128, 0));
        g2d.fillOval(x, y, width, height - 20);
        
        // Tree highlights
        g2d.setColor(new Color(50, 205, 50));
        g2d.fillOval(x + 5, y + 5, width / 3, height / 3);
    }
    
    private void drawSkyElements(Graphics2D g2d) {
        // Sun
        g2d.setColor(new Color(255, 255, 0));
        g2d.fillOval(800, 50, 60, 60);
        
        // Sun rays
        g2d.setStroke(new BasicStroke(3));
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4;
            int x1 = (int) (830 + 40 * Math.cos(angle));
            int y1 = (int) (80 + 40 * Math.sin(angle));
            int x2 = (int) (830 + 55 * Math.cos(angle));
            int y2 = (int) (80 + 55 * Math.sin(angle));
            g2d.drawLine(x1, y1, x2, y2);
        }
        
        // Clouds
        g2d.setColor(new Color(255, 255, 255, 200));
        drawCloud(g2d, 200, 80);
        drawCloud(g2d, 600, 60);
        
        // Birds
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        drawBird(g2d, 300, 150);
        drawBird(g2d, 350, 140);
        drawBird(g2d, 400, 160);
    }
    
    private void drawCloud(Graphics2D g2d, int x, int y) {
        g2d.fillOval(x, y, 40, 25);
        g2d.fillOval(x + 20, y - 5, 35, 30);
        g2d.fillOval(x + 35, y + 5, 30, 20);
        g2d.fillOval(x + 15, y + 10, 25, 15);
    }
    
    private void drawBird(Graphics2D g2d, int x, int y) {
        g2d.drawArc(x, y, 15, 8, 0, 180);
        g2d.drawArc(x + 10, y, 15, 8, 0, 180);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Beautiful Architectural Building");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new ArchitecturalBuilding());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            System.out.println("Beautiful architectural building created!");
            System.out.println("Features: 2-story building, fence, gate, windows, doors, balcony, garden, trees!");
        });
    }
}