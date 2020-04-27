package OAEI;

import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;
import java.util.HashMap;

public class OntologyProvider {
    private HashMap<String, OWLOntology> _ontologies;

    private OntologyProvider(HashMap<String, OWLOntology> ontologies){
        _ontologies = ontologies;
    }

    public static OntologyProvider FromFiles() throws OWLOntologyCreationException {
        HashMap<String, OWLOntology> ontologies = new HashMap<>();
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        ontologies.putIfAbsent("cmt", man.loadOntologyFromOntologyDocument(new File("src/test/resources/ontologies/cmt.owl")));
        ontologies.putIfAbsent("conference", man.loadOntologyFromOntologyDocument(new File("src/test/resources/ontologies/Conference.owl")));
        ontologies.putIfAbsent("edas", man.loadOntologyFromOntologyDocument(new File("src/test/resources/ontologies/edas.owl")));
        ontologies.putIfAbsent("ekaw", man.loadOntologyFromOntologyDocument(new File("src/test/resources/ontologies/ekaw.owl")));
        ontologies.putIfAbsent("iasted", man.loadOntologyFromOntologyDocument(new File("src/test/resources/ontologies/iasted.owl")));
        ontologies.putIfAbsent("sigkdd", man.loadOntologyFromOntologyDocument(new File("src/test/resources/ontologies/sigkdd.owl")));
        ontologies.putIfAbsent("confOf", man.loadOntologyFromOntologyDocument(new File("src/test/resources/ontologies/confOf.owl")));
        return new OntologyProvider(ontologies);
    }

    public OWLOntology getOntology(String name) {
        return _ontologies.get(name);
    }
}
