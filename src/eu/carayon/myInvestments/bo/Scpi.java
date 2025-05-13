package eu.carayon.myInvestments.bo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Scpi {
    private static final long DAY_WORTH = 1000L * 60 * 60 * 24;
    private static final long MONTH_WORTH = DAY_WORTH * 30;

    protected String nom;
    protected String organisme;
    protected byte frequency;
    protected byte delay;
    protected List<DatedAmount> movements = new ArrayList<DatedAmount>();
    protected List<DatedAmount> distributions = new ArrayList<DatedAmount>();
    protected List<DatedAmount> valuations = new ArrayList<DatedAmount>();
    
    public Scpi(String organisme, String nom, byte frequency, byte delay) {
        this.nom = nom;
        this.organisme = organisme;
        this.frequency = frequency;
        this.delay = delay;
    }

    public Scpi clone() {
        Scpi scpi = new Scpi(this.organisme, this.nom, this.frequency, this.delay);
        for (String type : List.of("mouvements", "distributions", "valorisations")) {
            for (DatedAmount da : (type.equals("mouvements") ? this.movements : type.equals("distributions") ? this.distributions : this.valuations)) {
                scpi.addToType(type, da);
            }            
        }
        return scpi;
    }

    public String getName() {
        return this.nom;
    }
    
    public String getOrganism() {
        return this.organisme;
    }
    
    public void addToType(String type, DatedAmount da) {
        try {
            switch (type) {
                case "mouvements":
                    movements.add(da);
                    break;
            
                case "distributions":
                    distributions.add(da);
                    break;
            
                case "valorisations":
                    valuations.add(da);
                    break;
                default:
                    throw new Exception("Le type n'existe pas.");
            }
        } catch (Exception e) {
            System.out.println("Problème");;
        }
    }

    public void removeFromType(String type, byte index) {
        try {
            switch (type) {
                case "mouvements":
                    movements.remove(index);
                    break;
            
                case "distributions":
                    distributions.remove(index);
                    break;
            
                case "valorisations":
                    valuations.remove(index);
                    break;
                default:
                    throw new Exception("Le type n'existe pas.");
            }
        } catch (Exception e) {
            System.out.println("Problème");;
        }
    }

    public List<DatedAmount> getFromType(String type) {
        try {
            switch (type) {
                case "mouvements":
                    return this.movements;
            
                case "distributions":
                    return this.distributions;
            
                case "valorisations":
                    return this.valuations;
                default:
                    throw new Exception("Le type n'existe pas.");
            }
        } catch (Exception e) {
            System.out.println("Problème");;
            return new ArrayList<DatedAmount>();
        }
    }

    public int getInvested() {
        int total = 0;
        for (DatedAmount da : movements) {
            total += da.amount;
        }
        return total;
    }

    private int getTotalDistribution() {
        int totalDistribution = 0;
        for (DatedAmount distribution : this.distributions) {
            totalDistribution += distribution.amount;
        }
        return totalDistribution;
    }

    public int getAverageMonthlyDistribution() {
        int totalDistribution = this.getTotalDistribution();
        if  (totalDistribution == 0) return 0;
        return totalDistribution / this.distributions.size() / this.frequency;
    }    

    private long getInvestedTimesTime(boolean includeDelay) {
        long moneyTime = 0;
        for (DatedAmount movement : this.movements) {
            long amount = movement.amount;
            long today = (new Date()).getTime() / DAY_WORTH;
            long movDay = movement.date.getTime() / DAY_WORTH + (includeDelay ? 0 : 30 * this.delay);
            moneyTime += amount * (today - movDay);
        }
        return moneyTime / 365;
    }

    public float getYearlyYield() {
        return this.getYearlyYield(false);
    }
    public float getYearlyYield(boolean includeDelay) {
        return ((float) this.getTotalDistribution()) / this.getInvestedTimesTime(includeDelay);
    }

    public float getRealYearlyYield() {
        return (float) (this.getMostRecentValuation().amount - this.getInvested() + this.getTotalDistribution()) / this.getInvestedTimesTime(true);
    }

    private DatedAmount getMostRecentValuation() {
        DatedAmount mostRecentValuation = new DatedAmount(new Date(0), 0);
        for (DatedAmount da : this.valuations) {
            if (da.date.getTime() > mostRecentValuation.date.getTime() && da.date.getTime() <= (new Date()).getTime()) mostRecentValuation = da.clone();
        }
        return mostRecentValuation;
    }

    private DatedAmount getLatestValuation() {
        DatedAmount latestValuation = new DatedAmount(new Date(0), 0);
        for (DatedAmount da : this.valuations) {
            if (da.date.getTime() > latestValuation.date.getTime()) latestValuation = da.clone();
        }
        return latestValuation;
    }


    public int getBreakEven() {
        int amd = this.getAverageMonthlyDistribution();
        if (amd == 0) {
            return (int) ((new Date()).getTime() / MONTH_WORTH - this.getLatestValuation().date.getTime() / MONTH_WORTH);
        };
        return (this.getMostRecentValuation().amount - this.getInvested() + this.getTotalDistribution()) / amd;
    }
}
