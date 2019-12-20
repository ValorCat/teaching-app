package teaching;

import teaching.model.TestCase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class ClientCodeExecutor {

    public static final ClientCodeExecutor INSTANCE = new ClientCodeExecutor();

    private ClientCodeExecutor() {}

    public void execute(String code, String tests) {
        System.out.println("Tests: " + tests);
        ProcessBuilder builder = new ProcessBuilder("python", "src/main/python/run_tests.py", code, tests);
        try {
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            System.out.println("-----------------------------");
            reader.lines().forEach(System.out::println);
            System.out.println("-----------------------------");
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
