package benchmarks;

import OAEI.OAEIMapping;
import org.semanticweb.owlapi.model.OWLOntology;

public class MergingSample {
    public OWLOntology firstOntology;
    public OWLOntology secondOntology;
    public OAEIMapping mapping;

    public MergingSample(OWLOntology firstOntology, OWLOntology secondOntology, OAEIMapping mapping) {
        this.firstOntology = firstOntology;
        this.secondOntology = secondOntology;
        this.mapping = mapping;
    }
}
