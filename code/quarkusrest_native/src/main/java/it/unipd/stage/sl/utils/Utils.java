package it.unipd.stage.sl.utils;

import java.util.List;

public abstract class Utils {

    /**
     * @param list source list
     * @param <T> type of object of the list
     * @return source list if not empty, null otherwise
     */
    public static <T> List<T> nullOnEmpty(List<T> list) {
        return list.size() > 0? list : null;
    }
}
