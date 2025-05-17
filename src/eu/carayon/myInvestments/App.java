package eu.carayon.myInvestments;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import eu.carayon.myInvestments.bll.EventService;
import eu.carayon.myInvestments.bll.InvestmentService;
import eu.carayon.myInvestments.bll.PortfolioService;
import eu.carayon.myInvestments.bo.Event;
import eu.carayon.myInvestments.bo.EventType;
import eu.carayon.myInvestments.bo.Investment;

public class App {
    private static Scanner s = new Scanner(System.in);
    private static PortfolioService portfolioService = new PortfolioService();


    public static void main(String[] args) throws IOException {
        try {
            portfolioService.load();
        } catch (Exception e) {
            System.out.println("Problème\n" + e.getMessage());
        }
        mainMenu();
    }

    private static void mainMenu() {
        String choice = "";
        do {
            listScpi();
            choice = userInput(
                    "Choisissez une action : [a]jouter une SCPI, sélectionner la SCPI d'index X avec [sX] ou [q]uitter",
                    Stream.concat(IntStream.rangeClosed(0, portfolioService.getInvestmentServices().size() - 1)
                            .mapToObj(o -> "s" + o), Stream.of("a", "q")).collect(Collectors.toSet()));
            if (choice.substring(0, 1).equals("s")) {
                int selectedIndex = Integer.parseInt(choice.substring(1));
                if (selectedIndex > portfolioService.getInvestmentServices().size() - 1) {
                    System.out.println("Index de SCPI invalide.");
                } else {
                    try {
                        interactWithScpiMenu(portfolioService.getInvestmentService(selectedIndex));
                    } catch (Exception e) {
                        System.out.println("Problème\n" + e.getMessage());
                    }
                }
                ;
            } else if (choice.equals("a")) {
                System.out.println("Entrez le nom de l'organisme.");
                String organism = s.nextLine();
                System.out.println("Entrez le nom du produit.");
                String name = s.nextLine();
                System.out.println(
                        "Entrez la fréquence de distribution (1 pour mensuel, 3 pour trimestriel, 12 pour annuel…).");
                int frequency = Integer.parseInt(s.nextLine());
                System.out.println("Entrez le délai de jouissance.");
                int delay = Integer.parseInt(s.nextLine());

                portfolioService.addInvestment(new Investment(name, organism, frequency, delay, null));
                try {
                    portfolioService.save();
                } catch (Exception e) {
                    System.out.println("Problème d'enregistrement du portfolio\n" + e.getMessage());
                }
            }
        } while (!choice.equals("q"));
    }

    private static void interactWithScpiMenu(InvestmentService investmentService) {
        System.out.println(
                investmentService.getInvestment().getOrganism() + " " + investmentService.getInvestment().getName());
        String choice = "";
        do {
            choice = userInput("Avec quoi voulez-vous interagir ? [m]ouvements, [l]oyers, [v]alorisations, [q]uitter",
                    Set.of("m", "l", "v", "q"));
            switch (choice) {
                case "m":
                    scpiTypeMenu(investmentService, EventType.MOVEMENT);
                    break;

                case "l":
                    scpiTypeMenu(investmentService, EventType.DISTRIBUTION);
                    break;

                case "v":
                    scpiTypeMenu(investmentService, EventType.VALUATION);
                    break;
            }
        } while (!choice.equals("q"));
    }

