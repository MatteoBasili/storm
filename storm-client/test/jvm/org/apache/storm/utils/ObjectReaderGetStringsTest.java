package org.apache.storm.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test unitari per il metodo getStrings di {@link ObjectReader}.
 */
@RunWith(Parameterized.class)
public class ObjectReaderGetStringsTest {

    private final Object input;
    private final List<String> expectedOutput;
    private final Class<? extends Exception> expectedException;

    public ObjectReaderGetStringsTest(Object input,
                                            List<String> expectedOutput,
                                            Class<? extends Exception> expectedException) {
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.expectedException = expectedException;
    }

    @Parameters(name = "{index}: getStrings({0})")
    public static Iterable<Object[]> testCases() {
        return Arrays.asList(new Object[][]{

                // -------------------- Variazioni su collezioni valide -------------------- //
                {Arrays.asList("a"), Arrays.asList("a"), null},                                  // T1: Superato
                {Arrays.asList("a", "b", "c"), Arrays.asList("a", "b", "c"), null},              // T2: Superato

                // -------------------- Variazioni su valori nulli -------------------- //
                {Collections.singletonList(null), Collections.emptyList(), null},                // T3: Superato
                {Arrays.asList("a", null, "b", null, "c"), Arrays.asList("a", "b", "c"), null},  // T4: Superato

                // -------------------- Variazioni su collezioni vuote -------------------- //
                {Collections.emptyList(), Collections.emptyList(), null},                        // T5: Superato

                // -------------------- Variazioni su collezioni eterogenee -------------------- //
                {Arrays.asList("a", 1, "b"), Arrays.asList("a", "1", "b"), null},                // T6: Superato
                {Arrays.asList(1, 2.56, 3), Arrays.asList("1", "2.56", "3"), null},              // T7: Superato

                // -------------------- Variazioni su singola stringa -------------------- //
                {"", Collections.singletonList(""), null},                                       // T8: Superato
                {"test", Collections.singletonList("test"), null},                               // T9: Superato

                // -------------------- Variazioni su oggetti non collezione -------------------- //
//                {1, Collections.singletonList("1")},                                           // T10: Fallito --> Eccezione: "Don't know how to convert to string list"
//                {new Object(), Collections.singletonList(new Object().toString())},            // T11: Fallito --> Eccezione: "Don't know how to convert to string list"

                // -------------------- Variazioni su input nullo -------------------- //
                {null, Collections.emptyList(), null},                                           // T12: Superato

                // -------------------- Aggiunti dopo l'analisi con Jacoco -------------------- //
                {1, null, IllegalArgumentException.class},                                       // J-T1: Superato
                {new Object(), null, IllegalArgumentException.class}                             // J-T2: Superato
        });
    }

    @Test(timeout = 5000)
    public void testGetStrings() {

        if (expectedException != null) {
            assertGetStringsFails(input, expectedException);
        } else {
            assertGetStringsSucceeds(input, expectedOutput);
        }
    }

    // ============================ METODI DI SUPPORTO ============================ //

    private void assertGetStringsFails(Object input,
                                       Class<? extends Exception> expectedException) {
        try {
            ObjectReader.getStrings(input);
            fail("getStrings non ha lanciato l'eccezione attesa: " + expectedException.getSimpleName());
        } catch (Exception e) {
            assertTrue("Eccezione lanciata non corrisponde a quella attesa",
                    expectedException.isInstance(e));
        }
    }

    private void assertGetStringsSucceeds(Object input,
                                          List<String> expectedOutput) {
        List<String> result = ObjectReader.getStrings(input);

        assertNotNull("Il risultato non dovrebbe essere null", result);
        assertEquals("La lista restituita non corrisponde a quella attesa", expectedOutput, result);
    }
}
