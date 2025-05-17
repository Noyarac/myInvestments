package eu.carayon.myInvestments.bo;

import java.time.LocalDate;

public class Event {
    private int id = -1;
    private LocalDate date;
    private int amount;
    private EventType type;
    public Event() {
    }
    public Event(int id, LocalDate date, int amount, EventType type) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.type = type;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
    public EventType getType() {
        return type;
    }
    public void setType(EventType type) {
        this.type = type;
    }
    @Override
    public String toString() {
        return "Event [id=" + id + ", date=" + date + ", amount=" + amount + ", type=" + type + "]";
    }
}
