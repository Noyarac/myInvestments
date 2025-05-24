package eu.carayon.myInvestments.dal;

public class DalException extends Exception {
    public DalException(String message) {
        super(message);
    }
    public DalException() {
        super();
    }
}
