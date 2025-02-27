package cn.lin.wolf.constants;


import lombok.Getter;

/**
 * @Description:扮演角色
 * @Author: linch
 * @Date: 2025-02-19
 */


@Getter
public enum RoleEnums {
    WOLF("狼人"),
    HUNTER("猎人"),
    PROPHET("预言家"),
    WITCH("女巫"),
    VILLAGER("村民");

    // 获取角色的显示名称
    private final String displayName;  // 显示的身份名称

    // 构造函数
    RoleEnums(String displayName) {
        this.displayName = displayName;
    }

    // 可以根据枚举名来获取角色对象
    public static RoleEnums fromString(String roleName) {
        for (RoleEnums role : RoleEnums.values()) {
            if (role.displayName.equals(roleName)) {
                return role;
            }
        }
        return null;
    }
}

