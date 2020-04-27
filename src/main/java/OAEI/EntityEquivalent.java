package OAEI;

public class EntityEquivalent{
    private String entityName;
    private double certainty;
    private double distance;

    public EntityEquivalent(String entityName, double certainty) {
        this.entityName = entityName;
        this.certainty = certainty;
        this.distance = 1 - certainty;
    }

    public double getCertainty() {
        return certainty;
    }

    public String getEntityName() {
        return entityName;
    }

    public double getDistance() {
        return distance;
    }


}
