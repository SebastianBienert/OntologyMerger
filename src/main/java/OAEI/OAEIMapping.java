package OAEI;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class OAEIMapping {
    private String firstOnotologyName;
    private String secondOntologyName;
    private HashMap<String, ArrayList<EntityEquivalent>> mapping;
    private int mappingSize;

    public OAEIMapping(String firstOnotologyName, String secondOntologyName, HashMap<String, ArrayList<EntityEquivalent>> mapping) {
        this.firstOnotologyName = firstOnotologyName;
        this.secondOntologyName = secondOntologyName;
        this.mapping = mapping;
    }

    public OAEIMapping(){
        mapping = new HashMap<String, ArrayList<EntityEquivalent>>();
        firstOnotologyName = "";
        secondOntologyName = "";
    }

    public String getFirstOnotologyName() {
        return firstOnotologyName;
    }

    public String getSecondOntologyName() {
        return secondOntologyName;
    }

    public HashMap<String, ArrayList<EntityEquivalent>> getMapping() {
        return mapping;
    }

    public String getMappedEntityName(String key){
        String firstEntityName = key.substring(7).replace('#', '@');
        String secondEntityName = this.mapping.get(key).get(0).getEntityName().replace('#', '@');

        ArrayList<String> alphabeticalSort = new ArrayList<>(Arrays.asList(firstEntityName, secondEntityName));
        alphabeticalSort.sort(Collator.getInstance());

        String result = "http://" + alphabeticalSort.get(0) + "||" + alphabeticalSort.get(1);
        return result;
    }

}

