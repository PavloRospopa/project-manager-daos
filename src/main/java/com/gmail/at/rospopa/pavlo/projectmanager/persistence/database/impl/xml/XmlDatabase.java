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
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.Database;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.exception.*;
import com.gmail.at.rospopa.pavlo.projectmanager.util.FileUtils;
import com.gmail.at.rospopa.pavlo.projectmanager.util.Pair;
import com.gmail.at.rospopa.pavlo.projectmanager.util.PropertiesLoader;
import com.gmail.at.rospopa.pavlo.projectmanager.util.Prototype;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class XmlDatabase implements Database {
    private static final Logger LOGGER = LogManager.getLogger();
    private static Properties XML_DB_PROP = PropertiesLoader.getInstance().getXmlDBProperties();

    private static final String ROOT_DIR = XML_DB_PROP.getProperty("xml.database.root.dir");
    private static final String TABLES_DIR = XML_DB_PROP.getProperty("xml.database.tables.dir");
    private static final String METADATA_FILE_NAME = XML_DB_PROP.getProperty("xml.database.metadata.file");

    private Path rootDir;
    private Path metadataFile;
    private Path tablesDir;

    private JavaType tablePropertiesPairType;
    private XmlMapper mapper;

    private Map<String, XmlTable<?>> tables;
    private boolean databaseInitialized;
    private boolean rewriteOldData;

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
    public void initDatabase() {
        tables = new HashMap<>();

        if (!rewriteOldData) {
            checkDatabaseDirectoryStructure();
            createTablesFromMetadataFile();
        } else {
            cleanDatabaseDirectories();
            createDatabaseStructure();
        }
        databaseInitialized = true;
    }

    @Override
    public boolean isInitialized() {
        return databaseInitialized;
    }

    @Override
    public Set<String> getTableNames() {
        checkInitialization();
        return tables.keySet().stream().collect(Collectors.toSet());
    }

    @Override
    public void createTable(String tableName, Class<? extends Prototype> objectsType) {
        checkInitialization();
        checkTableAbsence(tableName);

        XmlTable<?> xmlTable = new XmlTable<>(objectsType, tableName, tablesDir, mapper);
        xmlTable.initXmlTable(true);
        tables.put(tableName, xmlTable);
        updateMetadataFile(tableName, objectsType);
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
    public void clearTable(String tableName) {
        checkInitialization();
        checkTablePresence(tableName);
        tables.get(tableName).clear();
    }

    @Override
    public boolean tableExists(String tableName) {
        return tables.containsKey(tableName);
    }

    @Override
    public Long getNextId(String tableName) {
        return tables.get(tableName).getNextId();
    }

    @Override
    public <T extends Prototype> Map<Long, T> selectFrom(String tableName) {
        checkInitialization();
        checkTablePresence(tableName);

        XmlTable<T> table = getTable(tableName);

        return table.selectAll();
    }

    @Override
    public <T extends Prototype> T selectFrom(String tableName, Long id) {
        checkInitialization();
        checkTablePresence(tableName);

        XmlTable<T> table = getTable(tableName);
        return table.selectByKey(id);
    }

    @Override
    public <T extends Prototype> Map<Long, T> selectFrom(String tableName, Predicate<T> filter) {
        checkInitialization();
        checkTablePresence(tableName);

        XmlTable<T> table = getTable(tableName);
        return table.select(filter);
    }

    @Override
    public <T extends Prototype> Long add(String tableName, T object) {
        checkInitialization();
        checkTablePresence(tableName);

        XmlTable<T> table = getTable(tableName);
        Long id = table.getAndGenerateNextId();
        table.put(id, object);

        return id;
    }

    @Override
    public <T extends Prototype> void insert(String tableName, Long id, T object) {
        checkInitialization();
        checkTablePresence(tableName);

        XmlTable<T> table = getTable(tableName);
        table.put(id, object);
    }

    @Override
    public <T extends Prototype> boolean update(String tableName, Long id, T object) {
        checkInitialization();
        checkTablePresence(tableName);

        XmlTable<T> table = getTable(tableName);

        return table.replace(id, object);
    }

    @Override
    public boolean deleteFrom(String tableName, Long id) {
        checkInitialization();
        checkTablePresence(tableName);

        return tables.get(tableName).remove(id);
    }

    private void deleteTableDir(String tableName) {
        Path tableDirPath = tablesDir.resolve(tableName);
        try {
            FileUtils.deleteFileTree(tableDirPath);
        } catch (IOException e) {
            LOGGER.error("IO error occurred while trying to delete file tree of table directory " + tableName, e);
            throw new UncheckedIOException(e);
        }
    }

    private void updateMetadataFile(String tableName, Class<?> objectsType) {
        Pair<String, String> tablePropertiesPair = new Pair<>(tableName, objectsType.getName());

        String xml = toXml(tablePropertiesPair);
        try {
            Files.write(metadataFile, Collections.singleton(xml), StandardOpenOption.APPEND);
        } catch (IOException e) {
            LOGGER.error("IO Error while trying to write to xml database metadata file", e);
            throw new UncheckedIOException(e);
        }
    }

    private void createTablesFromMetadataFile() {
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
            table.initXmlTable(false);
            tables.put(tableName, table);
        }
    }

    private void cleanDatabaseDirectories() {
        if (Files.exists(rootDir)) {
            try {
                FileUtils.deleteFileTree(rootDir);
            } catch (IOException e) {
                LOGGER.error("IO error occurred while trying to delete xml database root directory recursively", e);
                throw new UncheckedIOException(e);
            }
        }
    }

    private void createDatabaseStructure() {
        try {
            Files.createDirectories(tablesDir);
            Files.createFile(metadataFile);
        } catch (IOException e) {
            LOGGER.error("Cannot create database directory structure in file system", e);
            throw new UncheckedIOException(e);
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

    private void checkDatabaseDirectoryStructure() {
        if (!(Files.exists(rootDir) && Files.exists(tablesDir)) && Files.exists(metadataFile)) {
            LOGGER.error("Xml database directory structure is invalid");
            throw new InvalidDatabaseStructureException();
        }
    }

    private void checkInitialization() {
        if (!databaseInitialized) {
            LOGGER.error("Database has to be initialized before working with it`s data");
            throw new DatabaseNotInitializedException();
        }
    }

    private void checkTablePresence(String tableName) {
        if (!tables.containsKey(tableName)) {
            LOGGER.error("Table with given name does not exist");
            throw new NoSuchTableException();
        }
    }

    private void checkTableAbsence(String tableName) {
        if (tables.containsKey(tableName)) {
            LOGGER.error("Table with given name already exists");
            throw new TableAdditionException();
        }
    }

    private <T> XmlTable<T> getTable(String tableName) {
        return (XmlTable<T>) tables.get(tableName);
    }
}
