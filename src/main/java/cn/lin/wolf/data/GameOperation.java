package cn.lin.wolf.data;


import cn.lin.wolf.config.GameConfig;
import cn.lin.wolf.constants.AudioEnums;
import cn.lin.wolf.constants.GameStatusEnums;
import cn.lin.wolf.constants.MessageTypeEnums;
import cn.lin.wolf.constants.RoleEnums;
import cn.lin.wolf.dto.MessageDTO;
import cn.lin.wolf.po.UserDO;
import cn.lin.wolf.utils.AudioPlayer;
import cn.lin.wolf.utils.ExecutorContainer;
import cn.lin.wolf.utils.MessageUtils;
import com.alibaba.fastjson2.JSON;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * @Description:
 * @Author: linch
 * @Date: 2025-02-19
 */

public class GameOperation {

    //用户和狼人操作计数器
    private static int playerCounter = 0;
    private static int wolfCounter = 0;
    //女巫是否可以救人或者毒人
    public static boolean witchCanSave = Boolean.TRUE;
    public static boolean witchCanKill = Boolean.TRUE;
    //投票计数器
    private static ConcurrentHashMap<String, Integer> voteMap = new ConcurrentHashMap<>();
    //记录夜晚狼杀、和女巫操作
    private static String wolfKillUserName = null;
    private static String witchKillUserName = null;
    private static boolean witchSave = false;


    private GameOperation() {
    }

    /**
     * 重置游戏数据
     */
    public static void resetGamingData() {
        playerCounter = 0;
        wolfCounter = 0;
        voteMap = new ConcurrentHashMap<>();
        wolfKillUserName = null;
        witchKillUserName = null;

        witchCanSave = Boolean.TRUE;
        witchCanKill = Boolean.TRUE;

        witchSave = false;
    }

    /**
     * 开始游戏
     */
    public static void startGame() {
        GameData.setGameState(GameStatusEnums.GAME_STARTED.getStatus());
        // 1. 分配用户身份并重置生命状态
        GameData.resetUserRole();
        GameData.flashUserLive();
        // 2. 给各个玩家发送消息提示其身份
        notifyPlayersOfTheirRole();
        // 3、准备入夜
        MessageUtils.broadcast(new MessageDTO(MessageTypeEnums.TELL_NIGHT_READY));
    }

    /**
     * 向每个玩家发送他们的角色
     */
    private static void notifyPlayersOfTheirRole() {
        Iterator<UserDO> iterable = GameData.getUserList().iterator();
        while (iterable.hasNext()) {
            UserDO user = iterable.next();
            // 创建 MessageDTO 消息
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setType(MessageTypeEnums.ROLE.getType());
            messageDTO.setMessage(user.getRole());

            user.addMessage(messageDTO);
        }
    }

    /**
     * 玩家已经准备好入夜
     *
     * @param userName
     * @throws InterruptedException
     */
    public static void playerReadyToNight(String userName) {
        MessageUtils.broadcast(new MessageDTO(MessageTypeEnums.COMMON, String.format("【%s】已准备好入夜", userName)));
        if (playerFinished()) {
            AudioPlayer.playTextToSpeech(AudioEnums.NIGHT_PHASE_START.getText());
            MessageUtils.broadcast(new MessageDTO(MessageTypeEnums.COMMON, "天黑请闭眼"));

            ExecutorContainer.pauseGameProcess(GameConfig.getPauseBeforeNotifyWolf());

            AudioPlayer.playTextToSpeech(AudioEnums.WEREWOLVES_WAKE_UP.getText());
            GameOperation.notifyWolf();
        }
    }

    /**
     * 玩家操作计数器，是否玩家已全部完成操作
     *
     * @return
     */
    public static boolean playerFinished() {
        playerCounter++;
        if (playerCounter == GameData.getLiveUserSize(null)) {
            playerCounter = 0;
            return true;
        }
        return false;
    }

    /**
     * 狼人玩家操作计数器，是否玩家已全部完成操作
     *
     * @return
     */
    public static boolean wolfFinished() {
        wolfCounter++;
        if (wolfCounter == GameData.getLiveUserSize(RoleEnums.WOLF)) {
            wolfCounter = 0;
            return true;
        }
        return false;
    }

