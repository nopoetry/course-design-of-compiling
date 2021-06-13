package guet.libuyan.com.compile_design.test4.exception;

/**
 * @author lan
 * @create 2021-06-12-14:56
 */
public class SyntaxException extends Exception {
    public SyntaxException(String message, int row, int column) {
        super("语法错误：" + message + " at " + row + ", " + column);
    }

    public SyntaxException(String needed, String found, int row, int column) {
        super("语法错误：需要 " + needed + " 但是发现了 " + found + " at " + row + ", " + column);
    }

    public SyntaxException(String message) {
        super("语法错误：" + message);
    }
}
