import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class QuizSystem {

    private static final String USERS_FILE = "users.json";
    private static final String QUIZ_FILE = "quiz.json";
    private static final String RESULTS_FILE = "results.json";
    private static final int NUM_QUESTIONS = 10;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        JSONParser jsonParser = new JSONParser();

        while (true) {
            System.out.println("System:> Enter your username");
            String username = scanner.nextLine();

            System.out.println("System:> Enter password");
            String password = scanner.nextLine();

            JSONObject user = getUserByUsernameAndPassword(jsonParser, username, password);

            if (user != null) {
                String role = (String) user.get("role");

                if ("admin".equals(role)) {
                    adminMenu(scanner, jsonParser);
                } else if ("student".equals(role)) {
                    studentMenu(scanner, jsonParser, username); // Pass username to studentMenu
                }
            } else {
                System.out.println("System:> Invalid credentials. Try again.");
            }

            System.out.println("Would you like to start again? press 's' for start or 'q' for quit");
            String choice = scanner.nextLine().trim();
            if ("q".equalsIgnoreCase(choice)) {
                break;
            }
        }

        System.out.println("Goodbye!");
    }

    private static JSONObject getUserByUsernameAndPassword(JSONParser jsonParser, String username, String password) {
        try {
            JSONArray usersArray = (JSONArray) jsonParser.parse(new FileReader("./src/main/resources/users.json"));

            for (Object userObj : usersArray) {
                JSONObject user = (JSONObject) userObj;
                String storedUsername = (String) user.get("username");
                String storedPassword = (String) user.get("password");

                if (storedUsername.equals(username) && storedPassword.equals(password)) {
                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void adminMenu(Scanner scanner, JSONParser jsonParser) {
        JSONArray quizArray = loadQuizData(jsonParser);
        int questionCounter = 1;

        while (true) {
            System.out.println("System:> Input your question");
            String question = scanner.nextLine();

            JSONObject quizQuestion = new JSONObject();
            quizQuestion.put("question", question);

            for (int i = 1; i <= 4; i++) {
                System.out.println("Admin:> Input option " + i + ":");
                String option = scanner.nextLine();
                quizQuestion.put("option " + i, option);
            }

            System.out.println("System:> What is the answer key?");
            int answerKey = Integer.parseInt(scanner.nextLine());
            quizQuestion.put("answerkey", answerKey);

            quizArray.add(quizQuestion);

            System.out.println("System:> Saved successfully! Do you want to add more questions? (press 's' for start and 'q' for quit)");
            String choice = scanner.nextLine().trim();
            if ("q".equalsIgnoreCase(choice)) {
                saveQuizData(quizArray);
                break;
            }

            questionCounter++;
            if (questionCounter >= NUM_QUESTIONS) {
                System.out.println("You have added the maximum number of questions.");
                break;
            }
        }
    }

    private static JSONArray loadQuizData(JSONParser jsonParser) {
        try {
            JSONArray quizArray = (JSONArray) jsonParser.parse(new FileReader("./src/main/resources/quiz.json"));
            if (quizArray == null) {
                quizArray = new JSONArray();
            }
            return quizArray;
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private static void saveQuizData(JSONArray quizArray) {
        try (FileWriter fileWriter = new FileWriter("./src/main/resources/quiz.json")) {
            fileWriter.write(quizArray.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void studentMenu(Scanner scanner, JSONParser jsonParser, String username) {
        JSONArray quizArray = loadQuizData(jsonParser);
        List<Integer> usedIndices = new ArrayList<>();
        int score = 0;

        System.out.println("System:> Welcome to the quiz! We will throw you " + NUM_QUESTIONS + " questions. Each MCQ mark is 1, and there is no negative marking. Are you ready? Press 's' for start.");
        String choice = scanner.nextLine().trim();

        if ("s".equalsIgnoreCase(choice)) {
            for (int i = 0; i < NUM_QUESTIONS; i++) {
                int randomIndex;
                do {
                    randomIndex = new Random().nextInt(quizArray.size());
                } while (usedIndices.contains(randomIndex));

                JSONObject quizQuestion = (JSONObject) quizArray.get(randomIndex);
                usedIndices.add(randomIndex);

                String question = (String) quizQuestion.get("question");
                String option1 = (String) quizQuestion.get("option 1");
                String option2 = (String) quizQuestion.get("option 2");
                String option3 = (String) quizQuestion.get("option 3");
                String option4 = (String) quizQuestion.get("option 4");
                int answerKey = ((Long) quizQuestion.get("answerkey")).intValue();

                System.out.println("[Question " + (i + 1) + "] " + question);
                System.out.println("1. " + option1);
                System.out.println("2. " + option2);
                System.out.println("3. " + option3);
                System.out.println("4. " + option4);

                int userChoice = -1;
                while (userChoice < 1 || userChoice > 4) {
                    System.out.print("Student:> ");
                    try {
                        userChoice = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        userChoice = -1;
                    }
                }

                if (userChoice == answerKey) {
                    score++;
                }
            }

            // Determine and display the appropriate message based on the score
            String message;
            if (score >= 8) {
                message = "Excellent!";
            } else if (score >= 5) {
                message = "Good.";
            } else if (score >= 2) {
                message = "Very poor!";
            } else {
                message = "Very sorry you are failed.";
            }

            System.out.println(message + " You have got " + score + " out of 10");

            System.out.println("Would you like to start again? Press 's' for start or 'q' for quit");
            choice = scanner.nextLine().trim();
            if ("s".equalsIgnoreCase(choice)) {
                // Start the quiz again.
                studentMenu(scanner, jsonParser, username);
            } else if ("q".equalsIgnoreCase(choice)) {
                // Quit the quiz.
                System.out.println("Goodbye!");
            }

            // Save the quiz result to results.json for all students
            saveQuizResult(username, score);
        }
    }

    private static void saveQuizResult(String username, int score) {
        try {
            JSONArray resultsArray = loadResultsData();

            JSONObject resultEntry = new JSONObject();
            resultEntry.put("username", username);
            resultEntry.put("score", score);

            resultsArray.add(resultEntry);

            try (FileWriter fileWriter = new FileWriter("./src/main/resources/results.json")) {
                fileWriter.write(resultsArray.toJSONString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static JSONArray loadResultsData() {
        try {
            JSONArray resultsArray = (JSONArray) new JSONParser().parse(new FileReader("./src/main/resources/results.json"));
            if (resultsArray == null) {
                resultsArray = new JSONArray();
            }
            return resultsArray;
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }
}
