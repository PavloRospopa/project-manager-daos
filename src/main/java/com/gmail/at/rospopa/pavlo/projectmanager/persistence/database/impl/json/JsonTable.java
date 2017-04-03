package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl.json;

import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl.AbstractFileTable;
import com.gmail.at.rospopa.pavlo.projectmanager.util.Pair;
import com.gmail.at.rospopa.pavlo.projectmanager.util.PropertiesLoader;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Predicate;

public class JsonTable<T> extends AbstractFileTable<T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static Properties JSON_DB_PROP = PropertiesLoader.getInstance().getJsonDBProperties();

    private static final String NEXT_ID_FILE_NAME = JSON_DB_PROP.getProperty("json.database.next_id.file");
    private static final Long FIRST_ID = Long.parseLong(JSON_DB_PROP.getProperty("json.database.first_id"));
    private static final int NUMBER_OF_FILES =
            Integer.parseInt(JSON_DB_PROP.getProperty("json.database.number_of_files"));
    private static final String TABLE_FILE_EXTENSION = JSON_DB_PROP.getProperty("json.database.file_extension");

    private Type pairType;
    private Gson gson;

    public JsonTable(Class<T> objectsType, String tableName, Path tablesDir, Gson gson) {
        this.objectsType = objectsType;
        pairType = new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{Long.class, objectsType};
            }

            @Override
            public Type getRawType() {
                return Pair.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };

        this.tableDirPath = tablesDir.resolve(tableName);
        this.nextIdFilePath = tableDirPath.resolve(NEXT_ID_FILE_NAME);
        this.gson = gson;
        tableFilesMap = new HashMap<>();
        generateTableFiles(tableName);
    }

    @Override
    public void put(Long key, T value) {
        checkInitialization();
        Pair<Long, T> pair = new Pair<>(key, value);
        String json = gson.toJson(pair, pairType);

        Path tableFilePath = getTableFilePath(key);
        try {
            Files.write(tableFilePath, Collections.singleton(json), StandardOpenOption.APPEND);
        } catch (IOException e) {
            LOGGER.error("Cannot write new data to json db table file", e);
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean remove(Long key) {
        checkInitialization();
        Path tableFilePath = getTableFilePath(key);
        Path tempFilePath = getTempFilePath(tableFilePath);
        createTempFile(tempFilePath);

        try (BufferedReader reader = Files.newBufferedReader(tableFilePath);
             BufferedWriter writer = Files.newBufferedWriter(tempFilePath)) {
            String line = reader.readLine();
            while (line != null) {
                Pair<Long, T> pair = gson.fromJson(line, pairType);
                if (!pair.getLeft().equals(key)) {
                    writer.write(line);
                    writer.newLine();
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            LOGGER.error("Cannot read from table file or write to temp table file", e);
            throw new UncheckedIOException(e);
        }
        deleteTableFile(tableFilePath);
        renameTempFile(tableFilePath, tempFilePath);

        return true;
    }

    @Override
    public boolean replace(Long key, T value) {
        checkInitialization();
        Path tableFilePath = getTableFilePath(key);
        Path tempFilePath = getTempFilePath(tableFilePath);
        createTempFile(tempFilePath);
        try (BufferedReader reader = Files.newBufferedReader(tableFilePath);
             BufferedWriter writer = Files.newBufferedWriter(tempFilePath)) {
            String line = reader.readLine();
            while (line != null) {
                Pair<Long, T> pair = gson.fromJson(line, pairType);
                if (pair.getLeft().equals(key)) {
                    Pair<Long, T> updatedPair = new Pair<>(pair.getLeft(), value);
                    String updatedPairJson = gson.toJson(updatedPair, pairType);
                    writer.write(updatedPairJson);
                } else {
                    writer.write(line);
                }
                writer.newLine();
                line = reader.readLine();
            }
        } catch (IOException e) {
            LOGGER.error("Cannot read from table file or write to temp table file", e);
            throw new UncheckedIOException(e);
        }
        deleteTableFile(tableFilePath);
        renameTempFile(tableFilePath, tempFilePath);
        return true;
    }

    @Override
    public T selectByKey(Long key) {
        checkInitialization();
        Path tableFilePath = getTableFilePath(key);

        try (BufferedReader reader = Files.newBufferedReader(tableFilePath)) {
            String line = reader.readLine();
            while (line != null) {
                Pair<Long, T> pair = gson.fromJson(line, pairType);
                if (pair.getLeft().equals(key)) {
                    return pair.getRight();
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            LOGGER.error("Cannot read from table file " + tableFilePath.getFileName().toString(), e);
            throw new UncheckedIOException(e);
        }
        return null;
    }

    @Override
    public Map<Long, T> selectAll() {
        return select(o -> true);
    }

    @Override
    public Map<Long, T> select(Predicate<T> filter) {
        checkInitialization();
        Map<Long, T> objectsMap = new HashMap<>();
        for (Map.Entry<Integer, Path> entry : tableFilesMap.entrySet()) {
            try (BufferedReader reader = Files.newBufferedReader(entry.getValue())) {
                String line = reader.readLine();
                while (line != null) {
                    Pair<Long, T> pair = gson.fromJson(line, pairType);
                    if (filter.test(pair.getRight())) {
                        objectsMap.put(pair.getLeft(), pair.getRight());
                    }
                    line = reader.readLine();
                }
            } catch (IOException e) {
                LOGGER.error("Cannot read from table file " + entry.getValue().getFileName().toString(), e);
                throw new UncheckedIOException(e);
            }
        }
        return objectsMap;
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    protected Long getFirstId() {
        return FIRST_ID;
    }

    @Override
    protected int getNumberOfFiles() {
        return NUMBER_OF_FILES;
    }

    @Override
    protected String getFileExtension() {
        return TABLE_FILE_EXTENSION;
    }
}
