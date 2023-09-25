import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
    private static final int NUM_QUESTIONS = 5;

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
                    studentMenu(scanner, jsonParser, username);
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
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void adminMenu(Scanner scanner, JSONParser jsonParser) {
        List<JSONObject> quizArray = loadQuizData(jsonParser);
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

    private static List<JSONObject> loadQuizData(JSONParser jsonParser) {
        try {
            List<JSONObject> quizArray = (List<JSONObject>) jsonParser.parse(new FileReader("./src/main/resources/quiz.json"));
            if (quizArray == null) {
                quizArray = new ArrayList<>();
            }
            return quizArray;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static void saveQuizData(List<JSONObject> quizArray) {
        try (FileWriter fileWriter = new FileWriter("./src/main/resources/quiz.json")) {
            fileWriter.write(JSONArray.toJSONString(quizArray));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void studentMenu(Scanner scanner, JSONParser jsonParser, String username) {
        List<JSONObject> quizArray = loadQuizData(jsonParser);
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

                JSONObject quizQuestion = quizArray.get(randomIndex);
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

            if (score == 0) {
                System.out.println("কিরে ! 🤨\n ০ পাইলি কেমনে পড়ালেখা করোস নাই কিছ ? ?? Press 's' for start or 'q' for quit। \n(Oops! You scored 0. Better luck next time.)");


            } else {
                System.out.println("Your Score: " + score + " out of " + NUM_QUESTIONS);
            }

            System.out.println("Would you like to start again? Press 's' for start or 'q' for quit");

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
                fileWriter.write(JSONArray.toJSONString(resultsArray));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static JSONArray loadResultsData() throws IOException, ParseException {
        JSONArray resultsArray = (JSONArray) RESULTS_FILE.chars();
        if (resultsArray == null) {
            resultsArray = new JSONArray();
        }
        return resultsArray;
    }
}
