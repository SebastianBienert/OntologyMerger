package OAEI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OAEIMappingCollection {
    private ArrayList<OAEIMapping> _mappings;
    public HashMap<String, ArrayList<EntityEquivalent>> allMapings;
    public OAEIMappingCollection(ArrayList<OAEIMapping> mappings){
        allMapings = new HashMap<String, ArrayList<EntityEquivalent>>();
        for (OAEIMapping mapping : mappings){
            mapping.getMapping().forEach((k, v ) -> allMapings.merge(k, v, (v1, v2) -> new ArrayList(Stream.of(v1, v2)
                    .flatMap(x -> x.stream())
                    .collect(Collectors.toList()))));
        }
        _mappings = mappings;
    }

    public ArrayList<EntityEquivalent> get(String key){
        String namespace = key.substring(key.lastIndexOf('/') + 1, key.indexOf('#'));
        boolean isEntityFirstLevel = !namespace.contains("_");
        String entityName = isEntityFirstLevel ? key.substring(7) : key.substring(key.indexOf('#') + 1);

        List<String> unescapedKey = Arrays.asList(entityName
                .replaceAll("@", "#")
                .split("__OR__"));

        List<EntityEquivalent> entityEquivalentList = allMapings.entrySet().stream()
                .filter(x -> unescapedKey.stream().anyMatch(k -> k.equals(x.getKey().substring(7))))
                .map(x -> x.getValue())
                .flatMap(x -> x.stream())
                .collect(Collectors.toList());

        return new ArrayList<>(entityEquivalentList);
    }



}
