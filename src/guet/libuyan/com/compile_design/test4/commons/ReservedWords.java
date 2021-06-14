package guet.libuyan.com.compile_design.test4.commons;

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

    /**
     * 通过传入单词名称，判断是否是保留字
     *
     * @param word
     * @return 是/否
     */
    public static boolean contains(String word) {
        return words.contains(word);
    }
}
