package server;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import message.Task;

public class JSONTaskReader {
    public List<Task> readConfigFile(String filePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Config config = mapper.readValue(new File(filePath), Config.class);
            return config.getTasks();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

// Classe auxiliar para representar a estrutura completa do JSON
class Config {
    private List<Task> tasks;

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}