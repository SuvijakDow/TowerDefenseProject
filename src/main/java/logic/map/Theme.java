package logic.map;

public enum Theme {
    NORMAL,
    AUTUMN,
    SPRING;

    public static Theme defaultIfNull(Theme theme) {
        return theme == null ? NORMAL : theme;
    }
}
