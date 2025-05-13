package eu.carayon.myInvestments.bo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DatedAmountTest {
    DatedAmount da;

    @BeforeEach
    void BeforeEach() {
        da = new DatedAmount(new Date(857), 50);
    }
    
    @Test
    void testClone() {
        DatedAmount da2 = da.clone();
        assertEquals(da, da2);
    }

    @Test
    void testCompareTo() {
        DatedAmount da2 = new DatedAmount(new Date(8570), 25);
        assertTrue(da2.compareTo(da) > 0);
        assertTrue(da.compareTo(da2) < 0);

    }

    @Test
    void testToString() {
        assertEquals("01/01/1970 - 0,50€", da.toString());
    }
}
