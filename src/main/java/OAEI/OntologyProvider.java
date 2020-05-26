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

        ontologies.putIfAbsent("cmt-conference", "src/test/resources/ontologies/cmt-conference.owl");
        ontologies.putIfAbsent("cmt-confOf", "src/test/resources/ontologies/cmt-confOf.owl");

        ontologies.putIfAbsent("edas-confOf", "src/test/resources/ontologies/edas-confOf.owl");
        ontologies.putIfAbsent("edas-iasted", "src/test/resources/ontologies/edas-iasted.owl");
        ontologies.putIfAbsent("edas-sigkdd", "src/test/resources/ontologies/edas-sigkdd.owl");

        ontologies.putIfAbsent("ekaw-sigkdd", "src/test/resources/ontologies/ekaw-sigkdd.owl");
        ontologies.putIfAbsent("ekaw-conference", "src/test/resources/ontologies/ekaw-conference.owl");

        ontologies.putIfAbsent("sigkdd-edas", "src/test/resources/ontologies/sigkdd-edas.owl");
        ontologies.putIfAbsent("sigkdd-confOf", "src/test/resources/ontologies/sigkdd-confOf.owl");

        ontologies.putIfAbsent("confOf-ekaw", "src/test/resources/ontologies/confOf-ekaw.owl");
        ontologies.putIfAbsent("confOf-edas", "src/test/resources/ontologies/confOf-edas.owl");

        ontologies.putIfAbsent("iasted-cmt", "src/test/resources/ontologies/iasted-cmt.owl");
    /////////////////

        ontologies.putIfAbsent("confOf_ekaw-sigkdd", "src/test/resources/ontologies/confOf_ekaw-sigkdd.owl");
        ontologies.putIfAbsent("conference_edas_confOf", "src/test/resources/ontologies/conference_edas-confOf.owl");
        ontologies.putIfAbsent("conference_sigkdd_confOf", "src/test/resources/ontologies/conference_sigkdd-confOf.owl");
        ontologies.putIfAbsent("conference_sigkdd_edas", "src/test/resources/ontologies/conference_sigkdd-edas.owl");
        ontologies.putIfAbsent("sigkdd_edas_confOf", "src/test/resources/ontologies/sigkdd_edas-confOf.owl");
        ontologies.putIfAbsent("ekaw_sigkdd-confOf", "src/test/resources/ontologies/ekaw_sigkdd-confOf.owl");
        ontologies.putIfAbsent("edas_iasted-cmt", "src/test/resources/ontologies/edas_iasted-cmt.owl");
        ontologies.putIfAbsent("edas_iasted-ekaw", "src/test/resources/ontologies/edas_iasted-ekaw.owl");
        ontologies.putIfAbsent("iasted_cmt-ekaw", "src/test/resources/ontologies/iasted_cmt-ekaw.owl");
        ontologies.putIfAbsent("edas_sigkdd-conference", "src/test/resources/ontologies/edas_sigkdd-conference.owl");
        ontologies.putIfAbsent("ekaw_conference-sigkdd", "src/test/resources/ontologies/ekaw_conference-sigkdd.owl");
        ontologies.putIfAbsent("confOf_edas-ekaw", "src/test/resources/ontologies/confOf_edas-ekaw.owl");
        ontologies.putIfAbsent("cmt_conference-sigkdd", "src/test/resources/ontologies/cmt_conference-sigkdd.owl");
        ontologies.putIfAbsent("edas_sigkdd-iasted", "src/test/resources/ontologies/edas_sigkdd-iasted.owl");


        //EXPERIMENT

        ontologies.putIfAbsent("edas_iasted_cmt_ekaw-confOf", "src/test/resources/ontologies/edas_iasted_cmt_ekaw-confOf.owl");
        ontologies.putIfAbsent("cmt_conference-edas_iasted", "src/test/resources/ontologies/cmt_conference-edas_iasted.owl");
        ontologies.putIfAbsent("edas_iasted_cmt-ekaw", "src/test/resources/ontologies/edas_iasted_cmt-ekaw.owl");
        ontologies.putIfAbsent("conference_sigkdd_edas-confOf", "src/test/resources/ontologies/conference_sigkdd_edas-confOf.owl");
        ontologies.putIfAbsent("conference_sigkdd_edas_confOf-cmt", "src/test/resources/ontologies/conference_sigkdd_edas_confOf-cmt.owl");
        ontologies.putIfAbsent("conference_sigkdd_edas_confOf_cmt-ekaw", "src/test/resources/ontologies/conference_sigkdd_edas_confOf_cmt-ekaw.owl");
        ontologies.putIfAbsent("confOf_ekaw_sigkdd-cmt_conference", "src/test/resources/ontologies/confOf_ekaw_sigkdd-cmt_conference.owl");
        ontologies.putIfAbsent("conference_sigkdd-confOf_edas", "src/test/resources/ontologies/conference_sigkdd-confOf_edas.owl");
        ontologies.putIfAbsent("edas_iasted_ekaw-sigkdd", "src/test/resources/ontologies/edas_iasted_ekaw-sigkdd.owl");
        ontologies.putIfAbsent("edas_iasted_ekaw_sigkdd-conference", "src/test/resources/ontologies/edas_iasted_ekaw_sigkdd-conference.owl");
        ontologies.putIfAbsent("edas_sigkdd_conference-confOf", "src/test/resources/ontologies/edas_sigkdd_conference-confOf.owl");
        ontologies.putIfAbsent("cmt_confOf-edas_iasted", "src/test/resources/ontologies/cmt_confOf-edas_iasted.owl");
        ontologies.putIfAbsent("conference_edas-sigkdd_confOf", "src/test/resources/ontologies/conference_edas-sigkdd_confOf.owl");
        ontologies.putIfAbsent("confOf_edas_ekaw-cmt_conference_sigkdd", "src/test/resources/ontologies/confOf_edas_ekaw-cmt_conference_sigkdd.owl");
        ontologies.putIfAbsent("confOf_ekaw-cmt_conference", "src/test/resources/ontologies/confOf_ekaw-cmt_conference.owl");














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
        if(!_ontologies.containsKey(name)){
            System.out.println("DOES NOT HAVE " + name);
            return man.createOntology();
        }

        OWLOntology ontology = man.loadOntologyFromOntologyDocument(new File(_ontologies.get(name)));
        return ontology;
    }
}
