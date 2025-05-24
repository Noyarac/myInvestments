package eu.carayon.myInvestments.bo;

import java.util.ArrayList;
import java.util.List;

public class Investment {
    protected String name;
    protected String organism;
    protected int frequency;
    protected int delay;
    protected List<Event> events = new ArrayList<>();
    public Investment() {
    }
    public Investment(String name, String organism, int frequency, int delay, List<Event> events) {
        this.name = name;
        this.organism = organism;
        this.frequency = frequency;
        this.delay = delay;
        if (events != null) this.events = events;
    }
    public String getName() {
        return name;
    }
    public void setName(String nom) {
        this.name = nom;
    }
    public String getOrganism() {
        return organism;
    }
    public void setOrganism(String organisme) {
        this.organism = organisme;
    }
    public int getFrequency() {
        return frequency;
    }
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
    public int getDelay() {
        return delay;
    }
    public void setDelay(int delay) {
        this.delay = delay;
    }
    public List<Event> getEvents() {
        return events;
    }
    public void setEvents(List<Event> events) {
        this.events = events;
    }
    @Override
    public String toString() {
        return "Investment [nom=" + name + ", organisme=" + organism + ", frequency=" + frequency + ", delay=" + delay
                + ", events=" + events + "]";
    }
}
