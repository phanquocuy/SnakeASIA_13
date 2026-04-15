import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

class PlayerScore {
    String name;
    int score;
    PlayerScore(String name, int score) { 
        this.name = name; 
        this.score = score; 
    }
}

public class ScorePanel extends JPanel {
    private App parentApp;
    private List<PlayerScore> scores = new ArrayList<>();
    private JButton btnBack;

    public ScorePanel(App parentApp) {
        this.parentApp = parentApp;
        setLayout(null); 

        btnBack = createCustomButton("Trở Về Menu");
        btnBack.addActionListener(e -> parentApp.showMenuScreen());
        add(btnBack);
    }

    
    @Override
    public void doLayout() {
        super.doLayout();
        if (btnBack != null) {
            int bw = 300;
            int bh = 55;
            btnBack.setBounds((getWidth() - bw) / 2, getHeight() - bh - 40, bw, bh);
        }
    }

    public void loadScores() {
        scores.clear();
        try {
            File file = new File("endless_scores.txt");
            if (!file.exists()) file.createNewFile();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    scores.add(new PlayerScore(parts[0], Integer.parseInt(parts[1])));
                }
            }
            br.close();
            scores.sort((s1, s2) -> Integer.compare(s2.score, s1.score));
        } catch (Exception e) { e.printStackTrace(); }
        repaint();
    }

    public static void saveScore(String name, int score) {
        try {
            List<PlayerScore> tempScores = new ArrayList<>();
            File file = new File("endless_scores.txt");
            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        tempScores.add(new PlayerScore(parts[0], Integer.parseInt(parts[1])));
                    }
                }
                br.close();
            }
            tempScores.add(new PlayerScore(name, score));
            tempScores.sort((s1, s2) -> Integer.compare(s2.score, s1.score));
            
            BufferedWriter bw = new BufferedWriter(new FileWriter("endless_scores.txt"));
            for (int i = 0; i < Math.min(10, tempScores.size()); i++) {
                bw.write(tempScores.get(i).name + ":" + tempScores.get(i).score);
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(248, 249, 250)); 
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(new Color(230, 230, 230)); 
        for(int i = 0; i < getWidth(); i += 40) g2d.drawLine(i, 0, i, getHeight());
        for(int i = 0; i < getHeight(); i += 40) g2d.drawLine(0, i, getWidth(), i);

        int boxWidth = Math.min(600, getWidth() - 40);
        int boxHeight = Math.min(500, getHeight() - 200);
        int startX = (getWidth() - boxWidth) / 2;
        int startY = (getHeight() - boxHeight) / 2 - 20;

        g2d.setColor(new Color(22, 56, 82)); 
        g2d.fillRoundRect(startX - 10, startY - 10, boxWidth + 20, boxHeight + 20, 35, 35);
        g2d.setColor(new Color(253, 246, 227)); 
        g2d.fillRoundRect(startX, startY, boxWidth, boxHeight, 20, 20);

        String title = "KỶ LỤC VÔ TẬN";
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 40));
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = startX + (boxWidth - fm.stringWidth(title)) / 2;
        g2d.setColor(new Color(240, 101, 67)); 
        g2d.drawString(title, titleX, startY + 60);

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 24));
        int scoreStartY = startY + 130;
        
        if (scores.isEmpty()) {
            g2d.setColor(Color.GRAY);
            String empty = "Chưa có cao thủ nào!";
            g2d.drawString(empty, startX + (boxWidth - g2d.getFontMetrics().stringWidth(empty))/2, scoreStartY);
        } else {
            for (int i = 0; i < Math.min(10, scores.size()); i++) {
                PlayerScore ps = scores.get(i);
                g2d.setColor(new Color(22, 56, 82));
                String rank = "Top " + (i + 1) + " :   " + ps.name;
                String scoreStr = String.valueOf(ps.score);
                
                g2d.drawString(rank, startX + 50, scoreStartY + i * 40);
                g2d.drawString(scoreStr, startX + boxWidth - 100, scoreStartY + i * 40);
                
                g2d.setColor(new Color(200, 200, 200));
                g2d.drawLine(startX + 30, scoreStartY + i * 40 + 10, startX + boxWidth - 30, scoreStartY + i * 40 + 10);
            }
        }
    }

    private JButton createCustomButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) g2.setColor(new Color(39, 174, 96));
                else if (getModel().isPressed()) g2.setColor(new Color(23, 100, 50));
                else g2.setColor(new Color(46, 204, 113)); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(39, 174, 96));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.setColor(Color.WHITE);
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 22));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            if (!App.isMuted) App.clickSound.playOnce();
        });
        return btn;
    }
}