import OAEI.MappingProvider;
import OAEI.OAEIOntologyMerger;
import OAEI.OntologyProvider;
import benchmarks.MergingGroup;
import benchmarks.MergingSample;
import org.javatuples.Pair;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Experiment {

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
                                ))))),
                        new MergingSample(
                                ontologyProvider.getOntology("conference_sigkdd_edas-confOf"),
                                ontologyProvider.getOntology("cmt"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "cmt-conference",
                                        "cmt-confOf",
                                        "cmt-sigkdd",
                                        "cmt-edas"
                                ))))),
                        new MergingSample(
                                ontologyProvider.getOntology("conference_sigkdd_edas_confOf-cmt"),
                                ontologyProvider.getOntology("ekaw"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "cmt-ekaw",
                                        "conference-ekaw",
                                        "confOf-ekaw",
                                        "edas-ekaw",
                                        "ekaw-sigkdd"
                                ))))),
                        new MergingSample(
                                ontologyProvider.getOntology("conference_sigkdd_edas_confOf_cmt-ekaw"),
                                ontologyProvider.getOntology("iasted"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "cmt-iasted",
                                        "conference-iasted",
                                        "confOf-iasted",
                                        "edas-iasted",
                                        "ekaw-iasted",
                                        "iasted-sigkdd"
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

    @Test
    public void ExperimentTests() throws Exception {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OAEIOntologyMerger merger = new OAEIOntologyMerger();
        OntologyProvider ontologyProvider = OntologyProvider.FromFiles();
        MappingProvider mappingProvider = MappingProvider.FromFiles();

        ArrayList<Pair<MergingGroup, Double>> groups = new ArrayList<>();
        //FIRST LEVEL

        // FIRST
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("confOf"),
                                ontologyProvider.getOntology("ekaw"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("confOf-ekaw"))))
                        ),
                        new MergingSample(
                                ontologyProvider.getOntology("confOf-ekaw"),
                                ontologyProvider.getOntology("sigkdd"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "confOf-sigkdd",
                                        "ekaw-sigkdd"))))
                        )
                    )),
                merger),
                0.9063));
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("cmt"),
                                ontologyProvider.getOntology("conference"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("cmt-conference"))))
                        ))),
                merger),
                0.9291));
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("edas"),
                                ontologyProvider.getOntology("iasted"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("edas-iasted"))))
                        ))),
                merger),
                0.9291));
        //SECOND
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("ekaw"),
                                ontologyProvider.getOntology("sigkdd"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("ekaw-sigkdd"))))
                        ))),
                merger),
                0.9257));
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("cmt"),
                                ontologyProvider.getOntology("conference"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("cmt-conference"))))
                        ))),
                merger),
                0.9506));
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("edas"),
                                ontologyProvider.getOntology("iasted"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("edas-iasted"))))
                        ))),
                merger),
                0.9506));
        //THIRD
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("conference"),
                                ontologyProvider.getOntology("sigkdd"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("conference-sigkdd"))))
                        ))),
                merger),
                0.9257));
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("edas"),
                                ontologyProvider.getOntology("iasted"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("edas-iasted"))))
                        ),
                        new MergingSample(
                                ontologyProvider.getOntology("edas-iasted"),
                                ontologyProvider.getOntology("cmt"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "cmt-edas",
                                        "cmt-iasted"))))
                        ),
                        new MergingSample(
                                ontologyProvider.getOntology("edas_iasted-cmt"),
                                ontologyProvider.getOntology("ekaw"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "cmt-ekaw",
                                        "edas-ekaw",
                                        "ekaw-iasted"))))
                        ),
                        new MergingSample(
                                ontologyProvider.getOntology("edas_iasted_cmt-ekaw"),
                                ontologyProvider.getOntology("confOf"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "cmt-confOf",
                                        "confOf-edas",
                                        "confOf-ekaw",
                                        "confOf-iasted"))))
                        )
                )),
                merger),
                0.9063));
        //FOURTH
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("confOf"),
                                ontologyProvider.getOntology("edas"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("confOf-edas"))))
                        ))),
                merger),
                0.9257));
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("iasted"),
                                ontologyProvider.getOntology("cmt"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("cmt-iasted"))))
                        ))),
                merger),
                0.9257));

        //SECOND LEVEL

        //FIRST
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("confOf_ekaw-sigkdd"),
                                ontologyProvider.getOntology("cmt-conference"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "conference-confOf",
                                        "conference-ekaw",
                                        "conference-sigkdd",
                                        "cmt-sigkdd",
                                        "cmt-ekaw",
                                        "cmt-confOf"
                                )))))
                )),
                merger),
                0.9968));
        //SECOND
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("ekaw-sigkdd"),
                                ontologyProvider.getOntology("confOf"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "confOf-ekaw",
                                        "confOf-sigkdd"
                                )))))
                )),
                merger),
                0.9969));
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("cmt-conference"),
                                ontologyProvider.getOntology("edas-iasted"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "cmt-iasted",
                                        "cmt-edas",
                                        "conference-edas",
                                        "conference-iasted"
                                )))))
                )),
                merger),
                0.9969));
        //THIRD
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("edas_iasted_cmt_ekaw-confOf"),
                                ontologyProvider.getOntology("conference-sigkdd"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "conference-edas",
                                        "conference-iasted",
                                        "cmt-conference",
                                        "conference-ekaw",
                                        "conference-confOf",
                                        "edas-sigkdd",
                                        "iasted-sigkdd",
                                        "cmt-sigkdd",
                                        "ekaw-sigkdd",
                                        "confOf-sigkdd"
                                )))))
                )),
                merger),
                0.9969));

        //FOURTH
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("conference-sigkdd"),
                                ontologyProvider.getOntology("confOf-edas"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "edas-sigkdd",
                                        "confOf-sigkdd",
                                        "conference-edas",
                                        "conference-confOf"
                                )))))
                )),
                merger),
                0.9969));

        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("iasted-cmt"),
                                ontologyProvider.getOntology("ekaw"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "cmt-ekaw",
                                        "cmt-iasted"
                                )))))
                )),
                merger),
                0.9969));

        //THIRD LEVEL
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("confOf_ekaw_sigkdd-cmt_conference"),
                                ontologyProvider.getOntology("edas-iasted"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "confOf-edas",
                                        "edas-ekaw",
                                        "edas-sigkdd",
                                        "cmt-edas",
                                        "conference-edas",
                                        "confOf-iasted",
                                        "ekaw-iasted",
                                        "iasted-sigkdd",
                                        "cmt-iasted",
                                        "conference-iasted"
                                )))))
                )),
                merger),
                0.9969));
        //SECOND
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("ekaw_sigkdd-confOf"),
                                ontologyProvider.getOntology("cmt_conference-edas_iasted"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "cmt-ekaw",
                                        "conference-ekaw",
                                        "edas-ekaw",
                                        "ekaw-iasted",
                                        "cmt-sigkdd",
                                        "conference-sigkdd",
                                        "edas-sigkdd",
                                        "iasted-sigkdd",
                                        "cmt-confOf",
                                        "conference-confOf",
                                        "confOf-edas",
                                        "confOf-iasted"
                                )))))
                )),
                merger),
                0.9969));

        //FOURTH
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("conference_sigkdd-confOf_edas"),
                                ontologyProvider.getOntology("iasted_cmt-ekaw"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "conference-iasted",
                                        "iasted-sigkdd",
                                        "confOf-iasted",
                                        "edas-iasted",
                                        "cmt-conference",
                                        "cmt-sigkdd",
                                        "cmt-confOf",
                                        "cmt-edas",
                                        "conference-ekaw",
                                        "ekaw-sigkdd",
                                        "confOf-ekaw",
                                        "edas-ekaw"
                                )))))
                )),
                merger),
                0.9969));

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
    public void ExperimentTestsPart2() throws Exception {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OAEIOntologyMerger merger = new OAEIOntologyMerger();
        OntologyProvider ontologyProvider = OntologyProvider.FromFiles();
        MappingProvider mappingProvider = MappingProvider.FromFiles();

        ArrayList<Pair<MergingGroup, Double>> groups = new ArrayList<>();
        //FIRST LEVEL

        // FIFTH
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("cmt"),
                                ontologyProvider.getOntology("confOf"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("cmt-confOf"))))
                        ))),
                merger),
                0.9291));

        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("edas"),
                                ontologyProvider.getOntology("iasted"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("edas-iasted"))))
                        ),
                        new MergingSample(
                                ontologyProvider.getOntology("edas-iasted"),
                                ontologyProvider.getOntology("ekaw"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "edas-ekaw",
                                        "ekaw-iasted"))))
                        ),
                        new MergingSample(
                                ontologyProvider.getOntology("edas_iasted-ekaw"),
                                ontologyProvider.getOntology("sigkdd"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "edas-sigkdd",
                                        "iasted-sigkdd",
                                        "ekaw-sigkdd"
                                ))))
                        ),
                        new MergingSample(
                                ontologyProvider.getOntology("edas_iasted_ekaw-sigkdd"),
                                ontologyProvider.getOntology("conference"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "conference-sigkdd",
                                        "conference-iasted",
                                        "conference-edas",
                                        "conference-ekaw"
                                ))))
                        )
                )),
                merger),
                0.9063));

        //SIXTH
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("ekaw"),
                                ontologyProvider.getOntology("conference"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("conference-ekaw"))))
                        ))),
                merger),
                0.9291));

        //SEVENTH
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("iasted"),
                                ontologyProvider.getOntology("cmt"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("cmt-iasted"))))
                        ),
                        new MergingSample(
                                ontologyProvider.getOntology("iasted-cmt"),
                                ontologyProvider.getOntology("ekaw"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "cmt-ekaw",
                                        "ekaw-iasted"))))
                        )
                )),
                merger),
                0.9063));

        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("edas"),
                                ontologyProvider.getOntology("sigkdd"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("edas-sigkdd"))))
                        ),
                        new MergingSample(
                                ontologyProvider.getOntology("edas-sigkdd"),
                                ontologyProvider.getOntology("conference"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "conference-edas",
                                        "conference-sigkdd"))))
                        ),
                        new MergingSample(
                                ontologyProvider.getOntology("edas_sigkdd-conference"),
                                ontologyProvider.getOntology("confOf"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "conference-confOf",
                                        "confOf-edas",
                                        "confOf-sigkdd"))))
                        )
                )),
                merger),
                0.9063));

        //EIGTH

        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("iasted"),
                                ontologyProvider.getOntology("cmt"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("cmt-iasted"))))
                        )
                )),
                merger),
                0.9063));

        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("conference"),
                                ontologyProvider.getOntology("edas"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("conference-edas"))))
                        )
                )),
                merger),
                0.9063));

        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("confOf"),
                                ontologyProvider.getOntology("sigkdd"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("confOf-sigkdd"))))
                        )
                )),
                merger),
                0.9063));


        //SECOND LEVEL

        //FIFTH
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("cmt-confOf"),
                                ontologyProvider.getOntology("edas_iasted_ekaw_sigkdd-conference"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "confOf-edas",
                                        "confOf-iasted",
                                        "confOf-ekaw",
                                        "confOf-sigkdd",
                                        "conference-confOf",
                                        "cmt-conference",
                                        "cmt-sigkdd",
                                        "cmt-ekaw",
                                        "cmt-iasted",
                                        "cmt-edas"
                                )))))
                )),
                merger),
                0.9968));


        //SIXTH
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("cmt-confOf"),
                                ontologyProvider.getOntology("edas-iasted"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "confOf-edas",
                                        "confOf-iasted",
                                        "cmt-edas",
                                        "cmt-iasted"
                                )))))
                )),
                merger),
                0.9969));

        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("ekaw-conference"),
                                ontologyProvider.getOntology("sigkdd"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "ekaw-sigkdd",
                                        "conference-sigkdd"
                                )))))
                )),
                merger),
                0.9969));

        //SEVENTH
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("iasted_cmt-ekaw"),
                                ontologyProvider.getOntology("edas_sigkdd_conference-confOf"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "edas-iasted",
                                        "iasted-sigkdd",
                                        "conference-iasted",
                                        "confOf-iasted",
                                        "cmt-edas",
                                        "cmt-sigkdd",
                                        "cmt-conference",
                                        "cmt-confOf",
                                        "edas-ekaw",
                                        "ekaw-sigkdd",
                                        "conference-ekaw",
                                        "confOf-ekaw"
                                )))))
                )),
                merger),
                0.9969));

        //EIGTH
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("iasted-cmt"),
                                ontologyProvider.getOntology("ekaw"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "ekaw-iasted",
                                        "cmt-ekaw"
                                )))))
                )),
                merger),
                0.9969));

        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("conference-edas"),
                                ontologyProvider.getOntology("sigkdd-confOf"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "conference-confOf",
                                        "conference-sigkdd",
                                        "edas-sigkdd",
                                        "confOf-edas"
                                )))))
                )),
                merger),
                0.9969));
