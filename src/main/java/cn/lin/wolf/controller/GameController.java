package cn.lin.wolf.controller;


import cn.lin.wolf.config.GameConfig;
import cn.lin.wolf.constants.AudioEnums;
import cn.lin.wolf.constants.GameRuleEnums;
import cn.lin.wolf.constants.MessageTypeEnums;
import cn.lin.wolf.constants.VoiceEnum;
import cn.lin.wolf.data.GameData;
import cn.lin.wolf.data.GameOperation;
import cn.lin.wolf.dto.GameConfigReqDTO;
import cn.lin.wolf.dto.MessageDTO;
import cn.lin.wolf.utils.AudioPlayer;
import cn.lin.wolf.utils.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Description:
 * @Author: linch
 * @Date: 2025-02-18
 */

@RestController
@RequestMapping("/game")
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    @PostMapping("/config")
    public void configGame(@RequestBody @Validated GameConfigReqDTO gameConfigReq) {
        try {
            GameRuleEnums gameRule = GameRuleEnums.find(gameConfigReq.getGameRule());
            assert gameRule != null;

            GameConfig.changeSetting(gameConfigReq.getUserNum()
                    , gameConfigReq.getWolfNum()
                    , gameConfigReq.getHunterNum()
                    , gameRule);

            restartGame();

            MessageUtils.broadcast(new MessageDTO(MessageTypeEnums.COMMON, "游戏设置已更改。"));
            AudioPlayer.playTextToSpeech(AudioEnums.GAME_CONFIG_CHANGED.getText());

            logger.info("游戏已重置，新配置：userNum={}, wolfNum={}, hunterNum={},gameRule={}",
                    gameConfigReq.getUserNum(), gameConfigReq.getWolfNum(), gameConfigReq.getHunterNum()
                    , gameRule.getDescription());

        } catch (Exception e) {
            logger.error("重置游戏失败", e);
            throw new RuntimeException("重置游戏失败");
        }
    }

    @PostMapping("/set/system/path")
    public void setFilePath(@RequestBody Map<String, Object> params) {
        GameConfig.setSystemDirectory((String) params.get("directory"));
    }


    @GetMapping("/restart")
    public void restartGame() {
        MessageUtils.broadcast(new MessageDTO(MessageTypeEnums.GAME_CONFIG_CHANGED));
        AudioPlayer.playTextToSpeech(AudioEnums.GAME_CONFIG_CHANGED.getText());
        GameData.reSet();
        GameOperation.resetGamingData();

        if (GameData.getUserSize() == GameConfig.getUserNum())
            GameOperation.startGame();
    }

    @PostMapping("voice/set")
    public String getChineseName(@RequestBody Map<String, Object> params) {
        String code = (String) params.get("code");
        VoiceEnum voiceEnum = VoiceEnum.getByCode(code);

        if (voiceEnum != null) {
            GameConfig.setVoice(voiceEnum.getCode());
            AudioPlayer.playTextToSpeech(String.format("设置游戏法官声音，名称为：%s,试听结束。", voiceEnum.getChineseName()));
            return voiceEnum.getChineseName();  // 返回中文名称
        } else {
            AudioPlayer.playTextToSpeech(AudioEnums.SWITCH_FAIL.getText());
            throw new RuntimeException("未找到对应的语音名称");
        }
    }





}
