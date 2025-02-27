package cn.lin.wolf.utils;


import cn.lin.wolf.data.GameData;
import cn.lin.wolf.dto.MessageDTO;

/**
 * @Description:
 * @Author: linch
 * @Date: 2025-02-20
 */

public class MessageUtils {

    private MessageUtils(){}

    public static void broadcast(MessageDTO messageDTO) {
        ExecutorContainer.singleThreadExecutor.submit(() -> GameData.sendMessageToAllUser(messageDTO));
    }

    public static void broadcastLiveUser(MessageDTO messageDTO) {
        ExecutorContainer.singleThreadExecutor.submit(() -> GameData.broadcastLiveUser(messageDTO));
    }

    public static void sendMessageToUser(MessageDTO messageDTO) {
        ExecutorContainer.singleThreadExecutor.submit(() -> GameData.sendMessageToUser(messageDTO, messageDTO.getUserName()));
    }

    public static void sendMessageToUserDirect(MessageDTO messageDTO) {
        GameData.sendMessageToUser(messageDTO, messageDTO.getUserName());
    }

    public static void broadcastDirect(MessageDTO messageDTO) {
        GameData.sendMessageToAllUser(messageDTO);
    }
}
