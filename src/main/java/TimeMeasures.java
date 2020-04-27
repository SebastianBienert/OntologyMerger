import java.util.ArrayList;

public class TimeMeasures {
    private ArrayList<Long> _measures;
    private double _mean;
    private Long _min;
    private double _max;
    private double _variance;
    private double _standardDeviation;

    public TimeMeasures(ArrayList<Long> measures){
        _measures = measures;
        _mean = ((double)_measures.stream().reduce(0L, (acc, elem) -> acc + elem)) / _measures.size();
        _min = _measures.stream().min(Long::compare).orElse(0L);
        _max = _measures.stream().max(Long::compare).orElse(0L);
        _variance = measures.stream()
                .map(i -> i - _mean)
                .map(i -> i*i)
                .mapToDouble(i -> i).average().orElse(0.0);
        _standardDeviation = Math.sqrt(_variance);
    }

    public ArrayList<Long> get_measures() {
        return _measures;
    }

    public double get_mean() {
        return _mean;
    }

    public Long get_min() {
        return _min;
    }

    public double get_max() {
        return _max;
    }

    public double get_variance() {
        return _variance;
    }

    public double get_standardDeviation() {
        return _standardDeviation;
    }

    @Override
    public String toString() {
        return "TimeMeasures{" +
                ", mean=" + _mean +
                ", min=" + _min +
                ", max=" + _max +
                ", variance=" + _variance +
                ", std=" + _standardDeviation +
                '}';
    }
}
