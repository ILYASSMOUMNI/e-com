package com.yourname.ecommerce.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageTest {
    public static void main(String[] args) {
        File mediaFolder = new File("media");
        if (!mediaFolder.exists()) {
            System.out.println("Media folder not found at: " + mediaFolder.getAbsolutePath());
            return;
        }

        System.out.println("Media folder found at: " + mediaFolder.getAbsolutePath());
        File[] files = mediaFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                try {
                    System.out.println("\nTesting file: " + file.getName());
                    System.out.println("File size: " + file.length() + " bytes");
                    System.out.println("Can read file: " + file.canRead());
                    
                    BufferedImage image = ImageIO.read(file);
                    if (image != null) {
                        System.out.println("Successfully loaded image!");
                        System.out.println("Image dimensions: " + image.getWidth() + "x" + image.getHeight());
                        System.out.println("Image type: " + image.getType());
                    } else {
                        System.out.println("Failed to load image: ImageIO.read returned null");
                    }
                } catch (Exception e) {
                    System.out.println("Error loading image: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
} 