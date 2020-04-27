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
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MergingBenchmark {
    @State(Scope.Thread)
    public static class MyState {
        public OAEIOntologyMerger merger;
        public OntologyProvider ontologies;
        public MappingProvider mappings;
        public HashMap<String, MergingSample> samples;

        @Param({"cmt-conference"})
        public String mergingSampleKey;

        @Setup
        public void init() throws Exception {
            merger = new OAEIOntologyMerger();
            ontologies = OntologyProvider.FromFiles();
            mappings = MappingProvider.FromFiles();
            samples = new HashMap<>();
            samples.putIfAbsent(
                    "cmt-conference",
                    new MergingSample(
                            ontologies.getOntology("cmt"),
                            ontologies.getOntology("conference"),
                            mappings.getMapping("cmt-conference")));
        }

        @Benchmark
        @BenchmarkMode(Mode.SampleTime)
        @OutputTimeUnit(TimeUnit.NANOSECONDS)
        public OWLOntology testMethod(MyState state) throws Exception {
            MergingSample sample = samples.get(state.mergingSampleKey);
            OWLOntology mergedOntology = state.merger.Merge(
                    sample.firstOntology,
                    sample.secondOntology,
                    sample.mapping);

            return mergedOntology;
        }

//    @Benchmark
//    @BenchmarkMode(Mode.SampleTime)
//    @OutputTimeUnit(TimeUnit.NANOSECONDS)
//    public OWLOntology test2(MyState state) throws OWLOntologyCreationException {
//        OWLOntology mergedOntology = state.merger.Merge(
//                state.cmtOntology,
//                state.conferenceOntology,
//                state.cmtConferenceMapping);
//
//        return mergedOntology;
//    }
    }
}