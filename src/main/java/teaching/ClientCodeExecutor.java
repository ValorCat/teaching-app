package teaching;

import teaching.model.TestCase;
import teaching.model.TestResult;
import teaching.model.TestResults;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClientCodeExecutor {

    public static final ClientCodeExecutor INSTANCE = new ClientCodeExecutor();

    private ClientCodeExecutor() {}

    public TestResults execute(String code, List<TestCase> tests, String testJson) {
        ProcessBuilder builder = new ProcessBuilder(
                "python",
                "src/main/python/run_tests.py",
                encode(code),
                encode(testJson));
        try {
            Process process = builder.start();
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String compileCheck = reader.readLine();
            if (compileCheck == null) {
                // an error occurred in the Python tester file
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String error = errorReader.lines().collect(Collectors.joining("\n"));
                errorReader.close();
                return new TestResults("A server error occurred while testing your submission:\n" + error);
            } else if (compileCheck.startsWith("NO_COMPILE")) {
                String[] details = compileCheck.split(" ", 4);
                int lineNum = Integer.parseInt(details[1]);
                int colNum = Integer.parseInt(details[2]);
                String message = details[3];
                return new TestResults(lineNum, colNum, message);
            }
            Map<Integer, TestCase> testIndex = buildCaseIndex(tests);
            TestResults results = getResults(testIndex, reader);
            reader.close();
            return results;
        } catch (IOException | InterruptedException e) {
            return new TestResults("A server error occurred while processing the results of your submission:\n" + e);
        }
    }

    private Map<Integer, TestCase> buildCaseIndex(List<TestCase> tests) {
        return tests.stream().collect(Collectors.toMap(TestCase::getId, Function.identity()));
    }

    private TestResults getResults(Map<Integer, TestCase> tests, BufferedReader reader) throws IOException {
        List<TestResult> results = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ", 5);
            TestCase test = tests.get(Integer.parseInt(parts[1]));
            results.addAll(parseCase(parts, test, reader));
        }
        boolean allPass = true;
        for (TestResult result : results) {
            allPass &= result.isPass();
        }
        return new TestResults(results, allPass);
    }

    private List<TestResult> parseCase(String[] line, TestCase test, BufferedReader reader) throws IOException {
        switch (line[2]) {
            case "PASS":
                return Collections.singletonList(new TestResult(true, test.getShortForm()));
            case "ERROR":
                return Collections.singletonList(new TestResult(false, test.getShortFormWithError(line[3], line[4])));
            case "FAIL":
                int numElements = Integer.parseInt(line[3]);
                List<TestResult> results = new ArrayList<>(numElements);
                for (int i = 0; i < numElements; i++) {
                    String[] elementLine = reader.readLine().split(" ", 4);
                    boolean status = elementLine[2].equals("PASS");
                    String message = test.getOneElementShortForm(elementLine[1], elementLine[3]);
                    results.add(new TestResult(status, message));
                }
                return results;
        }
        throw new IllegalArgumentException();
    }

    public static String encode(String text) {
        return text
                .replaceAll("\\\\", "\\\\\\\\") // escape backslashes
                .replaceAll("\"", "\\\\\"")     // escape double quotes
                .replaceAll("\t", "    ");      // convert tabs to spaces
    }

}
