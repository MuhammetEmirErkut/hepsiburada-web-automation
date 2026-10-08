package com.vrgsl.step.helper;

import com.thoughtworks.gauge.datastore.DataStore;
import com.thoughtworks.gauge.datastore.DataStoreFactory;

public class StoreHelper {

    // Scenario Level DataStore
    public static void putScenario(String key, Object value) {
        DataStoreFactory.getScenarioDataStore().put(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getScenario(String key) {
        return (T) DataStoreFactory.getScenarioDataStore().get(key);
    }

    public static String getScenarioString(String key, String defaultValue) {
        Object val = getScenario(key);
        return val != null ? String.valueOf(val) : defaultValue;
    }

    public static boolean hasScenarioKey(String key) {
        return DataStoreFactory.getScenarioDataStore().get(key) != null;
    }

    // Spec Level DataStore
    public static void putSpec(String key, Object value) {
        DataStoreFactory.getSpecDataStore().put(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getSpec(String key) {
        return (T) DataStoreFactory.getSpecDataStore().get(key);
    }

    // Suite Level DataStore
    public static void putSuite(String key, Object value) {
        DataStoreFactory.getSuiteDataStore().put(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getSuite(String key) {
        return (T) DataStoreFactory.getSuiteDataStore().get(key);
    }
}
