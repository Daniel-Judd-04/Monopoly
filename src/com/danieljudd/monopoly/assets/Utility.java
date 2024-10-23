package com.danieljudd.monopoly.assets;

import java.util.Scanner;

public class Utility {

    private static final Scanner input = new Scanner(System.in);

    public static void print(String output) {
        System.out.println(output);
    }
    public static void print(String output, boolean newLine) {
        if (newLine) {
            print(output);
        } else {
            System.out.print(output);
        }
    }

    public static String plural(int num) {
        return (num == 1) ? "" : "s";
    }
    public static String format(int price) { // Maybe add a ',' every thousand
        return Colours.MONEY + "£" + price + Colours.RESET;
    }

    public static String input() {
        return input.nextLine();
    }

    public static int getInt(String output, String[] outputs, int min, int max) {
        print("");
        for (int i = 0; i < outputs.length; i++) {
            print((i + 1) + " - " + outputs[i]);
        }
        System.out.print(output + " [" + min + "-" + max + "] - ");

        int userInput;
        try {
            // Add int protection
            userInput = Integer.parseInt(input.nextLine());

            if (userInput >= min && userInput <= max) {
                return userInput;
            } else {
                return getInt(output, outputs, min, max);
            }
        } catch (Exception e) {
            print("Please enter an int");
            return getInt(output, outputs, min, max);
        }
    }

    public static String findAndReplace(String text, String find, String replace) {
        // Check if the find string is empty or null
        if (find == null || find.isEmpty()) {
            return text; // No find string to replace, return original text
        }

        // Replace all occurrences of the find string with the replace string
        return text.replaceAll(find, replace);
    }

    public static String addIntPadding(int num) {
        String returnNum = String.valueOf(num);
        return " ".repeat(2 - returnNum.length()) + returnNum;
    }
}
