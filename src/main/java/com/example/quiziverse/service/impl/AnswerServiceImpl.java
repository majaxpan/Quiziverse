package com.example.quiziverse.service.impl;

import com.example.quiziverse.model.Answer;
import com.example.quiziverse.model.AnswerType;
import com.example.quiziverse.service.AnswerService;
import org.apache.jena.query.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnswerServiceImpl implements AnswerService {

    private static final String FUSEKI_URL = "http://localhost:3030/Quiziverse";

    @Override
    public Answer getAnswerByUri(String answerUri) {
        Answer answer = null;

        String queryStr = String.format(
                "PREFIX quiz: <http://example.com/quiz#> " +
                        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                        "SELECT ?answer ?answerText ?answerType ?answerTypeLabel WHERE { " +
                        "  <%s> a quiz:Answer ; " +
                        "         quiz:answerText ?answerText ; " +
                        "         quiz:hasAnswerType ?answerType . " +
                        "  ?answerType rdfs:label ?answerTypeLabel . " +
                        "} LIMIT 1", answerUri);

        Query query = QueryFactory.create(queryStr);

        try (QueryExecution qExec = QueryExecutionFactory.sparqlService(FUSEKI_URL, query)) {
            ResultSet results = qExec.execSelect();

            if (results.hasNext()) {
                QuerySolution solution = results.nextSolution();

                String answerText = solution.getLiteral("answerText").getString();
                String answerTypeUri = solution.getResource("answerType").getURI();
                String answerTypeLabel = solution.getLiteral("answerTypeLabel").getString();

                AnswerType answerType = new AnswerType();
                answerType.setUri(answerTypeUri);
                answerType.setLabel(answerTypeLabel);

                answer = new Answer();
                answer.setUri(answerUri);
                answer.setAnswerText(answerText);
                answer.setAnswerType(answerType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return answer;
    }

    @Override
    public List<Answer> getThreeWrongAnswers(Answer correctAnswer) {
        List<Answer> wrongAnswers = new ArrayList<>();

        String answerTypeUri = correctAnswer.getAnswerType().getUri();
        String correctAnswerUri = correctAnswer.getUri();

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
                wrongAnswer.setAnswerType(new AnswerType(correctAnswer.getAnswerType().getUri(), correctAnswer.getAnswerType().getLabel()));

                wrongAnswers.add(wrongAnswer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return wrongAnswers;
    }
}
