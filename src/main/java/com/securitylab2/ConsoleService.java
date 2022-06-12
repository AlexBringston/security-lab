package com.securitylab2;

import com.securitylab2.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleService {

    private static final FileManager fileManager = new FileManager();
    private final FileService fileService;

    @Autowired
    public ConsoleService(FileService fileService) {
        this.fileService = fileService;
    }

    public void startConsole() {
        Scanner scanner = new Scanner(System.in);

        printMenu();

        int num = scanner.nextInt();
        while (num != 0) {
            if (!FileManager.isCaptchaEntered()) {
                System.out.println("Please visit http://localhost:8080/ and enter a captcha to proceed");
            } else {
                if (fileManager.isActivated() || (!fileManager.isActivated() && num == 12)) {
                    getChosenOption(num);
                    System.out.println("\n\n\n");
                } else {
                    System.out.println("Program is not activated");
                }
            }
            printMenu();
            num = scanner.nextInt();
        }
    }

    private static void printMenu() {
        System.out.println("Please enter a number of action you want to be executed:");
        System.out.println("1. Open a file");
        System.out.println("2. Get file's content");
        System.out.println("3. Get file's properties");
        System.out.println("4. Create a file");
        System.out.println("5. Update file's content");
        System.out.println("6. Truncate file's content and add new");
        System.out.println("7. Rename a file");
        System.out.println("8. Copy a file to a different location");
        System.out.println("9. Encode a file");
        System.out.println("10. Decode a file");
        System.out.println("11. Close a file");
        System.out.println("12. Activate a program");
        System.out.println("0. Exit");
    }

    private static void getChosenOption(int number) {
        Scanner scanner = new Scanner(System.in);
        switch (number) {
            case 1:
                System.out.println("Enter file's destination (including file name)");
                String fileLocation = scanner.nextLine();
                fileManager.openFile(fileLocation);
                break;
            case 2:
                try {
                    String content = fileManager.getFileContent();
                    System.out.println(content);
                } catch (Exception exception) {
                    System.out.println("Could not get file's content");
                }
                break;
            case 3:
                try {
                    fileManager.printFileParameters();
                } catch (Exception exception) {
                    System.out.println("Could not get file's properties");
                }
                break;
            case 4:
                try {
                    System.out.println("Enter a file name including its location");
                    fileLocation = scanner.nextLine();
                    fileManager.createFile(fileLocation);
                } catch (Exception exception) {
                    System.out.println("Could not create a file");
                }
                break;
            case 5:
                try {
                    System.out.println("Enter what do you want to add to the file");
                    String content = scanner.nextLine();
                    fileManager.updateFileContents(content, false);
                } catch (Exception exception) {
                    System.out.println("Could not update a file");
                }
                break;
            case 6:
                try {
                    System.out.println("Enter what do you want to add to the file instead of the existing one");
                    String content = scanner.nextLine();
                    fileManager.updateFileContents(content, true);
                } catch (Exception exception) {
                    System.out.println("Could not update a file");
                }
                break;
            case 7:
                try {
                    System.out.println("Enter a new name for the file");
                    String name = scanner.nextLine();
                    fileManager.renameFile(name);
                } catch (Exception exception) {
                    System.out.println("Could not rename a file");
                }
                break;
            case 8:
                try {
                    System.out.println("Enter a destination where to copy a file");
                    String path = scanner.nextLine();
                    fileManager.copyFile(path);
                } catch (Exception exception) {
                    System.out.println("Could not copy a file");
                }
                break;
            case 9:
                try {
                    fileManager.encryptFile();
                } catch (Exception exception) {
                    System.out.println("Could not encode a file");
                }
                break;
            case 10:
                try {
                    fileManager.decryptFile();
                } catch (Exception exception) {
                    System.out.println("Could not decode a file");
                }
                break;
            case 11:
                fileManager.closeFile();
                break;
            case 12:
                try {
                    System.out.println("Enter an activation key");
                    String key = scanner.nextLine();
                    fileManager.activateAProgram(key);
                } catch (Exception exception) {
                    System.out.println("Could not activate a program");
                }
                break;
            default:
                System.out.println("Please choose a valid option");
                break;
        }
    }
}

