package eu.carayon.myInvestments.bll;

import eu.carayon.myInvestments.bo.Event;
import eu.carayon.myInvestments.dal.DalException;
import eu.carayon.myInvestments.dal.DaoFactory;
import eu.carayon.myInvestments.dal.EventDao;

public class EventService implements Comparable<EventService> {
    private static EventDao dao = (EventDao) DaoFactory.getDao(Event.class);
    private Event event;

    public EventService() {
    }

    public EventService(Event event) throws DalException{
        this.event = event;
        if (this.event.getId() < 0) {
            try {
                this.event.setId(dao.getNextid());
            } catch (Exception e) {
                throw new DalException(e.getMessage());
            }
        }
    }

    public Event getEvent() {
        return event;
    }

    public void load() throws DalException{
        event = dao.load(String.valueOf(event.getId()));
    }

    public void save() throws DalException{
        dao.save(event);
    }

    @Override
    public int compareTo(EventService es) {
        return event.getDate().compareTo(es.getEvent().getDate());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EventService that = (EventService) obj;
        return event.getAmount() == that.getEvent().getAmount() && event.getId() == that.getEvent().getId() && event.getDate().equals(that.getEvent().getDate()) && event.getType() == that.getEvent().getType();
    }
}
