package eu.carayon.myInvestments.bll;

import java.util.ArrayList;
import java.util.List;

import eu.carayon.myInvestments.bo.Scpi;
import eu.carayon.myInvestments.dal.ScpiDao;
import eu.carayon.myInvestments.dal.impl.ScpiDaoJson;

public class ScpiManager {
    private static ScpiManager instance;
    private List<Scpi> mesScpi = new ArrayList<>();
    private ScpiDao scpiDao = new ScpiDaoJson();

    private ScpiManager() {
        super();
    }
    
    public static ScpiManager getInstance() {
        if (instance == null) instance = new ScpiManager();
        return instance;
    }

    public List<Scpi> getAllScpis() {
        return mesScpi;
    }

    public void addScpi(Scpi scpi) {
        mesScpi.add(scpi);
    };

    public void save() {
        try {
            scpiDao.save(mesScpi);
        } catch (Exception e) {
            System.out.println("Erreur d'enregistrement des données.");
        }
    }

    public void load() {
        try {
            mesScpi = scpiDao.load();
        } catch (Exception e) {
            System.out.println("Erreur de chargement des données.");
        }
    }

    public  Scpi get(int index) {
        return mesScpi.get(index);
    }

    public  int getTotalInvested() {
        int total = 0;
        for (Scpi scpi : mesScpi) {
            total += scpi.getInvested();
        }
        return total;
    }

    public  int getAverageMonthlyTotalDistribution() {
        int answer = 0;
        for (Scpi scpi : mesScpi) {
            answer += scpi.getAverageMonthlyDistribution();
        }
        return answer;
    }

    public  float getTotalYearlyYield() {
        return getTotalYearlyYield(false);
    }
    public  float getTotalYearlyYield(boolean includeDelay) {
        float total = 0;
        for (Scpi scpi : mesScpi) {
            total += scpi.getYearlyYield(includeDelay) * scpi.getInvested() / getTotalInvested();
        }
        return total;
    }

    public  float getTotalAverageYearlyYield() {
        float total = 0;
        for (Scpi scpi : mesScpi) {
            total += scpi.getYearlyYield(false);
        }
        return total / mesScpi.size();
    }

    public  float getTotalRealYearlyYield() {
        float total = 0;
        for (Scpi scpi : mesScpi) {
            total += scpi.getRealYearlyYield() * scpi.getInvested() / getTotalInvested();
        }
        return total;
    }
}
