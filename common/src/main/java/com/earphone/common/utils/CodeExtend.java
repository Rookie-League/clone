package com.earphone.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.UUID;

import static java.awt.Color.CYAN;
import static java.awt.Color.GRAY;
import static java.awt.Color.LIGHT_GRAY;
import static java.awt.Color.MAGENTA;
import static java.awt.Color.ORANGE;
import static java.awt.Color.PINK;
import static java.awt.Color.WHITE;
import static java.awt.Color.YELLOW;

@Slf4j
public final class CodeExtend {
    private static final CodeExtend CODE_EXTEND = new CodeExtend();
    // 使用到Algerian字体，系统里没有的话需要安装字体，字体只显示大写，去掉了1,0,i,o几个容易混淆的字符
    private static final String VERIFY_CODES = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final ThreadLocal<Random> THREAD_LOCAL = ThreadLocal.withInitial(Random::new);

    private CodeExtend() {
    }

    public static String uniqueCode() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 使用系统默认字符源生成验证码
     *
     * @param length 验证码长度
     */
    public static String generate(final int length) {
        return generate(length, VERIFY_CODES);
    }

    /**
     * 使用指定源生成验证码，如果不指定字符源则使用默认源
     *
     * @param length  验证码长度
     * @param sources 验证码字符源
     */
    public static String generate(final int length, final String sources) {
        String code = formatSource(sources);
        StringBuilder source = new StringBuilder(length);
        Random random = THREAD_LOCAL.get();
        for (int i = 0, codeLength = code.length(); i < length; i++) {
            source.append(code.charAt(random.nextInt(codeLength - 1)));
        }
        return source.toString();
    }

    private static String formatSource(final String sources) {
        if (StringUtils.isBlank(sources)) {
            return VERIFY_CODES;
        }
        return sources;
    }

    /**
     * 输出随机验证码图片流,并返回验证码值
     */
    public static InputStream toStream(int width, int height, int length) throws IOException {
        return toStream(width, height, generate(length));
    }

    private static final Color[] COLORS = new Color[]{WHITE, CYAN, GRAY, LIGHT_GRAY, MAGENTA, ORANGE, PINK, YELLOW};

