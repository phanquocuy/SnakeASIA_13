import java.awt.*;
import javax.swing.*;

public class MenuPanel extends JPanel {
    
    private App parentApp;
    private JButton btnMusic; 
    private JPanel quitConfirmPanel; 
    private JPanel modernModePanel; 
    private JPanel classicModePanel;
    private JPanel buttonPanel;
    private JPanel instructionsPanel;

    public MenuPanel(App parentApp) {
        this.parentApp = parentApp;
        setLayout(null); 

        createQuitConfirmMenu();
        createModernModeMenu();
        createClassicModeMenu();
        createInstructionsMenu();
        // Panel chứa các nút bấm chính
        buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15); 
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Cổ điển (Classic - Chế Độ Vô Tận)
        JButton btnClassic = createCustomButton("Classic");
        
        btnClassic.addActionListener(e -> classicModePanel.setVisible(true));
        gbc.gridy = 0;
        buttonPanel.add(btnClassic, gbc);

        // 2. Hiện đại (Mở bảng chọn Player / AI)
        JButton btnModern = createCustomButton("Modern");
        btnModern.addActionListener(e -> modernModePanel.setVisible(true));
        gbc.gridy = 1;
        buttonPanel.add(btnModern, gbc);
        // 3. Hướng dẫn chơi
        JButton btnHelp = createCustomButton("How to Play");
        btnHelp.addActionListener(e -> instructionsPanel.setVisible(true));
        gbc.gridy = 2; 
        buttonPanel.add(btnHelp, gbc);

        // 4. Leaderboard (Bảng Xếp Hạng)
        JButton btnScore = createCustomButton("Leaderboard");
        btnScore.addActionListener(e -> parentApp.showScoreScreen());
        gbc.gridy = 3;
        buttonPanel.add(btnScore, gbc);

        // 5. Music
        btnMusic = createCustomButton(App.isMuted ? "Music: OFF" : "Music: ON");
        btnMusic.addActionListener(e -> {
            App.isMuted = !App.isMuted;
            updateMusicButton();
            if (App.isMuted) App.musicPlayer.stop();
            else App.musicPlayer.play();
        });
        gbc.gridy = 4;
        buttonPanel.add(btnMusic, gbc);

        // 6. Quit
        JButton btnQuit = createCustomButton("Quit");
        btnQuit.addActionListener(e -> quitConfirmPanel.setVisible(true));
        gbc.gridy = 5;
        buttonPanel.add(btnQuit, gbc);

