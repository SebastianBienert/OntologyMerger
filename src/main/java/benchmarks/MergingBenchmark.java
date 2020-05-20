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

        @Param({"conference-sigkdd", "confOf-edas",
                "conference-edas", "confOf-sigkdd",
                "conference-confOf", "edas-sigkdd",
                "sigkdd_edas-confOf",
                "conference_edas-confOf",
                "conference_sigkdd-confOf",
                "conference_sigkdd-edas",
                "conference_sigkdd-edas_confOf",
                "conference_edas-sigkdd_confOf",
                "conference_confOf-sigkdd_edas",
                "sigkdd_edas_confOf-conference",
                "conference_edas_confOf-sigkdd",
                "conference_sigkdd_confOf-edas",
                "conference_sigkdd_edas-confOf"})
        public String mergingSampleKey;

        @Setup
        public void init() throws Exception {
            merger = new OAEIOntologyMerger();
            ontologies = OntologyProvider.FromFiles();
            mappings = MappingProvider.FromFiles();
            samples = new HashMap<>();

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
                    "edas-sigkdd",
                    new MergingSample(
                            ontologies.getOntology("edas"),
                            ontologies.getOntology("sigkdd"),
                            mappings.getMappingCollection(new ArrayList(Arrays.asList("edas-sigkdd")))));
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