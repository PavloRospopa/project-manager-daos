package com.gmail.at.rospopa.pavlo.projectmanager.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class ResourcesUtil {
    private static final Logger LOGGER = LogManager.getLogger();

    public static InputStream getResourceInputStream(String path) {
        return ResourcesUtil.class.getClassLoader().getResourceAsStream(path);
    }

    public static File getResourceFile(String path) {
        URL url = ResourcesUtil.class.getClassLoader().getResource(path);
        if (url == null) {
            LOGGER.warn("The resource could not be found or " +
                    "the invoker doesn't have adequate privileges to get the resource");
            return null;
        }

        File file = new File(url.getFile());
        if (!file.isFile()) {
            LOGGER.warn("The file denoted by pathname is not a normal file");
            return null;
        }
        return file;
    }
}
