package eu.carayon;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DatedAmount implements Comparable<DatedAmount> {
    public Date date;
    public int amount;

    public DatedAmount(Date date, int amount) {
        this.date = date;
        this.amount = amount;
    }

    public DatedAmount clone() {
        return new DatedAmount((Date) this.date.clone(), this.amount);
    }

    @Override
    public String toString() {
        return (new SimpleDateFormat("dd/MM/yyyy")).format(date) + " - " + String.format("%.2f€",amount / 100.0);
    }

    @Override
    public int compareTo(DatedAmount da) {
        return this.date.compareTo(da.date);
    }
}
