package guet.libuyan.com.compile_design;

import guet.libuyan.com.compile_design.compile.Lexer;
import guet.libuyan.com.compile_design.compile.Parser;
import guet.libuyan.com.compile_design.pojo.Token;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * @author libuyan
 * @date 2021/5/16 17:16
 */
public class Main {
    public static void main(String[] args) {
        File file = null;
        System.out.println("\033[36m" + "请选择你要使用的文件: 1 - 正确的代码文件, 2 - 有错误的代码文件");
        Scanner scanner = new Scanner(System.in);
        int select = scanner.nextInt();
        if (select == 1) {
            file = new File("src/guet/libuyan/com/compile_design/file/compile_correct.txt");
        } else if (select == 2) {
            file = new File("src/guet/libuyan/com/compile_design/file/compile_error.txt");
        }
        Lexer lexer = new Lexer();
        try {
            List<Token> tokenList = lexer.lexer(file);
            Parser parser = new Parser(tokenList);
            parser.parser();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
