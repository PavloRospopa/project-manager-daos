package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl.xml;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.exception.*;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl.AbstractFileDatabase;
import com.gmail.at.rospopa.pavlo.projectmanager.util.Pair;
import com.gmail.at.rospopa.pavlo.projectmanager.util.PropertiesLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class XmlDatabase extends AbstractFileDatabase {
    private static final Logger LOGGER = LogManager.getLogger();
    private static Properties XML_DB_PROP = PropertiesLoader.getInstance().getXmlDBProperties();

    private static final String ROOT_DIR = XML_DB_PROP.getProperty("xml.database.root.dir");
    private static final String TABLES_DIR = XML_DB_PROP.getProperty("xml.database.tables.dir");
    private static final String METADATA_FILE_NAME = XML_DB_PROP.getProperty("xml.database.metadata.file");

    private JavaType tablePropertiesPairType;
    private XmlMapper mapper;

    public XmlDatabase(Path rootDirectoryPath, boolean rewriteOldData) {
        rootDir = rootDirectoryPath.resolve(ROOT_DIR);
        metadataFile = rootDir.resolve(METADATA_FILE_NAME);
        tablesDir = rootDir.resolve(TABLES_DIR);
        this.rewriteOldData = rewriteOldData;

        createXmlMapper();
    }

    public XmlDatabase(Path rootDirectoryPath) {
        this(rootDirectoryPath, false);
    }

    @Override
    public void createTable(String tableName, Class<?> objectsType) {
        checkInitialization();
        checkTableAbsence(tableName);

        XmlTable<?> xmlTable = new XmlTable<>(objectsType, tableName, tablesDir, mapper);
        xmlTable.initTable(true);
        tables.put(tableName, xmlTable);
        registerTable(tableName, objectsType);
    }

    @Override
    public void dropTable(String tableName) {
        checkInitialization();
        checkTablePresence(tableName);
        tables.remove(tableName);

        List<String> tablePropertiesList = new ArrayList<>();

        for (String table : tables.keySet()) {
            Pair<String, String> tablePropertiesPair = new Pair<>(table, tables.get(table).getObjectsType().getName());
            String xml = toXml(tablePropertiesPair);
            tablePropertiesList.add(xml);
        }
        try {
            Files.write(metadataFile, tablePropertiesList);
        } catch (IOException e) {
            LOGGER.error("Cannot rewrite xml db metadata file", e);
            throw new UncheckedIOException(e);
        }

        deleteTableDir(tableName);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    private void registerTable(String tableName, Class<?> objectsType) {
        Pair<String, String> tablePropertiesPair = new Pair<>(tableName, objectsType.getName());

        String xml = toXml(tablePropertiesPair);
        try {
            Files.write(metadataFile, Collections.singleton(xml), StandardOpenOption.APPEND);
        } catch (IOException e) {
            LOGGER.error("IO Error while trying to write to xml database metadata file", e);
            throw new UncheckedIOException(e);
        }
    }

    @Override
    protected void createTablesFromMetadataFile() {
        List<String> tablePropertiesList;
        try {
            tablePropertiesList = Files.readAllLines(metadataFile);
        } catch (IOException e) {
            LOGGER.error("IO error occurred while trying to read xml db metadata file", e);
            throw new UncheckedIOException(e);
        }

        for (String tableProperty : tablePropertiesList) {
            Pair<String, String> tablePropertiesPair = fromXml(tableProperty, tablePropertiesPairType);
            String tableName = tablePropertiesPair.getLeft();
            String className = tablePropertiesPair.getRight();
            Class<?> objectsType;
            try {
                objectsType = Class.forName(className);
            } catch (ClassNotFoundException e) {
                LOGGER.error(String.format("Cannot find and load class %s described in xml db metadata file " +
                        "for table %s", className, tableName), e);
                throw new InvalidObjectTypeException();
            }
            XmlTable<?> table = new XmlTable<>(objectsType, tableName, tablesDir, mapper);
            table.initTable(false);
            tables.put(tableName, table);
        }
    }

    private void createXmlMapper() {
        StdSerializer<java.sql.Date> dateSer = new StdSerializer<java.sql.Date>(java.sql.Date.class) {
            @Override
            public void serialize(java.sql.Date date, JsonGenerator jGen, SerializerProvider provider)
                    throws IOException {
                provider.defaultSerializeDateValue(date.getTime(), jGen);
            }
        };

        StdSerializer<java.sql.Timestamp> timestampSer = new StdSerializer<java.sql.Timestamp>(java.sql.Timestamp.class) {
            @Override
            public void serialize(java.sql.Timestamp date, JsonGenerator jGen, SerializerProvider provider)
                    throws IOException {
                provider.defaultSerializeDateValue(date.getTime(), jGen);
            }
        };

        StdDeserializer<java.sql.Date> dateDeser = new StdDeserializer<java.sql.Date>(java.sql.Date.class) {
            @Override
            public Date deserialize(JsonParser jsonparser, DeserializationContext context)
                    throws IOException, JsonProcessingException {
                long timestamp = jsonparser.getValueAsLong();
                return new Date(timestamp);
            }
        };

        StdDeserializer<java.sql.Timestamp> timestampDeser = new StdDeserializer<java.sql.Timestamp>(java.sql.Timestamp.class) {
            @Override
            public Timestamp deserialize(JsonParser jsonparser, DeserializationContext context)
                    throws IOException, JsonProcessingException {
                long timestamp = jsonparser.getValueAsLong();
                return new Timestamp(timestamp);
            }
        };

        SimpleModule module = new SimpleModule();
        module.addSerializer(java.sql.Date.class, dateSer)
                .addDeserializer(java.sql.Date.class, dateDeser)
                .addSerializer(java.sql.Timestamp.class, timestampSer)
                .addDeserializer(java.sql.Timestamp.class, timestampDeser);

        mapper = new XmlMapper();
        mapper.registerModule(module);
        tablePropertiesPairType = mapper.getTypeFactory()
                .constructParametricType(Pair.class, String.class, String.class);
    }

    private String toXml(Object object) {
        String xml;
        try {
            xml = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error while trying to serialize object to xml", e);
            throw new RuntimeException();
        }
        return xml;
    }

    private <T> T fromXml(String xml, JavaType type) {
        try {
            return mapper.readValue(xml, type);
        } catch (IOException e) {
            LOGGER.error("Error while trying deserialize object from xml string", e);
            throw new UncheckedIOException(e);
        }
    }
}
