package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl.xml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.Table;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.exception.InvalidTableStructureException;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.exception.TableNotInitializedException;
import com.gmail.at.rospopa.pavlo.projectmanager.util.Pair;
import com.gmail.at.rospopa.pavlo.projectmanager.util.PropertiesLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Predicate;

public class XmlTable<T> implements Table<Long, T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static Properties XML_DB_PROP = PropertiesLoader.getInstance().getXmlDBProperties();

    private static final String NEXT_ID_FILE_NAME = XML_DB_PROP.getProperty("xml.database.next_id.file");
    private static final String FIRST_ID = XML_DB_PROP.getProperty("xml.database.first_id");
    private static final int NUMBER_OF_FILES =
            Integer.parseInt(XML_DB_PROP.getProperty("xml.database.number_of_files"));

    private JavaType pairType;
    private Class<T> objectsType;
    private XmlMapper mapper;

    private Path tableDirPath;
    private Path nextIdFilePath;

    private Long nextId;
    private Map<Integer, Path> tableFilesMap;

    private boolean tableInitialized;

    public XmlTable(Class<T> objectsType, String tableName, Path tablesDir, XmlMapper mapper) {
        this.objectsType = objectsType;
        this.tableDirPath = tablesDir.resolve(tableName);
        this.nextIdFilePath = tableDirPath.resolve(NEXT_ID_FILE_NAME);
        this.mapper = mapper;
        pairType = mapper.getTypeFactory().constructParametricType(Pair.class, Long.class, objectsType);
        tableFilesMap = new HashMap<>();
        generateTableFiles(tableName);
    }

    public void initXmlTable(boolean isNew) {
        if (!isNew) {
            checkTableStructure();
        } else {
            createTableStructure();
        }
        nextId = readNextId();
        tableInitialized = true;
    }

    @Override
    public void put(Long key, T value) {
        checkInitialization();
        Pair<Long, T> pair = new Pair<>(key, value);
        String xml = toXml(pair);

        Path tableFilePath = getTableFilePath(key);
        try {
            Files.write(tableFilePath, Collections.singleton(xml), StandardOpenOption.APPEND);
        } catch (IOException e) {
            LOGGER.error("Cannot write new data to xml db table file", e);
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean remove(Long key) {
        checkInitialization();
        Path tableFilePath = getTableFilePath(key);
        Path tempFilePath = getTempFilePath(tableFilePath);
        try {
            Files.createFile(tempFilePath);
        } catch (IOException e) {
            LOGGER.error("Cannot create temporary table file in file system", e);
            throw new UncheckedIOException(e);
        }

        try (BufferedReader reader = Files.newBufferedReader(tableFilePath);
             BufferedWriter writer = Files.newBufferedWriter(tempFilePath)) {
            String line = reader.readLine();
            while (line != null) {
                Pair<Long, T> pair = fromXml(line, pairType);
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
        try {
            Files.delete(tableFilePath);
        } catch (IOException e) {
            LOGGER.error("IO error while trying to delete table file", e);
            throw new UncheckedIOException(e);
        }
        File tempFile = tempFilePath.toFile();
        if (!tempFile.renameTo(tableFilePath.toFile())) {
            LOGGER.error("Cannot rename temporary table file");
            throw new RuntimeException();
        }

        return true;
    }

    @Override
    public boolean replace(Long key, T value) {
        checkInitialization();
        Path tableFilePath = getTableFilePath(key);
        Path tempFilePath = getTempFilePath(tableFilePath);
        try {
            Files.createFile(tempFilePath);
        } catch (IOException e) {
            LOGGER.error("Cannot create temporary table file in file system", e);
            throw new UncheckedIOException(e);
        }
        try (BufferedReader reader = Files.newBufferedReader(tableFilePath);
             BufferedWriter writer = Files.newBufferedWriter(tempFilePath)) {
            String line = reader.readLine();
            while (line != null) {
                Pair<Long, T> pair = fromXml(line, pairType);
                if (pair.getLeft().equals(key)) {
                    Pair<Long, T> updatedPair = new Pair<>(pair.getLeft(), value);
                    String updatedPairXml = toXml(updatedPair);
                    writer.write(updatedPairXml);
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
        try {
            Files.delete(tableFilePath);
        } catch (IOException e) {
            LOGGER.error("IO error while trying to delete table file", e);
            throw new UncheckedIOException(e);
        }
        File tempFile = tempFilePath.toFile();
        if (!tempFile.renameTo(tableFilePath.toFile())) {
            LOGGER.error("Cannot rename temporary table file");
            throw new RuntimeException();
        }
        return true;
    }

    @Override
    public T selectByKey(Long key) {
        checkInitialization();
        Path tableFilePath = getTableFilePath(key);

        try (BufferedReader reader = Files.newBufferedReader(tableFilePath)) {
            String line = reader.readLine();
            while (line != null) {
                Pair<Long, T> pair = fromXml(line, pairType);
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
                    Pair<Long, T> pair = fromXml(line, pairType);
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
    public void clear() {
        checkInitialization();
        for (Map.Entry<Integer, Path> entry : tableFilesMap.entrySet()) {
            try {
                Files.write(entry.getValue(), Collections.emptyList());
            } catch (IOException e) {
                LOGGER.error("IO error while trying to clear data from table files", e);
                throw new UncheckedIOException(e);
            }
        }

        try {
            Files.write(nextIdFilePath, Collections.singleton(FIRST_ID));
        } catch (IOException e) {
            LOGGER.error("IO error while trying to rewrite next id file", e);
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Long getNextId() {
        return nextId;
    }

    @Override
    public Long getAndGenerateNextId() {
        writeNextId(nextId + 1);
        return nextId++;
    }

    @Override
    public Class<T> getObjectsType() {
        return objectsType;
    }

    private Long readNextId() {
        String nextIdStr;
        try {
            nextIdStr = Files.readAllLines(nextIdFilePath).get(0);
        } catch (IOException e) {
            LOGGER.error("Cannot read data from nextId file", e);
            throw new UncheckedIOException(e);
        }
        return Long.valueOf(nextIdStr);
    }

    private void writeNextId(Long nextId) {
        try {
            Files.write(nextIdFilePath, Collections.singleton(nextId.toString()));
        } catch (IOException e) {
            LOGGER.error("Cannot write nextId to file", e);
            throw new UncheckedIOException(e);
        }
    }

    private Path getTempFilePath(Path tableFilePath) {
        String tempFileName = String.format("_temp%s", tableFilePath.getFileName().toString());
        return tableDirPath.resolve(tempFileName);
    }

    private void generateTableFiles(String tableName) {
        for (int i = 0; i < NUMBER_OF_FILES; i++) {
            String tableFileName = String.format("%s%d.xml", tableName, i);
            Path tableFilePath = tableDirPath.resolve(tableFileName);
            tableFilesMap.put(i, tableFilePath);
        }
    }

    private Path getTableFilePath(Long id) {
        int tableNumber = (int) id.longValue() % NUMBER_OF_FILES;
        return tableFilesMap.get(tableNumber);
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

    private <V> V fromXml(String xml, JavaType type) {
        try {
            return mapper.readValue(xml, type);
        } catch (IOException e) {
            LOGGER.error("Error while trying deserialize object from xml string", e);
            throw new UncheckedIOException(e);
        }
    }

    private void createTableStructure() {
        try {
            Files.createDirectories(tableDirPath);
            for (Map.Entry<Integer, Path> entry : tableFilesMap.entrySet()) {
                Files.createFile(entry.getValue());
            }
            Files.createFile(nextIdFilePath);
            Files.write(nextIdFilePath, Collections.singleton(FIRST_ID));
        } catch (IOException e) {
            LOGGER.error("Cannot create table structure in file system", e);
            throw new UncheckedIOException(e);
        }
    }

    private void checkTableStructure() {
        if (!Files.exists(nextIdFilePath)) {
            LOGGER.error("Xml table structure is invalid");
            throw new InvalidTableStructureException();
        }
        for (Map.Entry<Integer, Path> entry : tableFilesMap.entrySet()) {
            if (!Files.exists(entry.getValue())) {
                LOGGER.error("Xml table structure is invalid");
                throw new InvalidTableStructureException();
            }
        }
    }

    private void checkInitialization() {
        if (!tableInitialized) {
            LOGGER.error("Table has to be initialized before working with it`s data");
            throw new TableNotInitializedException();
        }
    }
}
