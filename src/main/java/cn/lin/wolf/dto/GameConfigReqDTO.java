package cn.lin.wolf.dto;


import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class GameConfigReqDTO {

    @NotNull(message = "玩家数量不能为空")
    @Min(value = 5, message = "玩家数量至少为 5")
    public Integer userNum;

    @NotNull(message = "狼人数量不能为空")
    @Min(value = 1, message = "狼人数量至少为 1")
    public Integer wolfNum;

    @NotNull(message = "猎人数量不能为空")
    public Integer hunterNum;
    @NotNull(message = "游戏规则不能为空")
    public Integer gameRule;
}
