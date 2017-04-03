package com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl.bin;

import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.exception.InvalidObjectTypeException;
import com.gmail.at.rospopa.pavlo.projectmanager.persistence.database.impl.AbstractFileTable;
import com.gmail.at.rospopa.pavlo.projectmanager.util.FileUtils;
import com.gmail.at.rospopa.pavlo.projectmanager.util.Pair;
import com.gmail.at.rospopa.pavlo.projectmanager.util.PropertiesLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;

public class BinTable<T> extends AbstractFileTable<T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static Properties BIN_DB_PROP = PropertiesLoader.getInstance().getBinDBProperties();
    private static final String NEXT_ID_FILE_NAME = BIN_DB_PROP.getProperty("bin.database.next_id.file");
    private static final Long FIRST_ID = Long.valueOf(BIN_DB_PROP.getProperty("bin.database.first_id"));
    private static final int NUMBER_OF_FILES =
            Integer.parseInt(BIN_DB_PROP.getProperty("bin.database.number_of_files"));
    private static final String TABLE_FILE_EXTENSION = BIN_DB_PROP.getProperty("bin.database.file_extension");

    public BinTable(Class<T> objectsType, String tableName, Path tablesDir) {
        this.objectsType = objectsType;
        this.tableDirPath = tablesDir.resolve(tableName);
        this.nextIdFilePath = tableDirPath.resolve(NEXT_ID_FILE_NAME);
        tableFilesMap = new HashMap<>();
        generateTableFiles(tableName);
    }

    @Override
    public void put(Long key, T value) {
        checkInitialization();
        Pair<Long, T> newPair = new Pair<>(key, value);

        Path tableFilePath = getTableFilePath(key);
        List<Pair<Long, T>> pairList = selectFromTableFile(tableFilePath, p -> true);
        pairList.add(newPair);

        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(tableFilePath.toFile())))) {
            for (Pair<Long, T> pair : pairList) {
                outputStream.writeObject(pair);
            }
        } catch (IOException e) {
            LOGGER.error("Cannot write to bin db table file", e);
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean remove(Long key) {
        checkInitialization();
        Path tableFilePath = getTableFilePath(key);
        Path tempFilePath = getTempFilePath(tableFilePath);
        createTempFile(tempFilePath);

        try (ObjectInputStream inputStream =
                     new ObjectInputStream(new BufferedInputStream(new FileInputStream(tableFilePath.toFile())));
             ObjectOutputStream outputStream =
                     new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(tempFilePath.toFile())))) {
            while (true) {
                Pair<Long, T> pair = (Pair<Long, T>) inputStream.readObject();
                if (!pair.getLeft().equals(key)) {
                    outputStream.writeObject(pair);
                }
            }
        } catch (EOFException e) {
            //LOGGER.info("End iterating through binary file");
        } catch (ClassNotFoundException e) {
            LOGGER.error("Cannot find and load class of object stored in binary file", e);
            throw new InvalidObjectTypeException();
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
        try (ObjectInputStream inputStream =
                     new ObjectInputStream(new BufferedInputStream(new FileInputStream(tableFilePath.toFile())));
             ObjectOutputStream outputStream =
                     new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(tempFilePath.toFile())))) {
            while (true) {
                Pair<Long, T> pair = (Pair<Long, T>) inputStream.readObject();
                if (pair.getLeft().equals(key)) {
                    pair = new Pair<>(pair.getLeft(), value);
                }
                outputStream.writeObject(pair);
            }
        } catch (EOFException e) {
            //LOGGER.info("End iterating through binary file");
        } catch (ClassNotFoundException e) {
            LOGGER.error("Cannot find and load class of object stored in binary file", e);
            throw new InvalidObjectTypeException();
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

        List<Pair<Long, T>> pairList = selectFromTableFile(tableFilePath, pair -> pair.getLeft().equals(key));
        if (pairList.isEmpty()) {
            return null;
        }

        return pairList.get(0).getRight();
    }

    private List<Pair<Long, T>> selectFromTableFile(Path tableFilePath, Predicate<Pair<Long, T>> filter) {
        List<Pair<Long, T>> pairList = new ArrayList<>();

        try (ObjectInputStream objectInputStream =
                     new ObjectInputStream(new BufferedInputStream(new FileInputStream(tableFilePath.toFile())))) {
            while (true) {
                Pair<Long, T> pair = (Pair<Long, T>) objectInputStream.readObject();
                if (filter.test(pair)) {
                    pairList.add(pair);
                }
            }
        } catch (EOFException e) {
            //LOGGER.info("End iterating through binary file");
        } catch (IOException e) {
            LOGGER.error("Cannot read from table file " + tableFilePath.getFileName().toString(), e);
            throw new UncheckedIOException(e);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Cannot find and load class of object stored in binary file", e);
            throw new InvalidObjectTypeException();
        }

        return pairList;
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
            try (ObjectInputStream objectInputStream =
                         new ObjectInputStream(new BufferedInputStream(new FileInputStream(entry.getValue().toFile())))) {
                while (true) {
                    Pair<Long, T> pair = (Pair<Long, T>) objectInputStream.readObject();
                    if (filter.test(pair.getRight())) {
                        objectsMap.put(pair.getLeft(), pair.getRight());
                    }
                }
            } catch (EOFException e) {
                //LOGGER.info("End iterating through binary file");
            } catch (IOException e) {
                LOGGER.error("Cannot read from table file " + entry.getValue().getFileName().toString(), e);
                throw new UncheckedIOException(e);
            } catch (ClassNotFoundException e) {
                LOGGER.error("Cannot find and load class of object stored in binary file", e);
                throw new InvalidObjectTypeException();
            }
        }
        return objectsMap;
    }

    @Override
    protected void deleteTableFile(Path tableFilePath) {
        try {
            FileUtils.deleteFileTreeHard(tableFilePath);
        } catch (IOException e) {
            LOGGER.error("IO error while trying to delete table file", e);
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