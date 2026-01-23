package org.apache.storm.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Test unitari per il metodo getStrings di {@link ObjectReader}.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ObjectReaderGetStringsTest {

    /**
     * Genera i casi di test parametrizzati.
     * Ogni Arguments contiene:
     * - input Object
     * - output atteso (List<String>) oppure null
     * - eccezione attesa (null se non attesa)
     */
    private static Stream<Arguments> testCases() {
        return Stream.of(

                // -------------------- Variazioni su collezioni valide -------------------- //
                Arguments.of(
                        Arrays.asList("a"),
                        Arrays.asList("a"),                                    // T1: Superato
                        null
                ),
                Arguments.of(
                        Arrays.asList("a", "b", "c"),
                        Arrays.asList("a", "b", "c"),                          // T2: Superato
                        null
                ),

                // -------------------- Variazioni su valori nulli -------------------- //
                Arguments.of(
                        Collections.singletonList(null),
                        Collections.emptyList(),                               // T3: Superato
                        null
                ),
                Arguments.of(
                        Arrays.asList("a", null, "b", null, "c"),
                        Arrays.asList("a", "b", "c"),                          // T4: Superato
                        null
                ),

                // -------------------- Variazioni su collezioni vuote -------------------- //
                Arguments.of(
                        Collections.emptyList(),
                        Collections.emptyList(),                               // T5: Superato
                        null
                ),

                // -------------------- Variazioni su collezioni eterogenee -------------------- //
                Arguments.of(
                        Arrays.asList("a", 1, "b"),
                        Arrays.asList("a", "1", "b"),                          // T6: Superato
                        null
                ),
                Arguments.of(
                        Arrays.asList(1, 2.56, 3),
                        Arrays.asList("1", "2.56", "3"),                       // T7: Superato
                        null
                ),

                // -------------------- Variazioni su singola stringa -------------------- //
                Arguments.of(
                        "",
                        Collections.singletonList(""),                         // T8: Superato
                        null
                ),
                Arguments.of(
                        "test",
                        Collections.singletonList("test"),                     // T9: Superato
                        null
                ),

                // -------------------- Variazioni su oggetti non collezione -------------------- //
//                Arguments.of(
//                        1,
//                        Collections.singletonList("1")                       // T10: Fallito --> Eccezione: "Don't know how to convert to string list"
//                ),
//                Arguments.of(
//                        new Object(),
//                        Collections.singletonList(new Object().toString())   // T11: Fallito --> Eccezione: "Don't know how to convert to string list"
//                ),

                // -------------------- Variazioni su input nullo -------------------- //
                Arguments.of(
                        null,
                        Collections.emptyList(),                               // T12: Superato
                        null
                ),

                // -------------------- Aggiunti dopo l'analisi con Jacoco -------------------- //
                Arguments.of(
                        1,
                        null,                                                  // J-T1: Superato
                        IllegalArgumentException.class
                ),
                Arguments.of(
                        new Object(),
                        null,                                                  // J-T2: Superato
                        IllegalArgumentException.class
                )
        );
    }

    /**
     * Test parametrizzato del metodo getStrings.
     * Per ogni input viene verificato che la lista restituita
     * corrisponda all'output atteso.
     */
    @ParameterizedTest
    @MethodSource("testCases")
    @Timeout(value = 5, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    void testGetStrings(Object input,
                        List<String> expectedOutput,
                        Class<? extends Exception> expectedException) {

        if (expectedException != null) {
            assertGetStringsFails(input, expectedException);
        } else {
            assertGetStringsSucceeds(input, expectedOutput);
        }

    }

    // ============================ METODI DI SUPPORTO ============================ //

    /**
     * Verifica che getStrings lanci l'eccezione prevista.
     */
    private void assertGetStringsFails(Object input,
                                       Class<? extends Exception> expectedException) {

        Assertions.assertThrows(
                expectedException,
                () -> ObjectReader.getStrings(input),
                "getStrings non ha lanciato l'eccezione attesa: "
                        + expectedException.getSimpleName()
        );
    }

    /**
     * Verifica che getStrings restituisca il risultato atteso.
     */
    private void assertGetStringsSucceeds(Object input,
                                          List<String> expectedOutput) {

        List<String> result = ObjectReader.getStrings(input);

        Assertions.assertNotNull(result, "Il risultato non dovrebbe essere null");
        Assertions.assertEquals(
                expectedOutput,
                result,
                "La lista restituita non corrisponde a quella attesa"
        );
    }

}
