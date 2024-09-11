package com.example.quiziverse.service.impl;

import com.example.quiziverse.model.Category;
import com.example.quiziverse.service.CategoryService;
import org.apache.jena.query.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final String FUSEKI_URL = "http://localhost:3030/Quiziverse";

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();

        String queryStr = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                "PREFIX quiz: <http://example.com/quiz#> " +
                "SELECT ?category ?label WHERE { " +
                "  ?category a quiz:Category ; " +
                "            rdfs:label ?label . " +
                "}";

        Query query = QueryFactory.create(queryStr);

        try (QueryExecution qExec = QueryExecutionFactory.sparqlService(FUSEKI_URL, query)) {
            ResultSet results = qExec.execSelect();

            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                String categoryUri = solution.getResource("category").getURI();
                String label = solution.getLiteral("label").getString();

                Category category = new Category();
                category.setUri(categoryUri);
                category.setLabel(label);

                categories.add(category);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return categories;
    }

    @Override
    public Category getCategoryUriByName(String categoryName) {
        Category category = null;

        String queryStr = String.format(
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                        "PREFIX quiz: <http://example.com/quiz#> " +
                        "SELECT ?category WHERE { " +
                        "  ?category a quiz:Category ; " +
                        "            rdfs:label \"%s\" . " +
                        "}", categoryName);

        Query query = QueryFactory.create(queryStr);

        try (QueryExecution qExec = QueryExecutionFactory.sparqlService(FUSEKI_URL, query)) {
            ResultSet results = qExec.execSelect();

            if (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                String categoryUri = solution.getResource("category").getURI();

                category = new Category();
                category.setUri(categoryUri);
                category.setLabel(categoryName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return category;
    }
}
