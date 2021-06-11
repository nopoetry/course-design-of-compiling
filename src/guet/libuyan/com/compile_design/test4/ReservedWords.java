package guet.libuyan.com.compile_design.test4;

import java.util.Arrays;
import java.util.List;

/**
 * 存放保留字
 *
 * @author lan
 * @create 2021-06-11-12:26
 */
public class ReservedWords {
    private static final List<String> words =
            Arrays.asList("const", "var", "procedure", "begin", "end", "if", "then", "call", "while", "do");

    public static boolean contains(String word) {
        return words.contains(word);
    }

    public static boolean contains(char[] word) {
        return words.contains(String.valueOf(word));
    }
}
