package eu.carayon.myInvestments.dal;

import com.google.gson.Gson;

public interface JsonDao {
    public static Gson gson = new Gson();
    public static final String SAVE_ROOT_FOLDERNAME = "json";
}
