package com.blackbooks.sql;

import java.util.HashMap;

/**
 * Class to manage brokers.
 */
public class BrokerManager {

    private final static HashMap<Class<?>, Broker<?>> mBrokerMap = new HashMap<Class<?>, Broker<?>>();

    /**
     * Get the broker to access the table corresponding to a given type.
     *
     * @param type Class of the type whose table will be accessed by the broker.
     * @return Broker<T>.
     */
    @SuppressWarnings("unchecked")
    public static <T> Broker<T> getBroker(Class<T> type) {
        if (!mBrokerMap.containsKey(type)) {
            mBrokerMap.put(type, new Broker<T>(type));
        }
        return (Broker<T>) mBrokerMap.get(type);
    }
}
