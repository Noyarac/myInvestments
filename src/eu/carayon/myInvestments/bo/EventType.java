package eu.carayon.myInvestments.bo;

public enum EventType {
    MOVEMENT("Mouvement"),
    DISTRIBUTION("Distribution"),
    VALUATION("Valorisation");

    public final String label;
 
    private EventType(String label) {
        this.label = label;
    }

    public static EventType valueOfLabel(String label) {
        for (EventType e : values()) {
            if (e.label.equals(label)) return e;
        }
        return null;
    }

    @Override
    public String toString() {
        return this.label;
    }

    
}
