package com.longfish.zfSlider;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PuzzleDetection {

    public static void main(String[] args) throws IOException {
        // 1. 加载图像文件
        BufferedImage image = ImageIO.read(new File(".\\zf-slider\\src\\main\\java\\com\\longfish\\zfSlider\\response.png"));

        // 2. 获取图片的宽度和高度（最大宽度和最大高度）
        int width = image.getWidth();
        int height = image.getHeight();

        // 3. 输出图片的宽度和高度
        System.out.println("图片的最大宽度: " + width);
        System.out.println("图片的最大高度: " + height);

        // 4. 处理图片：将图片转换为灰度图
        BufferedImage grayImage = toGrayScale(image);

        // 5. 二值化：将灰度图转换为黑白图，便于检测空缺区域
        BufferedImage binaryImage = binarize(grayImage);

        // 6. 检测空缺区域
        java.awt.Point emptySpot = detectEmptySpot(binaryImage);

        // 7. 输出空缺位置
        if (emptySpot != null) {
            System.out.println("空缺位置坐标: (" + emptySpot.x + ", " + emptySpot.y + ")");
        } else {
            System.out.println("没有检测到空缺区域");
        }
    }

    // 将图像转换为灰度图
    public static BufferedImage toGrayScale(BufferedImage originalImage) {
        BufferedImage grayImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = grayImage.createGraphics();
        g.drawImage(originalImage, 0, 0, null);
        g.dispose();
        return grayImage;
    }

    // 对图像进行二值化处理
    public static BufferedImage binarize(BufferedImage grayImage) {
        BufferedImage binaryImage = new BufferedImage(grayImage.getWidth(), grayImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        for (int i = 0; i < grayImage.getWidth(); i++) {
            for (int j = 0; j < grayImage.getHeight(); j++) {
                int rgb = grayImage.getRGB(i, j);
                // 获取像素的灰度值
                int grayValue = (rgb >> 16) & 0xFF; // 灰度图像的红色通道值就是灰度值
                // 设定阈值，下面的阈值（127）可以根据实际情况调整
                if (grayValue < 127) {
                    binaryImage.setRGB(i, j, Color.BLACK.getRGB()); // 空缺区域可能是黑色
                } else {
                    binaryImage.setRGB(i, j, Color.WHITE.getRGB()); // 背景区域白色
                }
            }
        }
        return binaryImage;
    }

    // 检测空缺位置
    public static java.awt.Point detectEmptySpot(BufferedImage binaryImage) {
        int width = binaryImage.getWidth();
        int height = binaryImage.getHeight();

        // 在这里，我们简单地遍历图像，寻找空缺区域的开始位置（黑色区域）
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (binaryImage.getRGB(x, y) == Color.BLACK.getRGB()) {
                    // 返回找到的空缺位置
                    return new java.awt.Point(x, y);
                }
            }
        }
        return null; // 如果没有找到空缺，返回 null
    }
}
