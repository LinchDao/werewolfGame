package cn.lin.wolf.constants;


import lombok.Getter;

/**
 * @Description:游戏规则
 * @Author: linch
 * @Date: 2025-02-26
 */

@Getter
public enum GameRuleEnums {
    RULE_SET_1(1, "狼人、村民、神职任意一边全部死亡游戏结束。"),
    RULE_SET_2(2, "狼人、好人任意一边全部死亡游戏结束。");

    public final int value;
    private final String description;

    private GameRuleEnums(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public static GameRuleEnums find(int gameRule) {
        for (GameRuleEnums gameRuleEnums : GameRuleEnums.values()) {
            if (gameRuleEnums.value == gameRule) {
                return gameRuleEnums;
            }
        }
        return null;
    }
}