package com.blackbooks.sql;

import java.util.HashMap;

/**
 * Class to manage Full-Text-Search brokers.
 */
public class FTSBrokerManager {

    private final static HashMap<Class<?>, FTSBroker<?>> mBrokerMap = new HashMap<Class<?>, FTSBroker<?>>();

    /**
     * Get the broker to access the table corresponding to a given type.
     *
     * @param type Class of the type whose table will be accessed by the broker.
     * @return FTSBroker<T>.
     */
    @SuppressWarnings("unchecked")
    public static <T> FTSBroker<T> getBroker(Class<T> type) {
        if (!mBrokerMap.containsKey(type)) {
            mBrokerMap.put(type, new FTSBroker<T>(type));
        }
        return (FTSBroker<T>) mBrokerMap.get(type);
    }
}
