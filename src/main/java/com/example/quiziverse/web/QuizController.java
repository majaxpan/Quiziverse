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

    @GetMapping("/quiz")
    public String startQuiz(@RequestParam("category") String categoryName, HttpSession session, Model model) {
        List<Question> questions = questionService.getTenQuestions(categoryName);
        session.setAttribute("questions", questions);
        session.setAttribute("currentQuestionIndex", 0);
        session.setAttribute("correctAnswersCount", 0);
        session.setAttribute("quizStartTime", Instant.now());

        Question firstQuestion = questions.get(0);
        model.addAttribute("question", firstQuestion);
        model.addAttribute("currentIndex", 0);
        model.addAttribute("totalQuestions", questions.size());
        model.addAttribute("category", categoryName);

        model.addAttribute("remainingTime", 60);

        return "quiz";
    }

    @PostMapping("/answer")
    public String answerQuestion(@RequestParam("selectedAnswer") String userAnswer, HttpSession session, Model model) {
        Instant quizStartTime = (Instant) session.getAttribute("quizStartTime");
        Instant currentTime = Instant.now();

        long elapsedTimeInSeconds = java.time.Duration.between(quizStartTime, currentTime).getSeconds();

        long remainingTime = 60 - elapsedTimeInSeconds;

        if (remainingTime <= 0) {
            return "quiz-over";
        }

        List<Question> questions = (List<Question>) session.getAttribute("questions");
        int currentIndex = (int) session.getAttribute("currentQuestionIndex");
        int correctAnswersCount = (int) session.getAttribute("correctAnswersCount");

        Question currentQuestion = questions.get(currentIndex);

        if (!userAnswer.isEmpty() && userAnswer.equals(currentQuestion.getCorrectAnswer().getAnswerText())) {
            correctAnswersCount++;
            session.setAttribute("correctAnswersCount", correctAnswersCount);
        }

        currentIndex++;
        session.setAttribute("currentQuestionIndex", currentIndex);

        if (currentIndex < questions.size()) {
            Question nextQuestion = questions.get(currentIndex);
            model.addAttribute("question", nextQuestion);
            model.addAttribute("currentIndex", currentIndex);
            model.addAttribute("totalQuestions", questions.size());

            model.addAttribute("remainingTime", remainingTime);
            model.addAttribute("correctAnswersCount", correctAnswersCount);

            return "quiz";
        } else {
            model.addAttribute("correctAnswersCount", correctAnswersCount);
            model.addAttribute("totalQuestions", questions.size());

            return "results";
        }
    }

    @GetMapping("/quiz-over")
    public String quizOver(){
        return "quiz-over";
    }
}
