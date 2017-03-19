package com.gmail.at.rospopa.pavlo.projectmanager.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

public class PropertiesLoader {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final PropertiesLoader INSTANCE = new PropertiesLoader();

    private static final String DB_PROPERTIES = "database.properties";
    private static final String METAMODEL_PROPERTIES = "metamodel.properties";

    private Properties dbProperties;
    private Properties metamodelProperties;

    private PropertiesLoader() {
        dbProperties = loadProperties(ResourcesUtil.getResourceInputStream(DB_PROPERTIES));
        metamodelProperties = loadProperties(ResourcesUtil.getResourceInputStream(METAMODEL_PROPERTIES));
    }

    public static PropertiesLoader getInstance() {
        return INSTANCE;
    }

    public Properties getDbProperties() {
        return dbProperties;
    }

    public Properties getMetamodelProperties() {
        return metamodelProperties;
    }

    private Properties loadProperties(InputStream is) {
        Properties properties = new Properties();
        try {
            properties.load(is);
        } catch (IOException e) {
            LOGGER.error("IO Exception during loading properties from input stream", e);
            throw new UncheckedIOException(e);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Properties file represented this by input stream contains " +
                    "a malformed Unicode escape sequence", e);
            throw e;
        }

        return properties;
    }
}
