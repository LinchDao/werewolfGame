package cn.lin.wolf.constants;


import lombok.Getter;

/**
 * @Description:游戏状态
 * @Author: linch
 * @Date: 2025-02-19
 */

@Getter
public enum GameStatusEnums {
    GAME_WATTING(0, "游戏未开始"),
    GAME_STARTED(1, "游戏开始了"),
    ;
    private final int status;
    private final String description;

    GameStatusEnums(Integer status, String description) {
        this.status = status;
        this.description = description;
    }
}
