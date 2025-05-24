package eu.carayon.myInvestments.bll;

import java.util.List;
import java.util.stream.Collectors;

import eu.carayon.myInvestments.bo.Investment;
import eu.carayon.myInvestments.bo.Portfolio;
import eu.carayon.myInvestments.dal.DalException;
import eu.carayon.myInvestments.dal.DaoFactory;
import eu.carayon.myInvestments.dal.PortfolioDao;

public class PortfolioService {
    private static final PortfolioDao dao = (PortfolioDao) DaoFactory.getDao(Portfolio.class);
    private Portfolio portfolio = new Portfolio();

    public PortfolioService() {
    }

    public PortfolioService(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public void addInvestment(Investment investment) {
        portfolio.getInvestments().add(investment);
    }

    public void load() throws DalException {
        Portfolio loadedPortfolio = dao.load(null);
        portfolio.setInvestments(loadedPortfolio.getInvestments());
        List<Investment> investments = portfolio.getInvestments();
        for (int i = 0; i < investments.size(); i++) {
            InvestmentService investmentService = new InvestmentService(investments.get(i));
            investmentService.load();
            investments.set(i, investmentService.getInvestment());
        }
    }

    public void save() throws DalException {
        dao.save(portfolio);
    }

    public List<InvestmentService> getInvestmentServices() {
        return portfolio.getInvestments().stream()
                .map(i -> new InvestmentService(i))
                .collect(Collectors.toList());
    }

    public InvestmentService getInvestmentService(int index) throws DalException {
        return getInvestmentServices().get(index);
    }

    public void sort() throws DalException {
        getInvestmentServices().sort(null);
    }

    public int getAverageMonthlyTotalDistribution() throws Exception {
        return (int) getInvestmentServices().stream().mapToInt(InvestmentService::getAverageMonthlyDistribution)
                .average()
                .orElseThrow(() -> new Exception("Ne peut pas calculer la moyenne totale des distributions."));
    }

    public float getTotalYearlyYield() {
        return (float) getInvestmentServices().stream()
            .mapToDouble(invSer -> (double) invSer.getYearlyYield())
            .average()
            .orElse(0);
    }

    public float getTotalRealYearlyYield() {
        return (float) getInvestmentServices().stream()
            .mapToDouble(invSer -> (double) invSer.getYearlyYield(true))
            .average()
            .orElse(0);
    }
}
