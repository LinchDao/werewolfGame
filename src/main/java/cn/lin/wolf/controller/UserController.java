package cn.lin.wolf.controller;


import cn.lin.wolf.config.GameConfig;
import cn.lin.wolf.constants.AudioEnums;
import cn.lin.wolf.constants.GameStatusEnums;
import cn.lin.wolf.constants.MessageTypeEnums;
import cn.lin.wolf.data.GameData;
import cn.lin.wolf.data.GameOperation;
import cn.lin.wolf.data.GamingCheckService;
import cn.lin.wolf.dto.MessageDTO;
import cn.lin.wolf.po.UserDO;
import cn.lin.wolf.utils.AudioPlayer;
import cn.lin.wolf.utils.MessageUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

import static cn.lin.wolf.constants.MessageTypeEnums.HUNTER_KILL;
import static cn.lin.wolf.constants.MessageTypeEnums.HUNTER_KILL2;

/**
 * @Description:
 * @Author: linch
 * @Date: 2025-02-20
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping("/join")
    public String joinGame(@RequestParam String userName, HttpSession session) {
        if (GamingCheckService.canJoin(userName)) {
            UserDO user = GameData.joinUser(userName);
            //如果用户是第二次加入，查询用户的角色发送角色消息，再查询用户状态，发送存活消息
            if (GameData.getGameState() != GameStatusEnums.GAME_WATTING.getStatus()) {
                MessageUtils.sendMessageToUserDirect(new MessageDTO(MessageTypeEnums.ROLE, user.getRole()));
                if (Boolean.FALSE.equals(user.getIsLive())) {
                    MessageUtils.sendMessageToUserDirect(new MessageDTO(MessageTypeEnums.DEAD_MSG));
                }
            }
            session.setAttribute("userName", userName);
            return "success";
        }
        return "fail";
    }

    @PostMapping("/get/username")
    public String getUserName(HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        MessageUtils.broadcast(new MessageDTO(MessageTypeEnums.COMMON, String.format("【%s】加入了房间。", userName)));

        if (GameConfig.getUserNum() == GameData.getUserSize() && GameData.getGameState() == GameStatusEnums.GAME_WATTING.getStatus()) {
            MessageUtils.broadcast(new MessageDTO(MessageTypeEnums.COMMON, "所有玩家已准备完毕，开始发放身份。"));
            AudioPlayer.playTextToSpeech(AudioEnums.ALL_PLAYER_JOIN.getText());
            GameOperation.startGame();
        }

        return userName;
    }

    @PostMapping("/msg/send")
    public void receiveMessageFromUser(@RequestBody MessageDTO messageDTO) {

        String userName = messageDTO.getUserName();

        GamingCheckService.flush(userName);

        MessageTypeEnums messageTypeEnums = MessageTypeEnums.getMessageTypeEnum(messageDTO.getType());
        if (messageTypeEnums == null) {
            throw new RuntimeException("未知的消息类型。");
        }

        switch (messageTypeEnums) {
            case NIGHT_READY:
                GameOperation.playerReadyToNight(userName);
                break;
            case WOLF_PRE_SELECT:
                GameOperation.notifyWolfSelected(userName, messageDTO.getMessage(), MessageTypeEnums.WOLF_PRE_SELECT);
                break;
            case WOLF_SELECTED:
                GameOperation.notifyWolfSelected(userName, messageDTO.getMessage(), MessageTypeEnums.WOLF_SELECTED);
                break;
            case PROPHET_SELECT:
                GameOperation.tellUserRole(userName, messageDTO.getMessage());
                break;
            case PROPHET_COMFIRM:
                AudioPlayer.playTextToSpeech(AudioEnums.SEER_CLOSE_EYES.getText());
                GameOperation.notifyWitchSelected();
                break;
            case WITCH_SELECT:
                GameOperation.handleWitchSelect(userName, messageDTO.getMessage());
                break;
            case HUNTER_KILL:
                GameOperation.hunterKill(userName, messageDTO.getMessage(), HUNTER_KILL);
                break;
            case HUNTER_KILL2:
                GameOperation.hunterKill(userName, messageDTO.getMessage(), HUNTER_KILL2);
                break;
            case DAYLIGHT:
                break;
            case VILLAGER_SELECT:
                GameOperation.villagerSelect(userName, messageDTO.getMessage());
                break;
        }
    }

    @PostMapping("/msg/receive")
    public List<MessageDTO> sendMessageToUser(@RequestBody Map<String, Object> params) {
        return GameData.listUserMessage((String) params.get("userName"));
    }
}
