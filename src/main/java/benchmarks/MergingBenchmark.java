package benchmarks;

import OAEI.MappingProvider;
import OAEI.OAEIMapping;
import OAEI.OAEIOntologyMerger;
import OAEI.OntologyProvider;
import org.openjdk.jmh.annotations.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MergingBenchmark {
    @State(Scope.Thread)
    public static class MyState {
        public OAEIOntologyMerger merger;
        public OntologyProvider ontologies;
        public MappingProvider mappings;
        public HashMap<String, MergingSample> samples;
//REPRO
//        @Param({"conference-sigkdd", "confOf-edas",
//                "conference-edas", "confOf-sigkdd",
//                "conference-confOf", "edas-sigkdd",
//                "sigkdd_edas-confOf",
//                "conference_edas-confOf",
//                "conference_sigkdd-confOf",
//                "conference_sigkdd-edas",
//                "conference_sigkdd-edas_confOf",
//                "conference_edas-sigkdd_confOf",
//                "conference_confOf-sigkdd_edas",
//                "sigkdd_edas_confOf-conference",
//                "conference_edas_confOf-sigkdd",
//                "conference_sigkdd_confOf-edas",
//                "conference_sigkdd_edas-confOf"}

//SEQUENTIAL
//@Param({"conference_sigkdd_edas_confOf-cmt",
//        "conference_sigkdd_edas_confOf_cmt-ekaw",
//        "conference_sigkdd_edas_confOf_cmt_ekaw-iasted"})

//1-4
//@Param({"confOf-ekaw",
//        "confOf_ekaw-sigkdd",
//        "cmt-conference",
//        "edas-iasted",
//        "ekaw-sigkdd",
//        "conference-sigkdd",
//        "edas_iasted-cmt",
//        "edas_iasted_cmt-ekaw",
//        "edas_iasted_cmt_ekaw-confOf",
//        "confOf-edas",
//        "iasted-cmt",
//        "confOf_ekaw_sigkdd-cmt_conference",
//        "ekaw_sigkdd-confOf",
//        "cmt_conference-edas_iasted",
//        "edas_iasted_cmt_ekaw_confOf-conference_sigkdd",
//        "conference_sigkdd-confOf_edas",
//        "iasted_cmt-ekaw",
//        "confOf_ekaw_sigkdd_cmt_conference-edas_iasted",
//        "ekaw_sigkdd_confOf-cmt_conference_edas_iasted",
//        "conference_sigkdd_confOf_edas-iasted_cmt_ekaw"
//        })

//(5-8)
//@Param({
//            "cmt-confOf",
//            "edas-iasted",
//            "edas_iasted-ekaw",
//            "edas_iasted_ekaw-sigkdd",
//            "edas_iasted_ekaw_sigkdd-conference",
//            "ekaw-conference",
//            "iasted-cmt",
//            "iasted_cmt-ekaw",
//            "edas-sigkdd",
//            "edas_sigkdd-conference",
//            "edas_sigkdd_conference-confOf",
//            "conference-edas",
//            "confOf-sigkdd",
//            "cmt_confOf-edas_iasted_ekaw_sigkdd_conference",
//            "cmt_confOf-edas_iasted",
//            "ekaw-conference_sigkdd",
//            "iasted_cmt_ekaw-edas_sigkdd_conference_confOf",
//            "iasted_cmt-ekaw",
//            "conference_edas-sigkdd_confOf",
//            "cmt_confOf_edas_iasted-ekaw_conference_sigkdd",
//            "iasted_cmt_ekaw-conference_edas_sigkdd_confOf"
//        })

        //(9-12)
        @Param({
                "confOf-edas",
                "confOf_edas-ekaw",
                "cmt-conference",
                "cmt_conference-sigkdd",
                "confOf-ekaw",
                "edas-sigkdd",
                "confOf_edas_ekaw-cmt_conference_sigkdd",
                "confOf_ekaw-cmt_conference",
                "edas_sigkdd-iasted",
                "confOf_edas_ekaw_cmt_conference_sigkdd-iasted",
                "confOf_ekaw_cmt_conference-edas_sigkdd_iasted"
        })
        public String mergingSampleKey;

        @Setup
        public void init() throws Exception {
            merger = new OAEIOntologyMerger();
            ontologies = OntologyProvider.FromFiles();
            mappings = MappingProvider.FromFiles();
            samples = new HashMap<>();

            samples.putIfAbsent(
                    "confOf_ekaw_cmt_conference-edas_sigkdd_iasted",
                    new MergingSample(
                            ontologies.getOntology("confOf_ekaw-cmt_conference"),
                            ontologies.getOntology("edas_sigkdd-iasted"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
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
            );


            samples.putIfAbsent(
                    "confOf_edas_ekaw_cmt_conference_sigkdd-iasted",
                    new MergingSample(
                            ontologies.getOntology("confOf_edas_ekaw-cmt_conference_sigkdd"),
                            ontologies.getOntology("iasted"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "cmt-iasted",
                                    "conference-iasted",
                                    "confOf-iasted",
                                    "edas-iasted",
                                    "ekaw-iasted",
                                    "iasted-sigkdd"
                            )))))
            );

            samples.putIfAbsent(
                    "edas_sigkdd-iasted",
                    new MergingSample(
                            ontologies.getOntology("edas-sigkdd"),
                            ontologies.getOntology("iasted"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "edas-iasted",
                                    "iasted-sigkdd"
                            )))))
            );

            samples.putIfAbsent(
                    "confOf_ekaw-cmt_conference",
                    new MergingSample(
                            ontologies.getOntology("confOf-ekaw"),
                            ontologies.getOntology("cmt-conference"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "conference-confOf",
                                    "conference-ekaw",
                                    "cmt-confOf",
                                    "cmt-ekaw"
                            )))))
            );

            samples.putIfAbsent(
                    "confOf_edas_ekaw-cmt_conference_sigkdd",
                    new MergingSample(
                            ontologies.getOntology("confOf_edas-ekaw"),
                            ontologies.getOntology("cmt_conference-sigkdd"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                "cmt-confOf",
                                "cmt-edas",
                                "cmt-ekaw",
                                "conference-confOf",
                                "conference-edas",
                                "conference-ekaw",
                                "confOf-sigkdd",
                                "edas-sigkdd",
                                "ekaw-sigkdd"
                        ))))));

            samples.putIfAbsent(
                    "cmt-conference",
                    new MergingSample(
                            ontologies.getOntology("conference"),
                            ontologies.getOntology("cmt"),
                            mappings.getMappingCollection(new ArrayList(Arrays.asList("cmt-conference")))));
            samples.putIfAbsent(
                    "cmt-confOf",
                    new MergingSample(
                            ontologies.getOntology("cmt"),
                            ontologies.getOntology("confOf"),
                            mappings.getMappingCollection(new ArrayList(Arrays.asList("cmt-confOf")))));
            samples.putIfAbsent(
                    "ekaw-conference",
                    new MergingSample(
                            ontologies.getOntology("ekaw"),
                            ontologies.getOntology("conference"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList("conference-ekaw"))))
                    ));

            samples.putIfAbsent(
                    "conference-sigkdd",
                    new MergingSample(
                            ontologies.getOntology("conference"),
                            ontologies.getOntology("sigkdd"),
                            mappings.getMappingCollection(new ArrayList(Arrays.asList("conference-sigkdd")))));
            samples.putIfAbsent(
                    "confOf-edas",
                    new MergingSample(
                            ontologies.getOntology("confOf"),
                            ontologies.getOntology("edas"),
                            mappings.getMappingCollection(new ArrayList(Arrays.asList("confOf-edas")))));
            samples.putIfAbsent(
                    "confOf-ekaw",
                    new MergingSample(
                            ontologies.getOntology("confOf"),
                            ontologies.getOntology("ekaw"),
                            mappings.getMappingCollection(new ArrayList(Arrays.asList("confOf-ekaw")))));
            samples.putIfAbsent(
                    "conference-edas",
                    new MergingSample(
                            ontologies.getOntology("conference"),
                            ontologies.getOntology("edas"),
                            mappings.getMappingCollection(new ArrayList(Arrays.asList("conference-edas")))));
            samples.putIfAbsent(
                    "confOf-sigkdd",
                    new MergingSample(
                            ontologies.getOntology("confOf"),
                            ontologies.getOntology("sigkdd"),
                            mappings.getMappingCollection(new ArrayList(Arrays.asList("confOf-sigkdd")))));
            samples.putIfAbsent(
                    "conference-confOf",
                    new MergingSample(
                            ontologies.getOntology("conference"),
                            ontologies.getOntology("confOf"),
                            mappings.getMappingCollection(new ArrayList(Arrays.asList("conference-confOf")))));
            samples.putIfAbsent(
                    "conference-sigkdd",
                    new MergingSample(
                            ontologies.getOntology("conference"),
                            ontologies.getOntology("sigkdd"),
                            mappings.getMappingCollection(new ArrayList(Arrays.asList("conference-sigkdd")))));
            samples.putIfAbsent(
                    "edas-sigkdd",
                    new MergingSample(
                            ontologies.getOntology("edas"),
                            ontologies.getOntology("sigkdd"),
                            mappings.getMappingCollection(new ArrayList(Arrays.asList("edas-sigkdd")))));
            samples.putIfAbsent(
                    "edas-iasted",
                    new MergingSample(
                            ontologies.getOntology("edas"),
                            ontologies.getOntology("iasted"),
                            mappings.getMappingCollection(new ArrayList(Arrays.asList("edas-iasted")))));
            samples.putIfAbsent(
                    "ekaw-sigkdd",
                    new MergingSample(
                            ontologies.getOntology("ekaw"),
                            ontologies.getOntology("sigkdd"),
                            mappings.getMappingCollection(new ArrayList(Arrays.asList("ekaw-sigkdd")))));
            samples.putIfAbsent(
                    "iasted-cmt",
                    new MergingSample(
                            ontologies.getOntology("iasted"),
                            ontologies.getOntology("cmt"),
                            mappings.getMappingCollection(new ArrayList(Arrays.asList("cmt-iasted")))));
            samples.putIfAbsent(
                    "cmt_conference-sigkdd",
                    new MergingSample(
                            ontologies.getOntology("cmt-conference"),
                            ontologies.getOntology("sigkdd"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "cmt-sigkdd",
                                    "conference-sigkdd"))))
                    ));

            samples.putIfAbsent(
                    "confOf_edas-ekaw",
                    new MergingSample(
                            ontologies.getOntology("confOf-edas"),
                            ontologies.getOntology("ekaw"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "confOf-ekaw",
                                    "edas-ekaw"))))
                    ));

            samples.putIfAbsent(
                    "edas_iasted-cmt",
                    new MergingSample(
                            ontologies.getOntology("edas-iasted"),
                            ontologies.getOntology("cmt"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "cmt-edas",
                                    "cmt-iasted"))))
                    ));
            samples.putIfAbsent(
                    "edas_iasted-ekaw",
                    new MergingSample(
                            ontologies.getOntology("edas-iasted"),
                            ontologies.getOntology("ekaw"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "edas-ekaw",
                                    "ekaw-iasted"))))
                    ));
            samples.putIfAbsent(
                    "edas_iasted_cmt-ekaw",
                    new MergingSample(
                            ontologies.getOntology("edas_iasted-cmt"),
                            ontologies.getOntology("ekaw"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "cmt-ekaw",
                                    "edas-ekaw",
                                    "ekaw-iasted"))))
                    ));
            samples.putIfAbsent(
                    "edas_iasted_ekaw-sigkdd",
                    new MergingSample(
                            ontologies.getOntology("edas_iasted-ekaw"),
                            ontologies.getOntology("sigkdd"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "edas-sigkdd",
                                    "iasted-sigkdd",
                                    "ekaw-sigkdd"
                            ))))
                    ));
            samples.putIfAbsent(
                    "edas_iasted_ekaw_sigkdd-conference",
                    new MergingSample(
                            ontologies.getOntology("edas_iasted_ekaw-sigkdd"),
                            ontologies.getOntology("conference"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "conference-sigkdd",
                                    "conference-iasted",
                                    "conference-edas",
                                    "conference-ekaw"
                            ))))
                    ));
            samples.putIfAbsent(
                    "edas_iasted_cmt_ekaw-confOf",
                    new MergingSample(
                            ontologies.getOntology("edas_iasted_cmt-ekaw"),
                            ontologies.getOntology("confOf"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "cmt-confOf",
                                    "confOf-edas",
                                    "confOf-ekaw",
                                    "confOf-iasted"))))
                    ));
            samples.putIfAbsent(
                    "confOf_ekaw-sigkdd",
                    new MergingSample(
                            ontologies.getOntology("confOf-ekaw"),
                            ontologies.getOntology("sigkdd"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "confOf-sigkdd",
                                    "ekaw-sigkdd"))))
                    ));


            samples.putIfAbsent(
                    "edas_sigkdd-conference",
                    new MergingSample(
                            ontologies.getOntology("edas-sigkdd"),
                            ontologies.getOntology("conference"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "conference-edas",
                                    "conference-sigkdd"))))
                    ));
            samples.putIfAbsent(
                    "edas_sigkdd_conference-confOf",
                    new MergingSample(
                            ontologies.getOntology("edas_sigkdd-conference"),
                            ontologies.getOntology("confOf"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "conference-confOf",
                                    "confOf-edas",
                                    "confOf-sigkdd"))))
                    ));
            samples.putIfAbsent(
                    "sigkdd_edas-confOf",
                    new MergingSample(
                            ontologies.getOntology("sigkdd-edas"),
                            ontologies.getOntology("confOf"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "confOf-edas",
                                    "confOf-sigkdd"))))
                    ));
            samples.putIfAbsent(
                    "conference_edas-confOf",
                    new MergingSample(
                            ontologies.getOntology("conference-edas"),
                            ontologies.getOntology("confOf"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "conference-confOf",
                                    "confOf-edas"))))
                    ));
            samples.putIfAbsent(
                    "conference_sigkdd-confOf",
                    new MergingSample(
                            ontologies.getOntology("conference-sigkdd"),
                            ontologies.getOntology("confOf"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "conference-confOf",
                                    "confOf-sigkdd"))))
                    ));
            samples.putIfAbsent(
                    "conference_sigkdd-edas",
                    new MergingSample(
                            ontologies.getOntology("conference-sigkdd"),
                            ontologies.getOntology("edas"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "conference-edas",
                                    "edas-sigkdd"))))
                    ));
            samples.putIfAbsent(
                    "conference_sigkdd-edas_confOf",
                    new MergingSample(
                            ontologies.getOntology("conference-sigkdd"),
                            ontologies.getOntology("edas-confOf"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "conference-edas",
                                    "conference-confOf",
                                    "confOf-sigkdd",
                                    "edas-sigkdd"
                    ))))));
            samples.putIfAbsent(
                    "conference_edas-sigkdd_confOf",
                    new MergingSample(
                            ontologies.getOntology("conference-edas"),
                            ontologies.getOntology("sigkdd-confOf"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "conference-edas",
                                    "conference-confOf",
                                    "confOf-sigkdd",
                                    "edas-sigkdd"
                            ))))));
            samples.putIfAbsent(
                    "conference_confOf-sigkdd_edas",
                    new MergingSample(
                            ontologies.getOntology("conference-confOf"),
                            ontologies.getOntology("sigkdd-edas"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "conference-edas",
                                    "conference-sigkdd",
                                    "confOf-sigkdd",
                                    "confOf-edas"
                            ))))));

            samples.putIfAbsent(
                    "sigkdd_edas_confOf-conference",
                    new MergingSample(
                            ontologies.getOntology("sigkdd_edas_confOf"),
                            ontologies.getOntology("conference"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "conference-sigkdd",
                                    "conference-confOf",
                                    "conference-edas"
                    ))))));
            samples.putIfAbsent(
                    "conference_edas_confOf-sigkdd",
                    new MergingSample(
                            ontologies.getOntology("conference_edas_confOf"),
                            ontologies.getOntology("sigkdd"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "conference-sigkdd",
                                    "confOf-sigkdd",
                                    "edas-sigkdd"
                    ))))));
            samples.putIfAbsent(
                    "conference_sigkdd_confOf-edas",
                    new MergingSample(
                            ontologies.getOntology("conference_sigkdd_confOf"),
                            ontologies.getOntology("edas"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "edas-sigkdd",
                                    "confOf-edas",
                                    "conference-edas"
                    ))))));
            samples.putIfAbsent(
                    "conference_sigkdd_edas-confOf",
                    new MergingSample(
                            ontologies.getOntology("conference_sigkdd_edas"),
                            ontologies.getOntology("confOf"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "confOf-edas",
                                    "confOf-sigkdd",
                                    "conference-confOf"
                            ))))));
            samples.putIfAbsent(
                    "conference_sigkdd_edas_confOf-cmt",
                    new MergingSample(
                            ontologies.getOntology("conference_sigkdd_edas-confOf"),
                            ontologies.getOntology("cmt"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "cmt-conference",
                                    "cmt-confOf",
                                    "cmt-sigkdd",
                                    "cmt-edas"
                            )))))
            );
            samples.putIfAbsent(
                    "conference_sigkdd_edas_confOf_cmt-ekaw",
                    new MergingSample(
                            ontologies.getOntology("conference_sigkdd_edas_confOf-cmt"),
                            ontologies.getOntology("ekaw"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "cmt-ekaw",
                                    "conference-ekaw",
                                    "confOf-ekaw",
                                    "edas-ekaw",
                                    "ekaw-sigkdd"
                            )))))
            );
            samples.putIfAbsent(
                    "conference_sigkdd_edas_confOf_cmt_ekaw-iasted",
                    new MergingSample(
                            ontologies.getOntology("conference_sigkdd_edas_confOf_cmt-ekaw"),
                            ontologies.getOntology("iasted"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "cmt-iasted",
                                    "conference-iasted",
                                    "confOf-iasted",
                                    "edas-iasted",
                                    "ekaw-iasted",
                                    "iasted-sigkdd"
                            )))))
            );
            samples.putIfAbsent(
                    "confOf_ekaw_sigkdd-cmt_conference",
                    new MergingSample(
                            ontologies.getOntology("confOf_ekaw-sigkdd"),
                            ontologies.getOntology("cmt-conference"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "conference-confOf",
                                    "conference-ekaw",
                                    "conference-sigkdd",
                                    "cmt-sigkdd",
                                    "cmt-ekaw",
                                    "cmt-confOf"
                            )))))
            );
            samples.putIfAbsent(
                    "ekaw_sigkdd-confOf",
                    new MergingSample(
                            ontologies.getOntology("ekaw-sigkdd"),
                            ontologies.getOntology("confOf"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "confOf-ekaw",
                                    "confOf-sigkdd"
                            )))))
            );
            samples.putIfAbsent(
                    "cmt_conference-edas_iasted",
                    new MergingSample(
                            ontologies.getOntology("cmt-conference"),
                            ontologies.getOntology("edas-iasted"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "cmt-iasted",
                                    "cmt-edas",
                                    "conference-edas",
                                    "conference-iasted"
                            )))))
            );
            samples.putIfAbsent(
                    "edas_iasted_cmt_ekaw_confOf-conference_sigkdd",
                    new MergingSample(
                            ontologies.getOntology("edas_iasted_cmt_ekaw-confOf"),
                            ontologies.getOntology("conference-sigkdd"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
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
            );
            samples.putIfAbsent(
                    "conference_sigkdd-confOf_edas",
                    new MergingSample(
                            ontologies.getOntology("conference-sigkdd"),
                            ontologies.getOntology("confOf-edas"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "edas-sigkdd",
                                    "confOf-sigkdd",
                                    "conference-edas",
                                    "conference-confOf"
                            )))))
            );
            samples.putIfAbsent(
                    "iasted_cmt-ekaw",
                    new MergingSample(
                            ontologies.getOntology("iasted-cmt"),
                            ontologies.getOntology("ekaw"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "cmt-ekaw",
                                    "cmt-iasted"
                            )))))
            );
            samples.putIfAbsent(
                    "confOf_ekaw_sigkdd_cmt_conference-edas_iasted",
                    new MergingSample(
                            ontologies.getOntology("confOf_ekaw_sigkdd-cmt_conference"),
                            ontologies.getOntology("edas-iasted"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
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
            );
            samples.putIfAbsent(
                    "ekaw_sigkdd_confOf-cmt_conference_edas_iasted",
                    new MergingSample(
                            ontologies.getOntology("ekaw_sigkdd-confOf"),
                            ontologies.getOntology("cmt_conference-edas_iasted"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
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
            );
            samples.putIfAbsent(
                    "conference_sigkdd_confOf_edas-iasted_cmt_ekaw",
                    new MergingSample(
                            ontologies.getOntology("conference_sigkdd-confOf_edas"),
                            ontologies.getOntology("iasted_cmt-ekaw"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
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
            );
            samples.putIfAbsent(
                    "cmt_confOf-edas_iasted_ekaw_sigkdd_conference",
                    new MergingSample(
                            ontologies.getOntology("cmt-confOf"),
                            ontologies.getOntology("edas_iasted_ekaw_sigkdd-conference"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
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
            );

            samples.putIfAbsent(
                    "cmt_confOf-edas_iasted",
                    new MergingSample(
                            ontologies.getOntology("cmt-confOf"),
                            ontologies.getOntology("edas-iasted"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "confOf-edas",
                                    "confOf-iasted",
                                    "cmt-edas",
                                    "cmt-iasted"
                            )))))
            );

            samples.putIfAbsent(
                    "ekaw-conference_sigkdd",
                    new MergingSample(
                            ontologies.getOntology("ekaw-conference"),
                            ontologies.getOntology("sigkdd"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "ekaw-sigkdd",
                                    "conference-sigkdd"
                            )))))
            );

            samples.putIfAbsent(
                    "iasted_cmt_ekaw-edas_sigkdd_conference_confOf",
                    new MergingSample(
                            ontologies.getOntology("iasted_cmt-ekaw"),
                            ontologies.getOntology("edas_sigkdd_conference-confOf"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
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
            );

            samples.putIfAbsent(
                    "iasted_cmt-ekaw",
                    new MergingSample(
                            ontologies.getOntology("iasted-cmt"),
                            ontologies.getOntology("ekaw"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "ekaw-iasted",
                                    "cmt-ekaw"
                            )))))
            );

            samples.putIfAbsent(
                    "conference_edas-sigkdd_confOf",
                    new MergingSample(
                            ontologies.getOntology("conference-edas"),
                            ontologies.getOntology("sigkdd-confOf"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
                                    "conference-confOf",
                                    "conference-sigkdd",
                                    "edas-sigkdd",
                                    "confOf-edas"
                            )))))
            );

            samples.putIfAbsent(
                    "cmt_confOf_edas_iasted-ekaw_conference_sigkdd",
                    new MergingSample(
                            ontologies.getOntology("cmt_confOf-edas_iasted"),
                            ontologies.getOntology("ekaw_conference-sigkdd"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
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
            );

            samples.putIfAbsent(
                    "iasted_cmt_ekaw-conference_edas_sigkdd_confOf",
                    new MergingSample(
                            ontologies.getOntology("iasted_cmt-ekaw"),
                            ontologies.getOntology("conference_edas-sigkdd_confOf"),
                            mappings.getMappingCollection(new ArrayList<String>((Arrays.asList(
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
            );

        }

        @Benchmark
        @BenchmarkMode(Mode.AverageTime)
        @Warmup(iterations = 3, time = 1)
        @Measurement(iterations = 5, time = 1)
        @OutputTimeUnit(TimeUnit.MILLISECONDS)
        public OWLOntology Reproduction(MyState state) throws Exception {
            MergingSample sample = samples.get(state.mergingSampleKey);
            OWLOntology mergedOntology = state.merger.Merge(
                    sample.firstOntology,
                    sample.secondOntology,
                    sample.mappingCollection);

            return mergedOntology;
        }
    }
}