package com.securitylab2;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

@Data
@AllArgsConstructor
public class FileManager {

    private File file;
    private boolean isActivated;
    private static boolean isCaptchaEntered;

    public FileManager() {
        isActivated = checkIfProgramIsAvailableToUse();
        createActivationKeyFile();
    }

    public boolean openFile(String name) {
        if (!isActivated) {
            return false;
        }
        Path path = Paths.get(name);
        File newFile = new File(path.toUri());
        if (file == null) {
            file = newFile;
            return true;
        }

        return (!file.equals(newFile));
    }

    public boolean closeFile() {
        if (!isActivated || file == null || !isCaptchaEntered) {
            System.out.println("There is no opened file to close");
            return false;
        }
        file = null;
        return true;
    }

    public String getFileContent() throws IOException {
        if (!isActivated || file == null || !isCaptchaEntered) {
            throw new UnsupportedOperationException();
        }
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line).append(System.lineSeparator());
            }
        }
        return stringBuilder.toString();
    }

    public boolean printFileParameters() throws IOException {

        if (!isActivated || file == null) {
            return false;
        }
        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

        System.out.println("creationTime: " + attr.creationTime());
        System.out.println("lastAccessTime: " + attr.lastAccessTime());
        System.out.println("lastModifiedTime: " + attr.lastModifiedTime());

        System.out.println("isDirectory: " + attr.isDirectory());
        System.out.println("isOther: " + attr.isOther());
        System.out.println("isRegularFile: " + attr.isRegularFile());
        System.out.println("isSymbolicLink: " + attr.isSymbolicLink());
        System.out.println("size: " + attr.size());
        return true;

    }

    public boolean createFile(String path) throws IOException {
        if (!isActivated) {
            return false;
        }
        File newFile = new File(path);
        return newFile.createNewFile();
    }

    public boolean updateFileContents(String content, boolean replaceExistingText) throws IOException {
        if (!isActivated || file == null) {
            return false;
        } else if (replaceExistingText) {
            Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
        } else {
            Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        }
        return true;
    }

    public boolean renameFile(String name) {
        if (!isActivated || file == null) {
            return false;
        }

        System.out.println(file.toPath());
        File dest = new File(file.getParent() + "\\" + name);
        System.out.println(file.renameTo(dest));
        System.out.println(dest.getAbsolutePath());
        return true;
    }

    public boolean copyFile(String path) throws IOException {
        if (!isActivated || file == null) {
            return false;
        }
        Files.copy(file.toPath(), Paths.get(path + file.getName()));
        return true;
    }

    public boolean encryptFile() throws IOException {
        if (!isActivated || file == null) {
            return false;
        }
        Files.write(file.toPath(), caesarEncoding(getFileContent(), true).getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.TRUNCATE_EXISTING);
        return true;
    }

    public boolean decryptFile() throws IOException {
        if (!isActivated || file == null) {
            return false;
        }
        Files.write(file.toPath(), caesarEncoding(getFileContent(), false).getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.TRUNCATE_EXISTING);
        return true;
    }

    private String caesarEncoding(String text, boolean encrypt) {
        ArrayList<Character> upperCase = new ArrayList<>(Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'));
        ArrayList<Character> lowerCase = new ArrayList<>(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'));
        StringBuilder stringBuilder = new StringBuilder();
        int sign = encrypt ? 1 : -1;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (Character.isUpperCase(ch)) {
                stringBuilder.append(upperCase.get((upperCase.indexOf(ch) + sign * 3) % upperCase.size()));
            } else if (Character.isLowerCase(ch)) {
                stringBuilder.append(lowerCase.get((lowerCase.indexOf(ch) + sign * 3) % lowerCase.size()));
            } else {
                stringBuilder.append(ch);
            }
        }
        return stringBuilder.toString();
    }

    private boolean checkIfProgramIsAvailableToUse() {
        LocalDate localDate = LocalDate.now();
//        LocalDate finalDate = YearMonth.of(localDate.getYear(), 12).atEndOfMonth();
        LocalDate finalDate = LocalDate.now();
        if (localDate.equals(finalDate)) {
            System.out.println("You cannot use this program, because it is a 31st of December.");
            System.out.println("Please, activate a program to continue using it.");
            return false;
        }
        return true;
    }

    public void activateAProgram(String key) throws IOException {

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("keyword.txt");

        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);
        String activation = reader.readLine();
        if (activation.equals(caesarEncoding(key, true))) {
            isActivated = true;
            System.out.println("Program was successfully activated!");
        }
    }

    public void createActivationKeyFile(){
        try {
            Path source = Paths.get("C:\\Users\\admin\\IdeaProjects\\software-security-labs\\src\\main\\resources");
            File file = new File(source + "\\keyword.txt");
            file.createNewFile();
            Files.write(file.toPath(),
                    caesarEncoding("CiPkUs", true).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ignored) {
        }
    }

    public static boolean isCaptchaEntered() {
        return isCaptchaEntered;
    }

    public static void setIsCaptchaEntered(boolean isCaptchaEntered) {
        FileManager.isCaptchaEntered = isCaptchaEntered;
    }
}
