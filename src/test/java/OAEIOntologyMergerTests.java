import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class OAEIOntologyMergerTests {

    @Test
    public void GivenTwoOntologiesAndMappingShouldGenerateMergedOntology(){
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OAEIMappingParser parser = new OAEIMappingParser();
        OAEIOntologyMerger merger = new OAEIOntologyMerger();

        try {
            OAEIMapping mapping = parser.Parse(new File("src/test/resources/cmt-conference.rdf"));
            OWLOntology cmtOnto = man.loadOntologyFromOntologyDocument(new File("src/test/resources/cmt.owl"));
            OWLOntology confOf = man.loadOntologyFromOntologyDocument(new File("src/test/resources/Conference.owl"));
            OWLOntology mergedOntology = merger.Merge(cmtOnto, confOf, mapping);
            String xd = mergedOntology.getOntologyID().getOntologyIRI().get().getIRIString();
            IRI iri = mergedOntology.getOntologyID().getOntologyIRI().get();
            File file = new File("src/test/resources/result.owl");
            man.saveOntology(mergedOntology, cmtOnto.getFormat(), IRI.create(file.toURI()));
            //assertEquals(IRI.create("http://cmt_conference"), mergedOntology.getOntologyID().getOntologyIRI().get().getIRIString());
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void GivenOntologiesFromPaperShouldCalculateProperKnowledgeIncrease(){
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OAEIMappingParser parser = new OAEIMappingParser();
        OAEIOntologyMerger merger = new OAEIOntologyMerger();

        try {
            OAEIMapping mapping = parser.Parse(new File("src/test/resources/o1-o2.rdf"));
            OWLOntology o1 = man.loadOntologyFromOntologyDocument(new File("src/test/resources/o1.owl"));
            OWLOntology o2 = man.loadOntologyFromOntologyDocument(new File("src/test/resources/o2.owl"));
            double knowledgeIncrease = merger.CalculateKnwoledgeIncrease(o1, o2, mapping);
            assertEquals(0.83, knowledgeIncrease, 0.1);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            fail();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
