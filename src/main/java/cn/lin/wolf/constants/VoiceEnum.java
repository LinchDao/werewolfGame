package cn.lin.wolf.constants;


import lombok.Getter;

/**
 * @Description: 法官声音类型
 * @Author: linch
 * @Date: 2025-02-21
 */

public enum VoiceEnum {
    ZH_CN_XIAOXIAONEURAL("zh-CN-XiaoxiaoNeural", "Female", "News, Novel", "Warm", "小小"),
    ZH_CN_XIAOYINEURAL("zh-CN-XiaoyiNeural", "Female", "Cartoon, Novel", "Lively", "小怡"),
    ZH_CN_YUNJIANNEURAL("zh-CN-YunjianNeural", "Male", "Sports, Novel", "Passion", "云剑"),
    ZH_CN_YUNXINEURAL("zh-CN-YunxiNeural", "Male", "Novel", "Lively, Sunshine", "云熙"),
    ZH_CN_YUNXIANEURAL("zh-CN-YunxiaNeural", "Male", "Cartoon, Novel", "Cute", "云霞"),
    ZH_CN_YUNYANGNEURAL("zh-CN-YunyangNeural", "Male", "News", "Professional, Reliable", "云阳"),
    ZH_CN_LIAONING_XIAOBEINEURAL("zh-CN-liaoning-XiaobeiNeural", "Female", "Dialect", "Humorous", "小贝"),
    ZH_CN_SHAANXI_XIAONINEURAL("zh-CN-shaanxi-XiaoniNeural", "Female", "Dialect", "Bright", "小妮");

    @Getter
    private String code;
    @Getter
    private String chineseName;
    private String gender;
    private String style;
    private String tone;


    VoiceEnum(String code, String gender, String style, String tone, String chineseName) {
        this.code = code;
        this.gender = gender;
        this.style = style;
        this.tone = tone;
        this.chineseName = chineseName;
    }

    public static VoiceEnum getByCode(String code) {
        for (VoiceEnum voice : values()) {
            if (voice.getCode().equals(code)) {
                return voice;
            }
        }
        return null;
    }
}
