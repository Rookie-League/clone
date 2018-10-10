package com.earphone.common.utils;

import com.earphone.common.validation.Assert;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import static com.earphone.common.utils.CodeExtend.toStream;
import static com.earphone.common.utils.PathExtend.classpath;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/7/20
 * @createTime 11:26
 */
@Slf4j
public class CodeExtendTest {
    @Test(enabled = false)
    public void test() throws IOException {
        File dir = new File(classpath());
        File[] oldFiles = dir.listFiles();
        if (oldFiles != null) {
            for (File file : oldFiles) {
                if (file.isFile() && file.getName().endsWith(".jpg") && file.delete()) {
                    log.debug("Clean captcha file");
                }
            }
        }
        Set<String> codeSet = new HashSet<>();
        for (int w = 200, h = 80, i = 0; i < 50; i++) {
            String code = CodeExtend.generate(4);
            codeSet.add(code);
            File file = new File(dir, code.concat(".jpg"));
            outputVerifyCode(w, h, file, code);
        }
        Set<String> fileSet = new HashSet<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".jpg")) {
                    fileSet.add(file.getName().substring(0, 4));
                }
            }
        }
        Assert.wrapCollection(codeSet).isNotEmpty("Generate captcha failure");
        fileSet.removeAll(codeSet);
        Assert.wrapCollection(fileSet).isEmpty("Generate captcha failure");
    }

    /**
     * 生成指定验证码图像文件
     */
    private void outputVerifyCode(int width, int height, File outputFile, String code) throws IOException {
        Assert.wrapObject(outputFile).isNotEmpty("Output file is null");
        File dir = outputFile.getParentFile();
        if (!dir.exists() && dir.mkdirs()) {
            log.debug("Success create dir");
        }
        Assert.wrapBoolean(outputFile.createNewFile()).isTrue("Fail to create new file");
        try (FileOutputStream outputStream = new FileOutputStream(outputFile);
             InputStream inputStream = toStream(width, height, code)) {
            byte[] data = new byte[inputStream.available()];
            outputStream.write(inputStream.read(data));
        }
    }
}