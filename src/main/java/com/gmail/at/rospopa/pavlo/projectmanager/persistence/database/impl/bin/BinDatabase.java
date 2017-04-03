package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl.bin;

import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.exception.*;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl.AbstractFileDatabase;
import com.gmail.at.rospopa.pavlo.projectmanager.util.Pair;
import com.gmail.at.rospopa.pavlo.projectmanager.util.PropertiesLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class BinDatabase extends AbstractFileDatabase {
    private static final Logger LOGGER = LogManager.getLogger();
    private static Properties BIN_DB_PROP = PropertiesLoader.getInstance().getBinDBProperties();

    private static final String ROOT_DIR = BIN_DB_PROP.getProperty("bin.database.root.dir");
    private static final String TABLES_DIR = BIN_DB_PROP.getProperty("bin.database.tables.dir");
    private static final String METADATA_FILE_NAME = BIN_DB_PROP.getProperty("bin.database.metadata.file");

    public BinDatabase(Path rootDirectoryPath, boolean rewriteOldData) {
        rootDir = rootDirectoryPath.resolve(ROOT_DIR);
        metadataFile = rootDir.resolve(METADATA_FILE_NAME);
        tablesDir = rootDir.resolve(TABLES_DIR);
        this.rewriteOldData = rewriteOldData;
    }

    public BinDatabase(Path rootDirectoryPath) {
        this(rootDirectoryPath, false);
    }

    @Override
    public void createTable(String tableName, Class<?> objectsType) {
        checkInitialization();
        checkTableAbsence(tableName);

        BinTable<?> binTable = new BinTable<>(objectsType, tableName, tablesDir);
        binTable.initTable(true);
        tables.put(tableName, binTable);
        registerTable(tableName, objectsType);
    }

    @Override
    public void dropTable(String tableName) {
        checkInitialization();
        checkTablePresence(tableName);
        tables.remove(tableName);

        writeToMetadataFile(metadataFile, getMetadataPairList());

        deleteTableDir(tableName);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    protected void createTablesFromMetadataFile() {
        List<Pair<String, Class<?>>> metadataPairList = selectFromMetadataFile(metadataFile);

        for (Pair<String, Class<?>> pair : metadataPairList) {
            String tableName = pair.getLeft();
            Class<?> objectsType = pair.getRight();

            BinTable<?> table = new BinTable<>(objectsType, tableName, tablesDir);
            table.initTable(false);
            tables.put(tableName, table);
        }
    }

    private void registerTable(String tableName, Class<?> objectsType) {
        List<Pair<String, Class<?>>> metadataPairList = getMetadataPairList();

        Pair<String, Class<?>> newPair = new Pair<>(tableName, objectsType);
        metadataPairList.add(newPair);

        writeToMetadataFile(metadataFile, metadataPairList);
    }

    private void writeToMetadataFile(Path metadataFilePath, List<Pair<String, Class<?>>> pairList) {
        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(metadataFilePath.toFile())))) {
            for (Pair<String, Class<?>> pair : pairList) {
                outputStream.writeObject(pair);
            }
        } catch (IOException e) {
            LOGGER.error("Cannot write to bin db metadata file", e);
            throw new UncheckedIOException(e);
        }
    }

    private List<Pair<String, Class<?>>> selectFromMetadataFile(Path metadataFilePath) {
        List<Pair<String, Class<?>>> pairList = new ArrayList<>();

        try (ObjectInputStream objectInputStream =
                     new ObjectInputStream(new BufferedInputStream(new FileInputStream(metadataFilePath.toFile())))) {
            while (true) {
                Pair<String, Class<?>> pair = (Pair<String, Class<?>>) objectInputStream.readObject();
                pairList.add(pair);
            }
        } catch (EOFException e) {
            LOGGER.info("End iterating through binary file");
        } catch (IOException e) {
            LOGGER.error("Cannot read from bin db metadata file", e);
            throw new UncheckedIOException(e);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Cannot find and load class of object stored in binary file", e);
            throw new InvalidObjectTypeException();
        }

        return pairList;
    }

    private List<Pair<String, Class<?>>> getMetadataPairList() {
        return tables.entrySet()
                .stream()
                .map(entry -> new Pair<String, Class<?>>(entry.getKey(), entry.getValue().getObjectsType()))
                .collect(Collectors.toList());
    }
}