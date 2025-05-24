package eu.carayon.myInvestments.bo;

import java.util.ArrayList;
import java.util.List;

public class Portfolio {
    private List<Investment> investments = new ArrayList<>();

    public Portfolio(List<Investment> investments) {
        this.investments = investments;
    }

    public Portfolio() {
    }

    public List<Investment> getInvestments() {
        return investments;
    }

    public void setInvestments(List<Investment> investments) {
        this.investments = investments;
    }

    @Override
    public String toString() {
        return "Portfolio [investments=" + investments + "]";
    }
}
