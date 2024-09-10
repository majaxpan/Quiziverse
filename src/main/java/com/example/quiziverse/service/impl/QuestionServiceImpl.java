package com.example.quiziverse.service.impl;

import com.example.quiziverse.model.Answer;
import com.example.quiziverse.model.Question;
import com.example.quiziverse.service.QuestionService;
import org.apache.jena.query.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private static final String FUSEKI_URL = "http://localhost:3030/Quiziverse";

    @Override
    public List<Question> getQuestionsFromCategory(String categoryName) {
        List<Question> questions = new ArrayList<>();

        String categoryUri = getCategoryUriByName(categoryName);

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
                correctAnswer.setAnswerTypeUri(answerTypeUri);

                List<Answer> wrongAnswers = getWrongAnswers(answerTypeUri, correctAnswerUri);

                Question question = new Question();
                question.setUri(questionUri);
                question.setQuestionText(questionText);
                question.setCorrectAnswer(correctAnswer);
                question.setWrongAnswers(wrongAnswers);

                questions.add(question);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return questions;
    }

    public String getCategoryUriByName(String categoryName) {
        String categoryUri = null;

        String sparqlQuery = String.format(
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                        "PREFIX quiz: <http://example.com/quiz#> " +
                        "SELECT ?category WHERE { " +
                        "  ?category a quiz:Category ; " +
                        "            rdfs:label \"%s\" . " +
                        "}", categoryName);

        Query query = QueryFactory.create(sparqlQuery);

        try (QueryExecution queryExecution = QueryExecutionFactory.sparqlService(FUSEKI_URL, query)) {
            ResultSet resultSet = queryExecution.execSelect();

            if (resultSet.hasNext()) {
                QuerySolution solution = resultSet.nextSolution();
                categoryUri = solution.getResource("category").getURI();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return categoryUri;
    }

    public List<Answer> getWrongAnswers(String answerTypeUri, String correctAnswerUri) {
        List<Answer> wrongAnswers = new ArrayList<>();

        String queryStr = String.format(
                "PREFIX quiz: <http://example.com/quiz#> " +
                        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                        "SELECT ?answer ?answerText WHERE { " +
                        "  ?answer a quiz:Answer ; " +
                        "          quiz:answerText ?answerText ; " +
                        "          quiz:hasAnswerType <%s> . " +
                        "  FILTER(?answer != <%s>) " +
                        "} ORDER BY RAND() LIMIT 3", answerTypeUri, correctAnswerUri);

        Query query = QueryFactory.create(queryStr);

        try (QueryExecution qExec = QueryExecutionFactory.sparqlService(FUSEKI_URL, query)) {
            ResultSet results = qExec.execSelect();

            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();

                String answerUri = solution.getResource("answer").getURI();
                String answerText = solution.getLiteral("answerText").getString();

                Answer wrongAnswer = new Answer();
                wrongAnswer.setUri(answerUri);
                wrongAnswer.setAnswerText(answerText);
                wrongAnswer.setAnswerTypeUri(answerTypeUri);

                wrongAnswers.add(wrongAnswer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return wrongAnswers;
    }
}
