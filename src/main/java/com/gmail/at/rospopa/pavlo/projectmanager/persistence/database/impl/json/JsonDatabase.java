package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl.json;

import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.Database;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.exception.*;
import com.gmail.at.rospopa.pavlo.projectmanager.util.FileUtils;
import com.gmail.at.rospopa.pavlo.projectmanager.util.Pair;
import com.gmail.at.rospopa.pavlo.projectmanager.util.PropertiesLoader;
import com.gmail.at.rospopa.pavlo.projectmanager.util.Prototype;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class JsonDatabase implements Database {
    private static final Logger LOGGER = LogManager.getLogger();
    private static Properties JSON_DB_PROP = PropertiesLoader.getInstance().getJsonDBProperties();

    private static final String ROOT_DIR = JSON_DB_PROP.getProperty("json.database.root.dir");
    private static final String TABLES_DIR = JSON_DB_PROP.getProperty("json.database.tables.dir");
    private static final String METADATA_FILE_NAME = JSON_DB_PROP.getProperty("json.database.metadata.file");

    private Path rootDir;
    private Path metadataFile;
    private Path tablesDir;

    private Gson gson;
    private Type tablePropertiesPairType = new TypeToken<Pair<String, String>>(){}.getType();

    private Map<String, JsonTable<?>> tables;
    private boolean databaseInitialized;
    private boolean rewriteOldData;

    public JsonDatabase(Path rootDirectoryPath, boolean rewriteOldData) {
        rootDir = rootDirectoryPath.resolve(ROOT_DIR);
        metadataFile = rootDir.resolve(METADATA_FILE_NAME);
        tablesDir = rootDir.resolve(TABLES_DIR);
        this.rewriteOldData = rewriteOldData;
        createGson();
    }

    public JsonDatabase(Path rootDirectoryPath) {
        this(rootDirectoryPath, false);
    }

    @Override
    public void initDatabase() {
        tables = new HashMap<>();

        if (!rewriteOldData) {
            checkDatabaseDirectoryStructure();
            createTablesFromMetadataFile();
        }
        else {
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

        JsonTable<?> jsonTable = new JsonTable<>(objectsType, tableName, tablesDir, gson);
        jsonTable.initJsonTable(true);
        tables.put(tableName, jsonTable);
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
            String json = gson.toJson(tablePropertiesPair, tablePropertiesPairType);
            tablePropertiesList.add(json);
        }
        try {
            Files.write(metadataFile, tablePropertiesList);
        } catch (IOException e) {
            LOGGER.error("Cannot rewrite json db metadata file", e);
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

        JsonTable<T> table = getTable(tableName);

        return table.selectAll();
    }

    @Override
    public <T extends Prototype> T selectFrom(String tableName, Long id) {
        checkInitialization();
        checkTablePresence(tableName);

        JsonTable<T> table = getTable(tableName);
        return table.selectByKey(id);
    }

    @Override
    public <T extends Prototype> Map<Long, T> selectFrom(String tableName, Predicate<T> filter) {
        checkInitialization();
        checkTablePresence(tableName);

        JsonTable<T> table = getTable(tableName);
        return table.select(filter);
    }

    @Override
    public <T extends Prototype> Long add(String tableName, T object) {
        checkInitialization();
        checkTablePresence(tableName);

        JsonTable<T> table = getTable(tableName);
        Long id = table.getAndGenerateNextId();
        table.put(id, object);

        return id;
    }

    @Override
    public <T extends Prototype> void insert(String tableName, Long id, T object) {
        checkInitialization();
        checkTablePresence(tableName);

        JsonTable<T> table = getTable(tableName);
        table.put(id, object);
    }

    @Override
    public <T extends Prototype> boolean update(String tableName, Long id, T object) {
        checkInitialization();
        checkTablePresence(tableName);

        JsonTable<T> table = getTable(tableName);

        return table.replace(id, object);
    }

    @Override
    public boolean deleteFrom(String tableName, Long id) {
        checkInitialization();
        checkTablePresence(tableName);

        return tables.get(tableName).remove(id);
    }

    private void createGson() {
        JsonSerializer<java.sql.Date> dateSer = (src, typeOfSrc, context) ->
                src == null ? null : new JsonPrimitive(src.getTime());
        JsonDeserializer<java.sql.Date> dateDeser = (json, typeOfT, context) ->
                json == null ? null : new java.sql.Date(json.getAsLong());

        JsonSerializer<java.sql.Timestamp> timestampSer = (src, typeOfSrc, context) ->
                src == null ? null : new JsonPrimitive(src.getTime());
        JsonDeserializer<java.sql.Timestamp> timestampDeser = (json, typeOfT, context) ->
                json == null ? null : new java.sql.Timestamp(json.getAsLong());

        gson = new GsonBuilder()
                .registerTypeAdapter(java.sql.Date.class, dateSer)
                .registerTypeAdapter(java.sql.Date.class, dateDeser)
                .registerTypeAdapter(java.sql.Timestamp.class, timestampSer)
                .registerTypeAdapter(java.sql.Timestamp.class, timestampDeser)
                .create();
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

        String json = gson.toJson(tablePropertiesPair, tablePropertiesPairType);
        try {
            Files.write(metadataFile, Collections.singleton(json), StandardOpenOption.APPEND);
        } catch (IOException e) {
            LOGGER.error("IO Error while trying to write to json database metadata file", e);
            throw new UncheckedIOException(e);
        }
    }

    private void createTablesFromMetadataFile() {
        List<String> tablePropertiesList;
        try {
            tablePropertiesList = Files.readAllLines(metadataFile);
        } catch (IOException e) {
            LOGGER.error("IO error occurred while trying to read json db metadata file", e);
            throw new UncheckedIOException(e);
        }

        for (String tableProperty : tablePropertiesList) {
            Pair<String, String> tablePropertiesPair = gson.fromJson(tableProperty, tablePropertiesPairType);
            String tableName = tablePropertiesPair.getLeft();
            String className = tablePropertiesPair.getRight();
            Class<?> objectsType;
            try {
                objectsType = Class.forName(className);
            } catch (ClassNotFoundException e) {
                LOGGER.error(String.format("Cannot find and load class %s described in json db metadata file " +
                                "for table %s", className, tableName), e);
                throw new InvalidObjectTypeException();
            }
            JsonTable<?> table = new JsonTable<>(objectsType, tableName, tablesDir, gson);
            table.initJsonTable(false);
            tables.put(tableName, table);
        }
    }

    private void cleanDatabaseDirectories() {
        if (Files.exists(rootDir)) {
            try {
                FileUtils.deleteFileTree(rootDir);
            } catch (IOException e) {
                LOGGER.error("IO error occurred while trying to delete json database root directory recursively", e);
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

    private void checkDatabaseDirectoryStructure() {
        if (!(Files.exists(rootDir) && Files.exists(tablesDir)) && Files.exists(metadataFile)) {
            LOGGER.error("Json database directory structure is invalid");
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

    private <T> JsonTable<T> getTable(String tableName) {
        return (JsonTable<T>) tables.get(tableName);
    }
}

