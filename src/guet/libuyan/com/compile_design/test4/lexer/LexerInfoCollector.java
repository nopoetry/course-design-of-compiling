package guet.libuyan.com.compile_design.test4.lexer;

import guet.libuyan.com.compile_design.test4.commons.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lan
 * @create 2021-06-12-18:38
 */
public class LexerInfoCollector {
    private List<Word> words;   //有效单词列表
    private List<String> comments;  //注释列表
    private List<String> errors;    //词法错误列表

    public LexerInfoCollector() {
        words = new ArrayList<>(1024);
        comments = new ArrayList<>(20);
        errors = new ArrayList<>(20);
    }

    public void addComment(String comment) {
        comments.add(comment);
    }

    public void showAllComments() {
        comments.forEach(System.out::println);
    }

    public void addError(String error) {
        errors.add(error);
    }

    public void showAllErrors() {
        errors.forEach(System.out::println);
    }

    public void addWord(Word word) {
        words.add(word);
    }

    public void showAllWords() {
        words.forEach(System.out::println);
    }
}
