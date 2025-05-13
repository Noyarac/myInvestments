package eu.carayon;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class App {
    private static Scanner s = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        Scpi.load();
        mainMenu();
    }

    private static void mainMenu() {
        String choice = "";
        do {
            listScpi();
            choice = userInput("Choisissez une action : [a]jouter une SCPI, sélectionner la SCPI d'index X avec [sX] ou [q]uitter", Stream.concat(IntStream.rangeClosed(0, Scpi.mesScpi.size() - 1).mapToObj(o -> "s" + o), Stream.of("a", "q")).collect(Collectors.toSet()));
            if (choice.substring(0, 1).equals("s")) {
                byte selectedIndex = Byte.parseByte(choice.substring(1));
                if (selectedIndex > Scpi.mesScpi.size() - 1) {System.out.println("Index de SCPI invalide.");} else {
                    interactWithScpiMenu(Scpi.get((selectedIndex)));
                };
            } else if (choice.equals("a")) {
                    System.out.println("Entrez le nom de l'organisme, le nom de la SCPI, la fréquence des loyers et le délai de jouissance.");
                    new Scpi(s.nextLine(), s.nextLine(), Byte.parseByte(s.nextLine()), Byte.parseByte(s.nextLine()));
                    Scpi.save(); 
            }
        }
        while (!choice.equals("q"));
    }

    private static void interactWithScpiMenu(Scpi scpi) {
        System.out.println(scpi.getOrganism() + " " + scpi.getName());
        String choice = "";
        do {
            choice = userInput("Avec quoi voulez-vous interagir ? [m]ouvements, [l]oyers, [v]alorisations, [q]uitter", Set.of("m", "l", "v", "q"));
            switch (choice) {
                case "m":
                    scpiTypeMenu(scpi, "mouvements");
                    break;
            
                case "l":
                    scpiTypeMenu(scpi, "distributions");
                    break;
            
                case "v":
                    scpiTypeMenu(scpi, "valorisations");
                    break;
            }
        }
        while (!choice.equals("q"));
    }

    private static void scpiTypeMenu(Scpi scpi, String type) {
        String choice = "";
        do {
        byte index = 0;
        for (DatedAmount datedAmount : scpi.getFromType(type)) {
            System.out.println(String.valueOf(index++) + ") " + datedAmount);
        }
            choice = userInput("Que voulez-vous faire avec les " + type + " ? [a]jouter, [s]upprimer, [q]uitter", Set.of("a", "s", "q"));
            switch (choice) {
                case "a":
                    try {
                        System.out.println("Entrez la date (dd/MM/yyyy), puis le montant.");
                        scpi.addToType(type, new DatedAmount(
                            (new SimpleDateFormat("dd/MM/yyyy")).parse(s.nextLine())
                            , (int) (Float.parseFloat(s.nextLine()) * 100)));
                            Scpi.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;

                case "s":
                    scpi.removeFromType(
                        type,
                        Byte.parseByte(userInput(
                            "Entrez l'index de l'élément à supprimer.",
                            IntStream.rangeClosed(0, Scpi.mesScpi.size()).mapToObj(String::valueOf).collect(Collectors.toSet())
                        ))
                    );
                    break;
            }
        }
        while (!choice.equals("q"));
    }

    private static void listScpi() {
        System.out.println("┌───────┬───────────────────────────────┬─────────┬───────┬───────────────────┬────────────────┬───────────┬────────────────┐");
        System.out.println("│ Index │ Nom                           │ Investi │ Poids │ Loyer mens. moyen │ Rendement moy. │ BreakEven │ Rendement réel │");
        System.out.println("├───────┼───────────────────────────────┼─────────┼───────┼───────────────────┼────────────────┼───────────┼────────────────┤");
        byte i = 0;
        Collections.sort(
            Scpi.mesScpi,
            new Comparator<Scpi>() {
                public int compare(Scpi scpi1, Scpi scpi2) {
                    return scpi2.getInvested() - scpi1.getInvested();
                }
            });
        for (Scpi scpi : Scpi.mesScpi) {
            System.out.print('│');
            // if (i % 2 == 1) System.out.print("\u001B[44m");
            if (i % 2 == 1) System.out.print("\u001B[48;5;235m");
            System.out.println(String.format(
                " %5d │ %-29s │ %7s │ %3d %% │ %17s │ %14s │ %9s │ %14s \u001B[0m│",
                i++,
                scpi.getOrganism() + ' ' + scpi.getName(),
                String.format("%.0f k€", scpi.getInvested() / 100000.0),
                (int)((float) scpi.getInvested() / (float) Scpi.getTotalInvested() * 100.0),
                scpi.getAverageMonthlyDistribution() == 0 ? "" : String.format("%.2f €",  scpi.getAverageMonthlyDistribution() / 100.0),
                scpi.getYearlyYield() == 0 ? "" : (String.format("%.2f %%", scpi.getYearlyYield() * 100)),
                scpi.getBreakEven(),
                scpi.getRealYearlyYield() == 0 ? "" : String.format("%.2f %%", scpi.getRealYearlyYield() * 100)
            ));
        }
        System.out.println("├───────┼───────────────────────────────┼─────────┼───────┼───────────────────┼────────────────┼───────────┼────────────────┤");
        System.out.println(String.format(
            "│       │ %-29s │ %7s │       │ %17s │ %14s │  en mois  │ %14s │",
            "Toutes les SCPI",
            String.format("%.0f k€", Scpi.getTotalInvested() / 100000.0),
            Scpi.getAverageMonthlyTotalDistribution() == 0 ? "" : String.format("%.2f €",  Scpi.getAverageMonthlyTotalDistribution() / 100.0),
            Scpi.getTotalYearlyYield() == 0 ? "" : String.format("%.2f %%", Scpi.getTotalYearlyYield() * 100),
            String.format("%.2f %%", Scpi.getTotalRealYearlyYield() * 100)
        ));
    System.out.println("\u001B[0m" + "└───────┴───────────────────────────────┴─────────┴───────┴───────────────────┴────────────────┴───────────┴────────────────┘");
    }    


    private static String userInput(String prompt, Set<String> range) {
        String choice = "";
        do {
        System.out.println("\n" + prompt);
            try {
                choice = s.nextLine().toLowerCase();
                if (!range.contains(choice)) throw new IOException();
            } catch (Exception e) {
                System.out.println("Erreur de saisie.");
            }
        } while (!range.contains(choice));
        return choice;
    }
}
