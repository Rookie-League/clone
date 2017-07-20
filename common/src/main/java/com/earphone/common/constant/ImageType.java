package com.earphone.common.constant;

import com.earphone.common.utils.ByteExtend;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by YaoJiamin on 2016/11/2.
 */
public enum ImageType {
    JPEG("data:image/jpeg;", "FFD8FF", "jpg"),
    GIF("data:image/gif;", "47494638", "gif"),
    PNG("data:image/png;", "89504E47", "png"),
    ICO("data:image/x-icon;", "0000010001", "ico");

    private String type;
    private String header;
    private String extend;

    private static final Map<String, ImageType> extendMap = new HashMap<>();
    private static Lock lock = new ReentrantLock();

    public static String getExtend(String type) {
        if (extendMap.size() < ImageType.values().length) {
            try {
                lock.lock();
                if (extendMap.size() < ImageType.values().length) {
                    for (ImageType imageType : ImageType.values()) {
                        extendMap.put(imageType.getType(), imageType);
                    }
                }
            } finally {
                lock.unlock();
            }
        }
        return extendMap.get(type).getExtend();
    }

    public String getType() {
        return type;
    }

    public static String getType(byte[] bytes) {
        String typeHex = ByteExtend.byteArrayToHex(bytes);
        for (ImageType imageType : ImageType.values()) {
            if (typeHex.toUpperCase().startsWith(imageType.getHeader())) {
                return imageType.getType();
            }
        }
        return null;
    }

    public String getHeader() {
        return header;
    }

    public String getExtend() {
        return extend;
    }

    ImageType(String type, String header, String extend) {
        this.type = type;
        this.header = header;
        this.extend = extend;
    }
}
