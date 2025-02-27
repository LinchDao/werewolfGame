package cn.lin.wolf.constants;


import lombok.Getter;

/**
 * @Description:信息类型
 * @Author: linch
 * @Date: 2025-02-19
 */


@Getter
public enum MessageTypeEnums {
    ROLE(1, "身份消息"),
    TELL_NIGHT_READY(2, "准备入夜"),
    NIGHT_READY(3, "已准备好入夜"),
    WOLF_SELECT(4, "狼人开始选人"),
    WOLF_PRE_SELECT(5, "狼人【%s】预选【%s】"),
    WOLF_SELECTED(6, "狼人【%s】选定了【%s】"),
    WOLF_SLEEP(7, "通知狼人闭眼"),
    PROPHET_SELECT(8, "预言家查验身份"),
    PROPHET_COMFIRM(9, "预言家确认"),
    WITCH_SELECT(10, "女巫选择"),
    DAYLIGHT(11, "天亮"),
    HUNTER_KILL(12, "猎人狼杀"),
    HUNTER_KILL2(14, "猎人票杀"),
    VILLAGER_SELECT(13, "村民投票"),
    DEAD_MSG(20, "死亡信息"),
    GAME_END(21, "游戏结束"),
    GAME_RESTART(22, "游戏重开"),

    PLAYER_DISCONNECT(23, "玩家暂离。"),
    PLAYER_LEAVE(24, "玩家离开。"),
    GAME_CONFIG_CHANGED(25, "游戏设置已更改"),

    COMMON(99, "直接展示的消息"),

    ;
    private final Integer type;
    private final String description;

    MessageTypeEnums(Integer type, String description) {
        this.type = type;
        this.description = description;
    }

    public static MessageTypeEnums getMessageTypeEnum(Integer type) {
        for (MessageTypeEnums item : MessageTypeEnums.values()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }
}


