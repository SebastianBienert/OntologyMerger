package OAEI;

import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;
import java.util.HashMap;

public class OntologyProvider {
    private HashMap<String, String> _ontologies;

    private OntologyProvider(HashMap<String, String> ontologies) {
        _ontologies = ontologies;
    }

    public static OntologyProvider FromFiles() throws OWLOntologyCreationException {
        HashMap<String, String> ontologies = new HashMap<>();

        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        ontologies.putIfAbsent("cmt", "src/test/resources/ontologies/cmt.owl");
        ontologies.putIfAbsent("conference", "src/test/resources/ontologies/Conference.owl");
        ontologies.putIfAbsent("edas", "src/test/resources/ontologies/edas.owl");
        ontologies.putIfAbsent("ekaw", "src/test/resources/ontologies/ekaw.owl");
        ontologies.putIfAbsent("iasted", "src/test/resources/ontologies/iasted.owl");
        ontologies.putIfAbsent("sigkdd", "src/test/resources/ontologies/sigkdd.owl");
        ontologies.putIfAbsent("confOf", "src/test/resources/ontologies/confOf.owl");

        ontologies.putIfAbsent("conference-sigkdd", "src/test/resources/ontologies/conference-sigkdd.owl");
        ontologies.putIfAbsent("conference-edas", "src/test/resources/ontologies/conference-edas.owl");
        ontologies.putIfAbsent("conference-confOf", "src/test/resources/ontologies/conference-confOf.owl");

        ontologies.putIfAbsent("edas-confOf", "src/test/resources/ontologies/edas-confOf.owl");

        ontologies.putIfAbsent("sigkdd-edas", "src/test/resources/ontologies/sigkdd-edas.owl");
        ontologies.putIfAbsent("sigkdd-confOf", "src/test/resources/ontologies/sigkdd-confOf.owl");


        ontologies.putIfAbsent("conference_edas_confOf", "src/test/resources/ontologies/conference_edas-confOf.owl");
        ontologies.putIfAbsent("conference_sigkdd_confOf", "src/test/resources/ontologies/conference_sigkdd-confOf.owl");
        ontologies.putIfAbsent("conference_sigkdd_edas", "src/test/resources/ontologies/conference_sigkdd-edas.owl");
        ontologies.putIfAbsent("sigkdd_edas_confOf", "src/test/resources/ontologies/sigkdd_edas-confOf.owl");

        ontologies.putIfAbsent("A", "src/test/resources/A.owl");
        ontologies.putIfAbsent("B", "src/test/resources/B.owl");
        ontologies.putIfAbsent("C", "src/test/resources/C.owl");
        ontologies.putIfAbsent("D", "src/test/resources/D.owl");
        ontologies.putIfAbsent("A-B", "src/test/resources/A-B.owl");
        ontologies.putIfAbsent("A-C", "src/test/resources/A-C.owl");
        ontologies.putIfAbsent("A-D", "src/test/resources/A-D.owl");
        ontologies.putIfAbsent("B-C", "src/test/resources/B-C.owl");
        ontologies.putIfAbsent("B-D", "src/test/resources/B-D.owl");
        ontologies.putIfAbsent("C-D", "src/test/resources/C-D.owl");
        return new OntologyProvider(ontologies);
    }

    public OWLOntology getOntology(String name) throws OWLOntologyCreationException {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = man.loadOntologyFromOntologyDocument(new File(_ontologies.get(name)));
        return ontology;
    }
}
