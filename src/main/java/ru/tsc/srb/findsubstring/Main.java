package ru.tsc.srb.findsubstring;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class Main {

    static Main main = new Main();

    private ForkJoinPool forkJoinPool;
    private List<String> fileExtensions;
    private BufferedWriter bufferedWriter;
    private int numberOfThreads;

    public Main() {
        this.fileExtensions = new ArrayList<>();
    }

    public List<String> getFileExtensions() {
        return fileExtensions;
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public ForkJoinPool getForkJoinPool() {
        return forkJoinPool;
    }

    public void setForkJoinPool(ForkJoinPool forkJoinPool) {
        this.forkJoinPool = forkJoinPool;
    }

    public void setFileExtensions(List<String> fileExtensions) {
        this.fileExtensions = fileExtensions;
    }

    public void setBufferedWriter(BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public static Main getMain() {
        return main;
    }

    public static void main(String[] args) {

        Main main = Main.getMain();

        long startTime = System.currentTimeMillis();

        if (args.length < 5) {
            System.out.println("Некорретно введены данные");
            return;
        }


        try {
            main.setNumberOfThreads(Integer.parseInt(args[0]));
        } catch (NumberFormatException e) {
            System.out.println("Количество потоков введено не верно");
            return;
        }

        if (main.getNumberOfThreads() < 1) {
            System.out.println("Введено неположительное число потоков");
            return;
        }

        String searchedSubstring = args[1];
        String path = args[2];
        String outputFile = args[3];

        for (int i = 4; i < args.length; i++) {
            main.getFileExtensions().add(args[i]);
        }

        main.setForkJoinPool(new ForkJoinPool(main.getNumberOfThreads()));

        try {
            main.setBufferedWriter(new BufferedWriter(new FileWriter(new File(outputFile))));
        } catch (IOException e) {
            System.out.println("Не доступна папка для записи");
            return;
        }

        String result = foundSubstring(new File(path), searchedSubstring);

        System.out.println(result);

        long stopTime = System.currentTimeMillis();

        try {
            main.getBufferedWriter().append(Long.toString(stopTime - startTime));
            main.getBufferedWriter().flush();
            main.getBufferedWriter().close();
        } catch (IOException e) {
            System.out.println("Файл не доступен для записи");
            return;
        }

        System.out.println(stopTime - startTime);
    }

    public static String foundSubstring(File path, String searchedSubstring) {
        Main main = getMain();
        return main.getForkJoinPool().invoke(new FolderSearchTask(path, searchedSubstring));
    }
}
