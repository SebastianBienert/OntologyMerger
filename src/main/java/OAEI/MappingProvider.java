package OAEI;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class MappingProvider {
    private HashMap<String, OAEIMapping> _mappings;

    private MappingProvider(HashMap<String, OAEIMapping> mappings){
        _mappings = mappings;
    }

    public static MappingProvider FromFiles() throws Exception {
        HashMap<String, OAEIMapping> mappings = new HashMap<>();
        OAEIMappingParser parser = new OAEIMappingParser();
        mappings.putIfAbsent("cmt-conference", parser.Parse(new File("src/test/resources/mappings/cmt-conference.rdf")));
        mappings.putIfAbsent("cmt-confOf", parser.Parse(new File("src/test/resources/mappings/cmt-confOf.rdf")));
        mappings.putIfAbsent("cmt-edas", parser.Parse(new File("src/test/resources/mappings/cmt-edas.rdf")));
        mappings.putIfAbsent("cmt-ekaw", parser.Parse(new File("src/test/resources/mappings/cmt-ekaw.rdf")));
        mappings.putIfAbsent("cmt-iasted", parser.Parse(new File("src/test/resources/mappings/cmt-iasted.rdf")));
        mappings.putIfAbsent("cmt-sigkdd", parser.Parse(new File("src/test/resources/mappings/cmt-sigkdd.rdf")));
        mappings.putIfAbsent("conference-confOf", parser.Parse(new File("src/test/resources/mappings/conference-confOf.rdf")));
        mappings.putIfAbsent("conference-edas", parser.Parse(new File("src/test/resources/mappings/conference-edas.rdf")));
        mappings.putIfAbsent("conference-ekaw", parser.Parse(new File("src/test/resources/mappings/conference-ekaw.rdf")));
        mappings.putIfAbsent("conference-iasted", parser.Parse(new File("src/test/resources/mappings/conference-iasted.rdf")));
        mappings.putIfAbsent("conference-sigkdd", parser.Parse(new File("src/test/resources/mappings/conference-sigkdd.rdf")));
        mappings.putIfAbsent("confOf-edas", parser.Parse(new File("src/test/resources/mappings/confOf-edas.rdf")));
        mappings.putIfAbsent("confOf-ekaw", parser.Parse(new File("src/test/resources/mappings/confOf-ekaw.rdf")));
        mappings.putIfAbsent("confOf-iasted", parser.Parse(new File("src/test/resources/mappings/confOf-iasted.rdf")));
        mappings.putIfAbsent("confOf-sigkdd", parser.Parse(new File("src/test/resources/mappings/confOf-sigkdd.rdf")));
        mappings.putIfAbsent("edas-ekaw", parser.Parse(new File("src/test/resources/mappings/edas-ekaw.rdf")));
        mappings.putIfAbsent("edas-iasted", parser.Parse(new File("src/test/resources/mappings/edas-iasted.rdf")));
        mappings.putIfAbsent("edas-sigkdd", parser.Parse(new File("src/test/resources/mappings/edas-sigkdd.rdf")));
        mappings.putIfAbsent("ekaw-iasted", parser.Parse(new File("src/test/resources/mappings/ekaw-iasted.rdf")));
        mappings.putIfAbsent("ekaw-sigkdd", parser.Parse(new File("src/test/resources/mappings/ekaw-sigkdd.rdf")));
        mappings.putIfAbsent("iasted-sigkdd", parser.Parse(new File("src/test/resources/mappings/iasted-sigkdd.rdf")));
        mappings.putIfAbsent("A-B", parser.Parse(new File("src/test/resources/A-B.rdf")));
        mappings.putIfAbsent("A-C", parser.Parse(new File("src/test/resources/A-C.rdf")));
        mappings.putIfAbsent("A-D", parser.Parse(new File("src/test/resources/A-D.rdf")));
        mappings.putIfAbsent("B-C", parser.Parse(new File("src/test/resources/B-C.rdf")));
        mappings.putIfAbsent("B-D", parser.Parse(new File("src/test/resources/B-D.rdf")));
        mappings.putIfAbsent("C-D", parser.Parse(new File("src/test/resources/C-D.rdf")));
        return new MappingProvider(mappings);
    }

    public OAEIMapping getMapping(String name) {
        return _mappings.get(name);
    }

    public OAEIMappingCollection getMappingCollection(ArrayList<String> names){
        ArrayList<OAEIMapping> selectedMappings = new ArrayList<>();
        for(String name : names){
            selectedMappings.add(_mappings.get(name));
        }
        return new OAEIMappingCollection(selectedMappings);
    }
}
