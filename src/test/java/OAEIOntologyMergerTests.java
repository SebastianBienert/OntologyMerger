import OAEI.*;
import benchmarks.MergingSample;
import org.javatuples.Pair;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class OAEIOntologyMergerTests {

    @Test
    public void GivenTwoOntologiesAndMappingShouldGenerateMergedOntology(){
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OAEIMappingParser parser = new OAEIMappingParser();
        OAEIOntologyMerger merger = new OAEIOntologyMerger();

        try {
            OAEIMapping mapping = parser.Parse(new File("src/test/resources/mappings/cmt-conference.rdf"));
            ArrayList<OAEIMapping> mappings = new ArrayList<>();
            mappings.add(mapping);
            OAEIMappingCollection mappingsCollection = new OAEIMappingCollection(mappings);
            OWLOntology cmtOnto = man.loadOntologyFromOntologyDocument(new File("src/test/resources/ontologies/cmt.owl"));
            OWLOntology confOf = man.loadOntologyFromOntologyDocument(new File("src/test/resources/ontologies/Conference.owl"));

            assertTrue(cmtOnto.containsEntityInSignature(IRI.create("http://cmt#Preference")));
            assertTrue(confOf.containsEntityInSignature(IRI.create("http://conference#Review_preference")));
            OWLOntology mergedOntology = merger.Merge(cmtOnto, confOf, mappingsCollection);

            assertEquals("http://cmt_conference", mergedOntology.getOntologyID().getOntologyIRI().get().getIRIString());
            assertTrue(mergedOntology.containsEntityInSignature(IRI.create("http://cmt_conference#cmt@Preference||conference@Review_preference")));
            File file = new File("src/test/resources/result.owl");
            man.saveOntology(mergedOntology, cmtOnto.getFormat(), IRI.create(file.toURI()));
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
            ArrayList<OAEIMapping> mappings = new ArrayList<>();
            mappings.add(mapping);
            OAEIMappingCollection mappingsCollection = new OAEIMappingCollection(mappings);
            OWLOntology o1 = man.loadOntologyFromOntologyDocument(new File("src/test/resources/o1.owl"));
            OWLOntology o2 = man.loadOntologyFromOntologyDocument(new File("src/test/resources/o2.owl"));
            double knowledgeIncrease = merger.CalculateKnwoledgeIncrease(o1, o2, mappingsCollection);
            assertEquals(0.83, knowledgeIncrease, 0.01);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            fail();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void ExperimentReproduction() throws Exception {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OAEIOntologyMerger merger = new OAEIOntologyMerger();
        OntologyProvider ontologyProvider = OntologyProvider.FromFiles();
        MappingProvider mappingProvider = MappingProvider.FromFiles();

        ArrayList<Pair<MergingSample, Double>> samples = new ArrayList<>();
        //FIRST
        samples.add(new Pair<>(
                new MergingSample(
                        ontologyProvider.getOntology("conference"),
                        ontologyProvider.getOntology("sigkdd"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("conference-sigkdd"))))),
                0.9063));
        samples.add(new Pair<>(
                new MergingSample(
                        ontologyProvider.getOntology("edas"),
                        ontologyProvider.getOntology("confOf"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("confOf-edas"))))),
                0.9291));
        //SECOND
        samples.add(new Pair<>(
                new MergingSample(
                        ontologyProvider.getOntology("conference"),
                        ontologyProvider.getOntology("edas"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("conference-edas"))))),
                0.9257));
        samples.add(new Pair<>(
                new MergingSample(
                        ontologyProvider.getOntology("sigkdd"),
                        ontologyProvider.getOntology("confOf"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("confOf-sigkdd"))))),
                0.9506));
//        //THIRD
        samples.add(new Pair<>(
                new MergingSample(
                        ontologyProvider.getOntology("conference"),
                        ontologyProvider.getOntology("confOf"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("conference-confOf"))))),
                0.9257));
        samples.add(new Pair<>(
                new MergingSample(
                        ontologyProvider.getOntology("sigkdd"),
                        ontologyProvider.getOntology("edas"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("edas-sigkdd"))))),
                0.9506));
        try {
            for (Pair<MergingSample, Double> sample: samples) {
                OWLOntology mergedOntology = merger.Merge(
                        sample.getValue0().firstOntology,
                        sample.getValue0().secondOntology,
                        sample.getValue0().mappingCollection);
                String firstName = sample.getValue0().firstOntology.getOntologyID().getOntologyIRI().get().getShortForm();
                String secondName = sample.getValue0().secondOntology.getOntologyID().getOntologyIRI().get().getShortForm();
                File file = new File("src/test/resources/ontologies/" + firstName + "-" + secondName + ".owl");
                double knwoledgeIncrease = merger.CalculateKnwoledgeIncrease(
                        sample.getValue0().firstOntology,
                        sample.getValue0().secondOntology,
                        sample.getValue0().mappingCollection);

//                assertEquals(java.util.Optional.ofNullable(sample.getValue1()), merger.CalculateKnwoledgeIncrease(
//                        sample.getValue0().firstOntology,
//                        sample.getValue0().secondOntology,
//                        sample.getValue0().mapping));
                man.saveOntology(mergedOntology, sample.getValue0().firstOntology.getFormat(), IRI.create(file.toURI()));
                System.out.println("Expected: " + sample.getValue1() + ", actual: " + knwoledgeIncrease);
            }
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
