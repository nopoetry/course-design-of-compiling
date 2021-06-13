package guet.libuyan.com.compile_design.test4.parser;

import guet.libuyan.com.compile_design.test4.lexer.Lexer;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @author lan
 * @create 2021-06-12-16:52
 */
public class ParserTest {

    @Test
    public void test1() {
        Lexer lexer = new Lexer(new File("src/guet/libuyan/com/compile_design/file/compile_correct.txt"));
        Parser parser = new Parser(lexer);
        parser.startParsing();
    }

    @Test
    public void test2() {
        Lexer lexer = new Lexer(new File("src/guet/libuyan/com/compile_design/file/compile_error.txt"));
        Parser parser = new Parser(lexer);
        parser.startParsing();
    }

    @Test
    public void test3() {
        Lexer lexer = new Lexer(new File("src/guet/libuyan/com/compile_design/test4/program1.pl0"));
        Parser parser = new Parser(lexer);
        parser.startParsing();
    }
}
