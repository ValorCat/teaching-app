package teaching;

import teaching.model.TestResult;
import teaching.model.TestResults;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ClientCodeExecutor {

    public static final ClientCodeExecutor INSTANCE = new ClientCodeExecutor();

    private ClientCodeExecutor() {}

    public TestResults execute(String code, String tests) {
        ProcessBuilder builder = new ProcessBuilder("python", "src/main/python/run_tests.py");
        try {
            Process process = builder.start();

            // passes in inputs (code and test cases)
            PrintWriter writer = new PrintWriter(process.getOutputStream());
            writer.println(encodeWhitespace(code));
            writer.println(encodeWhitespace(tests));
            writer.close();
            process.waitFor(5, TimeUnit.SECONDS);

            // process test results
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String compileCheck = reader.readLine();

            // an error occurred in the Python tester file
            if (compileCheck == null) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String error = errorReader.lines().collect(Collectors.joining("\n"));
                errorReader.close();
                return new TestResults("A server error occurred while testing your submission:\n" + error);

            // the code contained a syntax error
            } else if (compileCheck.startsWith("NO_COMPILE")) {
                String[] details = compileCheck.split(" ", 4);
                int lineNum = Integer.parseInt(details[1]);
                int colNum = Integer.parseInt(details[2]);
                String message = details[3];
                return new TestResults(lineNum, colNum, message);
            }

            // the code successfully compiled
            TestResults results = getResults(reader);
            reader.close();
            return results;
        } catch (IOException | InterruptedException e) {
            return new TestResults("A server error occurred while processing the results of your submission:\n" + e);
        }
    }

    private TestResults getResults(BufferedReader reader) throws IOException {
        List<TestResult> results = new ArrayList<>(5);
        boolean allPass = true;
        String line;
        while ((line = reader.readLine()) != null) {
            boolean pass = line.startsWith("PASS");
            String message = line.substring(5);
            results.add(new TestResult(pass, message));
            allPass &= pass;
        }
        return new TestResults(results, allPass);
    }

    private static String encodeWhitespace(String text) {
        return text
                .replace("\n", "\0")
                .replace("\r", "")
                .replace("\t", "    ");
    }

    public static String encode(String text) {
        return text
                .replaceAll("\\\\", "\\\\\\\\") // escape backslashes
                .replaceAll("\"", "\\\\\"")     // escape double quotes
                .replaceAll("\t", "    ");      // convert tabs to spaces
    }

}
