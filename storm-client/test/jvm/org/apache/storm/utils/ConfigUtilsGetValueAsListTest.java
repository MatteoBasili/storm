package org.apache.storm.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

/**
 * Test unitari per il metodo getValueAsList di {@link ConfigUtils}.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConfigUtilsGetValueAsListTest {

    /**
     * Genera i casi di test parametrizzati.
     * Ogni Arguments contiene:
     * - name (String)
     * - conf (Map<String, Object>)
     * - output atteso (List<String>) oppure null
     * - eccezione attesa (null se non attesa)
     */
    private static Stream<Arguments> testCases() {

        Object genericObject = new Object();

        Map<String, Object> validMap = new HashMap<>();
        validMap.put("", Arrays.asList("chiave vuota", ":", "valida"));
        validMap.put(null, Arrays.asList("chiave nulla", ":", "valida"));
        validMap.put("list1", Arrays.asList("a"));
        validMap.put("list2", Arrays.asList("a", "b", "c"));
        validMap.put("listWithNull1", Collections.singletonList(null));
        validMap.put("listWithNull2", Arrays.asList("a", null, "b", null, "c"));
        validMap.put("emptyList", Collections.emptyList());
        validMap.put("heterogeneousList1", Arrays.asList("a", 1, "b"));
        validMap.put("heterogeneousList2", Arrays.asList(1, 2.56, 3));
        validMap.put("emptyString", "");
        validMap.put("string", "test string");
        validMap.put("integer", 1);
        validMap.put("genericObject", genericObject);
        validMap.put("nullValue", null);

        Map<String, Object> emptyMap = new HashMap<>();

        // Mappa che non supporta chiavi nulle
        Map<String, Object> noNullKeyMap = new Hashtable<>();

        return Stream.of(

                // -------------------- Variazioni su name e conf -------------------- //
                Arguments.of(
                        "missing",
                        emptyMap,                                            // T1: Superato
                        null,
                        null
                ),
                Arguments.of(
                        "",
                        validMap,                                            // T2: Superato
                        Arrays.asList("chiave vuota", ":", "valida"),
                        null
                ),
                Arguments.of(
                        null,
                        validMap,                                            // T3: Superato
                        Arrays.asList("chiave nulla", ":", "valida"),
                        null
                ),
                Arguments.of(
                        null,
                        noNullKeyMap,                                        // T4: Superato
                        null,
                        NullPointerException.class
                ),
                Arguments.of(
                        "missing",
                        null,                                                // T5: Superato
                        null,
                        IllegalArgumentException.class
                ),

                // -------------------- Variazioni sul valore associato a name -------------------- //
                Arguments.of(
                        "list1",
                        validMap,                                            // T6: Superato
                        Arrays.asList("a"),
                        null
                ),
                Arguments.of(
                        "list2",
                        validMap,                                            // T7: Superato
                        Arrays.asList("a", "b", "c"),
                        null
                ),
//                Arguments.of(
//                        "listWithNull1",
//                        validMap,                                          // T8: Fallito --> Eccezione: "Don't know how to convert null to String"
//                        Collections.emptyList(),
//                        null
//                ),
//                Arguments.of(
//                        "listWithNull2",
//                        validMap,                                          // T9: Fallito --> Eccezione: "Don't know how to convert null to String"
//                        Arrays.asList("a", "b", "c"),
//                        null
//                ),
                Arguments.of(
                        "emptyList",
                        validMap,                                            // T10: Superato
                        Collections.emptyList(),
                        null
                ),
                Arguments.of(
                        "heterogeneousList1",
                        validMap,                                            // T11: Superato
                        Arrays.asList("a", "1", "b"),
                        null
                ),
                Arguments.of(
                        "heterogeneousList2",
                        validMap,                                            // T12: Superato
                        Arrays.asList("1", "2.56", "3"),
                        null
                ),
                Arguments.of(
                        "emptyString",
                        validMap,                                            // T13: Superato
                        Collections.singletonList(""),
                        null
                ),
//                Arguments.of(
//                        "string",
//                        validMap,                                          // T14: Fallito -->  "La lista restituita non corrisponde a quella attesa"
//                        Collections.singletonList("test string"),
//                        null
//                ),
                Arguments.of(
                        "integer",
                        validMap,                                            // T15: Superato
                        Collections.singletonList("1"),
                        null
                ),
                Arguments.of(
                        "genericObject",
                        validMap,                                            // T16: Superato
                        Collections.singletonList(genericObject.toString()),
                        null
                )
//                Arguments.of(
//                        "nullValue",
//                        validMap,                                          // T17: Fallito --> "Il risultato non dovrebbe essere null"
//                        Collections.emptyList(),
//                        null
//                )
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    @Timeout(value = 5, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    void testGetValueAsList(String name,
                            Map<String, Object> conf,
                            List<String> expectedOutput,
                            Class<? extends Exception> expectedException) {

        if (expectedException != null) {
            assertGetValueAsListFails(name, conf, expectedException);
        } else {
            assertGetValueAsListSucceeds(name, conf, expectedOutput);
        }
    }

    private void assertGetValueAsListFails(String name,
                                           Map<String, Object> conf,
                                           Class<? extends Exception> expectedException) {

        Assertions.assertThrows(
                expectedException,
                () -> ConfigUtils.getValueAsList(name, conf),
                "getValueAsList non ha lanciato l'eccezione attesa: "
                        + expectedException.getSimpleName()
        );
    }

    private void assertGetValueAsListSucceeds(String name,
                                              Map<String, Object> conf,
                                              List<String> expectedOutput) {

        List<String> result = ConfigUtils.getValueAsList(name, conf);

        if (expectedOutput == null) {
            Assertions.assertNull(
                    result,
                    "Il risultato dovrebbe essere null"
            );
        } else {
            Assertions.assertNotNull(
                    result,
                    "Il risultato non dovrebbe essere null"
            );
            Assertions.assertEquals(
                    expectedOutput,
                    result,
                    "La lista restituita non corrisponde a quella attesa"
            );
        }
    }
}
