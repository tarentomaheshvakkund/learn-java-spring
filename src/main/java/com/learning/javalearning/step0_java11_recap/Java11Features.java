package com.learning.javalearning.step0_java11_recap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Java11Features {

  public static void main(String[] args) throws IOException {
    System.out.println("=== Java 11 Features Recap ===\n");

    // 1. String Methods
    stringMethods();

    // 2. Collection toArray
    collectionToArray();

    // 3. Predicate.not()
    predicateNot();

    // 4. Files readString/writeString
    filesMethods();

    // 5. Local Variable Syntax for Lambda
    varInLambda();
  }

  private static void stringMethods() {
    System.out.println("--- 1. String Methods ---");

    // isBlank()
    System.out.println("  \" \".isBlank(): " + " ".isBlank()); // true

    // strip() vs trim()
    String s = "\t Hello \u2005";
    System.out.println("  strip(): '" + s.strip() + "'"); // Removes unicode whitespace
    System.out.println("  trim():  '" + s.trim() + "'"); // Removes ASCII control codes only

    // lines()
    String multiline = "Line 1\nLine 2\nLine 3";
    System.out.println("  lines() count: " + multiline.lines().count());

    // repeat()
    System.out.println("  repeat(): " + "Na".repeat(3) + " Batman!");
    System.out.println();
  }

  private static void collectionToArray() {
    System.out.println("--- 2. Collection toArray ---");
    List<String> list = List.of("Java", "Kotlin", "Scala");

    // Java 11 way (cleaner)
    String[] arr = list.toArray(String[]::new);

    System.out.println("  Array: " + Arrays.toString(arr));
    System.out.println();
  }

  private static void predicateNot() {
    System.out.println("--- 3. Predicate.not() ---");
    List<String> lines = List.of("Java", "", "  ", "Spring");

    List<String> nonEmpty = lines.stream()
        // instead of .filter(s -> !s.isBlank())
        .filter(Predicate.not(String::isBlank))
        .collect(Collectors.toList());

    System.out.println("  Non-blank lines: " + nonEmpty);
    System.out.println();
  }

  private static void filesMethods() throws IOException {
    System.out.println("--- 4. Files readString/writeString ---");
    Path path = Path.of("demo.txt");

    // writeString
    Files.writeString(path, "Hello from Java 11!");

    // readString
    String content = Files.readString(path);
    System.out.println("  File content: " + content);

    Files.deleteIfExists(path);
    System.out.println();
  }

  private static void varInLambda() {
    System.out.println("--- 5. var in Lambda ---");
    List<Integer> nums = List.of(1, 2, 3);

    // Useful principally for adding annotations to lambda parameters
    String result = nums.stream()
        .map((@Deprecated var x) -> String.valueOf(x * 2)) // Annotation support
        .collect(Collectors.joining(", "));

    System.out.println("  Result: " + result);
    System.out.println();
  }
}
