package OAEI;

import java.util.ArrayList;
import java.util.HashMap;

public class OAEIMappingCollection {
    private ArrayList<OAEIMapping> _mappings;
    public HashMap<String, ArrayList<EntityEquivalent>> allMapings;
    public OAEIMappingCollection(ArrayList<OAEIMapping> mappings){
        allMapings = new HashMap<String, ArrayList<EntityEquivalent>>();
        for (OAEIMapping mapping : mappings){
            allMapings.putAll(mapping.getMapping());
        }
        _mappings = mappings;
    }

    public ArrayList<EntityEquivalent> get(String key){
        ArrayList<EntityEquivalent> value = allMapings.entrySet().stream()
                .filter(x -> x.getKey().contains(key))
                .map(x -> x.getValue())
                .findFirst()
                .orElse(new ArrayList<>());

        return value;
    }



}
