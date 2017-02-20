package com.earphone.utility.utils;

import com.earphone.common.constant.Charset;
import com.earphone.common.constant.ImageType;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.BaseNCodec;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by YaoJiamin on 2016/11/2.
 */
public final class Base64Utils {
    private Base64Utils() {
    }

    private interface Base64Process {
        byte[] process(Base64 base64, byte[] bytes);
    }

    private static final Base64 BASE_64 = new Base64();
    private static final Base64Process encodeProcess = BaseNCodec::encode;
    private static final Base64Process decodeProcess = BaseNCodec::decode;

    public static String encodeBytes(byte[] bytes) {
        return new String(encodeProcess.process(BASE_64, bytes));
    }

    public static String encode(String text) {
        return encode(text, Charset.UTF_8);
    }

    public static String encode(String text, Charset charset) {
        return process(encodeProcess, text, charset);
    }

    public static byte[] decodeToBytes(byte[] bytes) {
        return decodeProcess.process(BASE_64, bytes);
    }

    public static String decode(String text) {
        return decode(text, Charset.UTF_8);
    }

    public static String decode(String text, Charset charset) {
        return process(decodeProcess, text, charset);
    }

    private static String process(Base64Process process, String text, Charset charset) {
        try {
            return new String(process.process(BASE_64, text.getBytes(charset.getCharset())));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unsupported Encoding.");
        }
    }

    public final static Map<String, String> FILE_TYPE_MAP = new HashMap<>();

    static {
        FILE_TYPE_MAP.put("FFD8FF", "JPEG"); //JPEG (jpg)
        FILE_TYPE_MAP.put("89504E47", "PNG");  //PNG (png)
        FILE_TYPE_MAP.put("47494638", "GIF");  //GIF (gif)
        FILE_TYPE_MAP.put("0000010001", "ICO");  //ICO (ico)
        FILE_TYPE_MAP.put("49492A00", "TIFF");  //TIFF (tif)
//        FILE_TYPE_MAP.put("424D", "bmp"); //Windows Bitmap (bmp)
//        FILE_TYPE_MAP.put("41433130", "dwg"); //CAD (dwg)
//        FILE_TYPE_MAP.put("68746D6C3E", "html");  //HTML (html)
//        FILE_TYPE_MAP.put("7B5C727466", "rtf");  //Rich Text Format (rtf)
//        FILE_TYPE_MAP.put("3C3F786D6C", "xml");
//        FILE_TYPE_MAP.put("504B0304", "zip");
//        FILE_TYPE_MAP.put("52617221", "rar");
//        FILE_TYPE_MAP.put("38425053", "psd");  //Photoshop (psd)
//        FILE_TYPE_MAP.put("44656C69766572792D646174653A", "eml");  //Email [thorough only] (eml)
//        FILE_TYPE_MAP.put("CFAD12FEC5FD746F", "dbx");  //Outlook Express (dbx)
//        FILE_TYPE_MAP.put("2142444E", "pst");  //Outlook (pst)
//        FILE_TYPE_MAP.put("D0CF11E0", "xls");  //MS Word
//        FILE_TYPE_MAP.put("D0CF11E0", "doc");  //MS Excel 注意：word 和 excel的文件头一样
//        FILE_TYPE_MAP.put("5374616E64617264204A", "mdb");  //MS Access (mdb)
//        FILE_TYPE_MAP.put("FF575043", "wpd"); //WordPerfect (wpd)
//        FILE_TYPE_MAP.put("252150532D41646F6265", "eps");
//        FILE_TYPE_MAP.put("252150532D41646F6265", "ps");
//        FILE_TYPE_MAP.put("255044462D312E", "pdf");  //Adobe Acrobat (pdf)
//        FILE_TYPE_MAP.put("AC9EBD8F", "qdf");  //Quicken (qdf)
//        FILE_TYPE_MAP.put("E3828596", "pwl");  //Windows Password (pwl)
//        FILE_TYPE_MAP.put("57415645", "wav");  //Wave (wav)
//        FILE_TYPE_MAP.put("41564920", "avi");
//        FILE_TYPE_MAP.put("2E7261FD", "ram");  //Real Audio (ram)
//        FILE_TYPE_MAP.put("2E524D46", "rm");  //Real Media (rm)
//        FILE_TYPE_MAP.put("000001BA", "mpg");  //
//        FILE_TYPE_MAP.put("6D6F6F76", "mov");  //Quicktime (mov)
//        FILE_TYPE_MAP.put("3026B2758E66CF11", "asf"); //Windows Media (asf)
//        FILE_TYPE_MAP.put("4D546864", "mid");  //MIDI (mid)
    }

    private static final String DATA_ENCODE = "base64,";


    public static String encodeBase64Image(URL imageUrl) throws Exception {
        File file = new File(UUID.randomUUID().toString());
        try {
            if (Objects.nonNull(imageUrl)) {
                FileUtils.copyURLToFile(imageUrl, file);
                byte[] bytes = readBytes(file);
                String type = ImageType.getType(Arrays.copyOf(bytes, 50));
                if (isImage(file, type)) {
                    return type + DATA_ENCODE + Base64Utils.encodeBytes(bytes);
                }
            }
            return "";
        } finally {
            file.delete();
        }
    }

    private static boolean isImage(File file, String extend) {
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            return (bufferedImage != null && bufferedImage.getWidth() != 0 && bufferedImage.getHeight() != 0) || StringUtils.isNotBlank(extend);
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] readBytes(File file) {
        try (FileInputStream in = new FileInputStream(file)) {
            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            return bytes;
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("File Not Found!");
        } catch (IOException e) {
            throw new IllegalStateException("Unknow Read Error!");
        }
    }

    public static File decodeBase64Image(String filePath, String fileName, String imageData) {
        // 图片类型
        String type = StringUtils.substringBefore(imageData, DATA_ENCODE);
        File file = createStoreFile(filePath, fileName, ImageType.getExtend(type));
        // 图片数据
        String encodeImage = StringUtils.substringAfter(imageData, DATA_ENCODE);
        if (Objects.nonNull(file) && StringUtils.isNotBlank(encodeImage)) {
            try (FileOutputStream os = new FileOutputStream(file)) {
                os.write(Base64Utils.decodeToBytes(encodeImage.getBytes()));
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("File Not Found!");
            } catch (IOException e) {
                throw new IllegalStateException("Unknow Write Error!");
            }
        }
        return file;
    }

    private static File createStoreFile(String filePath, String fileName, String extend) {
        File path = new File(filePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        if (StringUtils.isNotBlank(extend)) {
            // 待存储的文件
            return new File(path.getPath() + File.separator + fileName + "." + extend);
        }
        return null;
    }
}
