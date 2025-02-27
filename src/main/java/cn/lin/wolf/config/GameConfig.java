package cn.lin.wolf.config;


import cn.lin.wolf.constants.GameRuleEnums;
import cn.lin.wolf.constants.RoleEnums;
import cn.lin.wolf.constants.VoiceEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Description: 游戏设置
 * @Author: linch
 * @Date: 2025-02-19
 */

public class GameConfig {

    //玩家、狼人、猎人数量
    @Getter
    private static int userNum = 8;
    private static int wolfNum = 3;
    private static int hunterNum = 1;
    @Getter
    private static List<String> roleList = new CopyOnWriteArrayList<>();
    @Getter
    @Setter
    //法官的语音名称
    private static String voice = VoiceEnum.ZH_CN_XIAOXIAONEURAL.getCode();

    //游戏中必要的暂停时机：天黑到狼人睁眼之间、如预言家死亡的查验时间、预言家闭眼到唤醒女巫的暂停时间
    @Getter
    private static long pauseBeforeNotifyWolf = 2000;
    @Getter
    private static long pauseIfProphetDead = 2000;
    @Getter
    private static long pauseBeforeNotifyWitch = 2000;
    @Getter
    @Setter
    //预设更高自由度
    private static String systemDirectory = null;
    @Getter
    private static final String PROJECT_DIRECTORY = "files" + File.separator;

    private GameConfig() {
    }

    /**
     * 游戏规则
     *
     * @see GameRuleEnums
     */
    @Getter
    private static GameRuleEnums gameRule = GameRuleEnums.RULE_SET_2;

    static {
        updateRoleList();
    }

    /**
     * 改变游戏设置
     *
     * @param userNumSet    玩家数量
     * @param wolfNumSet    狼人数量
     * @param hunterNumSet  猎人数量
     * @param gameRuleEnums 游戏规则
     */
    public static void changeSetting(int userNumSet, int wolfNumSet, int hunterNumSet, GameRuleEnums gameRuleEnums) {
        userNum = userNumSet;
        wolfNum = wolfNumSet;
        hunterNum = hunterNumSet;
        gameRule = gameRuleEnums;

        updateRoleList();
    }


    /**
     * 更新角色列表
     */
    public static void updateRoleList() {
        List<String> newRoleList = new ArrayList<>();

        for (int i = 0; i < wolfNum; i++) {
            newRoleList.add(RoleEnums.WOLF.getDisplayName());
        }
        for (int i = 0; i < hunterNum; i++) {
            newRoleList.add(RoleEnums.HUNTER.getDisplayName());
        }
        newRoleList.add(RoleEnums.PROPHET.getDisplayName());
        newRoleList.add(RoleEnums.WITCH.getDisplayName());

        for (int i = newRoleList.size(); i < userNum; i++) {
            newRoleList.add(RoleEnums.VILLAGER.getDisplayName());
        }

        // 清空原有的角色列表并更新
        roleList.clear();
        roleList.addAll(newRoleList);
    }


}
