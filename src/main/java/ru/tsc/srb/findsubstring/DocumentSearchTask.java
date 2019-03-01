package ru.tsc.srb.findsubstring;

import java.io.*;
import java.sql.SQLOutput;
import java.util.concurrent.RecursiveTask;

public class DocumentSearchTask extends RecursiveTask<String> {

    private File file;
    private String searchedSubstring;

    public DocumentSearchTask(File file, String searchedSubstring) {
        this.file = file;
        this.searchedSubstring = searchedSubstring;
    }

    @Override
    protected String compute() {
        return foundSubstring();
    }

    private String foundSubstring() {
        String line = "";
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            return "Файл не найден";
        }
        while (true) {
            try {
                if (!((line = bufferedReader.readLine()) != null)) break;
            } catch (IOException e) {
                return "Файл не доступен для чтения";
            }
            if (line.contains(searchedSubstring)) {
                long z = Thread.currentThread().getId();
                Main main = Main.getMain();
                if (z >= main.getNumberOfThreads()) {
                    z = z % main.getNumberOfThreads();
                }
                try {
                    synchronized (Main.class) {
                        main.getBufferedWriter().append(z + " " + file.getAbsolutePath() + " : " + line + "\n");
                        main.getBufferedWriter().flush();
                    }
                } catch (IOException e) {
                    System.out.println("Файл не доступен для записи");
                }
            }
        }
        return file.getAbsolutePath();
    }
}
