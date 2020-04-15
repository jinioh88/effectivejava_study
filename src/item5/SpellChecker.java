package item5;

import java.util.Objects;

public class SpellChecker {
    private final Lexicon dictionary;

    private SpellChecker(Lexicon dictionary) {
        this.dictionary = Objects.requireNonNull(dictionary);
    }

    public static boolean isValid(String word) {
        return true;
    }
}