    private static void scpiTypeMenu(InvestmentService investmentService, EventType type) {
        String choice = "";
        do {
            int index = 0;
            for (Event event : investmentService.getEventsFromType(type)) {
                System.out.println(String.format("%d) %s : %.2f €", index++, event.getDate().toString(),
                        (float) event.getAmount() / 100));
            }
            choice = userInput(
                    "Que voulez-vous faire avec les " + type.label.toLowerCase()
                            + "s ? [a]jouter, [s]upprimer, [q]uitter",
                    Set.of("a", "s", "q"));
            switch (choice) {
                case "a":
                    int amount;
                    LocalDate date;
                    System.out.println("Entrez la date (dd/MM/yyyy)");
                    date = LocalDate.parse(s.nextLine(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    System.out.println("Entrez le montant.");
                    amount = (int) (Float.parseFloat(s.nextLine()) * 100);
                    Event event = new Event();
                    event.setAmount(amount);
                    event.setDate(date);
                    event.setType(type);
                    try {
                        new EventService(event);
                    } catch (Exception e) {
                        System.out.println("Problème\n" + e.getMessage());
                    }
                    investmentService.addEvent(event);
                    try {
                        investmentService.save();
                    } catch (Exception e) {
                        System.out.println("Problème\n" + e.getMessage());
                    }
                    break;

                case "s":
                    List<Event> eventList = investmentService.getEventsFromType(type);
                    int indexToRemove = Integer.parseInt(
                            userInput(
                                    "Entrez l'index de l'élément à supprimer.",
                                    IntStream.rangeClosed(0, portfolioService.getInvestmentServices().size())
                                            .mapToObj(String::valueOf).collect(Collectors.toSet())));
                    investmentService.removeEvent(eventList.get(indexToRemove));
                    break;
            }
        } while (!choice.equals("q"));
    }

    private static void listScpi() {
        System.out.println(
                "┌───────┬───────────────────────────────┬─────────┬───────┬───────────────────┬────────────────┬───────────┬────────────────┐");
        System.out.println(
                "│ Index │ Nom                           │ Investi │ Poids │ Loyer mens. moyen │ Rendement moy. │ BreakEven │ Rendement réel │");
        System.out.println(
                "├───────┼───────────────────────────────┼─────────┼───────┼───────────────────┼────────────────┼───────────┼────────────────┤");
        int i = 0;
        try {
            portfolioService.sort();
        } catch (Exception e) {
            System.out.println("Problème\n" + e.getMessage());
        }
        for (InvestmentService investmentService : portfolioService.getInvestmentServices()) {
            System.out.print('│');
            // if (i % 2 == 1) System.out.print("\u001B[44m");
            if (i % 2 == 1)
                System.out.print("\u001B[48;5;235m");
            System.out.println(String.format(
                    " %5d │ %-29s │ %7s │ %3d %% │ %17s │ %14s │ %9s │ %14s \u001B[0m│",
                    i++,
                    investmentService.getInvestment().getOrganism() + ' ' + investmentService.getInvestment().getName(),
                    String.format("%.0f k€", investmentService.getInvested() / 100000.0),
                    (int) ((float) investmentService.getInvested() / (float) portfolioService.getInvestmentServices()
                            .stream().mapToInt(InvestmentService::getInvested).sum() * 100.0),
                    investmentService.getAverageMonthlyDistribution() == 0 ? ""
                            : String.format("%.2f €", investmentService.getAverageMonthlyDistribution() / 100.0),
                    investmentService.getYearlyYield() == 0 ? ""
                            : (String.format("%.2f %%", investmentService.getYearlyYield() * 100)),
                    investmentService.getBreakEven(),
                    investmentService.getRealYearlyYield() == 0 ? ""
                            : String.format("%.2f %%", investmentService.getRealYearlyYield() * 100)));
        }
        System.out.println(
                "├───────┼───────────────────────────────┼─────────┼───────┼───────────────────┼────────────────┼───────────┼────────────────┤");
        try {
            System.out.println(String.format(
                    "│       │ %-29s │ %7s │       │ %17s │ %14s │  en mois  │ %14s │",
                    "Toutes les SCPI",
                    String.format("%.0f k€",
                            portfolioService.getInvestmentServices().stream().mapToInt(InvestmentService::getInvested)
                                    .sum() / 100000.0),
                    portfolioService.getAverageMonthlyTotalDistribution() == 0 ? ""
                            : String.format("%.2f €", portfolioService.getAverageMonthlyTotalDistribution() / 100.0),
                    portfolioService.getTotalYearlyYield() == 0 ? ""
                            : String.format("%.2f %%", portfolioService.getTotalYearlyYield() * 100.0),
                    String.format("%.2f %%", portfolioService.getTotalRealYearlyYield() * 100.0)));
        } catch (Exception e) {
            System.out.println("Problème\n" + e.getMessage());
        }
        System.out.println("\u001B[0m"
                + "└───────┴───────────────────────────────┴─────────┴───────┴───────────────────┴────────────────┴───────────┴────────────────┘");
    }

    private static String userInput(String prompt, Set<String> range) {
        String choice = "";
        do {
            System.out.println("\n" + prompt);
            try {
                choice = s.nextLine().toLowerCase();
                if (!range.contains(choice))
                    throw new IOException();
            } catch (Exception e) {
                System.out.println("Erreur de saisie.");
            }
        } while (!range.contains(choice));
        return choice;
    }
}
