import javax.sound.sampled.*; 
import java.io.File;

public class MusicPlayer {
    private Clip clip;

    public void loadMusic(String filePath) {
        try {
            File musicPath = new File(filePath);
            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                clip = AudioSystem.getClip();
                clip.open(audioInput);
            } else {
                System.out.println("Không tìm thấy file nhạc: " + filePath);
            }
        } catch (Exception ex) {
            System.out.println("Lỗi đọc file nhạc: " + ex.getMessage());
        }
    }

    public void play() {
        if (clip != null) {
            clip.setFramePosition(0); 
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY); 
        }
    }

    // --- HÀM PHÁT MỘT LẦN CHUYÊN DÙNG CHO HIỆU ỨNG ÂM THANH (SFX) ---
    public void playOnce() {
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop(); 
            }
            clip.setFramePosition(0); 
            clip.start(); 
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }
}