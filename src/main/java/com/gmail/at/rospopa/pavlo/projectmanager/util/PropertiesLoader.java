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
    private static final String JSON_DB_PROPERTIES = "jsonDatabase.properties";
    private static final String XML_DB_PROPERTIES = "xmlDatabase.properties";
    private static final String BIN_DB_PROPERTIES = "binDatabase.properties";

    private Properties dbProperties;
    private Properties metamodelProperties;
    private Properties jsonDBProperties;
    private Properties xmlDBProperties;
    private Properties binDBProperties;

    private PropertiesLoader() {
        dbProperties = loadProperties(ResourcesUtil.getResourceInputStream(DB_PROPERTIES));
        metamodelProperties = loadProperties(ResourcesUtil.getResourceInputStream(METAMODEL_PROPERTIES));
        jsonDBProperties = loadProperties(ResourcesUtil.getResourceInputStream(JSON_DB_PROPERTIES));
        xmlDBProperties = loadProperties(ResourcesUtil.getResourceInputStream(XML_DB_PROPERTIES));
        binDBProperties = loadProperties(ResourcesUtil.getResourceInputStream(BIN_DB_PROPERTIES));
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

    public Properties getJsonDBProperties() {
        return jsonDBProperties;
    }

    public Properties getXmlDBProperties() {
        return xmlDBProperties;
    }

    public Properties getBinDBProperties() {
        return binDBProperties;
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
