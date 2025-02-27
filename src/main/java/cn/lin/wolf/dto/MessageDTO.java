package cn.lin.wolf.dto;


import cn.lin.wolf.constants.MessageTypeEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Description:
 * @Author: linch
 * @Date: 2025-02-19
 */

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private String userName;
    private Integer type;
    private String message;

    public MessageDTO(MessageTypeEnums typeEnums) {
        this.type = typeEnums.getType();
        this.message = typeEnums.getDescription();
    }

    public MessageDTO(MessageTypeEnums typeEnums, String message) {
        this.type = typeEnums.getType();
        this.message = message;
    }

    public MessageDTO(String userName, MessageTypeEnums typeEnums, String message) {
        this.userName = userName;
        this.type = typeEnums.getType();
        this.message = message;
    }

    public MessageDTO(String userName, MessageTypeEnums typeEnums) {
        this.userName = userName;
        this.type = typeEnums.getType();
        this.message = typeEnums.getDescription();
    }
}
