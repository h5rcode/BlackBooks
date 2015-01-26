package com.blackbooks.utils;

/**
 * ISBN numbers utility class.
 */
public final class IsbnUtils {

    /**
     * Private constructor.
     */
    private IsbnUtils() {
    }

    /**
     * Verifies if a given String represents a valid ISBN-10 or ISBN-13 number.
     *
     * @param isbn The String value to check.
     * @return True if the String value represents a valid ISBN-10 or ISBN-13
     * number. False otherwise.
     */
    public static boolean isValidIsbn(String isbn) {
        boolean isValidIsbn = false;
        if (isValidIsbn10(isbn)) {
            isValidIsbn = true;
        } else if (isValidIsbn13(isbn)) {
            isValidIsbn = true;
        }
        return isValidIsbn;
    }

    /**
     * Verifies if a given String represents a valid ISBN-10 number.
     *
     * @param isbn10 The String value to check.
     * @return True if the String value represents a valid ISBN-10 number. False
     * otherwise.
     */
    public static boolean isValidIsbn10(String isbn10) {
        if (isbn10 == null) {
            return false;
        }
        if (isbn10.length() != 10) {
            return false;
        }
        int[] digits = new int[10];
        for (int i = 0; i < isbn10.length(); i++) {
            Character c = isbn10.charAt(i);
            if (Character.isDigit(c)) {
                digits[i] = Integer.parseInt(c.toString());
            } else if (i == 9 && Character.toUpperCase(c) == 'X') {
                digits[i] = 10;
            } else {
                return false;
            }
        }
        int checkSum = 0;
        for (int i = 0; i < digits.length; i++) {
            checkSum += digits[i] * (digits.length - i);
        }
        int mod = checkSum % 11;
        return mod == 0;
    }

    /**
     * Verifies if a given String represents a valid ISBN-13 number.
     *
     * @param isbn13 The String value to check.
     * @return True if the String value represents a valid ISBN-13 number. False
     * otherwise.
     */
    public static boolean isValidIsbn13(String isbn13) {
        if (isbn13 == null) {
            return false;
        }
        if (isbn13.length() != 13) {
            return false;
        }
        if (!isbn13.startsWith("978") && !isbn13.startsWith("979")) {
            return false;
        }
        int[] digits = new int[13];
        for (int i = 0; i < isbn13.length(); i++) {
            Character c = isbn13.charAt(i);
            if (Character.isDigit(c)) {
                digits[i] = Integer.parseInt(c.toString());
            } else {
                return false;
            }
        }
        int checkSum = 0;
        for (int i = 0; i < digits.length; i++) {
            checkSum += digits[i] * (i % 2 == 0 ? 1 : 3);
        }
        int mod = checkSum % 10;
        return mod == 0;
    }
}
