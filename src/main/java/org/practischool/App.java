package org.practischool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Hello world!
 */


public class App {

    final static Scanner IN = new Scanner(System.in, "UTF-8");
    final static String FILENAME = "books.txt";

    static String[] titles;     // 书名
    static String[] authors;    // 作者
    static String[] borrowers;  // 借阅者
    static String[] ISBNs;      // ISBN

    /**
     * 主菜单
     * @throws IOException
     */
    static void mainMenu() throws IOException {
        System.out.println("[1] 借书");
        System.out.println("[2] 还书");
        System.out.println("[3] 录入新书");
        System.out.println("[4] 退出");
        System.out.println();
        System.out.println("请输入您的选择：");

        int option = IN.nextInt();
        switch (option) {
            case 1:
                borrowMenu();
                break;
            case 2:
                returnMenu();
                break;
            case 3:
                newMenu();
                break;
            case 4:
                save();
                return;
            default:
                System.out.println("您的输入有误，请重新输入！");
                break;
        }
        mainMenu();
    }

    /**
     * 借书菜单
     */
    static void borrowMenu() {
        System.out.println("[1] 搜索在馆图书");
        System.out.println("[2] 浏览在馆书目");
        System.out.println("[3] 返回上一级菜单");
        System.out.println();
        System.out.println("请输入您的选择：");

        int option = IN.nextInt();
        switch (option) {
            case 1:
                searchAndBorrow();
                break;
            case 2:
                listAndBorrow();
                break;
            case 3:
                return;
            default:
                System.out.println("您的输入有误，请重新输入！");
                borrowMenu();
                break;
        }
    }

    /**
     * 还书菜单
     */
    static void returnMenu() {
        System.out.println("[1] 根据ISBN还书");
        System.out.println("[2] 返回上一级菜单");
        System.out.println();
        System.out.println("请输入您的选择：");

        int option = IN.nextInt();
        switch (option) {
            case 1:
                returnBookByISBN();
                break;
            case 2:
                return;
            default:
                System.out.println("您的输入有误，请重新输入！");
                returnMenu();
                break;
        }
    }

    /**
     * 录入新书菜单
     */
    static void newMenu() {
        System.out.println("请输入新书的ISBN：");
        String isbn = IN.next();

        if (Utils.fetchBookFromDouban(isbn)) {
            int index = findFreeSlot();
            authors[index] = Utils.getAuthor();
            titles[index] = Utils.getTitle();
            ISBNs[index] = isbn;
            System.out.println("新书录入成功！");
        }
        else {
            System.out.println("此书无法录入，按回车键返回上一级菜单。");
            pressEnterToContinue();
        }
    }

    /**
     * 搜索并借书
     */
    static void searchAndBorrow() {
        System.out.println("请输入书名中的关键字：");
        String keyword = IN.next();

        int cnt = 0;
        for (int i = 0; i < titles.length; i++) {
            // 书名中含有关键字，且尚未出借
            if (titles[i].indexOf(keyword) != -1 && borrowers[i].equals("")) {
                // 书本编号从1开始，因为想把0留给返回上一级菜单
                System.out.println((i + 1) + ". " + titles[i]);
                cnt++;
            }
        }

        if (cnt == 0) {
            System.out.println("未找到相关图书，按回车键返回上一级菜单。");
            pressEnterToContinue();
        }
        else {
            System.out.println("共找到" + cnt + "本图书");

            int bookId;
            String name;
            System.out.println("请输入您想借的图书编号，返回请输入0：");
            bookId = IN.nextInt();
            if (bookId != 0) {
                System.out.println("你要借阅的图书是[" + titles[bookId - 1] + "]。请输入您的姓名：");
                name = IN.next();
                borrowers[bookId - 1] = name;
                System.out.println("借阅成功，按回车键返回上一级菜单。");
            }
        }
    }

    /**
     * 列出在馆图书，并让用户选择是否借书
     */
    static void listAndBorrow() {
        for (int i = 0; i < titles.length; i++) {
            // 只列出尚未出借的书
            if (borrowers[i].equals("") && !titles[i].equals("")) {
                // 书本编号从1开始，因为想把0留给返回上一级菜单
                System.out.println((i + 1) + ". " + titles[i]);
            }
        }

        int bookId;
        String name;
        System.out.println("请输入您想借的图书编号，返回请输入0：");
        bookId = IN.nextInt();
        if (bookId != 0) {
            System.out.println("你要借阅的图书是[" + titles[bookId - 1] + "]。请输入您的姓名：");
            name = IN.next();
            borrowers[bookId - 1] = name;
            System.out.println("借阅成功，按回车键返回上一级菜单。");
        }
    }

    /**
     * 根据ISBN还书
     */
    static void returnBookByISBN() {
        System.out.println("请输入您归还的图书ISBN：");
        String isbn = IN.next();

        for (int i = 0; i < ISBNs.length; i++) {
            if (ISBNs[i].equals(isbn)) {
                System.out.println("归还成功！");
                borrowers[i] = "";
                return;
            }
        }

        System.out.println("未找到此书！");
    }

    /**
     * 找到数组中的一个空位置
     * @return 如果找到了，就返回数组下标。如果没找到就返回-1
     */
    static int findFreeSlot() {
        for (int i = 0; i < titles.length; i++) {
            if (titles[i].equals("")) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 按回车键继续
     */
    static void pressEnterToContinue() {
        try {
            System.in.read();
        }
        catch (Exception e) {
        }
    }

    /**
     * 将图书数据存入硬盘
     * 注意：请不要改动这个函数！！！！
     * @throws IOException
     */
    static void save() throws IOException {
        Path file = Paths.get(FILENAME);
        Files.write(file, (titles.length + "\n").getBytes());
        Files.write(file, Arrays.asList(titles), StandardOpenOption.APPEND);
        Files.write(file, Arrays.asList(authors), StandardOpenOption.APPEND);
        Files.write(file, Arrays.asList(ISBNs), StandardOpenOption.APPEND);
        Files.write(file, Arrays.asList(borrowers), StandardOpenOption.APPEND);
    }

    /**
     * 从硬盘读取图书数据，或初始化一份新的数据
     * 注意：请不要改动这个函数！！！！
     */
    static void load() {
        int n;

        // 找到文件，从中读取以前的信息
        try {
            Scanner fin = new Scanner(new File(FILENAME));
            n = fin.nextInt();
            titles = new String[n];
            authors = new String[n];
            ISBNs = new String[n];
            borrowers = new String[n];
            for (int i = 0; i < n; i++) {
                titles[i] = fin.nextLine();
            }
            for (int i = 0; i < n; i++) {
                authors[i] = fin.nextLine();
            }
            for (int i = 0; i < n; i++) {
                ISBNs[i] = fin.nextLine();
            }
            for (int i = 0; i < n; i++) {
                borrowers[i] = fin.nextLine();
            }
            fin.close();
        }
        // 如果没找到，或读取出错，则视为没有旧信息，初始化为新的空数据
        catch (FileNotFoundException e) {
            n = 10;
            titles = new String[n];
            authors = new String[n];
            ISBNs = new String[n];
            borrowers = new String[n];
            Arrays.fill(titles, "");
            Arrays.fill(authors, "");
            Arrays.fill(ISBNs, "");
            Arrays.fill(borrowers, "");
        }
    }


    public static void main(String[] args) throws IOException {
        load();
        mainMenu();
    }
}