        add(buttonPanel);
    }

    // Tự động căn giữa hộp Menu và nút bấm khi phóng to thu nhỏ
    @Override
    public void doLayout() {
        super.doLayout();
        
        if (getWidth() == 0 || getHeight() == 0) return;

        // Tính kích thước Khung Menu 
        int boxW = Math.min(800, getWidth() - 40);
        int boxH = Math.min(700, getHeight() - 40);
        int boxX = (getWidth() - boxW) / 2;
        int boxY = (getHeight() - boxH) / 2;
        
        // Vị trí đặt các nút bấm (chiếm phần dưới của box kem)
        int btnAreaY = boxY + 150;
        int btnAreaH = boxH - 170;
        if (buttonPanel != null) {
            buttonPanel.setBounds(boxX, btnAreaY, boxW, btnAreaH);
        }
        
        if (quitConfirmPanel != null) {
            quitConfirmPanel.setBounds(0, 0, getWidth(), getHeight());
        }
        
        if (modernModePanel != null) {
            modernModePanel.setBounds(0, 0, getWidth(), getHeight());
        }
        if (classicModePanel != null) {
            classicModePanel.setBounds(0, 0, getWidth(), getHeight());
        }
        if (instructionsPanel != null) {
            instructionsPanel.setBounds(0, 0, getWidth(), getHeight());
        }
    }
    
    // --- TẠO BẢNG CHỌN CHẾ ĐỘ MODERN (PLAYER / AI) ---
    private void createModernModeMenu() {
        modernModePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Phủ mờ nền
                g2d.setColor(new Color(0, 0, 0, 180)); 
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Vẽ hộp thoại căn giữa
                int boxW = 450;
                int boxH = 340; 
                int boxX = (getWidth() - boxW) / 2;
                int boxY = (getHeight() - boxH) / 2;
                
                g2d.setColor(new Color(22, 56, 82)); 
                g2d.fillRoundRect(boxX, boxY, boxW, boxH, 25, 25);
                g2d.setColor(new Color(240, 101, 67)); 
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(boxX, boxY, boxW, boxH, 25, 25);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        modernModePanel.setOpaque(false);
        modernModePanel.setLayout(new GridBagLayout());
        modernModePanel.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 20, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel titleLabel = new JLabel("Select Mode");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32)); 
        titleLabel.setForeground(Color.WHITE);
        modernModePanel.add(titleLabel, gbc);

        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.gridy = 1;
        JButton btnPlayer = createCustomButton("Player");
        btnPlayer.addActionListener(e -> {
            modernModePanel.setVisible(false);
            parentApp.showGameScreen(false, false);
        }); 
        modernModePanel.add(btnPlayer, gbc);

        gbc.gridy = 2;
        JButton btnAI = createCustomButton("AI Play");
        btnAI.addActionListener(e -> {
            modernModePanel.setVisible(false);
            parentApp.showGameScreen(true, false);
        }); 
        modernModePanel.add(btnAI, gbc);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(15, 10, 5, 10); 
        JButton btnBack = createCustomButton("Back");
        btnBack.addActionListener(e -> modernModePanel.setVisible(false)); 
        modernModePanel.add(btnBack, gbc);

        add(modernModePanel);
        setComponentZOrder(modernModePanel, 0); 
    }

    private void createClassicModeMenu() {
        classicModePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(0, 0, 0, 180)); 
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                int boxW = 450;
                int boxH = 340; 
                int boxX = (getWidth() - boxW) / 2;
                int boxY = (getHeight() - boxH) / 2;
                
                g2d.setColor(new Color(22, 56, 82)); 
                g2d.fillRoundRect(boxX, boxY, boxW, boxH, 25, 25);
                g2d.setColor(new Color(240, 101, 67)); 
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(boxX, boxY, boxW, boxH, 25, 25);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        classicModePanel.setOpaque(false);
        classicModePanel.setLayout(new GridBagLayout());
        classicModePanel.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 20, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel titleLabel = new JLabel("Classic Mode");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32)); 
        titleLabel.setForeground(Color.WHITE);
        classicModePanel.add(titleLabel, gbc);

        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.gridy = 1;
        JButton btnPlayer = createCustomButton("Player");
        btnPlayer.addActionListener(e -> {
            classicModePanel.setVisible(false);
            parentApp.showGameScreen(false, true); // (isAI = false, isInfinite = true)
        }); 
        classicModePanel.add(btnPlayer, gbc);

        gbc.gridy = 2;
        JButton btnAI = createCustomButton("AI Play");
        btnAI.addActionListener(e -> {
            classicModePanel.setVisible(false);
            parentApp.showGameScreen(true, true); // (isAI = true, isInfinite = true)
        }); 
        classicModePanel.add(btnAI, gbc);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(15, 10, 5, 10); 
        JButton btnBack = createCustomButton("Back");
        btnBack.addActionListener(e -> classicModePanel.setVisible(false)); 
        classicModePanel.add(btnBack, gbc);

        add(classicModePanel);
        setComponentZOrder(classicModePanel, 0); 
    }

    private void createQuitConfirmMenu() {
        quitConfirmPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
               
                g2d.setColor(new Color(0, 0, 0, 180)); 
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
               
                int boxW = 400;
                int boxH = 260;
                int boxX = (getWidth() - boxW) / 2;
                int boxY = (getHeight() - boxH) / 2;
                
                g2d.setColor(new Color(22, 56, 82)); 
                g2d.fillRoundRect(boxX, boxY, boxW, boxH, 25, 25);
                g2d.setColor(new Color(240, 101, 67)); 
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(boxX, boxY, boxW, boxH, 25, 25);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        quitConfirmPanel.setOpaque(false);
        quitConfirmPanel.setLayout(new GridBagLayout());
        quitConfirmPanel.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 20, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel titleLabel = new JLabel("Quit Game?");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28)); 
        titleLabel.setForeground(Color.WHITE);
        quitConfirmPanel.add(titleLabel, gbc);

        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.gridy = 1;
        JButton btnYes = createCustomButton("Yes");
        btnYes.addActionListener(e -> System.exit(0)); 
        quitConfirmPanel.add(btnYes, gbc);

        gbc.gridy = 2;
        JButton btnNo = createCustomButton("No");
        btnNo.addActionListener(e -> quitConfirmPanel.setVisible(false)); 
        quitConfirmPanel.add(btnNo, gbc);

        add(quitConfirmPanel);
        setComponentZOrder(quitConfirmPanel, 0); 
    }

    public void updateMusicButton() {
        if(App.isMuted) btnMusic.setText("Music: OFF");
        else btnMusic.setText("Music: ON");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. NỀN CARO
        g2d.setColor(new Color(248, 249, 250)); 
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(new Color(230, 230, 230)); 
        for(int i = 0; i < getWidth(); i += 40) g2d.drawLine(i, 0, i, getHeight());
        for(int i = 0; i < getHeight(); i += 40) g2d.drawLine(0, i, getWidth(), i);
        
        // Vẽ mây và bụi cỏ 
        g2d.setColor(new Color(0, 168, 89)); 
        g2d.fillOval(getWidth() - 150, getHeight() - 100, 240, 180);
        g2d.fillOval(-60, getHeight() - 120, 180, 160);
        g2d.setColor(new Color(255, 193, 7)); 
        g2d.fillOval(getWidth() - 120, -60, 200, 150);
        g2d.setColor(new Color(171, 190, 251)); 
        g2d.fillOval(-90, 100, 150, 120);

        // Kích thước Khung Menu động
        int boxW = Math.min(800, getWidth() - 40);
        int boxH = Math.min(700, getHeight() - 40);
        int boxX = (getWidth() - boxW) / 2;
        int boxY = (getHeight() - boxH) / 2;

        // 2. KHUNG CHÍNH 
        g2d.setColor(new Color(22, 56, 82)); 
        g2d.fillRoundRect(boxX - 10, boxY - 10, boxW + 20, boxH + 20, 35, 35);
        g2d.setColor(new Color(253, 246, 227)); 
        g2d.fillRoundRect(boxX, boxY, boxW, boxH, 20, 20);

        // 3. TIÊU ĐỀ GAME
        String title = "SNAKE ASIA";
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 110));
        FontMetrics fm = g2d.getFontMetrics();
        int x = boxX + (boxW - fm.stringWidth(title)) / 2;
        int y = boxY + 120; 

        g2d.setColor(new Color(22, 56, 82)); 
        g2d.drawString(title, x + 5, y + 5);
        g2d.setColor(new Color(240, 101, 67)); 
        g2d.drawString(title, x, y);

       
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
        btn.setPreferredSize(new Dimension(300, 55));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            if (!App.isMuted) App.clickSound.playOnce();
        });

        return btn;
    }
    // --- TẠO BẢNG HƯỚNG DẪN CÁCH CHƠI ---
    private void createInstructionsMenu() {
        instructionsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(0, 0, 0, 200)); 
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                int boxW = 820; 
                int boxH = 600; 
                int boxX = (getWidth() - boxW) / 2;
                int boxY = (getHeight() - boxH) / 2;
                
                g2d.setColor(new Color(22, 56, 82)); 
                g2d.fillRoundRect(boxX, boxY, boxW, boxH, 25, 25);
                g2d.setColor(new Color(240, 101, 67)); 
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(boxX, boxY, boxW, boxH, 25, 25);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        instructionsPanel.setOpaque(false);
        instructionsPanel.setLayout(new GridBagLayout());
        instructionsPanel.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel titleLabel = new JLabel("HƯỚNG DẪN CÁCH CHƠI");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32)); 
        titleLabel.setForeground(Color.WHITE);
        instructionsPanel.add(titleLabel, gbc);

        // Nội dung hướng dẫn dùng HTML để dễ định dạng dòng
        String instructionsText = "<html><div style='text-align: center; font-family: Segoe UI; font-size: 16px; color: white;'>"
                + "<br><b style='color: #F1C40F;'>CÁC PHÍM ĐIỀU KHIỂN:</b><br>"
                + "Sử dụng mũi tên <b>[ LÊN / XUỐNG / TRÁI / PHẢI ]</b> hoặc <b>[ W / A / S / D ]</b> để di chuyển.<br>"
                + "Nhấn <b>[ SPACE ]</b> để Tạm dừng / Tiếp tục.<br>"
                + "Nhấn <b>[ ESC ]</b> để quay lại Menu chính.<br><br>"
                + "<b style='color: #F1C40F;'>TÍNH NĂNG ĐẶC BIỆT:</b><br>"
                + "<b>[ Táo lớn ]:</b> Xuất hiện sau khi ăn 4 táo nhỏ. Ăn càng nhanh điểm càng cao!<br>"
                + "<b>Phím [ F ]:</b> Tăng tốc độ game khi AI đang chơi (Chế độ SpeedRun).<br><br>"
                + "<b style='color: #F1C40F;'>CHẾ ĐỘ CHƠI:</b><br>"
                + "<b>Classic:</b> Chơi vô tận, có thể trườn xuyên qua viền màn hình.<br>"
                + "<b>Modern:</b> Vượt 6 màn chơi với các chướng ngại vật khác nhau.<br>"
                + "</div></html>";

        JLabel contentLabel = new JLabel(instructionsText);
        gbc.gridy = 1;
        instructionsPanel.add(contentLabel, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(20, 10, 10, 10); 
        JButton btnClose = createCustomButton("Đã hiểu!");
        btnClose.setPreferredSize(new Dimension(200, 50)); 
        btnClose.addActionListener(e -> instructionsPanel.setVisible(false)); 
        instructionsPanel.add(btnClose, gbc);

        add(instructionsPanel);
        setComponentZOrder(instructionsPanel, 0); 
    }
}