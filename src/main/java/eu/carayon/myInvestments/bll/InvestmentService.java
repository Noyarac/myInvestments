package eu.carayon.myInvestments.bll;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import eu.carayon.myInvestments.bo.Event;
import eu.carayon.myInvestments.bo.EventType;
import eu.carayon.myInvestments.bo.Investment;
import eu.carayon.myInvestments.dal.DalException;
import eu.carayon.myInvestments.dal.DaoFactory;
import eu.carayon.myInvestments.dal.InvestmentDao;

public class InvestmentService implements Comparable<InvestmentService> {
    private static final InvestmentDao dao = (InvestmentDao) DaoFactory.getDao(Investment.class);
    private Investment investment;

    public InvestmentService(Investment investment) {
        this.investment = investment;
    }

    public Investment getInvestment() {
        return investment;
    }

    public void setInvestment(Investment investment) {
        this.investment = investment;
    }

    public void load() throws DalException {
        investment = dao.load(investment.getOrganism() + investment.getName());
        for (int i = 0; i < investment.getEvents().size(); i++) {
            EventService eventService = new EventService(investment.getEvents().get(i));
            eventService.load();
            investment.getEvents().set(i, eventService.getEvent());
        }
    }

    public void save() throws DalException {
        dao.save(investment);
        for (Event event : investment.getEvents()) {
            EventService es = new EventService(event);
            es.save();
        }
    }
    public void addEvent(Event event) {
        investment.getEvents().add(event);
    }

    public void removeEvent(Event event) {
        investment.getEvents().remove(event);
    }

    @Override
    public int compareTo(InvestmentService o) {
        return getInvested() - o.getInvested();
    }

    public List<Event> getEventsFromType(EventType type) {
        return investment.getEvents().stream().filter(e -> e.getType() == type).collect(Collectors.toList());
    }

    public int getInvested() {
        return getEventsFromType(EventType.MOVEMENT).stream().mapToInt(Event::getAmount).sum();
    }

    private int getTotalDistribution() {
        return getEventsFromType(EventType.DISTRIBUTION).stream().mapToInt(Event::getAmount).sum();
    }

    public int getAverageMonthlyDistribution() {
        int totalDistribution = this.getTotalDistribution();
        if  (totalDistribution == 0) return 0;
        return totalDistribution / getEventsFromType(EventType.DISTRIBUTION).size() / investment.getFrequency();
    }    

    private long getInvestedTimesTime(boolean includeDelay) {
        return getEventsFromType(EventType.MOVEMENT).stream().mapToLong(e -> (long) e.getAmount() * ChronoUnit.DAYS.between(e.getDate().plusDays((includeDelay ? 0 : 30 * investment.getDelay())), LocalDate.now())).sum() / 365;
    }

    public float getYearlyYield() {
        return this.getYearlyYield(false);
    }
    public float getYearlyYield(boolean includeDelay) {
        return ((float) this.getTotalDistribution()) / this.getInvestedTimesTime(includeDelay);
    }

    public float getRealYearlyYield() {
        if (getFirstMovement() == null) return 0;
        return (float) (this.getMostRecentValuation().getAmount() - this.getInvested() + this.getTotalDistribution()) / this.getInvestedTimesTime(true);
    }

    private Event getMostRecentValuation() {
        return getEventsFromType(EventType.VALUATION).stream().filter(e -> e.getDate().compareTo(LocalDate.now()) < 0).max(Comparator.comparing(e -> e.getDate())).orElse(new Event(99,LocalDate.of(2000, 1, 1), 0, EventType.VALUATION));
    }

    private Event getFirstMovement() {
        return getEventsFromType(EventType.MOVEMENT).stream().min(Comparator.comparing(Event::getDate)).orElse(null);
    }

    public int getBreakEven() {
        if (getFirstMovement() == null) return 0;
        int amd = this.getAverageMonthlyDistribution();
        if (amd == 0) {
            return (int) (ChronoUnit.MONTHS.between(getFirstMovement().getDate().plusMonths(investment.getDelay()), LocalDate.now()));
        };
        return (this.getMostRecentValuation().getAmount() - this.getInvested() + this.getTotalDistribution()) / amd;
    }
}







// package eu.carayon.myInvestments.dll;

// import java.util.ArrayList;
// import java.util.List;

// import eu.carayon.myInvestments.bo.EventType;
// import eu.carayon.myInvestments.bo.Investment;
// import eu.carayon.myInvestments.dal.DaoFactory;
// import eu.carayon.myInvestments.dal.DaoProvider;
// import eu.carayon.myInvestments.dal.InvestmentDao;

// public class InvestmentManager {
//     private static InvestmentManager instance;
//     private List<Investment> investments = new ArrayList<>();
//     private InvestmentDao dao = (InvestmentDao) DaoFactory.getDao(Investment.class, DaoProvider.Json);

//     private InvestmentManager() {
//         super();
//     }
    
//     public static InvestmentManager getInstance() {
//         if (instance == null) instance = new InvestmentManager();
//         return instance;
//     }

//     public List<Investment> getAllScpis() {
//         return investments;
//     }

//     public void addScpi(Investment scpi) {
//         investments.add(scpi);
//     };

//     public void save() {
//         try {
//             dao.save(investments);
//         } catch (Exception e) {
//             System.out.println("Erreur d'enregistrement des données.");
//         }
//     }

//     public void load() {
//         try {
//             investments = dao.load();
//         } catch (Exception e) {
//             System.out.println("Erreur de chargement des données.");
//         }
//     }

//     public  Investment get(int index) {
//         return investments.get(index);
//     }

//     public  int getTotalInvested() {
//         int total = 0;
//         for (Investment scpi : investments) {
//             total += scpi.getInvested();
//         }
//         return total;
//     }

//     public  int getAverageMonthlyTotalDistribution() {
//         int answer = 0;
//         for (Investment scpi : investments) {
//             answer += scpi.getAverageMonthlyDistribution();
//         }
//         return answer;
//     }

//     public  float getTotalYearlyYield() {
//         return getTotalYearlyYield(false);
//     }
//     public  float getTotalYearlyYield(boolean includeDelay) {
//         float total = 0;
//         for (Investment scpi : investments) {
//             total += scpi.getYearlyYield(includeDelay) * scpi.getInvested() / getTotalInvested();
//         }
//         return total;
//     }

//     public  float getTotalAverageYearlyYield() {
//         float total = 0;
//         for (Investment scpi : investments) {
//             total += scpi.getYearlyYield(false);
//         }
//         return total / investments.size();
//     }

//     public  float getTotalRealYearlyYield() {
//         float total = 0;
//         for (Investment scpi : investments) {
//             total += scpi.getRealYearlyYield() * scpi.getInvested() / getTotalInvested();
//         }
//         return total;
//     }
// }
