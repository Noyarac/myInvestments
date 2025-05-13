package eu.carayon.myInvestments.bo;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DatedAmount that = (DatedAmount) obj;
        return amount == that.amount && date.equals(that.date);
    }

    
}
