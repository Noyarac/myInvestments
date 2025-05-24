package eu.carayon.myInvestments.bll;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.carayon.myInvestments.bll.EventService;
import eu.carayon.myInvestments.bll.InvestmentService;
import eu.carayon.myInvestments.bo.Event;
import eu.carayon.myInvestments.bo.EventType;
import eu.carayon.myInvestments.bo.Investment;

public class InvestmentServiceTest {
        Event e1;
        Event e2;
        Event e3;
        Event e4;
        Event e5;
        Event e6;
        Event e7;
        EventService es1;
        EventService es2;
        EventService es3;

        Investment inv1;
        Investment inv2;
        Investment inv3;
        Investment inv4;
        InvestmentService invSer1;
        InvestmentService invSer2;
        InvestmentService invSer3;
        InvestmentService invSer4;


    @BeforeEach
    void beforeEach() {
        e1 = new Event(1, LocalDate.of(2023, 4, 5), 1500000, EventType.MOVEMENT);
        e2 = new Event(1, LocalDate.of(2023, 4, 5), 1500000, EventType.MOVEMENT);
        e3 = new Event(2, LocalDate.of(2024, 8, 14), -200000, EventType.MOVEMENT);
        e4 = new Event(3, LocalDate.of(2024, 9, 14), 3000000, EventType.MOVEMENT);
        e5 = new Event(4, LocalDate.of(2025, 4, 11), 10000, EventType.DISTRIBUTION);
        e6 = new Event(5, LocalDate.of(2025, 3, 14), 30000, EventType.DISTRIBUTION);
        e7 = new Event(5, LocalDate.of(2025, 1, 14), 2920000, EventType.VALUATION);
        try {
        } catch (Exception e) {
            System.out.println("Problème\n" + e.getMessage());
        }
        inv1 = new Investment("Eurion", "Corum", 1, 5, null);
        inv2 = new Investment("Eurion", "Corum", 1, 5, null);
        inv3 = new Investment("Pierval Santé", "Euryale", 3, 6, null);
        inv4 = new Investment("Optimale", "Consultim", 1, 6, null);
        invSer1 = new InvestmentService(inv1);
        invSer2 = new InvestmentService(inv2);
        invSer3 = new InvestmentService(inv3);
        invSer4 = new InvestmentService(inv4);
        invSer1.addEvent(e1);
        invSer2.addEvent(e2);
        invSer3.addEvent(e3);
        invSer4.addEvent(e4);
        invSer4.addEvent(e5);
        invSer4.addEvent(e6);
        invSer4.addEvent(e7);
    }


    @Test
    void testAddEvent() {
        assertEquals(e1, invSer1.getEventsFromType(EventType.MOVEMENT).get(0));
    }

    @Test
    void testCompareTo() {
        assertTrue(invSer1.compareTo(invSer2) == 0);
        assertTrue(invSer1.compareTo(invSer3) > 0);
    }

    @Test
    void testGetAverageMonthlyDistribution() {
        assertEquals(20000, invSer4.getAverageMonthlyDistribution());
    }

    @Test
    void testGetBreakEven() {
        assertEquals((e7.getAmount() - e4.getAmount() + e5.getAmount() + e6.getAmount()) / invSer4.getAverageMonthlyDistribution(), invSer4.getBreakEven());
    }

    @Test
    void testGetEventsFromType() {
        List<Event> eventList = new ArrayList<>();
        eventList.add(e5);
        eventList.add(e6);
        assertEquals(eventList, invSer4.getEventsFromType(EventType.DISTRIBUTION));
    }

    @Test
    void testGetInvested() {
        assertEquals(e4.getAmount(), invSer4.getInvested());
    }

    @Test
    void testGetRealYearlyYield() {

    }

    @Test
    void testGetYearlyYield() {

    }

    @Test
    void testGetYearlyYield2() {

    }

    @Test
    void testLoad() {

    }

    @Test
    void testRemoveEvent() {
        invSer4.removeEvent(e5);
        List<Event> expectedResult = new  ArrayList<>();
        expectedResult.add(e6);
        assertEquals(expectedResult, invSer4.getEventsFromType(EventType.DISTRIBUTION));
    }
}
