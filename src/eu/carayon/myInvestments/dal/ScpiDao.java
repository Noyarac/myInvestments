package eu.carayon.myInvestments.dal;

import java.util.ArrayList;
import java.util.List;

import eu.carayon.myInvestments.bo.Scpi;

public interface ScpiDao {
    void save(List<Scpi> arrayList) throws Exception;
    ArrayList<Scpi> load() throws Exception;
}
