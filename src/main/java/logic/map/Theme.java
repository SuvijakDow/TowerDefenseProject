package logic.map;

/**
 * Visual environment themes used to style the map.
 */
public enum Theme {
    NORMAL,
    AUTUMN,
    SPRING;

    /**
     * Returns {@link #NORMAL} when the given theme is {@code null}.
     *
     * @param theme theme to validate
     * @return provided theme, or {@link #NORMAL} if null
     */
    public static Theme defaultIfNull(Theme theme) {
        return theme == null ? NORMAL : theme;
    }
}
