package ru.tsc.srb.findsubstring;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class FolderSearchTask extends RecursiveTask<String> {

    private File path;
    private String searchedSubstring;

    public FolderSearchTask(File path, String searchedSubstring) {
        this.path = path;
        this.searchedSubstring = searchedSubstring;
    }

    private String findAndProcessedFolderAndFile(File path) {
        String nameFile = "";
        List<RecursiveTask<String>> forks = new ArrayList<>();
        if (path.canRead()) {
            for (File entry : path.listFiles()) {
                if (entry.isFile()) {
                    if (Main.getFileExtensions().contains(entry.getName().substring(entry.getName().lastIndexOf(".") + 1))) {
                        DocumentSearchTask task = new DocumentSearchTask(entry, searchedSubstring);
                        forks.add(task);
                        task.fork();
                    }
                } else if (entry.listFiles() != null) {
                    FolderSearchTask folderSearchTask = new FolderSearchTask(entry, searchedSubstring);
                    folderSearchTask.compute();
                }
            }
            for (RecursiveTask<String> task : forks) {
                nameFile = task.join();
                System.out.println("Обработан файл: " + nameFile);
            }
            return "Все файлы были обработаны";
        } else {
            return "Корневая папка не доступна для чтения";
        }
    }

    @Override
    protected String compute() {
        return findAndProcessedFolderAndFile(path);
    }
}
