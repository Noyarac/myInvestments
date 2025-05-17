package eu.carayon.myInvestments.dal.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.google.gson.reflect.TypeToken;

import eu.carayon.myInvestments.bo.Portfolio;
import eu.carayon.myInvestments.bo.Event;
import eu.carayon.myInvestments.bo.Investment;
import eu.carayon.myInvestments.dal.DalException;
import eu.carayon.myInvestments.dal.JsonDao;
import eu.carayon.myInvestments.dal.PortfolioDao;

public class PortfolioDaoJson implements PortfolioDao, JsonDao {
    private static String SAVE_FOLDERNAME = "portfolio";

    @Override
    public Portfolio load(String ref) throws DalException {
        List<String[]> investmentsFullNames = new ArrayList<>();
        File folder = new File(SAVE_ROOT_FOLDERNAME + "/" + SAVE_FOLDERNAME);
        folder.mkdirs();
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    try (Scanner s = new Scanner(new File(listOfFiles[i].getAbsolutePath()))) {
                        String json = s.useDelimiter("\\A").next();
                        Type listType = new TypeToken<ArrayList<String[]>>() {
                        }.getType();
                        investmentsFullNames = gson.fromJson(json, listType);
                    } catch (FileNotFoundException e) {
                        throw new DalException("Le fichier json pour charger le portfolio n'a pas été trouvé.\n" + e.getMessage());
                    }
                }
            }
        }
        Portfolio portfolio = new Portfolio();
        for (String[] fullName : investmentsFullNames) {
            portfolio.getInvestments().add(new Investment(fullName[1], fullName[0], 0, 0, new ArrayList<Event>()));
        }
        return portfolio;
    }

    @Override
    public void save(Portfolio portfolio) throws DalException {
        new File(SAVE_ROOT_FOLDERNAME + "/" + SAVE_FOLDERNAME).mkdir();
        try (FileWriter fw = new FileWriter(SAVE_ROOT_FOLDERNAME + "/" + SAVE_FOLDERNAME + "/" + "investmentsNames.json")) {
            fw.write(gson.toJson(portfolio.getInvestments().stream().map(i -> new String[] {i.getOrganism(), i.getName()}).collect(Collectors.toList())));
        } catch (IOException e) {
            throw new DalException("Impossible d'enregistrer le fichier investmentsNames.json\n" + e.getMessage());
        }        
    }
}
