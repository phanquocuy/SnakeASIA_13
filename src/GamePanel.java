import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.PriorityQueue;
import java.util.Arrays;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener {

    private App parentApp;

    // Tọa độ lưới (Logic)
    public static final int WIDTH_TILES = 25;
    public static final int HEIGHT_TILES = 20;
    
    // Các thông số đồ họa sẽ được tính toán lại tự động khi phóng to/thu nhỏ
    private int TILE = 30; 
    private int PAD_X = 0;
    private int PAD_TOP = 0;

    private Timer timer = new Timer(150, this); 
    private int currentBaseDelay = 150; 

    private int fruitX, fruitY;
    private int bigFruitX, bigFruitY;
    private boolean isBigFruitActive = false;
    private int smallFruitCount = 0;
    private int bigFruitTimer = 0; 

    private int[] snakeX = new int[400];
    private int[] snakeY = new int[400];
    private int snakeLength;
    private char dir;

    private boolean gameOver = false;
    private boolean levelClear = false;
    private boolean gameFinished = false;
    
    private boolean isAIPlaying = false; 
    private boolean isInfiniteMode = false; 
    private boolean isPaused = false; 
    private boolean isSpeedRun = false;

    private int score;
    private int level = 1;
    private int targetScore = 50; 

    private ArrayList<Point> walls = new ArrayList<>();

    private static final int[][] LEVEL_1 = new int[20][25];
    private static final int[][] LEVEL_2 = new int[20][25];
    private static final int[][] LEVEL_3 = new int[20][25];
    private static final int[][] LEVEL_4 = new int[20][25];
    private static final int[][] LEVEL_5 = new int[20][25];
    private static final int[][] LEVEL_6 = new int[20][25];

    private JButton btnSound; 
    
    private JPanel pauseMenuPanel;
    private JPanel gameOverPanel;
    private JPanel levelClearPanel;
    private JPanel gameFinishedPanel;
    private JPanel nameInputPanel; 
    private JTextField txtNameInput;
    
    private JButton btnPauseSound; 

    public void resetLevel() { this.level = 1; }

    public GamePanel(App parentApp) {
        this.parentApp = parentApp;
        setFocusable(true);
        setLayout(null); 

        initMaps();
        
        createPauseMenu();
        createGameOverMenu();
        createLevelClearMenu();
        createGameFinishedMenu();
        createNameInputMenu();
        
        btnSound = new JButton("Sound: ON");
        btnSound.setBackground(new Color(22, 56, 82)); 
        btnSound.setForeground(Color.WHITE);
        btnSound.setFocusPainted(false);
        btnSound.setFocusable(false); 
        btnSound.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnSound.addActionListener(e -> {
            if (!App.isMuted) App.clickSound.playOnce(); 
            App.isMuted = !App.isMuted;
            updateSoundButtonState();
            if (App.isMuted) App.musicPlayer.stop();
            else {
                App.clickSound.playOnce(); 
                App.musicPlayer.play();
            }
            requestFocusInWindow(); 
        });
        add(btnSound);
        
        setupKeyBindings(); 
    }

    @Override
    public void doLayout() {
        super.doLayout();
        int w = getWidth();
        int h = getHeight();
        
        if (w == 0 || h == 0) return;

        TILE = Math.max(15, Math.min(w / (WIDTH_TILES + 4), h / (HEIGHT_TILES + 6)));
        PAD_X = (w - (TILE * WIDTH_TILES)) / 2;
        PAD_TOP = (h - (TILE * HEIGHT_TILES)) / 2 + (int)(TILE * 1.5);
        
        if (pauseMenuPanel != null) pauseMenuPanel.setBounds(0, 0, w, h);
        if (gameOverPanel != null) gameOverPanel.setBounds(0, 0, w, h);
        if (levelClearPanel != null) levelClearPanel.setBounds(0, 0, w, h);
        if (gameFinishedPanel != null) gameFinishedPanel.setBounds(0, 0, w, h);
        if (nameInputPanel != null) nameInputPanel.setBounds(0, 0, w, h);
        
        if (btnSound != null) {
            btnSound.setFont(new Font("Comic Sans MS", Font.BOLD, Math.max(12, TILE / 2)));
            btnSound.setBounds(w - (TILE * 6) - 20, h - (TILE * 2) - 20, TILE * 6, (int)(TILE * 1.5));
        }
    }

    private void createNameInputMenu() {
        nameInputPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(0, 0, 0, 220)); 
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        nameInputPanel.setOpaque(false);
        nameInputPanel.setLayout(new GridBagLayout());
        nameInputPanel.setVisible(false);

        JPanel box = new JPanel(new GridBagLayout());
        box.setBackground(new Color(22, 56, 82));
        box.setBorder(BorderFactory.createLineBorder(new Color(240, 101, 67), 4, true));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel lblTitle = new JLabel("XÁC NHẬN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        box.add(lblTitle, gbc);

        gbc.gridy = 1;
        txtNameInput = new JTextField(12);
        txtNameInput.setFont(new Font("Segoe UI", Font.BOLD, 22));
        txtNameInput.setHorizontalAlignment(JTextField.CENTER);
        txtNameInput.setBackground(new Color(253, 246, 227));
        txtNameInput.setForeground(new Color(22, 56, 82));
        box.add(txtNameInput, gbc);

        gbc.gridy = 2;
        JButton btnSave = createCustomButton("Xác Nhận & Tiếp Tục");
        btnSave.addActionListener(e -> {
            if (!App.isMuted) App.clickSound.playOnce();
            String name = txtNameInput.getText().trim();
            if (name.isEmpty()) name = "Ẩn Danh";
            ScorePanel.saveScore(name, score);
            nameInputPanel.setVisible(false);
            gameOverPanel.setVisible(true);
            requestFocusInWindow();
        });
        box.add(btnSave, gbc);

        nameInputPanel.add(box);
        add(nameInputPanel);
        setComponentZOrder(nameInputPanel, 0); 
    }

    private void createPauseMenu() {
        pauseMenuPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(0, 0, 0, 200)); 
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        pauseMenuPanel.setOpaque(false);
        pauseMenuPanel.setLayout(new GridBagLayout());
        pauseMenuPanel.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("TẠM DỪNG");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 30, 10); 
        pauseMenuPanel.add(titleLabel, gbc);

        gbc.insets = new Insets(10, 10, 10, 10);

        JButton btnResume = createCustomButton("Tiếp tục chơi");
        btnResume.addActionListener(e -> togglePause());
        gbc.gridy = 1;
        pauseMenuPanel.add(btnResume, gbc);

        btnPauseSound = createCustomButton(App.isMuted ? "Âm thanh: OFF" : "Âm thanh: ON");
        btnPauseSound.addActionListener(e -> {
            App.isMuted = !App.isMuted;
            updateSoundButtonState(); 
            if (App.isMuted) App.musicPlayer.stop();
            else App.musicPlayer.play();
            requestFocusInWindow();
        });
        gbc.gridy = 2;
        pauseMenuPanel.add(btnPauseSound, gbc);

        JButton btnMainMenu = createCustomButton("Trở về Menu");
        btnMainMenu.addActionListener(e -> returnToMenu());
        gbc.gridy = 3;
        pauseMenuPanel.add(btnMainMenu, gbc);

        add(pauseMenuPanel);
        setComponentZOrder(pauseMenuPanel, 0); 
    }

    private void createGameOverMenu() {
        gameOverPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(150, 0, 0, 200)); 
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        gameOverPanel.setOpaque(false);
        gameOverPanel.setLayout(new GridBagLayout());
        gameOverPanel.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("GAME OVER");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 50));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 40, 10); 
        gameOverPanel.add(titleLabel, gbc);

        gbc.insets = new Insets(10, 10, 10, 10);

        JButton btnRetry = createCustomButton("Chơi lại");
        btnRetry.addActionListener(e -> { level = 1; startGame(isAIPlaying, isInfiniteMode); });
        gbc.gridy = 1;
        gameOverPanel.add(btnRetry, gbc);

        JButton btnMainMenu = createCustomButton("Trở về Menu");
        btnMainMenu.addActionListener(e -> returnToMenu());
        gbc.gridy = 2;
        gameOverPanel.add(btnMainMenu, gbc);

        add(gameOverPanel);
        setComponentZOrder(gameOverPanel, 0); 
    }

    private void createLevelClearMenu() {
        levelClearPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(46, 204, 113, 200)); 
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        levelClearPanel.setOpaque(false);
        levelClearPanel.setLayout(new GridBagLayout());
        levelClearPanel.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("LEVEL CLEAR!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 50));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 40, 10); 
        levelClearPanel.add(titleLabel, gbc);

        gbc.insets = new Insets(10, 10, 10, 10);

        JButton btnNext = createCustomButton("Màn tiếp theo");
        btnNext.addActionListener(e -> { level++; startGame(isAIPlaying, isInfiniteMode); });
        gbc.gridy = 1;
        levelClearPanel.add(btnNext, gbc);

        JButton btnMainMenu = createCustomButton("Trở về Menu");
        btnMainMenu.addActionListener(e -> returnToMenu());
        gbc.gridy = 2;
        levelClearPanel.add(btnMainMenu, gbc);

        add(levelClearPanel);
        setComponentZOrder(levelClearPanel, 0); 
    }

    private void createGameFinishedMenu() {
        gameFinishedPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(19, 70, 31, 220)); 
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        gameFinishedPanel.setOpaque(false);
        gameFinishedPanel.setLayout(new GridBagLayout());
        gameFinishedPanel.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("CHÚC MỪNG");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 50));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gameFinishedPanel.add(titleLabel, gbc);
        
        JLabel subLabel = new JLabel("Ông chủ thắng lớn!!");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        subLabel.setForeground(Color.WHITE);
        subLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 10, 30, 10);
        gameFinishedPanel.add(subLabel, gbc);

        gbc.insets = new Insets(10, 10, 10, 10);

        JButton btnReplay = createCustomButton("Chơi lại từ đầu");
        btnReplay.addActionListener(e -> { level = 1; startGame(isAIPlaying, isInfiniteMode); });
        gbc.gridy = 2;
        gameFinishedPanel.add(btnReplay, gbc);

        JButton btnMainMenu = createCustomButton("Trở về Menu");
        btnMainMenu.addActionListener(e -> returnToMenu());
        gbc.gridy = 3;
        gameFinishedPanel.add(btnMainMenu, gbc);

        add(gameFinishedPanel);
        setComponentZOrder(gameFinishedPanel, 0); 
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
        btn.setPreferredSize(new Dimension(240, 55));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            if (!App.isMuted) App.clickSound.playOnce();
        });
        return btn;
    }

    private void checkAndSaveScore() {
        if (!isAIPlaying && isInfiniteMode && score > 0) {
            txtNameInput.setText("");
            nameInputPanel.setVisible(true); 
        } else {
            gameOverPanel.setVisible(true);
        }
    }

    private void togglePause() {
        if (!gameOver && !levelClear && !gameFinished && !nameInputPanel.isVisible()) {
            isPaused = !isPaused;
            pauseMenuPanel.setVisible(isPaused); 
            if (!isPaused) requestFocusInWindow(); 
            repaint();
        }
    }
    
    private void triggerGameOver() {
        gameOver = true;
        timer.stop();
        if (!App.isMuted) App.gameOverSound.playOnce(); 
        checkAndSaveScore(); 
        repaint();
    }
    
    private void triggerLevelClear() {
        levelClear = true;
        timer.stop();
        if (!App.isMuted) App.levelUpSound.playOnce(); 
        levelClearPanel.setVisible(true);
    }
    
    private void triggerGameFinished() {
        gameFinished = true;
        timer.stop();
    
        if (!App.isMuted) App.gameWinSound.playOnce(); 
        
        gameFinishedPanel.setVisible(true);
        repaint();
    }
    
    private void returnToMenu() {
        timer.stop();
        gameOver = false;
        levelClear = false;
        gameFinished = false;
        isPaused = false;
        pauseMenuPanel.setVisible(false);
        gameOverPanel.setVisible(false);
        levelClearPanel.setVisible(false);
        gameFinishedPanel.setVisible(false);
        nameInputPanel.setVisible(false);
        parentApp.showMenuScreen();
    }

    private void updateSoundButtonState() {
        if (App.isMuted) {
            btnSound.setText("Sound: OFF");
            btnSound.setBackground(new Color(231, 76, 60)); 
            if (btnPauseSound != null) btnPauseSound.setText("Âm thanh: OFF");
        } else {
            btnSound.setText("Sound: ON");
            btnSound.setBackground(new Color(22, 56, 82)); 
            if (btnPauseSound != null) btnPauseSound.setText("Âm thanh: ON");
        }
    }

    private void setupKeyBindings() {
        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), "up");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "down");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "left");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "right");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "space_action"); 
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "next");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), "pause");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, 0), "speed");

        am.put("right", new AbstractAction() { public void actionPerformed(ActionEvent e) { if (!isAIPlaying && !isPaused && dir != 'l') dir = 'r'; }});
        am.put("left", new AbstractAction() { public void actionPerformed(ActionEvent e) { if (!isAIPlaying && !isPaused && dir != 'r') dir = 'l'; }});
        am.put("up", new AbstractAction() { public void actionPerformed(ActionEvent e) { if (!isAIPlaying && !isPaused && dir != 'd') dir = 'u'; }});
        am.put("down", new AbstractAction() { public void actionPerformed(ActionEvent e) { if (!isAIPlaying && !isPaused && dir != 'u') dir = 'd'; }});

        am.put("space_action", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (gameOver && gameOverPanel.isVisible()) { 
                    level = 1; startGame(isAIPlaying, isInfiniteMode); 
                } else if (!levelClear && !gameFinished && !nameInputPanel.isVisible()) {
                    togglePause(); 
                }
            }
        });

        am.put("pause", new AbstractAction() { public void actionPerformed(ActionEvent e) { togglePause(); }});
        am.put("escape", new AbstractAction() { public void actionPerformed(ActionEvent e) { returnToMenu(); }});

        am.put("next", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (levelClear && level < 6 && !isInfiniteMode) { 
                    level++; startGame(isAIPlaying, isInfiniteMode); 
                }
            }
        });

        am.put("speed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (isAIPlaying && !gameOver && !levelClear && !gameFinished && !isPaused) {
                    isSpeedRun = !isSpeedRun;
                    timer.setDelay(isSpeedRun ? 30 : currentBaseDelay); 
                }
            }
        });
    }

    private void initMaps() {
        for(int r=8; r<=11; r++) { for(int c=10; c<=14; c++) LEVEL_1[r][c] = 1; }
        for(int r=2; r<=7; r++) { LEVEL_1[r][3]=1; LEVEL_1[r][4]=1; LEVEL_1[r][20]=1; LEVEL_1[r][21]=1; }
        for(int c=5; c<=8; c++) { LEVEL_1[6][c]=1; LEVEL_1[7][c]=1; }
        for(int c=16; c<=19; c++) { LEVEL_1[6][c]=1; LEVEL_1[7][c]=1; }
        for(int r=12; r<=17; r++) { LEVEL_1[r][3]=1; LEVEL_1[r][4]=1; LEVEL_1[r][20]=1; LEVEL_1[r][21]=1; }
        for(int c=5; c<=8; c++) { LEVEL_1[12][c]=1; LEVEL_1[13][c]=1; }
        for(int c=16; c<=19; c++) { LEVEL_1[12][c]=1; LEVEL_1[13][c]=1; }

        for(int r=0; r<=6; r++) { LEVEL_2[r][3]=1; LEVEL_2[r][4]=1; LEVEL_2[r][20]=1; LEVEL_2[r][21]=1; }
        for(int r=13; r<=19; r++) { LEVEL_2[r][3]=1; LEVEL_2[r][4]=1; LEVEL_2[r][20]=1; LEVEL_2[r][21]=1; }
        for(int r=2; r<=6; r++) { LEVEL_2[r][9]=1; LEVEL_2[r][10]=1; LEVEL_2[r][14]=1; LEVEL_2[r][15]=1; } 
        for(int c=11; c<=13; c++) { LEVEL_2[5][c]=1; LEVEL_2[6][c]=1; LEVEL_2[13][c]=1; LEVEL_2[14][c]=1; } 
        for(int r=13; r<=17; r++) { LEVEL_2[r][9]=1; LEVEL_2[r][10]=1; LEVEL_2[r][14]=1; LEVEL_2[r][15]=1; } 

        for(int r=0; r<=7; r++) { LEVEL_3[r][3]=1; LEVEL_3[r][4]=1; LEVEL_3[r][20]=1; LEVEL_3[r][21]=1; }
        for(int c=5; c<=8; c++) { LEVEL_3[6][c]=1; LEVEL_3[7][c]=1; }
        for(int c=16; c<=19; c++) { LEVEL_3[6][c]=1; LEVEL_3[7][c]=1; }
        for(int c=0; c<=22; c++) LEVEL_3[10][c] = 1; 
        
        
        for(int r=13; r<=16; r++) { LEVEL_3[r][4]=1; LEVEL_3[r][5]=1; LEVEL_3[r][16]=1; LEVEL_3[r][17]=1; } 
        for(int r=11; r<=15; r++) { LEVEL_3[r][10]=1; LEVEL_3[r][11]=1; } 
        

        for(int r=4; r<=15; r++) { LEVEL_4[r][7]=1; LEVEL_4[r][17]=1; }
        for(int c=4; c<=20; c++) { LEVEL_4[5][c]=1; LEVEL_4[14][c]=1; }
        LEVEL_4[5][7]=0; LEVEL_4[5][17]=0; LEVEL_4[14][7]=0; LEVEL_4[14][17]=0;
        LEVEL_4[10][7]=0; LEVEL_4[10][17]=0; LEVEL_4[5][12]=0; LEVEL_4[14][12]=0;

        for(int r=2; r<=17; r+=3) { for(int c=2; c<=22; c+=3) LEVEL_5[r][c] = 1; }
        for(int c=0; c<25; c++) { LEVEL_5[0][c] = 1; LEVEL_5[19][c] = 1; }
        for(int r=0; r<20; r++) { LEVEL_5[r][0] = 1; LEVEL_5[r][24] = 1; }
        LEVEL_5[0][12] = 0; LEVEL_5[19][12] = 0; 
        LEVEL_5[10][0] = 0; LEVEL_5[10][24] = 0; 

        for(int c=2; c<=22; c++) { LEVEL_6[2][c] = 1; LEVEL_6[17][c] = 1; }
        for(int r=2; r<=17; r++) { LEVEL_6[r][2] = 1; LEVEL_6[r][22] = 1; }
        for(int c=6; c<=18; c++) { LEVEL_6[6][c] = 1; LEVEL_6[13][c] = 1; }
        for(int r=6; r<=13; r++) { LEVEL_6[r][6] = 1; LEVEL_6[r][18] = 1; }
        LEVEL_6[2][12] = 0; LEVEL_6[17][12] = 0;
        LEVEL_6[6][12] = 0; LEVEL_6[13][12] = 0;
        LEVEL_6[10][2] = 0; LEVEL_6[10][22] = 0;
        LEVEL_6[10][6] = 0; LEVEL_6[10][18] = 0;
    }

    public void startGame(boolean aiMode, boolean infiniteMode) { 
        this.isAIPlaying = aiMode; 
        this.isInfiniteMode = infiniteMode;
        
        if (level == 1 || isInfiniteMode) {
            snakeLength = 1;
            score = 0;
            targetScore = 50; 
        } else {
            targetScore = score + 50;
        }
        
        snakeX[0] = 12; snakeY[0] = 10;
        
        if (!isInfiniteMode) {
            if (level == 1) { snakeY[0] = 4; } 
            else if (level == 2) { snakeY[0] = 9; } 
            else if (level == 3) { snakeY[0] = 8; } 
            else if (level == 4) { snakeY[0] = 2; }  
            else if (level == 5) { snakeY[0] = 10; } 
            else if (level == 6) { snakeY[0] = 10; }
        }
        dir = 'r';

        for (int i = 1; i < snakeLength; i++) {
            snakeX[i] = snakeX[0] - i;
            snakeY[i] = snakeY[0];
        }

        smallFruitCount = 0;
        isBigFruitActive = false;
        bigFruitTimer = 0;
        
        gameOver = false;
        levelClear = false;
        gameFinished = false;
        isPaused = false; 
        isSpeedRun = false;
        
        pauseMenuPanel.setVisible(false);
        gameOverPanel.setVisible(false);
        levelClearPanel.setVisible(false);
        gameFinishedPanel.setVisible(false);
        nameInputPanel.setVisible(false);
        
        if (isInfiniteMode) currentBaseDelay = 110; 
        else {
            if (level == 1) currentBaseDelay = 150;
            else if (level == 2) currentBaseDelay = 135;
            else if (level == 3) currentBaseDelay = 120;
            else if (level == 4) currentBaseDelay = 105;
            else if (level == 5) currentBaseDelay = 90;
            else if (level == 6) currentBaseDelay = 75; 
        }

        timer.setDelay(currentBaseDelay);
        updateSoundButtonState();

        if (App.isMuted) {
            if (App.musicPlayer != null) App.musicPlayer.stop();
        } else {
            if (App.musicPlayer != null) App.musicPlayer.play();
        }
        loadLevel();
        spawnFruit();
        timer.start();
    }

    private void loadLevel() {
        walls.clear();
        if (isInfiniteMode) return; 

        int[][] map;
        switch(level) {
            case 1: map = LEVEL_1; break;
            case 2: map = LEVEL_2; break;
            case 3: map = LEVEL_3; break;
            case 4: map = LEVEL_4; break;
            case 5: map = LEVEL_5; break;
            case 6: map = LEVEL_6; break;
            default: map = LEVEL_1; break;
        }

        for (int r = 0; r < HEIGHT_TILES; r++) {
            for (int c = 0; c < WIDTH_TILES; c++) {
                if (map[r][c] == 1) walls.add(new Point(c, r));
            }
        }
    }

    private void spawnFruit() {
        Random rand = new Random();
        while (true) {
            fruitX = rand.nextInt(WIDTH_TILES);
            fruitY = rand.nextInt(HEIGHT_TILES);
            Point p = new Point(fruitX, fruitY);
            if (!walls.contains(p)) {
                boolean onSnake = false;
                for(int i=0; i<snakeLength; i++) {
                    if(snakeX[i] == fruitX && snakeY[i] == fruitY) onSnake = true;
                }
                if (isBigFruitActive && fruitX == bigFruitX && fruitY == bigFruitY) onSnake = true; 
                if(!onSnake) break; 
            }
        }
    }

    private void spawnBigFruit() {
        Random rand = new Random();
        while (true) {
            bigFruitX = rand.nextInt(WIDTH_TILES);
            bigFruitY = rand.nextInt(HEIGHT_TILES);
            Point p = new Point(bigFruitX, bigFruitY);
            if (!walls.contains(p)) {
                boolean onSnake = false;
                for(int i=0; i<snakeLength; i++) {
                    if(snakeX[i] == bigFruitX && snakeY[i] == bigFruitY) onSnake = true;
                }
                if (bigFruitX == fruitX && bigFruitY == fruitY) onSnake = true; 
                if(!onSnake) break; 
            }
        }
    }

    // --- THUẬT TOÁN BEST FIRST SEARCH & CƠ CHẾ FLOOD FILL ---
    private int heuristic(int c1, int r1, int c2, int r2)  {
        return Math.abs(c1 - c2) + Math.abs(r1 - r2);
    }

    class BFSNode implements Comparable<BFSNode> {
        int c, r;
        char initialMove;
        int h; // Ưu tiên thuần heuristic

        BFSNode(int c, int r, char initialMove, int h) {
            this.c = c; this.r = r;
            this.initialMove = initialMove;
            this.h = h;
        }

        @Override
        public int compareTo(BFSNode other) {
            return Integer.compare(this.h, other.h); // Greedy Best First Search
        }
    }

    private char calculateAIMove() {
        int headC = snakeX[0], headR = snakeY[0];
        int targetC = fruitX, targetR = fruitY;
        if (isBigFruitActive) {
            targetC = bigFruitX;
            targetR = bigFruitY;
        }

        // Lập bản đồ vật cản
        boolean[][] obstacles = new boolean[HEIGHT_TILES][WIDTH_TILES];
        for (Point w : walls) obstacles[w.y][w.x] = true;
        
        // Không liệt kê khúc đuôi sát cuối vào vật cản vì nhịp sau nó sẽ trườn đi
        for (int i = 0; i < snakeLength - 1; i++) { 
            int c = snakeX[i], r = snakeY[i];
            if (c >= 0 && c < WIDTH_TILES && r >= 0 && r < HEIGHT_TILES) {
                obstacles[r][c] = true;
            }
        }

        // CHIẾN THUẬT 1: Tìm đường đến táo (Best First Search)
        char move = findBFSPath(headC, headR, targetC, targetR, obstacles);
        if (move != ' ') return move;

        // CHIẾN THUẬT 2: Survival Mode - Cố đuổi theo đuôi
        int tailC = snakeX[snakeLength - 1];
        int tailR = snakeY[snakeLength - 1];
        if (tailC >= 0 && tailC < WIDTH_TILES && tailR >= 0 && tailR < HEIGHT_TILES) {
            obstacles[tailR][tailC] = false; 
            move = findBFSPath(headC, headR, tailC, tailR, obstacles);
            if (move != ' ') return move;
        }

        // CHIẾN THUẬT 3: TÌM ĐƯỜNG CÂU GIỜ BẰNG FLOOD FILL
        // Đếm xem rẽ hướng nào thì khoảng trống sống sót sẽ dài nhất
        int[] dc = {0, 0, -1, 1};
        int[] dr = {-1, 1, 0, 0};
        char[] dirs = {'u', 'd', 'l', 'r'};
        
        int maxSpace = -1;
        char bestDir = dir; 

        for (int i = 0; i < 4; i++) {
            int nc = headC + dc[i], nr = headR + dr[i];
            if (nc >= 0 && nc < WIDTH_TILES && nr >= 0 && nr < HEIGHT_TILES && !obstacles[nr][nc]) {
                if ((dir == 'u' && dirs[i] == 'd') || (dir == 'd' && dirs[i] == 'u') ||
                    (dir == 'l' && dirs[i] == 'r') || (dir == 'r' && dirs[i] == 'l')) continue;
                
                int space = countOpenSpace(nc, nr, obstacles);
                if (space > maxSpace) {
                    maxSpace = space;
                    bestDir = dirs[i];
                }
            }
        }
        
        return bestDir; 
    }

    // Lõi đường đi sử dụng thuật toán Best First Search 
    private char findBFSPath(int startC, int startR, int targetC, int targetR, boolean[][] obstacles) {
        int[] dc = {0, 0, -1, 1};
        int[] dr = {-1, 1, 0, 0};
        char[] dirs = {'u', 'd', 'l', 'r'};

        PriorityQueue<BFSNode> openSet = new PriorityQueue<>();
        boolean[][] closedSet = new boolean[HEIGHT_TILES][WIDTH_TILES];

        for (int i = 0; i < 4; i++) {
            int nc = startC + dc[i], nr = startR + dr[i];
            if (nc >= 0 && nc < WIDTH_TILES && nr >= 0 && nr < HEIGHT_TILES && !obstacles[nr][nc]) {
                if ((dir == 'u' && dirs[i] == 'd') || (dir == 'd' && dirs[i] == 'u') ||
                    (dir == 'l' && dirs[i] == 'r') || (dir == 'r' && dirs[i] == 'l')) continue;
                
                int h = heuristic(nc, nr, targetC, targetR);
                openSet.add(new BFSNode(nc, nr, dirs[i], h));
            }
        }

        while (!openSet.isEmpty()) {
            BFSNode curr = openSet.poll();
            
            if (curr.c == targetC && curr.r == targetR) {
                return curr.initialMove;
            }

            if (closedSet[curr.r][curr.c]) continue;
            closedSet[curr.r][curr.c] = true;

            for (int i = 0; i < 4; i++) {
                int nc = curr.c + dc[i], nr = curr.r + dr[i];
                if (nc >= 0 && nc < WIDTH_TILES && nr >= 0 && nr < HEIGHT_TILES && !obstacles[nr][nc] && !closedSet[nr][nc]) {
                    int h = heuristic(nc, nr, targetC, targetR);
                    openSet.add(new BFSNode(nc, nr, curr.initialMove, h));
                }
            }
        }
        return ' '; 
    }

    // Thuật toán Loang đếm số lượng ô không gian an toàn hiện hữu
    private int countOpenSpace(int startC, int startR, boolean[][] obstacles) {
        boolean[][] visited = new boolean[HEIGHT_TILES][WIDTH_TILES];
        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(startC, startR));
        visited[startR][startC] = true;
        
        int count = 0;
        int[] dc = {0, 0, -1, 1};
        int[] dr = {-1, 1, 0, 0};

        while (!queue.isEmpty() && count < 200) { 
            Point p = queue.poll();
            count++;

            for (int i = 0; i < 4; i++) {
                int nc = p.x + dc[i], nr = p.y + dr[i];
                if (nc >= 0 && nc < WIDTH_TILES && nr >= 0 && nr < HEIGHT_TILES 
                    && !obstacles[nr][nc] && !visited[nr][nc]) {
                    visited[nr][nc] = true;
                    queue.add(new Point(nc, nr));
                }
            }
        }
        return count;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // NỀN MÀU TOÀN MÀN HÌNH
        g2d.setColor(new Color(248, 249, 250)); 
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Lưới mờ
        g2d.setColor(new Color(230, 230, 230)); 
        for(int i = 0; i < getWidth(); i += TILE) g2d.drawLine(i, 0, i, getHeight());
        for(int i = 0; i < getHeight(); i += TILE) g2d.drawLine(0, i, getWidth(), i);
        
        // Trang trí góc
        g2d.setColor(new Color(0, 168, 89)); 
        g2d.fillOval(getWidth() - TILE*5, getHeight() - TILE*3, TILE*8, TILE*5);
        g2d.fillOval(-TILE*2, getHeight() - TILE*4, TILE*6, TILE*5);
        g2d.setColor(new Color(255, 193, 7)); 
        g2d.fillOval(getWidth() - TILE*4, -TILE*2, TILE*6, TILE*4);
        g2d.setColor(new Color(171, 190, 251)); 
        g2d.fillOval(-TILE*3, TILE*4, TILE*4, TILE*3);

        // BẢNG THÔNG TIN GÓC TRÊN TRÁI 
        g2d.setColor(new Color(22, 56, 82)); 
        g2d.fillRoundRect(PAD_X, PAD_TOP/2 - (int)(TILE*1.2), TILE*9, (int)(TILE*1.8), 10, 10);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, Math.max(12, (int)(TILE * 0.8)))); 
        
        String modeTxt = isAIPlaying ? "CHẾ ĐỘ: AI" : (isInfiniteMode ? "CHẾ ĐỘ: CLASSIC" : "MÀN: " + level);
        if(isSpeedRun) modeTxt += " (SPEED)";
        g2d.drawString(modeTxt, PAD_X + 15, PAD_TOP/2 + TILE/4);

        // ĐIỂM
        g2d.setColor(new Color(240, 101, 67)); 
        String topScoreTxt = "Score: " + score;
        FontMetrics fmTop = g2d.getFontMetrics();
        int sBoxW = fmTop.stringWidth(topScoreTxt) + 30;
        g2d.fillRoundRect(getWidth() - PAD_X - sBoxW, PAD_TOP/2 - (int)(TILE*1.2), sBoxW, (int)(TILE*1.8), 10, 10);
        g2d.setColor(Color.WHITE);
        g2d.drawString(topScoreTxt, getWidth() - PAD_X - sBoxW + 15, PAD_TOP/2 + TILE/4);

        // KHUNG GAME
        g2d.setColor(new Color(22, 56, 82)); 
        g2d.fillRoundRect(PAD_X - 10, PAD_TOP - 10, TILE*WIDTH_TILES + 20, TILE*HEIGHT_TILES + 20, 35, 35);

        Shape oldClip = g2d.getClip();
        g2d.clipRect(PAD_X, PAD_TOP, TILE*WIDTH_TILES, TILE*HEIGHT_TILES);
        g2d.translate(PAD_X, PAD_TOP); 

        g2d.setColor(new Color(253, 246, 227)); 
        g2d.fillRect(0, 0, TILE*WIDTH_TILES, TILE*HEIGHT_TILES);

        // VẼ TƯỜNG (Logical * TILE)
        for (Point w : walls) {
            g2d.setColor(new Color(143, 104, 76)); 
            g2d.fillRoundRect(w.x * TILE + 1, w.y * TILE + 1, TILE - 2, TILE - 2, 8, 8);
        }

        // VẼ TÁO
        g2d.setColor(new Color(231, 76, 60)); 
        g2d.fillOval(fruitX * TILE + 2, fruitY * TILE + 2, TILE - 4, TILE - 4);
        g2d.setColor(new Color(46, 204, 113)); 
        g2d.fillOval(fruitX * TILE + TILE/2, fruitY * TILE, TILE/3, TILE/4);

        if (isBigFruitActive) {
            int pulse = (int)(Math.sin(System.currentTimeMillis() / 150.0) * (TILE / 6.0));
            g2d.setColor(new Color(139, 69, 19)); 
            g2d.fillOval(bigFruitX*TILE - pulse, bigFruitY*TILE - pulse, TILE + pulse*2, TILE + pulse*2);
            g2d.setColor(Color.WHITE);
            g2d.fillOval(bigFruitX*TILE + TILE/4, bigFruitY*TILE + TILE/4, TILE/4, TILE/4);
            g2d.fillOval(bigFruitX*TILE + TILE - TILE/2, bigFruitY*TILE + TILE/4, TILE/4, TILE/4);
            g2d.setColor(Color.BLACK);
            g2d.fillOval(bigFruitX*TILE + TILE/4 + 2, bigFruitY*TILE + TILE/4 + 2, TILE/8, TILE/8);
            g2d.fillOval(bigFruitX*TILE + TILE - TILE/2 + 2, bigFruitY*TILE + TILE/4 + 2, TILE/8, TILE/8);
            
            // Vẽ thanh thời gian đếm ngược cho Big Fruit
            int maxTicks = 5000 / currentBaseDelay;
            int barWidth = (int) ((double) bigFruitTimer / maxTicks * TILE);
            g2d.setColor(Color.RED);
            g2d.fillRect(bigFruitX * TILE, bigFruitY * TILE + TILE, barWidth, 4);
        }

        // VẼ RẮN
        for (int i = 0; i < snakeLength; i++) {
            if (i == 0) {
                g2d.setColor(new Color(255, 204, 0)); 
                g2d.fillRoundRect(snakeX[i]*TILE, snakeY[i]*TILE, TILE, TILE, 15, 15);
                
                g2d.setColor(Color.WHITE);
                int eyeSize = Math.max(3, TILE / 3);
                if (dir == 'u' || dir == 'd') {
                    g2d.fillOval(snakeX[i]*TILE + TILE/5, snakeY[i]*TILE + TILE/5, eyeSize, eyeSize);
                    g2d.fillOval(snakeX[i]*TILE + TILE - TILE/5 - eyeSize, snakeY[i]*TILE + TILE/5, eyeSize, eyeSize);
                    g2d.setColor(Color.BLACK); 
                    g2d.fillOval(snakeX[i]*TILE + TILE/5 + eyeSize/4, snakeY[i]*TILE + TILE/5 + eyeSize/4, eyeSize/2, eyeSize/2);
                    g2d.fillOval(snakeX[i]*TILE + TILE - TILE/5 - eyeSize + eyeSize/4, snakeY[i]*TILE + TILE/5 + eyeSize/4, eyeSize/2, eyeSize/2);
                } else {
                    g2d.fillOval(snakeX[i]*TILE + TILE/5, snakeY[i]*TILE + TILE/5, eyeSize, eyeSize);
                    g2d.fillOval(snakeX[i]*TILE + TILE/5, snakeY[i]*TILE + TILE - TILE/5 - eyeSize, eyeSize, eyeSize);
                    g2d.setColor(Color.BLACK); 
                    g2d.fillOval(snakeX[i]*TILE + TILE/5 + eyeSize/4, snakeY[i]*TILE + TILE/5 + eyeSize/4, eyeSize/2, eyeSize/2);
                    g2d.fillOval(snakeX[i]*TILE + TILE/5 + eyeSize/4, snakeY[i]*TILE + TILE - TILE/5 - eyeSize + eyeSize/4, eyeSize/2, eyeSize/2);
                }
            } else {
                g2d.setColor(new Color(255, 220, 50)); 
                g2d.fillRoundRect(snakeX[i]*TILE + 2, snakeY[i]*TILE + 2, TILE - 4, TILE - 4, 10, 10);
            }
        }

        g2d.translate(-PAD_X, -PAD_TOP);
        g2d.setClip(oldClip);
    }

    public void actionPerformed(ActionEvent e) {
        if (!gameOver && !levelClear && !gameFinished && !isPaused && !nameInputPanel.isVisible()) {

            if (isBigFruitActive) {
                bigFruitTimer--;
                if (bigFruitTimer <= 0) isBigFruitActive = false; 
            }

            if (isAIPlaying) dir = calculateAIMove();

            for (int i = snakeLength; i > 0; i--) {
                snakeX[i] = snakeX[i - 1];
                snakeY[i] = snakeY[i - 1];
            }

            if (dir == 'r') snakeX[0]++;
            if (dir == 'l') snakeX[0]--;
            if (dir == 'u') snakeY[0]--;
            if (dir == 'd') snakeY[0]++;

            if (isInfiniteMode) {
                if (snakeX[0] < 0) snakeX[0] = WIDTH_TILES - 1;
                else if (snakeX[0] >= WIDTH_TILES) snakeX[0] = 0;
                
                if (snakeY[0] < 0) snakeY[0] = HEIGHT_TILES - 1;
                else if (snakeY[0] >= HEIGHT_TILES) snakeY[0] = 0;
            } else {
                if (snakeX[0] < 0 || snakeY[0] < 0 || snakeX[0] >= WIDTH_TILES || snakeY[0] >= HEIGHT_TILES) {
                    triggerGameOver();
                    return;
                }
            }

            if (!isInfiniteMode && walls.contains(new Point(snakeX[0], snakeY[0]))) {
                triggerGameOver();
                return;
            }
                
            for (int i = 1; i < snakeLength; i++) {
                if (snakeX[0] == snakeX[i] && snakeY[0] == snakeY[i]) {
                    triggerGameOver();
                    return;
                }
            }

            if (snakeX[0] == fruitX && snakeY[0] == fruitY) {
                if (!App.isMuted) App.foodSoundPlayer.playOnce(); 
                score += 5;
                snakeLength++;
                smallFruitCount++; 
                spawnFruit();

                if (smallFruitCount == 4) {
                    spawnBigFruit();
                    isBigFruitActive = true;
                    bigFruitTimer = 5000 / currentBaseDelay; 
                    smallFruitCount = 0; 
                }

                if (!isInfiniteMode && score >= targetScore) { 
                    if (level == 6) triggerGameFinished(); 
                    else triggerLevelClear();
                    return;
                }
            }

            if (isBigFruitActive && snakeX[0] == bigFruitX && snakeY[0] == bigFruitY) {
                if (!App.isMuted) App.foodSoundPlayer.playOnce(); 
                
                int maxTicks = 5000 / currentBaseDelay;
                
              
                int maxBigFruitScore = 30; 
                int bonusScore = (int) ((double) bigFruitTimer / maxTicks * maxBigFruitScore);
                
                
                if (bonusScore < 5) {
                    bonusScore = 5;
                }
                
                score += bonusScore;
                
                snakeLength++;
                isBigFruitActive = false; 

                if (!isInfiniteMode && score >= targetScore) { 
                    if (level == 6) triggerGameFinished(); 
                    else triggerLevelClear();
                    return;
                }
            }
        }
        repaint(); 
    }
}