package OAEI;

import java.util.ArrayList;
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
}