    /**
     * <p>
     * /**
     * 输出指定验证码图片流
     */
    public static InputStream toStream(int width, int height, String code) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawBorderColor(width, height, g2d);
        drawBackgroundColor(width, height, g2d);
        drawInterferingLine(width, height, g2d);
        addHotPixel(width, height, image);
        shear(g2d, width, height, getRandColor(200, 250));// 使图片扭曲
        drawVerifyCode(width, height, code, g2d);
        g2d.dispose();
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();) {
            ImageIO.write(image, "jpg", os);
            return new ByteArrayInputStream(os.toByteArray());
        }
    }

    private static void drawVerifyCode(int width, int height, String code, Graphics2D g2d) {
        g2d.setColor(getRandColor(100, 160));
        int fontSize = height - 4;
        Font font = new Font("arial", Font.ITALIC, fontSize);
        g2d.setFont(font);
        char[] chars = code.toCharArray();
        for (int i = 0, verifySize = code.length(); i < verifySize; i++) {
            AffineTransform affine = new AffineTransform();
            affine.setToRotation(getTheta(), getAnchrox(width, fontSize, i, verifySize), getAnchroy(height));
            g2d.setTransform(affine);
            g2d.drawChars(chars, i, 1, getX(width, i, verifySize), getY(height, fontSize));
        }
    }

    private static int getY(int height, int fontSize) {
        return height / 2 + fontSize / 2 - 10;
    }

    private static int getX(int width, int i, int verifySize) {
        return ((width - 10) / verifySize) * i + 5;
    }

    private static int getAnchroy(int height) {
        return height / 2;
    }

    private static int getAnchrox(int width, int fontSize, int i, int verifySize) {
        return (width / verifySize) * i + getAnchroy(fontSize);
    }

    private static double getTheta() {
        Random random = THREAD_LOCAL.get();
        return Math.PI / 4 * random.nextDouble() * (random.nextBoolean() ? 1 : -1);
    }

    private static void addHotPixel(int width, int height, BufferedImage image) {
        // 噪声率
        float yawpRate = 0.05f;
        Random random = THREAD_LOCAL.get();
        // 添加噪点
        for (int area = (int) (yawpRate * width * height), i = 0; i < area; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int rgb = getRandomIntColor();
            image.setRGB(x, y, rgb);
        }
    }

    private static void drawBorderColor(int width, int height, Graphics2D g2d) {
        // 设置画笔颜色，绘制边框色
        Random random = THREAD_LOCAL.get();
        g2d.setColor(COLORS[random.nextInt(COLORS.length)]);
        g2d.fillRect(0, 0, width, height);
    }

    private static void drawBackgroundColor(int width, int height, Graphics2D g2d) {
        // 设置画笔颜色，绘制背景色
        g2d.setColor(getRandColor(200, 250));
        g2d.fillRect(0, 2, width, height - 4);
    }

    private static void drawInterferingLine(int width, int height, Graphics2D g2d) {
        // 设置画笔颜色，绘制干扰线
        g2d.setColor(getRandColor(160, 200));
        Random random = THREAD_LOCAL.get();
        for (int i = 0; i < 20; i++) {
            int x = random.nextInt(width - 1);
            int y = random.nextInt(height - 1);
            int xl = random.nextInt(6) + 1;
            int yl = random.nextInt(12) + 1;
            g2d.drawLine(x, y, x + xl + 40, y + yl + 20);
        }
    }

    private static Color getRandColor(int rangeFrom, int rangeTo) {
        if (rangeFrom > 255) {
            rangeFrom = 255;
        }
        if (rangeTo > 255) {
            rangeTo = 255;
        }
        Random random = THREAD_LOCAL.get();
        int r = rangeFrom + random.nextInt(rangeTo - rangeFrom);
        int g = rangeFrom + random.nextInt(rangeTo - rangeFrom);
        int b = rangeFrom + random.nextInt(rangeTo - rangeFrom);
        return new Color(r, g, b);
    }

    private static int getRandomIntColor() {
        int[] rgb = getRandomRgb();
        int color = 0;
        for (int c : rgb) {
            color = color << 8;
            color = color | c;
        }
        return color;
    }

    private static int[] getRandomRgb() {
        int[] rgb = new int[3];
        Random random = THREAD_LOCAL.get();
        for (int i = 0; i < 3; i++) {
            rgb[i] = random.nextInt(255);
        }
        return rgb;
    }

    private static void shear(Graphics g, int w1, int h1, Color color) {
        shearX(g, w1, h1, color);
        shearY(g, w1, h1, color);
    }

    private static void shearX(Graphics g, int w1, int h1, Color color) {
        Random random = THREAD_LOCAL.get();
        int period = random.nextInt(2);
        int frames = 1;
        int phase = random.nextInt(2);
        for (int i = 0; i < h1; i++) {
            double d = (double) (period >> 1) * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
            g.copyArea(0, i, w1, 1, (int) d, 0);
            g.setColor(color);
            g.drawLine((int) d, i, 0, i);
            g.drawLine((int) d + w1, i, w1, i);
        }
    }

    private static void shearY(Graphics g, int w1, int h1, Color color) {
        Random random = THREAD_LOCAL.get();
        int period = random.nextInt(40) + 10;
        int frames = 20;
        int phase = 7;
        for (int i = 0; i < w1; i++) {
            double d = (double) (period >> 1) * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
            g.copyArea(i, 0, 1, h1, 0, (int) d);
            g.setColor(color);
            g.drawLine(i, (int) d, i, 0);
            g.drawLine(i, (int) d + h1, i, h1);
        }
    }
}
