package guet.libuyan.com.compile_design.test4;

import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @author lan
 * @create 2021-06-11-15:37
 */
public class LexerTest {
    @Test
    public void test1() {
        System.out.println(ReservedWords.contains("var"));
        System.out.println(ReservedWords.contains("if"));
        System.out.println(ReservedWords.contains("if1"));
    }

    @Test
    public void test2() {
        Lexer lexer = new Lexer("var  a,   b1a, c2cc, d, e;\n" +
                "begin\n" +
                "\n" +
                "  a:=(10 + 1);\n" +
                "\n" +
                "  b1a:=20; /* 这是多行注释 */\n" +
                "\n" +
                "  while a>=10 do\n" +
                "    a := 30;\n" +
                "\n" +
                "  if b1a < c2cc then\n" +
                "\n" +
                "    d := a + 3;\n" +
                "\n" +
                "  else\n" +
                "\n" +
                "    a := a*b1a;");

        while (lexer.hasCharacter()) {
            System.out.println(lexer.getNextWord());
        }
    }

    @Test
    public void test3() {
        Lexer lexer = new Lexer(new File("src/guet/libuyan/com/compile_design/file/compile_correct.txt"));

        while (lexer.hasCharacter()) {
            Word word = lexer.getNextWord();
            if (word != null) {
                System.out.println(word);
            }
        }
        Character ch = null;
    }

    @Test
    public void test4() {
        Lexer lexer = new Lexer(new File("src/guet/libuyan/com/compile_design/file/compile_error.txt"));

        while (lexer.hasCharacter()) {
            Word word = lexer.getNextWord();
            if (word != null) {
                System.out.println(word);
            }
        }
        Character ch = null;
    }
}

