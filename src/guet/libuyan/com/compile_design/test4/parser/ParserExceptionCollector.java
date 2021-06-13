package guet.libuyan.com.compile_design.test4.parser;

import guet.libuyan.com.compile_design.test4.exception.SyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author lan
 * @create 2021-06-12-17:29
 */
public class ParserExceptionCollector {
    private List<SyntaxException> syntaxExceptionList;

    public ParserExceptionCollector(int initialCapacity) {
        syntaxExceptionList = new ArrayList<>(initialCapacity);
    }

    public ParserExceptionCollector(List<SyntaxException> syntaxExceptionList) {
        this.syntaxExceptionList = syntaxExceptionList;
    }

    public void add(SyntaxException e) {
        syntaxExceptionList.add(e);
    }

    public void addAndShow(SyntaxException e) {
        syntaxExceptionList.add(e);
        System.out.println("\033[31;4m" + e.getMessage() + "\033[0m");
    }

    public List<SyntaxException> getList() {
        return syntaxExceptionList;
    }

    public void showAllSyntaxException() {
        syntaxExceptionList.forEach(e -> System.out.println("\033[31;4m" + e.getMessage() + "\033[0m"));
    }

    public boolean hasSyntaxException() {
        return syntaxExceptionList.size() != 0;
    }
}
