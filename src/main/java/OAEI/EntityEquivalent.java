package OAEI;

public class EntityEquivalent{
    private String entityName;
    private double certainty;
    private double distance;

    public EntityEquivalent(String entityURL, double certainty) {
        this.entityName = entityURL.substring(7);
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