//
//        //THIRD LEVEL

        //SIXTH
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("cmt_confOf-edas_iasted"),
                                ontologyProvider.getOntology("ekaw_conference-sigkdd"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "cmt-ekaw",
                                        "confOf-ekaw",
                                        "edas-ekaw",
                                        "ekaw-iasted",
                                        "cmt-conference",
                                        "conference-confOf",
                                        "conference-edas",
                                        "conference-iasted",
                                        "cmt-sigkdd",
                                        "confOf-sigkdd",
                                        "edas-sigkdd",
                                        "iasted-sigkdd"
                                )))))
                )),
                merger),
                0.9969));
        //EIGTH
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("iasted_cmt-ekaw"),
                                ontologyProvider.getOntology("conference_edas-sigkdd_confOf"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "cmt-ekaw",
                                        "confOf-ekaw",
                                        "edas-ekaw",
                                        "ekaw-iasted",
                                        "cmt-conference",
                                        "conference-confOf",
                                        "conference-edas",
                                        "conference-iasted",
                                        "cmt-sigkdd",
                                        "confOf-sigkdd",
                                        "edas-sigkdd",
                                        "iasted-sigkdd"
                                )))))
                )),
                merger),
                0.9969));



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
    public void ExperimentTestsPart3() throws Exception {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OAEIOntologyMerger merger = new OAEIOntologyMerger();
        OntologyProvider ontologyProvider = OntologyProvider.FromFiles();
        MappingProvider mappingProvider = MappingProvider.FromFiles();

        ArrayList<Pair<MergingGroup, Double>> groups = new ArrayList<>();
        //FIRST LEVEL

        // NINETH
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("confOf"),
                                ontologyProvider.getOntology("edas"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("confOf-edas"))))
                        ),
                        new MergingSample(
                                ontologyProvider.getOntology("confOf-edas"),
                                ontologyProvider.getOntology("ekaw"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "confOf-ekaw",
                                        "edas-ekaw"))))
                        )
                )),
                merger),
                0.9063));

        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("cmt"),
                                ontologyProvider.getOntology("conference"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("cmt-conference"))))
                        ),
                        new MergingSample(
                                ontologyProvider.getOntology("cmt-conference"),
                                ontologyProvider.getOntology("sigkdd"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "cmt-sigkdd",
                                        "conference-sigkdd"))))
                        )
                )),
                merger),
                0.9063));

        //TENTH
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("confOf"),
                                ontologyProvider.getOntology("ekaw"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("confOf-ekaw"))))
                        ))),
                merger),
                0.9291));
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("conference"),
                                ontologyProvider.getOntology("cmt"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("cmt-conference"))))
                        ))),
                merger),
                0.9291));
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("edas"),
                                ontologyProvider.getOntology("sigkdd"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList("edas-sigkdd"))))
                        ))),
                merger),
                0.9291));



        //SECOND LEVEL

        //NINETH
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("confOf_edas-ekaw"),
                                ontologyProvider.getOntology("cmt_conference-sigkdd"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "cmt-confOf",
                                        "cmt-edas",
                                        "cmt-ekaw",
                                        "conference-confOf",
                                        "conference-edas",
                                        "conference-ekaw",
                                        "confOf-sigkdd",
                                        "edas-sigkdd",
                                        "ekaw-sigkdd"
                                )))))
                )),
                merger),
                0.9968));


        //TENTH
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("confOf-ekaw"),
                                ontologyProvider.getOntology("cmt-conference"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "conference-confOf",
                                        "conference-ekaw",
                                        "cmt-confOf",
                                        "cmt-ekaw"
                                )))))
                )),
                merger),
                0.9969));

        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("edas-sigkdd"),
                                ontologyProvider.getOntology("iasted"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "edas-iasted",
                                        "iasted-sigkdd"
                                )))))
                )),
                merger),
                0.9969));


//       //THIRD LEVEL
//
       // NINTH
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("confOf_edas_ekaw-cmt_conference_sigkdd"),
                                ontologyProvider.getOntology("iasted"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "cmt-iasted",
                                        "conference-iasted",
                                        "confOf-iasted",
                                        "edas-iasted",
                                        "ekaw-iasted",
                                        "iasted-sigkdd"
                                )))))
                )),
                merger),
                0.9969));
        //TENTH
        groups.add(new Pair<>(new MergingGroup(
                new ArrayList<MergingSample>(Arrays.asList(
                        new MergingSample(
                                ontologyProvider.getOntology("confOf_ekaw-cmt_conference"),
                                ontologyProvider.getOntology("edas_sigkdd-iasted"),
                                mappingProvider.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                        "cmt-ekaw",
                                        "confOf-ekaw",
                                        "edas-ekaw",
                                        "ekaw-iasted",
                                        "cmt-conference",
                                        "conference-confOf",
                                        "conference-edas",
                                        "conference-iasted",
                                        "cmt-sigkdd",
                                        "confOf-sigkdd",
                                        "edas-sigkdd",
                                        "iasted-sigkdd"
                                )))))
                )),
                merger),
                0.9969));



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
