import OAEI.*;
import benchmarks.MergingGroup;
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
    public void GivenTwoOntologiesAndMappingShouldGenerateMergedOntology() throws Exception {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OAEIMappingParser parser = new OAEIMappingParser();
        OAEIOntologyMerger merger = new OAEIOntologyMerger();

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
        File file = new File("src/test/resources/result.owl");
        man.saveOntology(mergedOntology, cmtOnto.getFormat(), IRI.create(file.toURI()));
        assertTrue(mergedOntology.containsEntityInSignature(IRI.create("http://cmt_conference#cmt@Preference__OR__conference@Review_preference")));
    }

    @Test
    public void GivenOntologiesFromPaperShouldCalculateProperKnowledgeIncrease() {
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
    public void TwoStageMerge() throws Exception {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OAEIOntologyMerger merger = new OAEIOntologyMerger();
        OntologyProvider ontologyProvider = OntologyProvider.FromFiles();
        MappingProvider mappingProvider = MappingProvider.FromFiles();

        ArrayList<Pair<MergingSample, Double>> samples = new ArrayList<>();
        //FIRST LEVEL
        samples.add(new Pair<>(
                new MergingSample(
                        ontologyProvider.getOntology("A"),
                        ontologyProvider.getOntology("B"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("A-B"))))),
                0.0));
        samples.add(new Pair<>(
                new MergingSample(
                        ontologyProvider.getOntology("C"),
                        ontologyProvider.getOntology("D"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("C-D"))))),
                0.0));

        samples.add(new Pair<>(
                new MergingSample(
                        ontologyProvider.getOntology("A"),
                        ontologyProvider.getOntology("C"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("A-C"))))),
                0.0));
        samples.add(new Pair<>(
                new MergingSample(
                        ontologyProvider.getOntology("B"),
                        ontologyProvider.getOntology("D"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("B-D"))))),
                0.0));

        samples.add(new Pair<>(
                new MergingSample(
                        ontologyProvider.getOntology("A"),
                        ontologyProvider.getOntology("D"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("A-D"))))),
                0.0));
        samples.add(new Pair<>(
                new MergingSample(
                        ontologyProvider.getOntology("B"),
                        ontologyProvider.getOntology("C"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("B-C"))))),
                0.0));
        //SECOND LEVEL
        samples.add(new Pair<>(
                new MergingSample(
                        ontologyProvider.getOntology("A-B"),
                        ontologyProvider.getOntology("C-D"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                "A-C",
                                "A-D",
                                "B-C",
                                "B-D"
                        ))))),
                0.0));
        samples.add(new Pair<>(
                new MergingSample(
                        ontologyProvider.getOntology("A-C"),
                        ontologyProvider.getOntology("B-D"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                "A-B",
                                "A-D",
                                "B-C",
                                "C-D"
                        ))))),
                0.0));
        samples.add(new Pair<>(
                new MergingSample(
                        ontologyProvider.getOntology("A-D"),
                        ontologyProvider.getOntology("B-C"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                "A-C",
                                "A-B",
                                "C-D",
                                "B-D"
                        ))))),
                0.0));

        for (Pair<MergingSample, Double> sample : samples) {
            OWLOntology mergedOntology = merger.Merge(
                    sample.getValue0().firstOntology,
                    sample.getValue0().secondOntology,
                    sample.getValue0().mappingCollection);
            String firstName = sample.getValue0().firstOntology.getOntologyID().getOntologyIRI().get().getShortForm();
            String secondName = sample.getValue0().secondOntology.getOntologyID().getOntologyIRI().get().getShortForm();
            File file = new File("src/test/resources/" + firstName + "-" + secondName + ".owl");
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
    }


    @Test
    public void ExperimentReproduction() throws Exception {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OAEIOntologyMerger merger = new OAEIOntologyMerger();
        OntologyProvider ontologyProvider = OntologyProvider.FromFiles();
        MappingProvider mappingProvider = MappingProvider.FromFiles();

        ArrayList<Pair<MergingGroup, Double>> groups = new ArrayList<>();
       // FIRST
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                    new MergingSample(
                        ontologyProvider.getOntology("conference"),
                        ontologyProvider.getOntology("sigkdd"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("conference-sigkdd"))))
                    ))),
                    merger),
                0.9063));
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                    new MergingSample(
                            ontologyProvider.getOntology("edas"),
                            ontologyProvider.getOntology("confOf"),
                            mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("confOf-edas"))))
                    ))),
                merger),
                0.9291));
        //SECOND
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                    new MergingSample(
                            ontologyProvider.getOntology("conference"),
                            ontologyProvider.getOntology("edas"),
                            mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("conference-edas"))))
                    ))),
                merger),
                0.9257));
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                    new MergingSample(
                            ontologyProvider.getOntology("sigkdd"),
                            ontologyProvider.getOntology("confOf"),
                            mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("confOf-sigkdd"))))
                    ))),
                merger),
                0.9506));
