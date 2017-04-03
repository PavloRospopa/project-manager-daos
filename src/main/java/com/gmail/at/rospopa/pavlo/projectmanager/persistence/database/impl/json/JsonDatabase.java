package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl.json;

import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.exception.*;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl.AbstractFileDatabase;
import com.gmail.at.rospopa.pavlo.projectmanager.util.Pair;
import com.gmail.at.rospopa.pavlo.projectmanager.util.PropertiesLoader;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

public class JsonDatabase extends AbstractFileDatabase {
    private static final Logger LOGGER = LogManager.getLogger();
    private static Properties JSON_DB_PROP = PropertiesLoader.getInstance().getJsonDBProperties();

    private static final String ROOT_DIR = JSON_DB_PROP.getProperty("json.database.root.dir");
    private static final String TABLES_DIR = JSON_DB_PROP.getProperty("json.database.tables.dir");
    private static final String METADATA_FILE_NAME = JSON_DB_PROP.getProperty("json.database.metadata.file");

    private Gson gson;
    private Type tablePropertiesPairType = new TypeToken<Pair<String, String>>() {}.getType();

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
    public void createTable(String tableName, Class<?> objectsType) {
        checkInitialization();
        checkTableAbsence(tableName);

        JsonTable<?> jsonTable = new JsonTable<>(objectsType, tableName, tablesDir, gson);
        jsonTable.initTable(true);
        tables.put(tableName, jsonTable);
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

    private void registerTable(String tableName, Class<?> objectsType) {
        Pair<String, String> tablePropertiesPair = new Pair<>(tableName, objectsType.getName());

        String json = gson.toJson(tablePropertiesPair, tablePropertiesPairType);
        try {
            Files.write(metadataFile, Collections.singleton(json), StandardOpenOption.APPEND);
        } catch (IOException e) {
            LOGGER.error("IO Error while trying to write to json database metadata file", e);
            throw new UncheckedIOException(e);
        }
    }

    @Override
    protected void createTablesFromMetadataFile() {
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
            table.initTable(false);
            tables.put(tableName, table);
        }
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}