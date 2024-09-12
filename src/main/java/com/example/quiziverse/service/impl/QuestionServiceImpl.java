package com.example.quiziverse.service.impl;

import com.example.quiziverse.model.Answer;
import com.example.quiziverse.model.Question;
import com.example.quiziverse.service.AnswerService;
import com.example.quiziverse.service.AnswerTypeService;
import com.example.quiziverse.service.QuestionService;
import org.apache.jena.query.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private static final String FUSEKI_URL = "http://localhost:3030/Quiziverse";

    private final AnswerService answerService;
    private final AnswerTypeService answerTypeService;

    public QuestionServiceImpl(AnswerService answerService, AnswerTypeService answerTypeService) {
        this.answerService = answerService;
        this.answerTypeService = answerTypeService;
    }

    @Override
    public List<Question> getTenQuestions(String categoryName) {
        if(!categoryName.equals("Random")){
            return getQuestionsFromCategory(categoryName);
        }
        else return getQuestionsFromRandomCategories();
    }

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

        return getQuestions(questions, queryStr);
    }

    private void setData(List<Question> questions, String questionUri, String questionText, Answer correctAnswer) {
        List<Answer> wrongAnswers = answerService.getThreeWrongAnswers(correctAnswer);

        Question question = new Question();
        question.setUri(questionUri);
        question.setQuestionText(questionText);
        question.setCorrectAnswer(correctAnswer);
        question.setWrongAnswers(wrongAnswers);

        questions.add(question);
    }

    private List<Question> getQuestionsFromCategory(String categoryUri) {
        List<Question> questions = new ArrayList<>();

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
        return getQuestions(questions, queryStr);
    }

    private List<Question> getQuestions(List<Question> questions, String queryStr) {
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

                setData(questions, questionUri, questionText, correctAnswer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return questions;
    }
}
