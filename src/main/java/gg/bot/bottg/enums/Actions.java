package gg.bot.bottg.enums;

public enum Actions {

    START_COMMAND_NEW_USER("START_COMMAND_NEW_USER"),
    START_COMMAND_EXIST_USER("START_COMMAND_EXIST_USER"),
    YES_COMMAND("YES_COMMAND"),
    NO_COMMAND("NO_COMMAND"),
    GIZMO_LOGIN_ENTER("GIZMO_LOGIN_ENTER"),
    GIZMO_PASS_ENTER("GIZMO_PASS_ENTER"),
    GIZMO_USER_EXIST("GIZMO_USER_EXIST"),
    GIZMO_LOGIN_AND_PASS_CORRECT("GIZMO_LOGIN_AND_PASS_CORRECT"),
    GIZMO_LOGIN_AND_PASS_INCORRECT("GIZMO_LOGIN_AND_PASS_INCORRECT"),
    GETPRIZES_COMMAND("GETPRIZES_COMMAND"),
    CALLBACK_UPDATE_PAGE("CALLBACK_UPDATE_PAGE"),
    CALLBACK_DAY,
    CALLBACK_PAGE_GO_TO,
    GET_COMPUTERS_SESSIONS_AUTH,
    GET_COMPUTERS_SESSIONS_NO_AUTH;

    private String action;

    Actions(String action) {
    }

    Actions() {
    }

    public String getActionName() {
        return action;
    }

    public String callbackDay(int day) {
        return "CALLBACK_DAY_" + day;
    }

    public String callbackPage(int page) {
        return "CALLBACK_PAGE_GO_TO_" + page;
    }
}

