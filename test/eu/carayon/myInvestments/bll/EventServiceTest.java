package eu.carayon.myInvestments.bll;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.carayon.myInvestments.bo.Event;
import eu.carayon.myInvestments.bo.EventType;

public class EventServiceTest {
    Event e1;
    Event e2;
    Event e3;
    Event e4;
    EventService es1;
    EventService es2;
    EventService es3;
    EventService es4;

    @BeforeEach
    void beforeEach() {
        e1 = new Event(1, LocalDate.of(2023, 4, 5), 1500000, EventType.MOVEMENT);
        e2 = new Event(1, LocalDate.of(2023, 4, 5), 1500000, EventType.MOVEMENT);
        e3 = new Event(1, LocalDate.of(2025, 8, 14), -200000, EventType.MOVEMENT);
        e4 = new Event();
        e4.setDate(LocalDate.of(2025, 8, 14));
        e4.setAmount(-200000);
        e4.setType(EventType.MOVEMENT);
        try {
            es1 = new EventService(e1);
            es2 = new EventService(e2);
            es3 = new EventService(e3);
            es4 = new EventService(e4);

        } catch (Exception e) {
            System.out.println("Problème");
        }
    }

    @Test
    void testCompareTo() {
        assertTrue(es1.compareTo(es2) == 0);
        assertTrue(es1.compareTo(es3) < 0);
        assertTrue(es3.compareTo(es1) > 0);
    }

    @Test
    void testEquals() {
        assertTrue(es1.equals(es1));
        assertTrue(es1.equals(es2));
        assertFalse(es1.equals(es3));
    }

    @Test
    void testGetEvent() {
        assertEquals(e1, es1.getEvent());
        assertNotEquals(e2, es1.getEvent());
        assertNotEquals(e3, es1.getEvent());
    }

    @Test
    void testConstructorWithNullId() {
        System.out.println("Coucou " + es4.getEvent().getId());
        assertNotNull(es4.getEvent().getId());
    }

    @Test
    void testLoad() {

    }
}
