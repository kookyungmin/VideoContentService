package net.happykoo.vcs.common;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static java.lang.reflect.Modifier.isStatic;

@Slf4j
public class CacheNames {
    public static final String SEPARATOR = ":";
    public static final String CHANNEL = "channel";

    public static final String VIDEO = "video";
    public static final String VIDEO_LIKE = "video-like";
    public static final String VIDEO_LIST = VIDEO + SEPARATOR + "list";
    public static final String VIDEO_VIEW_COUNT = VIDEO + SEPARATOR + "view-count";

    public static final String USER = "user";
    public static final String USER_SESSION = "user-session";

    public static final String SUBSCRIBE = "subscribe";
    public static final String SUBSCRIBE_CHANNEL_BY_USER = SUBSCRIBE + SEPARATOR + "channel-by-user";
    public static final String SUBSCRIBE_USER = SUBSCRIBE + SEPARATOR + "user";

    public static final String COMMENT = "comment";
    public static final String COMMENT_LIKE = COMMENT + SEPARATOR + "like";
    public static final String COMMENT_PINNED = COMMENT + SEPARATOR + "pinned";

    public static List<String> getCacheNames() {
        List<String> cacheNames = new ArrayList<>();

        Field[] declaredFields = CacheNames.class.getDeclaredFields();

        for(Field field : declaredFields) {
            if (isStatic(field.getModifiers())) {
                try {
                    if(!SEPARATOR.equals(field.get(null))) {
                        cacheNames.add((String) field.get(null));
                    }
                } catch (IllegalAccessException e) {
                    log.error(e.getMessage());
                }
            }
        }

        return cacheNames;
    }
}
