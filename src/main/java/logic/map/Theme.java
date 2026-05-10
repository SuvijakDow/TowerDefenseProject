package logic.map;

/**
 * Visual environment themes used to style the map.
 */
public enum Theme {
    /** Standard grass and trees theme. */
    NORMAL,
    /** Orange/brown autumn-colored flora theme. */
    AUTUMN,
    /** Pink cherry blossom and spring flora theme. */
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
