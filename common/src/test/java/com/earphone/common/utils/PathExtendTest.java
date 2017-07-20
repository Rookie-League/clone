package com.earphone.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static com.earphone.common.utils.PathExtend.classpath;
import static com.earphone.common.utils.PathExtend.currentPath;
import static com.earphone.common.utils.PathExtend.isLinux;
import static com.earphone.common.utils.PathExtend.isWindows;
import static org.testng.Assert.*;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/7/20
 * @createTime 9:50
 */
@Slf4j
public class PathExtendTest {
    @Test
    public void testClasspath() throws Exception {
        log.info(classpath());
    }

    @Test
    public void testCurrentPath() throws Exception {
        assertEquals(currentPath(PathExtend.class), currentPath(PathExtendTest.class));
    }

    @Test
    public void testPlatform() throws Exception {
        assertTrue(isWindows() || isLinux());
        assertFalse(isWindows() && isLinux());
    }
}