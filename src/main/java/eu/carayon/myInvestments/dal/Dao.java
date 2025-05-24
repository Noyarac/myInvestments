package eu.carayon.myInvestments.dal;

public interface Dao<T> {
    void save(T instance) throws DalException;
    T load(String ref) throws DalException;
}
