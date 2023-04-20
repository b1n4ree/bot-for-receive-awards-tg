package gg.bot.bottg.jsonObjects;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserIdGizmoJson implements Serializable {

    private Integer version;
    private Integer gizmoId;
    private String httpStatusCode;
    private String message;
    private Boolean isError;
}
