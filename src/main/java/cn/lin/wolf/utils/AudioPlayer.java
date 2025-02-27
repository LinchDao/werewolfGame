package cn.lin.wolf.utils;

import cn.lin.wolf.config.GameConfig;
import cn.lin.wolf.constants.AudioEnums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * @Description:
 * @Author: linch
 * @Date: 2025-02-24
 */

public class AudioPlayer {

    private static final Logger logger = LoggerFactory.getLogger(AudioPlayer.class);

    private static final String PROJECT_DIRECTORY = GameConfig.getPROJECT_DIRECTORY();
    private static final String AUDIO_DIRECTORY_NAME = "audio";

    private AudioPlayer() {
    }

    // 播放文本对应的语音
    public static void playTextToSpeech(String text) {
        logger.info("开始执行播放语音：{}", text);
        ExecutorContainer.singleThreadExecutor.submit(() -> {
            // 使用 voice 和 text 作为键查找语音文件路径
            String fileName = generateFileName(GameConfig.getVoice(), text);
            File file = getMp3File(fileName);

            if (file != null && file.exists()) {
                play(file);
            } else {
                playTextToSpeech(AudioEnums.GEN.getText());
            }
        });
    }

    /**
     * 播放音频文件
     *
     * @param file 音频文件的路径
     */
    private static void play(File file) {
        try (AudioInputStream audio = load(file)) {
            playAudio(audio);
        } catch (Exception e) {
            playTextToSpeech(AudioEnums.PLAYER_FALL.getText());
            e.printStackTrace();
        }
    }

    /**
     * 播放音频
     *
     * @param audio 音频流
     */
    private static void playAudio(AudioInputStream audio) {
        try (SourceDataLine line = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, audio.getFormat()))) {
            line.open(audio.getFormat());
            line.start();

            byte[] buffer = new byte[1024];
            int len;
            while ((len = audio.read(buffer)) > 0) {
                line.write(buffer, 0, len);
            }

            line.drain();
            line.stop();
        } catch (Exception e) {
            throw new RuntimeException("播放音频时出错: " + e.getMessage(), e);
        }
    }

    /**
     * 加载音频文件并设置为PCM格式
     *
     * @param file 音频文件
     * @return AudioInputStream 音频输入流
     * @throws UnsupportedAudioFileException, IOException
     */
    private static AudioInputStream load(File file) throws UnsupportedAudioFileException, IOException {
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
        AudioFormat format = audioStream.getFormat();

        // 转换音频格式为PCM格式
        format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(), 16, format.getChannels(),
                format.getChannels() * 2, format.getSampleRate(), false);

        return AudioSystem.getAudioInputStream(format, audioStream);
    }

    private static File getMp3File(String fileName) {
        // 从资源文件夹读取
        ClassPathResource classPathResource = new ClassPathResource(PROJECT_DIRECTORY + AUDIO_DIRECTORY_NAME + File.separator + fileName);
        if (classPathResource.exists()) {
            try {
                return classPathResource.getFile();
            } catch (IOException e) {
                throw new RuntimeException("无法加载资源文件：" + fileName, e);
            }
        }

        // 从系统目录读取
        String systemDirectory = GameConfig.getSystemDirectory();
        if (systemDirectory != null) {
            File file = new File(systemDirectory + AUDIO_DIRECTORY_NAME + File.separator + fileName);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }

    // 生成 voice 和 text 的唯一键
    private static String generateFileName(String voice, String text) {
        return voice + "_" + text.hashCode() + ".mp3";
    }


}
