package cn.lin.wolf.data;


import cn.lin.wolf.config.GameConfig;
import cn.lin.wolf.constants.GameStatusEnums;
import cn.lin.wolf.constants.MessageTypeEnums;
import cn.lin.wolf.dto.MessageDTO;
import cn.lin.wolf.utils.MessageUtils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:检测玩家是否暂离
 * @Author: linch
 * @Date: 2025-02-24
 */

public class GamingCheckService {

    //超过该间隔，认为玩家离线
    private static long disconnectTime = 10000;

    private static final ConcurrentHashMap<String, Long> gamingCheck = new ConcurrentHashMap<>();

    private GamingCheckService() {
    }

    /**
     * 判断是否可以加入玩家
     *
     * @param userName
     * @return
     */
    public static boolean canJoin(String userName) {
        return gamingCheck.containsKey(userName) || GameConfig.getUserNum() >= gamingCheck.size();
    }

    /**
     * 刷新玩家心跳
     *
     * @param userName
     */
    public static void flush(String userName) {
        gamingCheck.put(userName, System.currentTimeMillis());
    }

    /**
     * 检查游戏中玩家的上次心跳，超过十秒认为离线
     */
    public static void checkGamingAndDelete() {
        long now = System.currentTimeMillis();
        for (String userName : gamingCheck.keySet()) {
            if (now - gamingCheck.get(userName) > disconnectTime) {
                gamingCheck.remove(userName);
                if (GameData.getGameState() == GameStatusEnums.GAME_WATTING.getStatus()) {
                    MessageUtils.broadcastDirect(new MessageDTO(MessageTypeEnums.PLAYER_LEAVE, String.format("【%s】离开了房间。", userName)));
                    GameData.deleteUser(userName);
                } else {
                    MessageUtils.broadcastDirect(new MessageDTO(MessageTypeEnums.PLAYER_DISCONNECT, String.format("【%s】暂离了房间。", userName)));
                }
            }
        }
    }
}
