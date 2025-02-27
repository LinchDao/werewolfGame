package cn.lin.wolf.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description:
 * @Author: linch
 * @Date: 2025-02-19
 */

@Controller
public class HtmlController {

    @RequestMapping("/join")
    public String joinHtml() {
        return "join";
    }

    @RequestMapping("/game/set")
    public String setGame() {
        return "setGame";
    }

    @RequestMapping("/gaming")
    public String gamingHtml() {
        return "gaming";
    }

    @RequestMapping("/entry")
    public String entryHtml() {
        return "entry";
    }

    @RequestMapping("/file/set")
    public String filesetHtml() {
        return "fileset";
    }

    @RequestMapping("/voice/set")
    public String voicesetHtml() {
        return "voiceset";
    }

}
