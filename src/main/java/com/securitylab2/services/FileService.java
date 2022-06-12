package com.securitylab2.services;

import com.securitylab2.models.Captcha;
import com.securitylab2.repositories.CaptchaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

@Service
public class FileService {
    private final Random random = new Random();
    private final CaptchaRepository captchaRepository;

    public FileService(CaptchaRepository captchaRepository) {
        this.captchaRepository = captchaRepository;
    }

    public Captcha getRandomCaptchaFromDB() {
        List<Captcha> captchaList = captchaRepository.findAll();
        return captchaList.get((int) (new Random().nextFloat() * captchaList.size()));
    }

    @Transactional
    public void loadDatabase(int number) {
        for (int i = 0; i < number; i++) {
            Captcha image = generateCaptchaImage(300, 100);
            captchaRepository.save(image);
        }
    }

    public void setupCaptchaDB() {
        if (captchaRepository.findAll().isEmpty()) {
            loadDatabase(40);
        }
    }

    public String generateCaptchaText() {

        int size = 5;
        return random.ints(48, 123)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(size)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }


    public Color generateColor(float saturation, float luminance) {
        final float hue = random.nextFloat();
        return Color.getHSBColor(hue, saturation, luminance);
    }

    public Captcha generateCaptchaImage(int width, int height) {
        String text = generateCaptchaText();
        int len = (width - 60) / text.length();
        int count = (int) (width * 0.1);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Font font = new Font("TimesNewRoman", Font.BOLD, 48);
        Graphics2D graphics = image.createGraphics();
        Color backGroundColor = generateColor(0.9f, 1.0f);
        graphics.setPaint(backGroundColor);
        graphics.fillRect(0, 0, width, height);
        for (int i = 0; i < text.length(); i++) {
            Graphics2D graphics2d = image.createGraphics();
            BasicStroke outlineStroke = new BasicStroke(5.0f);
            graphics2d.setFont(font);
            Color color = generateColor(0.9f,0.7f);
            graphics2d.rotate(Math.toRadians(random.nextFloat() * 60 - 30), count, (int) (height / 1.4));
            graphics2d.setColor(color);
            graphics2d.setStroke(outlineStroke);
            graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            graphics2d.drawString(String.valueOf(text.charAt(i)), count, (int) (height / 1.4));
            count += len;
        }

        return new Captcha(text, getPNGImageInByteArray(getImageWithNoises(image)));
    }


    private BufferedImage getImageWithNoises(BufferedImage image) {
        BufferedImage newImage = image;
        int width = newImage.getWidth();
        int height = newImage.getHeight();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = image.getRGB(j, i);
                newImage.setRGB(j, i, changePixelARGB(pixel));
            }
        }
        return newImage;
    }

    private byte[] getPNGImageInByteArray(BufferedImage newImage) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(newImage, "png", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public int changePixelARGB(int pixel) {
        Random random = new Random();
        Color defaultColor = new Color(pixel, true);
        int red = defaultColor.getRed();
        int green = defaultColor.getGreen();
        int blue = defaultColor.getBlue();
        int boundary = 30;
        int newRed = checkBoundaries((int) (random.nextFloat() * boundary * 2 + red - boundary));
        int newGreen = checkBoundaries((int) (random.nextFloat() * boundary * 2 + green - boundary));
        int newBlue = checkBoundaries((int) (random.nextFloat() * boundary * 2 + blue - boundary));

        return new Color(newRed, newGreen, newBlue, defaultColor.getAlpha()).getRGB();
    }

    public int checkBoundaries(int color) {
        if (color < 0) {
            return 0;
        } else return Math.min(color, 255);
    }
}
