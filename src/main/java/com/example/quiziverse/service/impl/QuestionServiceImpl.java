package com.example.quiziverse.service.impl;

import com.example.quiziverse.model.Answer;
import com.example.quiziverse.model.Question;
import com.example.quiziverse.service.AnswerService;
import com.example.quiziverse.service.AnswerTypeService;
import com.example.quiziverse.service.CategoryService;
import com.example.quiziverse.service.QuestionService;
import org.apache.jena.query.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private static final String FUSEKI_URL = "http://localhost:3030/Quiziverse";

    private final AnswerService answerService;
    private final AnswerTypeService answerTypeService;
    private final CategoryService categoryService;

    public QuestionServiceImpl(AnswerService answerService, AnswerTypeService answerTypeService, CategoryService categoryService) {
        this.answerService = answerService;
        this.answerTypeService = answerTypeService;
        this.categoryService = categoryService;
    }

    @Override
    public List<Question> getTenQuestions(String categoryName) {
        //if specific category is selected, return 10 questions from that specific category
        if(!categoryName.equals("Random")){
            return getQuestionsFromCategory(categoryName);
        }
        //if it is chosen to be random, return 10 questions no matter the category
        else return getQuestionsFromRandomCategories();
    }

    //Sparql query to get 10 questions from certain category
    private List<Question> getQuestionsFromCategory(String categoryName) {
        List<Question> questions = new ArrayList<>();

        String categoryUri = categoryService.getCategoryUriByName(categoryName).getUri();

        String queryStr = String.format(
                "PREFIX quiz: <http://example.com/quiz#> " +
                        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                        "SELECT ?question ?questionText ?correctAnswer ?correctAnswerText ?requiresAnswerType WHERE { " +
                        "  ?question a quiz:Question ; " +
                        "            quiz:questionText ?questionText ; " +
                        "            quiz:correctAnswer ?correctAnswer ; " +
                        "            quiz:requiresAnswerType ?requiresAnswerType ; " +
                        "            quiz:belongsToCategory <%s> . " +
                        "  ?correctAnswer quiz:answerText ?correctAnswerText . " +
                        "} ORDER BY RAND() LIMIT 10", categoryUri);
        return setData(questions, queryStr);
    }

    //Sparql query to get 10 questions no matter the category
    private List<Question> getQuestionsFromRandomCategories() {
        List<Question> questions = new ArrayList<>();

        String queryStr = "PREFIX quiz: <http://example.com/quiz#> " +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                "SELECT ?question ?questionText ?correctAnswer ?correctAnswerText ?requiresAnswerType WHERE { " +
                "  ?question a quiz:Question ; " +
                "            quiz:questionText ?questionText ; " +
                "            quiz:correctAnswer ?correctAnswer ; " +
                "            quiz:requiresAnswerType ?requiresAnswerType ; " +
                "            quiz:belongsToCategory ?category . " +
                "  ?correctAnswer quiz:answerText ?correctAnswerText . " +
                "} ORDER BY RAND() LIMIT 10";

        return setData(questions, queryStr);
    }

    private void setQuestions(List<Question> questions, String questionUri, String questionText, Answer correctAnswer) {
        List<Answer> wrongAnswers = answerService.getThreeWrongAnswers(correctAnswer);

        Question question = new Question();
        question.setUri(questionUri);
        question.setQuestionText(questionText);
        question.setCorrectAnswer(correctAnswer);

        //putting 3 wrong answers in the list
        List<Answer> allAnswersList = new ArrayList<>(wrongAnswers);
        //adding the correct answer in the list
        allAnswersList.add(correctAnswer);
        //shuffling the list so the correct answer is on different position
        Collections.shuffle(allAnswersList);

        //adding the answer list to the question
        question.setAnswersList(allAnswersList);

        questions.add(question);
    }



    private List<Question> setData(List<Question> questions, String queryStr) {
        Query query = QueryFactory.create(queryStr);

        try (QueryExecution qExec = QueryExecutionFactory.sparqlService(FUSEKI_URL, query)) {
            ResultSet results = qExec.execSelect();

            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();

                String questionUri = solution.getResource("question").getURI();
                String questionText = solution.getLiteral("questionText").getString();
                String correctAnswerUri = solution.getResource("correctAnswer").getURI();
                String correctAnswerText = solution.getLiteral("correctAnswerText").getString();
                String answerTypeUri = solution.getResource("requiresAnswerType").getURI();

                Answer correctAnswer = new Answer();
                correctAnswer.setUri(correctAnswerUri);
                correctAnswer.setAnswerText(correctAnswerText);
                correctAnswer.setAnswerType(answerTypeService.getAnswerTypeLabelByUri(answerTypeUri));

                setQuestions(questions, questionUri, questionText, correctAnswer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return questions;
    }
}
