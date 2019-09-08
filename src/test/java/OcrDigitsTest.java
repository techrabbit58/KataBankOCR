import de.pruefbit.kata.OcrDigits;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OcrDigitsTest {

    /**
     * All the input files used here for testing of user stories 1, 3 and 4 were prepared by another project.
     * I copied all the data from there.
     * Please follow the link for reference.
     * https://github.com/dvrensk/bank_ocr_kata/tree/master/fixtures
     */

    private static final String US1_INPUT_FILENAME = "use_case_1_in.txt";
    private static final String EXPECTED_US1_RESULTS_FILENAME = "use_case_1_out.txt";
    private static final String US3_INPUT_FILENAME = "use_case_3_in.txt";
    private static final String EXPECTED_US3_RESULTS_FILENAME = "use_case_3_out.txt";
    private static final List<String> expectedUs1Results = new ArrayList<>();
    private static final List<String> expectedUs3Results = new ArrayList<>();

    static BufferedReader open(String fn) throws FileNotFoundException {
        return new BufferedReader(new FileReader(ClassLoader.getSystemResource(fn).getFile()));
    }

    /**
     * To set up the test environment, we must first create lists with our expected outcomes.
     */
    @BeforeAll
    static void initAll() throws IOException {
        loadExpectedResults(EXPECTED_US1_RESULTS_FILENAME, expectedUs1Results);
        loadExpectedResults(EXPECTED_US3_RESULTS_FILENAME, expectedUs3Results);
    }

    private static void loadExpectedResults(
            String expectedResultsFilename, List<String> expectedResults) throws IOException {
        BufferedReader expectedNumbers = open(expectedResultsFilename);
        String s;
        while ((s = expectedNumbers.readLine()) != null) {
            expectedResults.add(s);
        }
        expectedNumbers.close();
    }

    /**
     * User Story 1
     *
     * An input file is valid, if ...
     * - all its lines are 27 characters long (if ignoring the line break symbols)
     * - all lines consist only of blanks, underscores and pipe characters (" _|")
     * - its line count is greater than 0
     * - its line count is a multiple of 4
     */
    @Test
    void inputFileHasValidFormat() throws IOException {
        BufferedReader fileUnderTest = open(US1_INPUT_FILENAME);
        Set<Character> allowedCharacters = new HashSet<>(Arrays.asList(' ', '_', '|'));
        int linesInFile = 0;
        String s;
        while ((s = fileUnderTest.readLine()) != null) {
            for (char ch : s.toCharArray()) {
                assertTrue(allowedCharacters.contains(ch));
            }
            assertEquals(27, s.length());
            linesInFile += 1;
        }
        assertTrue(linesInFile > 0);
        assertEquals(0, linesInFile % 4);
        fileUnderTest.close();
    }

    /**
     * User Story 1
     *
     * A scan consists of four lines, with the line separator already stripped.
     * A scan is valid, if it has three lines with 27 characters each, that can be
     * decoded to 9 consecutive digits, and if the fourth line appears to be 27
     * consecutive blanks.
     * In any other case the single scan is considered broken, and the test shall fail.
     */
    @Test
    void allScansInFileAreRecognizableNumbers() throws IOException {
        BufferedReader fileUnderTest = open(US1_INPUT_FILENAME);
        String s;
        String[] scanLine = new String[3];
        int scanLineNumber = 0;
        int relativeLineNumber = 0;
        String actualResult;
        while ((s = fileUnderTest.readLine()) != null) {
            if (relativeLineNumber < 3) {
                scanLine[relativeLineNumber] = s;
            } else {
                assertTrue(s.chars().allMatch(c -> c == ' ') && s.chars().count() == 27);
                actualResult = OcrDigits.decode(scanLine);
                assertEquals(expectedUs1Results.get(scanLineNumber), actualResult);
                scanLineNumber += 1;
            }
            relativeLineNumber = (relativeLineNumber + 1) % 4;
        }
        fileUnderTest.close();
    }

    /**
     * User Story 2
     *
     * To validate that the numbers you read are in fact valid account numbers, the checksums of the
     * decoded scan results must be checked.
     * The checksum for a given decoded scan result shall be checked by the isAccountNumberValid function
     * of the OcrDigits class.
     *
     * @param candidate ... to be used as input to the test
     * @param expectation ... about the outcome of the test
     */
    @ParameterizedTest
    @CsvSource({
            "345882865, true",
            "123456789, true",
            "987654321, false",
            "111111111, false",
            "198473192, true",
            "000000051, true",
            "000000057, false",
    })
    void checksumValidation(String candidate, boolean expectation) {
        assertEquals(expectation, OcrDigits.isAccountNumberValid(candidate));
    }

    /**
     * User Story 3
     *
     * Test if we can recognize illegal (ILL) or erroneous (ERR) account numbers.
     * ILL numbers ar those that contain '?' characters where digits shall be.
     * ERR are well formed numbers, but have not passed the checksum test.
     * The "decode()" routine shall reliably recognize ILL and ERR, and shall mark such
     * numbers with the appropriate tag.
     */
    @Test
    void recognizeIllegalOrErrorCorrectly() throws IOException {
        BufferedReader fileUnderTest = open(US3_INPUT_FILENAME);
        String s;
        String[] scanLine = new String[3];
        int scanLineNumber = 0;
        int relativeLineNumber = 0;
        String actualResult;
        while ((s = fileUnderTest.readLine()) != null) {
            if (relativeLineNumber < 3) {
                scanLine[relativeLineNumber] = s;
            } else {
                assertTrue(s.chars().allMatch(c -> c == ' ') && s.chars().count() == 27);
                actualResult = OcrDigits.decode(scanLine);
                if (!OcrDigits.isAccountNumberValid(actualResult)) {
                    actualResult += actualResult.contains("?") ? " ILL" : " ERR";
                }
                assertEquals(expectedUs3Results.get(scanLineNumber), actualResult);
                scanLineNumber += 1;
            }
            relativeLineNumber = (relativeLineNumber + 1) % 4;
        }
        fileUnderTest.close();
    }

}
