package com.example.quiziverse.service.impl;

import com.example.quiziverse.model.AnswerType;
import com.example.quiziverse.service.AnswerTypeService;
import org.springframework.stereotype.Service;
import org.apache.jena.query.*;

@Service
public class AnswerTypeServiceImpl implements AnswerTypeService {

    private static final String FUSEKI_URL = "http://localhost:3030/Quiziverse";

    @Override
    public AnswerType getAnswerTypeLabelByUri(String answerTypeUri) {
        String queryStr = String.format(
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                        "SELECT ?label WHERE { " +
                        "  <%s> rdfs:label ?label . " +
                        "}", answerTypeUri);

        Query query = QueryFactory.create(queryStr);

        try (QueryExecution qExec = QueryExecutionFactory.sparqlService(FUSEKI_URL, query)) {
            ResultSet results = qExec.execSelect();

            if (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                return new AnswerType(answerTypeUri, solution.getLiteral("label").getString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;  // Return null or handle error as needed
    }
}
