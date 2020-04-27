import OAEI.OAEIMapping;
import OAEI.OAEIOntologyMerger;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import java.util.ArrayList;

public class ExperimentMeasures {
    private static final int REPETITION = 100;

//    public TimeMeasures GetOntologyMergingTime(OWLOntology firstOntology, OWLOntology secondOntology, OAEIMapping mapping) throws Exception {
//        OAEIOntologyMerger merger = new OAEIOntologyMerger();
//        ArrayList<Long> measures = new ArrayList<Long>();
//
//        for(int i=0; i< REPETITION; i++){
//            long startTime = System.nanoTime();
//            OWLOntology mergedOntology = merger.Merge(firstOntology, secondOntology, mapping);
//            long elapsedTime = (System.nanoTime() - startTime) / REPETITION;
//            measures.add(elapsedTime);
//        }
//
//        TimeMeasures results = new TimeMeasures(measures);
//        return results;
//    }

}
