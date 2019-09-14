package de.pruefbit.kata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OcrDecode {

    private static final String[] symbols = {
            " _     _  _     _  _  _  _  _ ",
            "| |  | _| _||_||_ |_   ||_||_|",
            "|_|  ||_  _|  | _||_|  ||_| _|",
            "000111222333444555666777888999"
    };

    private static final Map<Character, char[]> alternatives = new HashMap<>();
    private static final String[] illegalSymbols = {
            "    _  _  _  _  _           _  _  _  _     _  _  _                 _  _  _     _  _  _  _  _     _  _  _     _  _ ",
            "| ||  | || || |  |  |    _| _   | _| _| _|  | _| _  _|| ||_ |_||_ |  |_ |_ |_  _ |_ |_   |   |_| _||_||_||_||_||_|",
            "|_||_||_ | | _||_|     ||_ |_ |_  _ |   _| _|  | _|  |  |  |    _| _| _   ||_||_||_ | |     ||_||_|| ||_  _|  | _ ",
            "000001002003004005006007008009010011012013014015016017018019020021022023024025026027028029030031032033034035036037"
    };
    private static final Map<String, char[]> possibleDigits = new HashMap<>();

    static {
        alternatives.put('0', new char[]{'8'});
        alternatives.put('1', new char[]{'7'});
        alternatives.put('2', new char[]{});
        alternatives.put('3', new char[]{'9'});
        alternatives.put('4', new char[]{});
        alternatives.put('5', new char[]{'6', '9'});
        alternatives.put('6', new char[]{'5', '8'});
        alternatives.put('7', new char[]{'1'});
        alternatives.put('8', new char[]{'0', '6', '9'});
        alternatives.put('9', new char[]{'5', '8'});
    }

    static {
        possibleDigits.put("000", new char[]{'0'});
        possibleDigits.put("001", new char[]{'0', '6'});
        possibleDigits.put("002", new char[]{'0'});
        possibleDigits.put("003", new char[]{'0'});
        possibleDigits.put("004", new char[]{'0', '9'});
        possibleDigits.put("005", new char[]{'0'});
        possibleDigits.put("006", new char[]{'1'});
        possibleDigits.put("007", new char[]{'1'});
        possibleDigits.put("008", new char[]{'2'});
        possibleDigits.put("009", new char[]{'2'});
        possibleDigits.put("010", new char[]{'2'});
        possibleDigits.put("011", new char[]{'2', '3'});
        possibleDigits.put("012", new char[]{'2'});
        possibleDigits.put("013", new char[]{'3'});
        possibleDigits.put("014", new char[]{'3'});
        possibleDigits.put("015", new char[]{'3'});
        possibleDigits.put("016", new char[]{'3', '5'});
        possibleDigits.put("017", new char[]{'4', '1'});
        possibleDigits.put("018", new char[]{'4', '1'});
        possibleDigits.put("019", new char[]{'4'});
        possibleDigits.put("020", new char[]{'4'});
        possibleDigits.put("021", new char[]{'5'});
        possibleDigits.put("022", new char[]{'5'});
        possibleDigits.put("023", new char[]{'5'});
        possibleDigits.put("024", new char[]{'5'});
        possibleDigits.put("025", new char[]{'6'});
        possibleDigits.put("026", new char[]{'6'});
        possibleDigits.put("027", new char[]{'6'});
        possibleDigits.put("028", new char[]{'6'});
        possibleDigits.put("029", new char[]{'7'});
        possibleDigits.put("030", new char[]{'7'});
        possibleDigits.put("031", new char[]{'8'});
        possibleDigits.put("032", new char[]{'8', '2', '3'});
        possibleDigits.put("033", new char[]{'8'});
        possibleDigits.put("034", new char[]{'8', '2'});
        possibleDigits.put("035", new char[]{'9', '4'});
        possibleDigits.put("036", new char[]{'9', '4'});
        possibleDigits.put("037", new char[]{'9'});
    }

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
        while (symbolPosition < length) {
            numberString.append(decodeOneDigit(extractOneSymbol(scanLine, symbolPosition), symbols).charAt(0));
            symbolPosition += 3;
        }
        return numberString.toString();
    }

    private static String[] extractOneSymbol(String[] scanLine, int symbolPosition) {
        String[] oneDigit = new String[3];
        for (int n = 0; n < 3; n += 1) {
            oneDigit[n] = scanLine[n].substring(symbolPosition, symbolPosition + 3);
        }
        return oneDigit;
    }

    private static String decodeOneDigit(String[] oneDigit, String[] symbols) {
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
            return "???";
        }
        return symbols[3].substring(symbolPosition, symbolPosition + 3);
    }

    /**
     * To validate that the decoded account numbers are in fact valid account numbers, a checksum has to be
     * calculated. The algorithm is as follows:
     * <p>
     * 1 From left to right go through the decode result (i.e.: the account number candidate).
     * 2 Multiply the leftmost digits value by 9, the next by 8 and so on down to the last digit, which
     * can simply be added (i.e. it is only multiplied by 1).
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

    /**
     * If an account number is not valid, the caller might want to know if the decoded result
     * has a bad checksum (ERR), or if there are unrecognizable digits in the raw scan result.
     * In case of unreadable digits, the decoder puts "?" into the result string.
     * If only the checksum is invalid, all digits seem to be recognized, but perhaps one
     * of it might have a "_" or "|" too much or might miss one in only one digit.
     * If there is only one "?" in the decoded scan result, the corresponding digit might also
     * lack one element, or might have one element too much.
     * In both cases, some recovery may be possible.
     * The function shall count the illegal digits "?" in the decoded scan result, and shall
     * return the "?" count. A 0 result than indicates "all digits valid" (in case of wrong
     * this means "ERR"), and a number > 0 means "ILL" for illegal characters.
     *
     * @param candidate the result from the decode operation (a string of digits)
     * @return the number of '?' in the given string of digits
     */
    public static long numberOfUnrecognizedDigits(String candidate) {
        return candidate.chars().filter(ch -> ch == '?').count();
    }

    /**
     * Very similar to the "numberOfUnrecognizedDigits()", but this returns a list of
     * character positions where the unrecognized digits sit in the candidate string.
     *
     * @param candidate a string of digits
     * @return a list with the character positions of the "?" characters, with count
     * beginning at 0
     */
    public static List<Integer> unrecognizedDigitsIndex(String candidate) {
        List<Integer> index = new ArrayList<>();
        for (int i = 0; i < candidate.length(); i += 1) {
            if (candidate.charAt(i) == '?') {
                index.add(i);
            }
        }
        return index;
    }

    /**
     * In case, the candidate digits give a wrong checksum, we can try to recover
     * one of the digits after the other, and append it to the alternatives list,
     * if the guess gives a correct checksum.
     *
     * @param candidate is the erroneous digit string to recover
     * @return the list of possible alternatives
     */
    public static List<String> recoverError(String candidate) {
        List<String> results = new ArrayList<>();
        int position = 0;
        int length = candidate.length();
        while (position < length) {
            char[] altDigits = alternatives.get(candidate.charAt(position));
            for (char ch : altDigits) {
                String checkable = candidate.substring(0, position) + ch + candidate.substring(position + 1);
                if (isAccountNumberValid(checkable)) {
                    results.add(checkable);
                }
            }
            position += 1;
        }
        return results;
    }

    /**
     * In case, there is just one single digit not recognized from the raw scan result, a list of alternative digits
     * shall be searched from the illegalSymbols table, so that a recovery can be tried.
     *
     * @param scanLine               the scan line with the illegal symbol
     * @param unrecognizedDigitIndex where the illegal symbol is, in the scan line
     * @return the alternative digit string
     */
    public static char[] findAlternatives(String[] scanLine, int unrecognizedDigitIndex) {
        String[] oneSymbol = extractOneSymbol(scanLine, unrecognizedDigitIndex * 3);
        return possibleDigits.get(decodeOneDigit(oneSymbol, illegalSymbols));
    }

    public static List<String> recoverUnreadable(String candidate, int position, char[] alternatives) {
        List<String> results = new ArrayList<>();
        for (char ch : alternatives) {
            String checkable = candidate.substring(0, position) + ch + candidate.substring(position + 1);
            if (isAccountNumberValid(checkable)) {
                results.add(checkable);
            }
        }
        return results;
    }
}
