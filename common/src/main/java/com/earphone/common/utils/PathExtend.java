package com.earphone.common.utils;


import com.earphone.common.exception.NonCaptureException;

import java.io.File;

import static com.earphone.common.constant.Charset.UTF8;
import static java.net.URLDecoder.decode;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/7/7
 * @createTime 14:37
 */
public final class PathExtend {

    private static final String OS_LINUX = "linux";
    private static final String OS_WINDOWS = "window";
    public static final String BIT_32 = "32";
    public static final String BIT_64 = "64";
    public static final String BIT = "bit";

    public static final String OS_NAME = System.getProperty("os.name");
    public static final String SUN_ARCH_DATA_MODEL = System.getProperty("sun.arch.data.model");
    public static final String JAVA_LIBRARY_PATH = System.getProperty("java.library.path");
    public static final String JAVA_HOME = System.getProperty("java.home");

    private PathExtend() {

    }

    public static String classpath() {
        try {
            return decode(new File(PathExtend.class.getResource("/").toURI()).getAbsolutePath(), UTF8.getValue());
        } catch (Exception e) {
            throw new NonCaptureException(e.getMessage(), e);
        }
    }

    public static String currentPath(Class<?> clazz) {
        try {
            return decode(new File(clazz.getResource("").toURI()).getAbsolutePath(), UTF8.getValue());
        } catch (Exception e) {
            throw new NonCaptureException(e.getMessage(), e);
        }
    }

    /**
     * 判断是否是windows系统
     */
    public static boolean isWindows() {
        return containsIgnoreCase(OS_NAME, OS_WINDOWS);
    }

    /**
     * 判断是否是linux系统
     */
    public static boolean isLinux() {
        return containsIgnoreCase(OS_NAME, OS_LINUX);
    }

}
