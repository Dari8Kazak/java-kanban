package service;

import exceptions.ManagerLoadException;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FileBackedTaskManagerTest {

    @BeforeEach
    void setUp() {
        File file = new File("test_data.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
    }

    @Test
    public void testSaveEmptyFile() throws IOException {
        File tempFile = File.createTempFile("tempTaskManager", ".csv");
        tempFile.deleteOnExit();
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        manager.save();

        try (BufferedReader br = new BufferedReader(new FileReader(tempFile))) {

            String header = br.readLine();
            assertEquals("id,type,name,status,description,epic", header);
            assertNull(br.readLine());
        }
    }

    @Test
    public void testSaveMultipleTasks() throws IOException {
        File tempFile = File.createTempFile("tempTaskManager", ".csv");
        tempFile.deleteOnExit();
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task("Task 1", "Description 1");
        Epic epic1 = new Epic("Epic 1", "Epic Description 1");
        SubTask subTask1 = new SubTask("SubTask 1", "SubTask Description 1", epic1.getId());


        manager.createTask(task1);
        manager.createEpic(epic1);
        manager.createSubTask(subTask1);

        manager.save();

        try (BufferedReader br = new BufferedReader(new FileReader(tempFile))) {
            String header = br.readLine();
            assertEquals("id,type,name,status,description,epic", header);

            int lineCount = 0;
            while (br.readLine() != null) {
                lineCount++;
            }
            assertEquals(3, lineCount);
        }
    }

    @Test
    public void testLoadMultipleTasks() throws IOException, ManagerLoadException {
        File tempFile = File.createTempFile("tempTaskManager", ".csv");
        tempFile.deleteOnExit();

        try (FileWriter fw = new FileWriter(tempFile)) {
            fw.write("id,type,name,status,description,epic\n");
            fw.write("1,TASK,Task1,NEW,Description task1,\n");
            fw.write("2,EPIC,Epic2,DONE,Description epic2,\n");
            fw.write("3,SUBTASK,SubTask3,NEW,Description subTask3,1\n");
        }

        TaskManager manager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, manager.getAllTasks().size());
        assertEquals(1, manager.getAllEpics().size());
        assertEquals(1, manager.getAllSubTasks().size());
    }
}