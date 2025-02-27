package cn.lin.wolf.constants;


import lombok.Getter;

/**
 * @Description: 语音内容
 * @Author: linch
 * @Date: 2025-02-19
 */


@Getter
public enum AudioEnums {
    PROG_START("程序已启动。"),
    NIGHT_PHASE_START("所有玩家请闭眼，游戏进入夜晚阶段。"),
    WEREWOLVES_WAKE_UP("狼人请睁眼，选择你们今晚要攻击的玩家。"),
    WEREWOLVES_CLOSE_EYES("狼人请闭眼。"),
    SEER_WAKE_UP("预言家请睁眼，选择你想查验的玩家。"),
    SEER_CLOSE_EYES("预言家请闭眼。"),
    WITCH_WAKE_UP("女巫请睁眼，你有一瓶解药和一瓶毒药，你可以选择救人或毒人。请做出选择。"),
    WITCH_CLOSE_EYES("女巫请闭眼。"),
    ALL_PLAYERS_WAKE_UP("所有人请睁眼，夜晚阶段结束。"),
    DAY_PHASE_START("白天来临，通过系统消息查看昨晚的受害人"),
    DISCUSSION_TIME("请大家开始讨论，看看谁是狼人。"),
    WOLF_REVOTE("狼人请统一你们的意见。"),
    SWEET_NIGHT("昨晚是平安夜。"),
    HUNTER("猎人请选择你要攻击谁。"),
    VILLAGER_VOTE_END("投票已完成。"),

    GAME_END1("游戏结束，好人胜利，全部狼人被淘汰。"),
    GAME_END2("游戏结束，狼人胜利，全部村民都已经被淘汰。"),
    GAME_END3("游戏结束，狼人胜利，全部神职都已经被淘汰。"),
    GAME_END4("游戏结束，狼人胜利，好人数量小于或等于狼人。"),

    ALL_PLAYER_JOIN("所有玩家已经加入房间。开始发放身份。"),
    EXECUTING_RESTART("游戏已经重开。"),
    ALL_PLAYER_READY_NIGHT("所有玩家已经加入房间。开始发放身份。"),


    GEN("你有一条未生成的语音。"),
    GEN_FINISH("语音切换成功。"),
    SWITCH_FAIL("语音切换失败。"),
    PLAYER_FALL("语音播放失败。"),
    GAME_CONFIG_CHANGED("游戏规则已更改。");

    // 获取角色的显示名称
    private final String text;  // 显示的身份名称

    // 构造函数
    AudioEnums(String text) {
        this.text = text;
    }

    // 可以根据枚举名来获取角色对象
    public static AudioEnums fromString(String roleName) {
        for (AudioEnums role : AudioEnums.values()) {
            if (role.text.equals(roleName)) {
                return role;
            }
        }
        return null;
    }
}

