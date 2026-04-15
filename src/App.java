import java.awt.*;
import javax.swing.*;

public class App extends JFrame {

    private CardLayout cardLayout = new CardLayout();
    private JPanel containerPanel = new JPanel(cardLayout);
    
    private MenuPanel menuPanel;
    private GamePanel gamePanel;
    private ScorePanel scorePanel; 
    
    // Âm thanh
    public static MusicPlayer musicPlayer = new MusicPlayer();
    public static MusicPlayer foodSoundPlayer = new MusicPlayer();
    public static MusicPlayer gameOverSound = new MusicPlayer();
    public static MusicPlayer levelUpSound = new MusicPlayer();
    public static MusicPlayer clickSound = new MusicPlayer();
    public static MusicPlayer gameWinSound = new MusicPlayer(); 
    
    public static boolean isMuted = false; 

    public App() {
        
        musicPlayer.loadMusic("sounds/themee.wav");
        foodSoundPlayer.loadMusic("sounds/eat.wav"); 
        gameOverSound.loadMusic("sounds/dead.wav");
        levelUpSound.loadMusic("sounds/winn.wav"); 
        clickSound.loadMusic("sounds/click.wav");
        
        
        gameWinSound.loadMusic("sounds/win.wav"); 

        if (!isMuted) musicPlayer.play();

        menuPanel = new MenuPanel(this);
        gamePanel = new GamePanel(this);
        scorePanel = new ScorePanel(this); 

        containerPanel.add(menuPanel, "Menu");
        containerPanel.add(gamePanel, "Game");
        containerPanel.add(scorePanel, "Score"); 

        this.add(containerPanel);
        
        this.setTitle("Snake Game");
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        this.setResizable(true); 
        this.setMinimumSize(new Dimension(800, 600));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null); 
        this.setVisible(true);

        cardLayout.show(containerPanel, "Menu");
    }

    public void showGameScreen(boolean isAI, boolean isInfinite) {
        cardLayout.show(containerPanel, "Game");
        gamePanel.resetLevel();
        gamePanel.startGame(isAI, isInfinite); 
        gamePanel.requestFocusInWindow(); 
    }

    public void showScoreScreen() {
        scorePanel.loadScores();
        cardLayout.show(containerPanel, "Score");
        scorePanel.requestFocusInWindow();
    }

    public void showMenuScreen() {
        cardLayout.show(containerPanel, "Menu");
        menuPanel.updateMusicButton(); 
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        new App();
    }
}