package benchmarks;

import OAEI.OAEIMapping;
import OAEI.OAEIMappingCollection;
import org.semanticweb.owlapi.model.OWLOntology;

public class MergingSample {
    public OWLOntology firstOntology;
    public OWLOntology secondOntology;
    public OAEIMappingCollection mappingCollection;

    public MergingSample(OWLOntology firstOntology, OWLOntology secondOntology, OAEIMappingCollection mappingCollection) {
        this.firstOntology = firstOntology;
        this.secondOntology = secondOntology;
        this.mappingCollection = mappingCollection;
    }
}
