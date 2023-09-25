# Java_Quiz_Project
# About the Project 
# The project titled "QuizSystem" is a Java-based application that provides a simple quiz management system. It allows users to log in as either administrators or students to perform various tasks related to quizzes. Here's an overview of its key features:

### 1. **User Authentication:** Users are required to provide a username and password for authentication. The system validates these credentials against user data stored in a JSON file.
### 2. **Role-Based Access:** The system supports two user roles: "admin" and "student." Administrators have privileges to create and manage quiz questions, while students can take quizzes.
### 3. **Admin Menu:** Administrators can add multiple-choice questions to the quiz. Each question includes the question itself, four options, and an answer key. Admins can continue adding questions until a predefined limit is reached.
### 4. **Student Menu:** Students can take the quiz, and the system randomly selects questions from the quiz bank. Students answer questions and receive immediate feedback on their scores.
### 5. **Scoring:** The system calculates and displays the student's score out of the total number of questions attempted. There is no negative marking.
### 6. **Result Storage:** Quiz results for each student are saved in a JSON file ("results.json") with their usernames and scores.
### 7. **Data Persistence:** User data, quiz questions, and quiz results are stored in JSON files to ensure data persistence across program runs.
### 8. **Replay Quiz:** After completing a quiz, students have the option to start a new quiz or exit the system.
### 9. **Menu Navigation:** Users can easily navigate through menus by entering specific choices (e.g., 's' to start or 'q' to quit).
#### Overall, this Java application provides a basic framework for managing quizzes, making it useful for educational purposes or quiz-based assessments. Users can create quizzes and students can take them, with results being stored and accessible for future reference.

# Video Demonstration:
-https://drive.google.com/file/d/1n-CoY8GSanpFnTwaugX-RFQG4z3upSYZ/view?usp=sharing