    /**
     * 唤醒狼人
     */
    public static void notifyWolf() {
        List<UserDO> liveUserList = GameData.listLiveUser(null);
        List<UserDO> liveWolfList = GameData.listLiveUser(RoleEnums.WOLF);

        Map<String, List<String>> res = new HashMap<>();
        res.put("liveUserList", liveUserList.stream().map(UserDO::getUserName).collect(Collectors.toList()));
        res.put("liveWolfList", liveWolfList.stream().map(UserDO::getUserName).collect(Collectors.toList()));

        for (UserDO user : liveWolfList) {
            MessageUtils.sendMessageToUser(new MessageDTO(user.getUserName(), MessageTypeEnums.WOLF_SELECT, JSON.toJSONString(res)));
        }
    }

    /**
     * 唤醒女巫
     *
     * @param userName
     * @param selectedName
     * @param messageTypeEnums
     */
    public static void notifyWolfSelected(String userName, String selectedName, MessageTypeEnums messageTypeEnums) {

        List<UserDO> liveWolfList = GameData.listLiveUser(RoleEnums.WOLF);


        for (UserDO user : liveWolfList.stream().filter(u -> !u.getUserName().equals(userName)).collect(Collectors.toList())) {
            MessageUtils.sendMessageToUser(new MessageDTO(user.getUserName(), messageTypeEnums, String.format(messageTypeEnums.getDescription(), userName, selectedName)));
        }

        if (messageTypeEnums.equals(MessageTypeEnums.WOLF_SELECTED)) {
            Integer voteNum = voteMap.getOrDefault(selectedName, 0);
            voteMap.put(selectedName, voteNum + 1);
            if (wolfFinished()) {
                try {
                    String highestVotedPlayer = getHighestVotedPlayer();

                    if (isTie(highestVotedPlayer)) {
                        AudioPlayer.playTextToSpeech(AudioEnums.WOLF_REVOTE.getText());
                        notifyWolf();
                    }
                    wolfKillUserName = highestVotedPlayer;
                    voteMap.clear();

                    AudioPlayer.playTextToSpeech(AudioEnums.WEREWOLVES_CLOSE_EYES.getText());
                    AudioPlayer.playTextToSpeech(AudioEnums.SEER_WAKE_UP.getText());

                    notifyProphet();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 唤醒预言家
     *
     */
    private static void notifyProphet() {
        List<UserDO> prophetList = GameData.listLiveUser(RoleEnums.PROPHET);

        if (!prophetList.isEmpty()) {
            UserDO prophet = prophetList.get(0);
            List<UserDO> liveUserList = GameData.listLiveUser(null);
            List<String> userNameList = liveUserList.stream().filter(x -> !x.getUserName().equals(prophet.getUserName())).map(UserDO::getUserName).collect(Collectors.toList());
            MessageUtils.sendMessageToUser(new MessageDTO(prophet.getUserName(), MessageTypeEnums.PROPHET_SELECT, JSON.toJSONString(userNameList)));
        } else {
            ExecutorContainer.pauseGameProcess(GameConfig.getPauseIfProphetDead());
            AudioPlayer.playTextToSpeech(AudioEnums.SEER_CLOSE_EYES.getText());
            notifyWitchSelected();
        }
    }

    /**
     * 查找票数最高的玩家
     *
     * @return
     */
    private static String getHighestVotedPlayer() {
        return voteMap.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * 判断是否有多个玩家票数相同
     *
     * @param highestVotedPlayer
     * @return
     */
    private static boolean isTie(String highestVotedPlayer) {
        int maxVotes = voteMap.getOrDefault(highestVotedPlayer, 0);
        return Collections.frequency(voteMap.values(), maxVotes) > 1;
    }

    /**
     * 返回玩家的阵营信息
     *
     * @param userName
     * @param selectedUserName
     */
    public static void tellUserRole(String userName, String selectedUserName) {
        UserDO selectedUser = GameData.getUserByUsername(selectedUserName);
        String type;
        if (Objects.equals(selectedUser.getRole(), RoleEnums.WOLF.getDisplayName())) {
            type = "狼";
        } else {
            type = "好人";
        }
        MessageUtils.sendMessageToUser(new MessageDTO(userName, MessageTypeEnums.PROPHET_COMFIRM, String.format("【%s】的身份是【%s】", userName, type)));
    }

    /**
     * 唤醒女巫
     *
     * @throws InterruptedException
     */
    public static void notifyWitchSelected() {
        ExecutorContainer.pauseGameProcess(GameConfig.getPauseBeforeNotifyWitch());

        AudioPlayer.playTextToSpeech(AudioEnums.WITCH_WAKE_UP.getText());

        List<UserDO> witchUserList = GameData.listLiveUser(RoleEnums.WITCH);
        List<UserDO> liveUserList = GameData.listLiveUser(null);

        if (!witchUserList.isEmpty() && (witchCanKill || witchCanSave)) {
            UserDO witchUser = witchUserList.get(0);

            Map<String, Object> res = new HashMap<>();
            res.put("target", wolfKillUserName);  // 被狼人杀害的玩家姓名
            res.put("canSave", witchCanSave);  // 是否可以救人
            res.put("canPoison", witchCanKill);  // 是否可以毒人
            res.put("poisonTargets", liveUserList.stream()
                    .map(UserDO::getUserName)
                    .filter(userName -> !Objects.equals(userName, witchUser.getUserName()))
                    .collect(Collectors.toList()));  // 可毒杀的玩家列表

            MessageUtils.sendMessageToUser(new MessageDTO(witchUser.getUserName(), MessageTypeEnums.WITCH_SELECT, JSON.toJSONString(res)));
            return;
        }

        AudioPlayer.playTextToSpeech(AudioEnums.WITCH_CLOSE_EYES.getText());
        if (witchUserList.isEmpty() || (!witchCanKill && !witchCanSave)) {
            dayLight();
        }
    }

    /**
     * 天亮操作
     */
    public static void dayLight() {
        //1、统计人员受害情况
        List<String> deadUserName = new ArrayList<>();
        if (!witchSave) {
            GameData.setUserLive(wolfKillUserName, false);
            deadUserName.add(wolfKillUserName);
        }
        if (witchKillUserName != null) {
            GameData.setUserLive(witchKillUserName, false);
            deadUserName.add(witchKillUserName);
            MessageUtils.sendMessageToUser(new MessageDTO(witchKillUserName, MessageTypeEnums.DEAD_MSG));
        }
        AudioPlayer.playTextToSpeech(AudioEnums.ALL_PLAYERS_WAKE_UP.getText());

        if (deadUserName.isEmpty()) {
            MessageUtils.broadcastLiveUser(new MessageDTO(MessageTypeEnums.COMMON
                    , "昨晚是平安夜。"));
            AudioPlayer.playTextToSpeech(AudioEnums.SWEET_NIGHT.getText());
        } else {
            MessageUtils.broadcast(new MessageDTO(MessageTypeEnums.COMMON
                    , "昨晚的受害者是：" + String.join(",", deadUserName)));
            AudioPlayer.playTextToSpeech(AudioEnums.DAY_PHASE_START.getText());
        }

        //2、判断是否游戏结束
        if (gameEnd()) return;

        List<UserDO> liveUserList = GameData.listLiveUser(null);
        String liveUserJson = JSON.toJSONString(liveUserList.stream().map(UserDO::getUserName).collect(Collectors.toList()));

        //3、如果猎人狼杀，猎人开枪
        UserDO killuser = GameData.getUserByUsername(wolfKillUserName);
        if (!witchSave && Objects.equals(killuser.getRole(), RoleEnums.HUNTER.getDisplayName())) {
            MessageUtils.sendMessageToUser(new MessageDTO(killuser.getUserName(), MessageTypeEnums.HUNTER_KILL, liveUserJson));
            AudioPlayer.playTextToSpeech(AudioEnums.HUNTER.getText());
        } else {
            for (UserDO user : liveUserList) {
                MessageUtils.sendMessageToUser(new MessageDTO(user.getUserName(), MessageTypeEnums.VILLAGER_SELECT, liveUserJson));
            }
        }
        witchSave = false;
        witchKillUserName = null;
        wolfKillUserName = null;
    }

    /**
     * 判断游戏是否结束
     *
     * @return true:false 结束/继续
     */
    private static boolean gameEnd() {
        boolean gameOver = false;

        // 获取游戏中各阵营的存活玩家列表
        List<UserDO> allLiveUsers = GameData.listLiveUser(null); // 存活的所有玩家
        List<UserDO> liveWolfList = allLiveUsers.stream().filter(x -> Objects.equals(x.getRole(), RoleEnums.WOLF.getDisplayName())).collect(Collectors.toList()); // 存活的狼人
        List<UserDO> liveVillagerList = allLiveUsers.stream().filter(x -> Objects.equals(x.getRole(), RoleEnums.VILLAGER.getDisplayName())).collect(Collectors.toList()); // 存活的村民
        UserDO witch = allLiveUsers.stream().filter(x -> Objects.equals(x.getRole(), RoleEnums.WITCH.getDisplayName())).findFirst().orElse(null);
        UserDO prophet = allLiveUsers.stream().filter(x -> Objects.equals(x.getRole(), RoleEnums.PROPHET.getDisplayName())).findFirst().orElse(null);
        UserDO hunter = allLiveUsers.stream().filter(x -> Objects.equals(x.getRole(), RoleEnums.HUNTER.getDisplayName())).findFirst().orElse(null);

        int effectiveVillagerCount = allLiveUsers.size() - liveWolfList.size();

        if (witchCanSave && witchCanKill && witch != null) {
            effectiveVillagerCount++;  // 女巫既有解药也有毒药，表示好人阵营增加1人
        }

        // 判断游戏是否结束的逻辑
        switch (GameConfig.getGameRule()) {
            case RULE_SET_1:
                // 1. 如果所有狼人都死了，游戏结束，且好人胜利
                if (liveWolfList.isEmpty()) {
                    gameOver = true;
                    MessageUtils.broadcast(new MessageDTO(MessageTypeEnums.GAME_END, "好人胜利，全部狼人被淘汰。"));
                    AudioPlayer.playTextToSpeech(AudioEnums.GAME_END1.getText());
                }
                // 2. 如果所有村民都死了，游戏结束，狼人胜利
                else if (liveVillagerList.isEmpty()) {
                    gameOver = true;
                    MessageUtils.broadcast(new MessageDTO(MessageTypeEnums.GAME_END, "狼人胜利，全部村民都已经被淘汰。"));
                    AudioPlayer.playTextToSpeech(AudioEnums.GAME_END2.getText());
                }
                // 3. 如果神职全部死亡，游戏结束，狼人胜利
                else if (prophet == null && witch == null && hunter == null) {
                    gameOver = true;
                    MessageUtils.broadcast(new MessageDTO(MessageTypeEnums.GAME_END, "狼人胜利，全部神职都已经被淘汰。"));
                    AudioPlayer.playTextToSpeech(AudioEnums.GAME_END3.getText());
                }
                // 4. 如果狼人和好人的比例达到 1:1 或者好人数量小于等于狼人数量，游戏结束，狼人胜利
                else if (effectiveVillagerCount <= liveWolfList.size()) {
                    gameOver = true;
                    MessageUtils.broadcast(new MessageDTO(MessageTypeEnums.GAME_END, "狼人胜利，好人数量小于或等于狼人。"));
                    AudioPlayer.playTextToSpeech(AudioEnums.GAME_END4.getText());
                }
                break;

            case RULE_SET_2:
                // 1. 如果狼人和好人的比例达到 1:1 或者好人数量小于等于狼人数量，游戏结束，狼人胜利
                if (effectiveVillagerCount <= liveWolfList.size()) {
                    gameOver = true;
                    MessageUtils.broadcast(new MessageDTO(MessageTypeEnums.GAME_END, "狼人胜利，好人数量小于或等于狼人。"));
                    AudioPlayer.playTextToSpeech(AudioEnums.GAME_END4.getText());
                }
                // 2. 如果所有狼人都死了，游戏结束，好人胜利
                else if (liveWolfList.isEmpty()) {
                    gameOver = true;
                    MessageUtils.broadcast(new MessageDTO(MessageTypeEnums.GAME_END, "好人胜利，全部狼人被淘汰。"));
                    AudioPlayer.playTextToSpeech(AudioEnums.GAME_END1.getText());
                }
                break;

            default:
                throw new IllegalArgumentException("未知的游戏结束规则: ");
        }

        // 如果游戏结束,设置游戏状态
        if (gameOver) {
            GameData.setGameState(GameStatusEnums.GAME_WATTING.getStatus());
        }

        return gameOver;
    }

    /**
     * 处理女巫的操作
     *
     * @param userName
     * @param message
     */
    public static void handleWitchSelect(String userName, String message) {
        Map res = JSON.parseObject(message, Map.class);
        Boolean save = (Boolean) res.get("save");
        String poisonTarget = (String) res.get("poisonTarget");
        if (Boolean.TRUE.equals(save)) {
            witchCanSave = Boolean.FALSE;
        }
        witchSave = save;
        if (poisonTarget != null && !poisonTarget.isEmpty()) {
            GameData.setUserLive(poisonTarget, false);
            witchCanSave = Boolean.FALSE;
            witchKillUserName = poisonTarget;
        }

        AudioPlayer.playTextToSpeech(AudioEnums.WITCH_CLOSE_EYES.getText());
        dayLight();
    }

    /**
     * 处理猎人反杀
     *
     * @param userName
     * @param selectedUserName
     * @param hunterKill
     */
    public static void hunterKill(String userName, String selectedUserName, MessageTypeEnums hunterKill) {
        GameData.setUserLive(selectedUserName, false);
        MessageUtils.broadcast(new MessageDTO(MessageTypeEnums.COMMON, String.format("猎人【%s】攻击了【%s】", userName, selectedUserName)));
        MessageUtils.sendMessageToUser(new MessageDTO(userName, MessageTypeEnums.DEAD_MSG));
        if (gameEnd()) {
            return;
        }
        List<UserDO> liveUserList = GameData.listLiveUser(null);
        String liveUserJson = JSON.toJSONString(liveUserList.stream().map(UserDO::getUserName).collect(Collectors.toList()));
        if (hunterKill.getType().equals(MessageTypeEnums.HUNTER_KILL.getType())) {
            for (UserDO user : liveUserList) {
                MessageUtils.sendMessageToUser(new MessageDTO(user.getUserName(), MessageTypeEnums.VILLAGER_SELECT, liveUserJson));
            }
        } else {
            for (UserDO user : liveUserList) {
                MessageUtils.sendMessageToUser(new MessageDTO(user.getUserName(), MessageTypeEnums.TELL_NIGHT_READY, liveUserJson));
            }
        }

    }

    /**
     * 白天投票处理
     *
     * @param userName
     * @param selectedUserName
     */
    public static void villagerSelect(String userName, String selectedUserName) {
        //1、通知投票情况
        if (selectedUserName != null && !selectedUserName.isEmpty()) {
            MessageUtils.broadcast(new MessageDTO(MessageTypeEnums.COMMON, String.format("【%s】投给了【%s】", userName, selectedUserName)));
            Integer voteNum = voteMap.getOrDefault(selectedUserName, 0);
            voteMap.put(selectedUserName, voteNum + 1);
        } else {
            MessageUtils.broadcast(new MessageDTO(MessageTypeEnums.COMMON, String.format("【%s】弃票", userName)));
        }
        //2、如果所有人都已经投票，取最高票人标记死亡，平票则入夜
        if (playerFinished()) {
            String highestVotedPlayer = getHighestVotedPlayer();

            if (highestVotedPlayer != null && !isTie(highestVotedPlayer)) {
                GameData.setUserLive(highestVotedPlayer, false);
                String role = GameData.getUserRole(highestVotedPlayer);
                MessageUtils.broadcastLiveUser(new MessageDTO(MessageTypeEnums.COMMON, String.format("最高票数的人是【%s】", highestVotedPlayer)));

                //3、被处决者如果是猎人要处理猎人特殊逻辑，否则判断游戏是否结束
                if (Objects.equals(role, RoleEnums.HUNTER.getDisplayName())) {
                    List<UserDO> liveUserList = GameData.listLiveUser(null);
                    String liveUserJson = JSON.toJSONString(liveUserList.stream().map(UserDO::getUserName).collect(Collectors.toList()));
                    MessageUtils.sendMessageToUser(new MessageDTO(highestVotedPlayer, MessageTypeEnums.HUNTER_KILL2, liveUserJson));
                    AudioPlayer.playTextToSpeech(AudioEnums.HUNTER.getText());
                    return;
                } else if (gameEnd()) {
                    return;
                }
            }
            AudioPlayer.playTextToSpeech(AudioEnums.VILLAGER_VOTE_END.getText());
            MessageUtils.broadcastLiveUser(new MessageDTO(MessageTypeEnums.TELL_NIGHT_READY));
        }
    }


}

