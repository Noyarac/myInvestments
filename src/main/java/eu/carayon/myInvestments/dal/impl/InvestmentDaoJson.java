package eu.carayon.myInvestments.dal.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.google.gson.reflect.TypeToken;

import eu.carayon.myInvestments.bo.Event;
import eu.carayon.myInvestments.bo.Investment;
import eu.carayon.myInvestments.dal.DalException;
import eu.carayon.myInvestments.dal.InvestmentDao;
import eu.carayon.myInvestments.dal.JsonDao;

public class InvestmentDaoJson implements InvestmentDao, JsonDao {
    private static final String SAVE_FOLDERNAME = "investments";

    @Override
    public Investment load(String fullName) throws DalException {
        Investment investment = null;
        Map<String, Object> hmap;
        File folder = new File(SAVE_ROOT_FOLDERNAME + "/" + SAVE_FOLDERNAME);
        folder.mkdirs();
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile() && listOfFiles[i].getName().equals(fullName + ".json")) {
                    try (Scanner s = new Scanner(
                            new File(listOfFiles[i].getAbsolutePath()))) {
                        String json = s.useDelimiter("\\A").next();

                        Type listType = new TypeToken<Map<String, Object>>() {
                        }.getType();
                        // Type listType = new TypeToken<Investment>() {}.getType();
                        hmap = gson.fromJson(json, listType);
                        investment = new Investment();
                        investment.setName((String) hmap.get("name"));
                        investment.setOrganism((String) hmap.get("organism"));
                        investment.setFrequency(((Double) hmap.get("frequency")).intValue());
                        investment.setDelay(((Double) hmap.get("delay")).intValue());
                        List<?> idListRaw = (List<?>) hmap.get("eventIds");
                        List<Event> events = idListRaw.stream().map(val -> {
                            int id = ((Double) val).intValue();
                            Event e = new Event();
                            e.setId(id);
                            return e;
                        }).collect(Collectors.toList());
                        // List<Integer> re = listOfIds.stream().map(val -> (Integer)
                        // val).collect(Collectors.toList());
                        investment.setEvents(new ArrayList<Event>(events));
                        break;
                    } catch (FileNotFoundException e) {
                        throw new DalException(
                                "Le fichier json pour charger le portfolio n'a pas été trouvé.\n" + e.getMessage());
                    }
                }
            }
        }
        return investment;
    }

    @Override
    public void save(Investment investment) throws DalException {
        File folder = new File(SAVE_ROOT_FOLDERNAME + "/" + SAVE_FOLDERNAME + "/");
        folder.mkdir();
        try (FileWriter fw = new FileWriter(SAVE_ROOT_FOLDERNAME + "/" + SAVE_FOLDERNAME + "/"
                + investment.getOrganism() + investment.getName() + ".json")) {
            var map = new HashMap<String, Object>();
            map.put("name", investment.getName());
            map.put("organism", investment.getOrganism());
            map.put("frequency", investment.getFrequency());
            map.put("delay", investment.getDelay());
            List<Integer> ids = investment.getEvents().stream()
                    .map(Event::getId)
                    .collect(Collectors.toList());
            map.put("eventIds", ids);
            fw.write(gson.toJson(map));
            // fw.write(gson.toJson(investment));
        } catch (IOException e) {
            throw new DalException(
                    String.format("Impossible d'enregistrer le fichier json de %s %s '\n" + e.getMessage(),
                            investment.getOrganism(), investment.getName()));
        }
    }
}
