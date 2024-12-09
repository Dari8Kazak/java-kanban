package service;


public class FileBackedTaskManagerTest {
//
//    @Test
//    public void testSaveEmptyFile() throws IOException {
//        File tempFile = File.createTempFile("tempTaskManager", ".csv");
//        tempFile.deleteOnExit();
//        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
//        manager.save();
//
//        try (BufferedReader br = new BufferedReader(new FileReader(tempFile))) {
//
//            String header = br.readLine();
//            assertEquals("id,type,name,status,description,epic", header);
//            assertNull(br.readLine());
//        }
//    }
//
//    @Test
//    public void testSaveMultipleTasks() throws IOException {
//        File tempFile = File.createTempFile("tempTaskManager", ".csv");
//        tempFile.deleteOnExit();
//        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
//
//        Task task1 = new Task(1, "Task 1", TaskStatus.NEW, "Description 1");
//        Epic epic1 = new Epic("Epic 1", "Epic Description 1");
//        SubTask subTask1 = new SubTask(1, "SubTask 1", TaskStatus.NEW, "SubTask Description 1", 2);
//        manager.createTask(task1);
//        manager.createEpic(epic1);
//        manager.createSubTask(subTask1);
//
//        manager.save();
//
//        try (BufferedReader br = new BufferedReader(new FileReader(tempFile))) {
//            String header = br.readLine();
//            assertEquals("id,type,name,status,description,epic", header);
//
//            int lineCount = 0;
//            while (br.readLine() != null) {
//                lineCount++;
//            }
//            assertEquals(3, lineCount);
//        }
//    }
//
//    @Test
//    public void testLoadMultipleTasks() throws IOException, FileException {
//        File tempFile = File.createTempFile("tempTaskManager", ".csv");
//        tempFile.deleteOnExit();
//
//        try (FileWriter fw = new FileWriter(tempFile)) {
//            fw.write("id,type,name,status,description,epic\n");
//            fw.write("1,TASK,Task1,NEW,Description task1,\n");
//            fw.write("2,EPIC,Epic2,DONE,Description epic2,\n");
//            fw.write("3,SUBTASK,SubTask3,NEW,Description subTask3,1\n");
//        }
//        TaskManager manager = FileBackedTaskManager.load(tempFile);
//
//        assertEquals(1, manager.getAllTasks().size());
//        assertEquals(1, manager.getAllEpics().size());
//        assertEquals(1, manager.getAllSubTasks().size());
//    }
}