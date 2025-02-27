package cn.lin.wolf.po;


import cn.lin.wolf.dto.MessageDTO;
import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @Description:
 * @Author: linch
 * @Date: 2025-02-19
 */

@Data
@Builder
public class UserDO {

    private static final Logger logger = LoggerFactory.getLogger(UserDO.class);

    private String userName;
    private String role;
    private Boolean isLive;
    private LinkedList<MessageDTO> messageList;

    /**
     * 向消息队列中加入一条消息
     *
     * @param message 要加入的消息
     */
    public void addMessage(MessageDTO message) {

        synchronized (this) {
            if (messageList == null) {
                messageList = new LinkedList<>();
            }
            logger.info("【{}】addMessage: {}", this.userName, message);
            messageList.add(message); // 将消息加入队列
        }
    }

    /**
     * 从消息队列中一次性取出所有消息
     *
     * @return 所有消息的列表
     */
    public List<MessageDTO> pollAllMessages() {
        synchronized (this) {
            List<MessageDTO> messages = new ArrayList<>();
            if (messageList == null || messageList.isEmpty()) {
                return messages;
            }
            messages = new ArrayList<>(messageList);

            messageList.clear();

            for (MessageDTO message : messages) {
                logger.info("【{}】pollAllMessages: {}", this.userName, message);
            }
            return messages;

        }
    }
}
