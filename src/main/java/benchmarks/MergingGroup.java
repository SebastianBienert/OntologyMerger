package benchmarks;

import OAEI.OAEIOntologyMerger;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class MergingGroup {
    private ArrayList<MergingSample> samples;
    private OAEIOntologyMerger merger;

    public MergingGroup(ArrayList<MergingSample> samples, OAEIOntologyMerger merger) {
        this.samples = samples;
        this.merger = merger;
    }

    public double CalculateKnowledgeIncrease(){
        double normalized = merger.NormalizeKnowledgeIncrease(
                    samples.stream()
                    .map(s -> merger.CalculateKnwoledgeIncrease(
                            s.firstOntology,
                            s.secondOntology,
                            s.mappingCollection))
                        .collect(Collectors.toList()));
        return normalized;
    }

    public ArrayList<MergingSample> getSamples() {
        return samples;
    }

}
