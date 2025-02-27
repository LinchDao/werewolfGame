package cn.lin.wolf.data;


import cn.lin.wolf.config.GameConfig;
import cn.lin.wolf.constants.GameStatusEnums;
import cn.lin.wolf.constants.MessageTypeEnums;
import cn.lin.wolf.constants.RoleEnums;
import cn.lin.wolf.dto.MessageDTO;
import cn.lin.wolf.po.UserDO;
import cn.lin.wolf.utils.MessageUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @Description:
 * @Author: linch
 * @Date: 2025-02-19
 */

public class GameData {

    @Getter
    private static List<UserDO> userList = new ArrayList<>();

    @Getter
    @Setter
    private static int gameState = GameStatusEnums.GAME_WATTING.getStatus();

    private GameData() {
    }

    public static int getUserSize() {
        return userList.size();
    }

    public static UserDO joinUser(String userName) {
        UserDO user = getUserByUsername(userName);
        if (user != null) {
            return user;
        }

        user = UserDO.builder().userName(userName).isLive(true).build();
        userList.add(user);
        return user;
    }

    public static void flashUserLive() {
        userList.forEach(user -> user.setIsLive(true));
    }

    public static void resetUserRole() {
        List<String> roleList = GameConfig.getRoleList();
        Collections.shuffle(roleList);
        for (int i = 0; i < roleList.size(); i++) {
            userList.get(i).setRole(roleList.get(i));
        }
    }

    public static int getLiveUserSize(RoleEnums roleEnum) {
        int counter = 0;
        for (UserDO user : userList) {
            if (Boolean.TRUE.equals(user.getIsLive()) && (roleEnum == null || user.getRole().equals(roleEnum.getDisplayName()))) {
                counter++;
            }

        }
        return counter;
    }

    public static List<UserDO> listLiveUser(RoleEnums roleEnums) {
        return userList.stream()
                .filter(user -> user.getIsLive() && (roleEnums == null || user.getRole().equals(roleEnums.getDisplayName())))
                .collect(Collectors.toList());
    }

    public static UserDO getUserByUsername(String userName) {
        return userList.stream()
                .filter(user -> Objects.equals(user.getUserName(), userName))
                .findFirst()
                .orElse(null);
    }

    public static void setUserLive(String userName, boolean live) {
        UserDO user = getUserByUsername(userName);
        if (user != null) {
            user.setIsLive(live);
            if (!live && !Objects.equals(user.getRole(), RoleEnums.HUNTER.getDisplayName())) {
                MessageUtils.sendMessageToUser(new MessageDTO(userName, MessageTypeEnums.DEAD_MSG));
            }
        }
    }

    public static void reSet() {
        setGameState(GameStatusEnums.GAME_WATTING.getStatus());
    }

    public static String getUserRole(String userName) {
        UserDO user = getUserByUsername(userName);
        if (user != null) {
            return user.getRole();
        }
        return null;
    }

    public static void deleteUser(String userName) {
        synchronized (GameData.class) {
            userList.removeIf(user -> Objects.equals(user.getUserName(), userName));
        }
    }

    public static void broadcastLiveUser(MessageDTO messageDTO) {
        userList.stream()
                .filter(UserDO::getIsLive)
                .forEach(user -> user.addMessage(messageDTO));
    }

    public static List<MessageDTO> listUserMessage(String userName) {
        for (UserDO user : userList) {
            if (Objects.equals(user.getUserName(), userName)) {
                return user.pollAllMessages();
            }
        }
        return Collections.emptyList();
    }

    public static void sendMessageToAllUser(MessageDTO messageDTO) {
        userList.forEach(user -> user.addMessage(messageDTO));
    }

    public static void sendMessageToUser(MessageDTO messageDTO, String userName) {
        for (UserDO userDTO : userList) {
            if (Objects.equals(userDTO.getUserName(), userName)) {
                userDTO.addMessage(messageDTO);
            }
        }
    }
}
