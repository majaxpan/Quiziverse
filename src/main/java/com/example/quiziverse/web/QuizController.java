package com.example.quiziverse.web;

import com.example.quiziverse.model.Question;
import com.example.quiziverse.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.time.Instant;
import java.util.List;

@Controller
public class QuizController {

    private final QuestionService questionService;

    public QuizController(QuestionService questionService) {
        this.questionService = questionService;
    }

    // Quiz entry point - first question
    @GetMapping("/quiz")
    public String startQuiz(@RequestParam("category") String categoryName, HttpSession session, Model model) {
        // Retrieve 10 questions based on the selected category
        List<Question> questions = questionService.getTenQuestions(categoryName);
        session.setAttribute("questions", questions);
        session.setAttribute("currentQuestionIndex", 0);
        session.setAttribute("correctAnswersCount", 0);
        session.setAttribute("quizStartTime", Instant.now());  // Store the quiz start time

        // Load the first question
        Question firstQuestion = questions.get(0);
        model.addAttribute("question", firstQuestion);
        model.addAttribute("currentIndex", 0);
        model.addAttribute("totalQuestions", questions.size());
        model.addAttribute("category", categoryName);  // Passing the category to the view

        return "quiz";  // Render the quiz page
    }

    @PostMapping("/answer")
    public String answerQuestion(@RequestParam("answer") String userAnswer, HttpSession session, Model model) {
        Instant quizStartTime = (Instant) session.getAttribute("quizStartTime");
        Instant currentTime = Instant.now();

        // Calculate elapsed time in seconds
        long elapsedTimeInSeconds = java.time.Duration.between(quizStartTime, currentTime).getSeconds();

        // Total quiz time is 60 seconds
        long remainingTime = 60 - elapsedTimeInSeconds;

        // If more than 60 seconds have passed, redirect to the "Game Over" page
        if (remainingTime <= 0) {
            return "game-over";
        }

        List<Question> questions = (List<Question>) session.getAttribute("questions");
        int currentIndex = (int) session.getAttribute("currentQuestionIndex");
        int correctAnswersCount = (int) session.getAttribute("correctAnswersCount");

        // Get the current question
        Question currentQuestion = questions.get(currentIndex);

        // Check if the answer is correct
        if (userAnswer.equals(currentQuestion.getCorrectAnswer().getAnswerText())) {
            correctAnswersCount++;
            session.setAttribute("correctAnswersCount", correctAnswersCount);
        }

        // Move to the next question
        currentIndex++;
        session.setAttribute("currentQuestionIndex", currentIndex);

        if (currentIndex < questions.size()) {
            // Show the next question
            Question nextQuestion = questions.get(currentIndex);
            model.addAttribute("question", nextQuestion);
            model.addAttribute("currentIndex", currentIndex);
            model.addAttribute("totalQuestions", questions.size());

            // Send remaining time to the client
            model.addAttribute("remainingTime", remainingTime);

            return "quiz";  // Render the quiz page with the next question
        } else {
            // Quiz is complete, show results
            model.addAttribute("correctAnswersCount", correctAnswersCount);
            model.addAttribute("totalQuestions", questions.size());

            return "results";  // Display result page after the quiz
        }
    }
}
