package eu.carayon.myInvestments.dal;

import eu.carayon.myInvestments.bo.Event;

public interface EventDao extends Dao<Event>{
    int getNextid() throws DalException;
}