//        //THIRD
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                    new MergingSample(
                            ontologyProvider.getOntology("conference"),
                            ontologyProvider.getOntology("confOf"),
                            mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("conference-confOf"))))
                    ))),
                merger),
                0.9257));
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                    new MergingSample(
                            ontologyProvider.getOntology("sigkdd"),
                            ontologyProvider.getOntology("edas"),
                            mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("edas-sigkdd"))))
                    ))),
                merger),
                0.9506));
        //FOURTH:
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                    new MergingSample(
                            ontologyProvider.getOntology("sigkdd"),
                            ontologyProvider.getOntology("edas"),
                            mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("edas-sigkdd"))))
                    ),
                    new MergingSample(
                            ontologyProvider.getOntology("sigkdd-edas"),
                            ontologyProvider.getOntology("confOf"),
                            mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "confOf-edas",
                                    "confOf-sigkdd"))))
                    )
                )),
                merger),
                0.0));
        //FIFTH:
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                    new MergingSample(
                            ontologyProvider.getOntology("conference"),
                            ontologyProvider.getOntology("edas"),
                            mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("conference-edas"))))
                    ),
                    new MergingSample(
                            ontologyProvider.getOntology("conference-edas"),
                            ontologyProvider.getOntology("confOf"),
                            mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "conference-confOf",
                                    "confOf-edas"
                            )))))
                )),
                merger),
                0.9257));
        //SIXTH:
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                new MergingSample(
                        ontologyProvider.getOntology("conference"),
                        ontologyProvider.getOntology("sigkdd"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("conference-sigkdd"))))),
                new MergingSample(
                        ontologyProvider.getOntology("conference-sigkdd"),
                        ontologyProvider.getOntology("confOf"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                "conference-confOf",
                                "confOf-sigkdd"
                        )))))
                )),
                merger),
                0.9063));
        //SEVENTH:
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                new MergingSample(
                        ontologyProvider.getOntology("conference"),
                        ontologyProvider.getOntology("sigkdd"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("conference-sigkdd"))))),
                new MergingSample(
                                ontologyProvider.getOntology("conference-sigkdd"),
                                ontologyProvider.getOntology("edas"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "conference-edas",
                                        "edas-sigkdd"
                                )))))
                )),
                merger),
                0.9063));
        //SECOND LEVEL
        //FIRST
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                new MergingSample(
                        ontologyProvider.getOntology("conference-sigkdd"),
                        ontologyProvider.getOntology("edas-confOf"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                "conference-edas",
                                "conference-confOf",
                                "confOf-sigkdd",
                                "edas-sigkdd"
                        )))))
                )),
                merger),
                0.9968));
        //SECOND
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                new MergingSample(
                        ontologyProvider.getOntology("conference-edas"),
                        ontologyProvider.getOntology("sigkdd-confOf"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                "conference-sigkdd",
                                "conference-confOf",
                                "confOf-edas",
                                "edas-sigkdd"
                        )))))
                )),
                merger),
                0.9969));
        //THIRD
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                new MergingSample(
                        ontologyProvider.getOntology("conference-confOf"),
                        ontologyProvider.getOntology("sigkdd-edas"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                "conference-edas",
                                "conference-sigkdd",
                                "confOf-sigkdd",
                                "confOf-edas"
                        )))))
                )),
                merger),
                0.9969));
        //FOURTH
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                new MergingSample(
                        ontologyProvider.getOntology("sigkdd_edas_confOf"),
                        ontologyProvider.getOntology("conference"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                "conference-sigkdd",
                                "conference-confOf",
                                "conference-edas"
                        )))))
                )),
                merger),
                0.9968));
        //FIFTH
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                new MergingSample(
                        ontologyProvider.getOntology("conference_edas_confOf"),
                        ontologyProvider.getOntology("sigkdd"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                "conference-sigkdd",
                                "confOf-sigkdd",
                                "edas-sigkdd"
                        )))))
                )),
                merger),
                0.9967));
        //SIXTH
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                new MergingSample(
                        ontologyProvider.getOntology("conference_sigkdd_confOf"),
                        ontologyProvider.getOntology("edas"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                "edas-sigkdd",
                                "confOf-edas",
                                "conference-edas"
                        )))))
                )),
                merger),
                0.9968));
        //SEVENTH
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                new MergingSample(
                        ontologyProvider.getOntology("conference_sigkdd_edas"),
                        ontologyProvider.getOntology("confOf"),
                        mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                "confOf-edas",
                                "confOf-sigkdd",
                                "conference-confOf"
                        )))))
                )),
                merger),
                0.9968));

        try {
            for (Pair<MergingGroup, Double> group : groups) {

                double knwoledgeIncrease = group.getValue0().CalculateKnowledgeIncrease();

                OWLOntology mergedOntology = null;
                for (MergingSample sample : group.getValue0().getSamples()){
                    String firstName = sample.firstOntology.getOntologyID().getOntologyIRI().get().getShortForm();
                    String secondName = sample.secondOntology.getOntologyID().getOntologyIRI().get().getShortForm();
                    File file = new File("src/test/resources/ontologies/" + firstName + "-" + secondName + ".owl");
                    mergedOntology = merger.Merge(
                            sample.firstOntology,
                            sample.secondOntology,
                            sample.mappingCollection);
                    man.saveOntology(mergedOntology, sample.firstOntology.getFormat(), IRI.create(file.toURI()));
                }
                System.out.println(mergedOntology.getOntologyID().getOntologyIRI().get().getShortForm() + " Expected: " + group.getValue1() + ", actual: " + knwoledgeIncrease);
     }
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void SequentialMerge() throws Exception {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OAEIOntologyMerger merger = new OAEIOntologyMerger();
        OntologyProvider ontologyProvider = OntologyProvider.FromFiles();
        MappingProvider mappingProvider = MappingProvider.FromFiles();

        ArrayList<Pair<MergingGroup, Double>> groups = new ArrayList<>();


        groups.add(new Pair<MergingGroup, Double>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("conference"),
                                ontologyProvider.getOntology("sigkdd"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("conference-sigkdd"))))),
                        new MergingSample(
                                ontologyProvider.getOntology("conference-sigkdd"),
                                ontologyProvider.getOntology("edas"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "conference-edas",
                                        "edas-sigkdd"
                                ))))),
                        new MergingSample(
                            ontologyProvider.getOntology("conference_sigkdd_edas"),
                            ontologyProvider.getOntology("confOf"),
                            mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "confOf-edas",
                                    "confOf-sigkdd",
                                    "conference-confOf"
                            )))))
                        )),
                merger),
                0.9063));
        try {
            for (Pair<MergingGroup, Double> group : groups) {

                double knwoledgeIncrease = group.getValue0().CalculateKnowledgeIncrease();

                OWLOntology mergedOntology = null;
                for (MergingSample sample : group.getValue0().getSamples()){
                    String firstName = sample.firstOntology.getOntologyID().getOntologyIRI().get().getShortForm();
                    String secondName = sample.secondOntology.getOntologyID().getOntologyIRI().get().getShortForm();
                    File file = new File("src/test/resources/ontologies/" + firstName + "-" + secondName + ".owl");
                    mergedOntology = merger.Merge(
                            sample.firstOntology,
                            sample.secondOntology,
                            sample.mappingCollection);
                    man.saveOntology(mergedOntology, sample.firstOntology.getFormat(), IRI.create(file.toURI()));
                }
                System.out.println(mergedOntology.getOntologyID().getOntologyIRI().get().getShortForm() + " Expected: " + group.getValue1() + ", actual: " + knwoledgeIncrease);
            }
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

