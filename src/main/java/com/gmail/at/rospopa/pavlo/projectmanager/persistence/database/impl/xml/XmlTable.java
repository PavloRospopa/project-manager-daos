package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl.xml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl.AbstractFileTable;
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

public class XmlTable<T> extends AbstractFileTable<T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static Properties XML_DB_PROP = PropertiesLoader.getInstance().getXmlDBProperties();
    private static final String NEXT_ID_FILE_NAME = XML_DB_PROP.getProperty("xml.database.next_id.file");
    private static final Long FIRST_ID = Long.parseLong(XML_DB_PROP.getProperty("xml.database.first_id"));
    private static final int NUMBER_OF_FILES =
            Integer.parseInt(XML_DB_PROP.getProperty("xml.database.number_of_files"));
    private static final String TABLE_FILE_EXTENSION = XML_DB_PROP.getProperty("xml.database.file_extension");

    private JavaType pairType;
    private XmlMapper mapper;

    public XmlTable(Class<T> objectsType, String tableName, Path tablesDir, XmlMapper mapper) {
        this.objectsType = objectsType;
        this.tableDirPath = tablesDir.resolve(tableName);
        this.nextIdFilePath = tableDirPath.resolve(NEXT_ID_FILE_NAME);
        this.mapper = mapper;
        pairType = mapper.getTypeFactory().constructParametricType(Pair.class, Long.class, objectsType);
        tableFilesMap = new HashMap<>();
        generateTableFiles(tableName);
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
        createTempFile(tempFilePath);

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