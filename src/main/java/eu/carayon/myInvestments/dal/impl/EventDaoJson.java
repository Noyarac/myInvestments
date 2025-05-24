package eu.carayon.myInvestments.dal.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.reflect.TypeToken;

import eu.carayon.myInvestments.bo.Event;
import eu.carayon.myInvestments.bo.EventType;
import eu.carayon.myInvestments.dal.DalException;
import eu.carayon.myInvestments.dal.EventDao;
import eu.carayon.myInvestments.dal.JsonDao;

public class EventDaoJson implements EventDao, JsonDao {
    private static final String SAVE_FOLDERNAME = "events";

    @Override
    public int getNextid() throws DalException {
        File folder = new File(SAVE_ROOT_FOLDERNAME + "/" + SAVE_FOLDERNAME);
        folder.mkdirs();
        List<File> listOfFiles = new ArrayList<>(Arrays.asList(folder.listFiles()));
        if (listOfFiles.size() == 0)
            return 0;
        return 1 + listOfFiles.stream()
            .mapToInt(f -> Integer.parseInt(f.getName().replace(".json", "")))
            .max()
            .orElseThrow(() -> new DalException("Impossible de récupérer le prochain ID pour un événement."));
    }

    @Override
    public Event load(String ref) throws DalException {
        Event event = null;
        Map<String, Object> hmap = new HashMap<>();
        File folder = new File(SAVE_ROOT_FOLDERNAME + "/" + SAVE_FOLDERNAME);
        folder.mkdir();
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile() && listOfFiles[i].getName().equals(ref + ".json")) {
                    try (Scanner s = new Scanner(
                            new File(listOfFiles[i].getAbsolutePath()))) {
                        String json = s.useDelimiter("\\A").next();
                        Type listType = new TypeToken<Map<String, Object>>() {
                        }.getType();
                        hmap = gson.fromJson(json, listType);
                        event = new Event(((Double)hmap.get("id")).intValue(), LocalDate.parse((String) hmap.get("date")), ((Double)hmap.get("amount")).intValue(), (EventType.valueOf((String)hmap.get("type"))));
                    } catch (FileNotFoundException e) {
                        throw new DalException(
                                "Le fichier json pour charger le portfolio n'a pas été trouvé.\n" + e.getMessage());
                    }
                }
            }
        }
        return event;
    }

    @Override
    public void save(Event event) throws DalException {
        File folder = new File(SAVE_ROOT_FOLDERNAME + "/" + SAVE_FOLDERNAME);
        folder.mkdir();
        Map<String, Object> hmap = new HashMap<String, Object>();
        hmap.put("id", event.getId());
        hmap.put("date", event.getDate().toString());
        hmap.put("amount", event.getAmount());
        hmap.put("type", event.getType());
        try (FileWriter fw = new FileWriter(
                SAVE_ROOT_FOLDERNAME + "/" + SAVE_FOLDERNAME + "/" + String.valueOf(event.getId()) + ".json")) {
            fw.write(gson.toJson(hmap));
        } catch (IOException e) {
            throw new DalException(String.format(
                    "Impossible d'enregistrer le fichier json de l'événement %d '\n" + e.getMessage(), event.getId()));
        }
    }

}
