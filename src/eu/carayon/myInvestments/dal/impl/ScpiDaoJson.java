package eu.carayon.myInvestments.dal.impl;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import eu.carayon.myInvestments.bo.Scpi;
import eu.carayon.myInvestments.dal.ScpiDao;

public class ScpiDaoJson implements ScpiDao{
    private static Gson gson = new Gson();
    private static final String SAVE_FILENAME = "mesScpi.json";

    @Override
    public ArrayList<Scpi> load() throws Exception {
        try (Scanner s = new Scanner(new File(SAVE_FILENAME))) {
            String json = s.useDelimiter("\\A").next();
            Type listType = new TypeToken<ArrayList<Scpi>>() {}.getType();
            return gson.fromJson(json, listType);
        }
    }

    @Override
    public void save(List<Scpi> mesScpi) throws Exception {
        try (FileWriter fw = new FileWriter(SAVE_FILENAME)) {
            fw.write(gson.toJson(mesScpi));
        }
    }
    

}
