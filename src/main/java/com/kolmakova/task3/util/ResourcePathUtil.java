package com.kolmakova.task3.util;

import com.kolmakova.task3.exception.CustomThreadException;

import java.io.File;
import java.net.URL;

public class ResourcePathUtil {

    public static String getResourcePath(String resourceName) throws CustomThreadException {
        URL url = ResourcePathUtil.class
                .getClassLoader()
                .getResource(resourceName);
        if (url == null) {
            throw new CustomThreadException("Resource " + resourceName + " is not found");
        }

        return new File(url.getFile()).getAbsolutePath();
    }
}
