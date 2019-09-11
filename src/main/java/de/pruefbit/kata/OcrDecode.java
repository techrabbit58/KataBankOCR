package de.pruefbit.kata;

public class OcrDecode {

    private static final String[] symbols = {
            " _     _  _     _  _  _  _  _ ",
            "| |  | _| _||_||_ |_   ||_||_|",
            "|_|  ||_  _|  | _||_|  ||_| _|",
            "000111222333444555666777888999"
    };

    /**
     * scanLine is assumed to be an array of three strings, each consisting of the same number of characters.
     * If there are different length lines, the length of the first line will give the significant length.
     * Shorter strings at second or third position will cause a runtime error.
     * In any case, the length of all strings is assumed to be divisible by 3. Other cases will cause a
     * runtime error.
     * Only the characters blank, underscore and pipe are allowed in the strings.
     * Decode the three strings to a decimal number.
     * Symbols that can not be decoded, will lead to a runtime error.
     *
     * @param scanLine an array of strings
     * @return the digits recognized, as a string, in scanned order left to right
     */
    public static String decode(String[] scanLine) {
        int symbolPosition = 0;
        int length = scanLine[0].length();
        StringBuilder numberString = new StringBuilder();
        String[] oneDigit = new String[3];
        while (symbolPosition < length) {
            for (int n = 0; n < 3; n += 1) {
                oneDigit[n] = scanLine[n].substring(symbolPosition, symbolPosition + 3);
            }
            numberString.append(decodeOneDigit(oneDigit));
            symbolPosition += 3;
        }
        return numberString.toString();
    }

    private static String decodeOneDigit(String[] oneDigit) {
        int symbolPosition = 0;
        while (symbolPosition < symbols[0].length()) {
            int matchCount = 0;
            for (int n = 0; n < 3; n += 1) {
                String scanLine = oneDigit[n];
                String symbolLine = symbols[n].substring(symbolPosition, symbolPosition + 3);
                if (scanLine.equals(symbolLine)) {
                    matchCount += 1;
                }
            }
            if (matchCount == 3) {
                break;
            }
            symbolPosition += 3;
        }
        if (symbolPosition >= symbols[0].length()) {
            return "?";
        }
        return Character.toString(symbols[3].charAt(symbolPosition));
    }

    /**
     * To validate that the decoded account numbers are in fact valid account numbers, a checksum has to be
     * calculated. The algorithm is as follows:
     *
     * 1 From left to right go through the decode result (i.e.: the account number candidate).
     * 2 Multiply the leftmost digits value by 9, the next by 8 and so on down to the last digit, which
     *   can simply be added (i.e. it is only multiplied by 1).
     * 3 Sum all multiplication results up.
     * 4 The account number is valid if divisible by 11, and not valid in any other case.
     *
     * @param candidate the account number string under test
     * @return result
     */
    public static boolean isAccountNumberValid(String candidate) {
        int checksum = 0;
        for (int n = 9; n > 0; n -= 1) {
            int index = 9 - n;
            String ch = candidate.substring(index, index + 1);
            if (ch.equals("?")) {
                return false;
            }
            checksum += Integer.parseInt(ch) * n;
        }
        return (checksum % 11) == 0;
    }
}
