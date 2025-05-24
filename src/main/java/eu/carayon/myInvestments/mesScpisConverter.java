package eu.carayon.myInvestments;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import eu.carayon.myInvestments.bll.EventService;
import eu.carayon.myInvestments.bll.InvestmentService;
import eu.carayon.myInvestments.bll.PortfolioService;
import eu.carayon.myInvestments.bo.Event;
import eu.carayon.myInvestments.bo.EventType;
import eu.carayon.myInvestments.bo.Investment;
import eu.carayon.myInvestments.bo.Portfolio;

public class mesScpisConverter {
    public static void main(String[] args) {
        Gson gson = new Gson();

        try (Scanner s = new Scanner(new File("mesScpi.json"))) {
            List<Map<String, Object>> portfolioJson;
            String json = s.useDelimiter("\\A").next();
            Type listType = new TypeToken<List<Map<String, Object>>>() {
            }.getType();
            portfolioJson = gson.fromJson(json, listType);
            Portfolio ptf = new Portfolio();
            PortfolioService ptfs = new PortfolioService(ptf);
            for (Map<String, Object> scpi : portfolioJson) {
                String name = (String) scpi.get("nom");
                String organism = (String) scpi.get("organisme");
                int frequency = ((Double) scpi.get("frequency")).intValue();
                int delay = ((Double) scpi.get("delay")).intValue();
                Investment inv = new Investment(name, organism, frequency, delay, null);
                InvestmentService invServ = new InvestmentService(inv);
                try {
                    invServ.save();
                } catch (Exception e) {
                }
                ptfs.addInvestment(inv);
                try {
                    ptfs.save();
                } catch (Exception e) {
                }
                for (String movType : List.of("movements", "distributions", "valuations")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> movl = (List<Map<String, Object>>) scpi.get(movType);
                    for (Map<String, Object> mov : movl) {
                        int amount = (int) ((Double) mov.get("amount")).intValue();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
                        String tinyStr = ((String) mov.get("date")).split(", 12:00")[0];
                        LocalDate date = LocalDate.parse(tinyStr, formatter);
                        try {
                            EventService es = new EventService(new Event(-1, date, amount, (movType.equals("movements")
                                    ? EventType.MOVEMENT
                                    : movType.equals("distributions") ? EventType.DISTRIBUTION : EventType.VALUATION)));
                            es.save();
                            invServ.addEvent(es.getEvent());
                            invServ.save();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
        }
    }
}
