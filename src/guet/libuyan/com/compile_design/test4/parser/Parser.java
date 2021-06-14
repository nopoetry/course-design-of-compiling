package guet.libuyan.com.compile_design.test4.parser;

import guet.libuyan.com.compile_design.test4.commons.ReservedWords;
import guet.libuyan.com.compile_design.test4.commons.SymbolTable;
import guet.libuyan.com.compile_design.test4.commons.Type;
import guet.libuyan.com.compile_design.test4.commons.Word;
import guet.libuyan.com.compile_design.test4.exception.SyntaxException;
import guet.libuyan.com.compile_design.test4.lexer.Lexer;

import java.util.ArrayList;
import java.util.List;

import static guet.libuyan.com.compile_design.test4.commons.Type.*;

/**
 * @author lan
 * @create 2021-06-11-21:08
 */
public class Parser {
    private final ParserExceptionCollector exceptionCollector;
    private final Lexer lexer;
    private List<String> semErrors = new ArrayList<>(32);
    private Word word;

    private SymbolTable table = new SymbolTable();
    private int relativeAddr;//符号表相对地址
    private SymbolTable.Symbol symbol;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        exceptionCollector = new ParserExceptionCollector(20);
    }

    /**
     * 语法分析入口
     */
    public void startParsing() {
        boolean success = lexer.analyse();
        if (!success) {
            System.out.println("未通过词法分析");
        }

        if (!success) {
            return;
        }

        word = lexer.getNextWordAndShow();
        parseMainProgram();

        if (!exceptionCollector.hasSyntaxException()) {
            System.out.println("通过语法分析");
        } else {
            System.out.println("未通过语法分析");
        }

        success = false;
        if (semErrors.size() == 0) {
            System.out.println("通过语义分析");
        } else {
            System.out.println("未通过语义分析");
        }

        System.out.println("符号表如下：");
        table.showTable();
    }

    /**
     * <主程序> ::= <分程序>
     * select: #，var，标识符，if，while，begin
     */
    private void parseMainProgram() {
        if (word == null) {
            return;
        }

        switch (word.getType()) {
            case pound:
            case identifier:
            case ifsym:
            case whilesym:
            case beginsym:
            case varsym:
                parseSubProgram();
                break;
            default:
                exceptionCollector.addAndShow(new SyntaxException("#，标识符，if，while，begin", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(pound, varsym, identifier, ifsym, whilesym, beginsym);//select: #，var，标识符，if，while，begin
                if (isDelimiter(word.getType()) && !lexer.hasWord()) {
                    return;
                }
                word = lexer.getNextWordAndShow();
                parseMainProgram();
        }

        if (lexer.hasWord()) {
            word = lexer.getNextWordAndShow();
            parseMainProgram();
        }
    }


    /**
     * <分程序> ::= <变量说明部分> <语句>
     * select: #，var，标识符，if，while，begin
     */
    private void parseSubProgram() {
        if (word == null) {
            return;
        }

        switch (word.getType()) {
            case pound:
            case identifier:
            case ifsym:
            case whilesym:
            case beginsym:
            case varsym:
                parseVariable();
                parseStatement();
                break;
            default:
                exceptionCollector.addAndShow(new SyntaxException("#，标识符，if，while，begin", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(pound, varsym, identifier, ifsym, whilesym, beginsym);
                if (isDelimiter(word.getType())) {
                    return;
                }
                parseSubProgram();
        }
    }

    /**
     * <变量说明部分> ::= VAR 标识符 <更多标识符> ; | <空>
     * select:var
     * select:#，标识符，if，while，begin
     */
    private void parseVariable() {
        if (word == null) {
            return;
        }

        switch (word.getType()) {
            case varsym:
                word = lexer.getNextWordAndShow();
                addToSymbolTable();
                if (word.getType() == Type.identifier) {
                    word = lexer.getNextWordAndShow();
                    parseMoreIdentifiers();
                    if (word.getType() == Type.semicolon) {
                        word = lexer.getNextWordAndShow();
                    } else {
                        exceptionCollector.addAndShow(new SyntaxException(";", word.getName(), word.getRow(), word.getColumn()));
                        skipErrors(semicolon);
                        word = lexer.getNextWordAndShow();
                    }
                } else {
                    exceptionCollector.addAndShow(new SyntaxException("标识符", word.getName(), word.getRow(), word.getColumn()));
                    skipErrors(identifier);

                    addToSymbolTable();
                    word = lexer.getNextWordAndShow();
                    parseMoreIdentifiers();
                    if (word.getType() == Type.semicolon) {
                        word = lexer.getNextWordAndShow();
                    } else {
                        exceptionCollector.addAndShow(new SyntaxException(";", word.getName(), word.getRow(), word.getColumn()));
                        skipErrors(semicolon);
                        word = lexer.getNextWordAndShow();
                    }
                }
                break;
            case pound:
            case identifier:
            case ifsym:
            case whilesym:
            case beginsym:
                break;
            default:
                exceptionCollector.addAndShow(new SyntaxException("#，标识符，if，while，begin", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(varsym, pound, identifier, ifsym, whilesym, beginsym);//varsym,pound,identifier,ifsym,whilesym,beginsym
                if (isDelimiter(word.getType())) {
                    return;
                }
                parseVariable();
        }
    }

    /**
     * <更多标识符> ::= , 标识符 <更多标识符> | <空>
     * select:,
     * select:;
     */
    private void parseMoreIdentifiers() {
        if (word == null) {
            return;
        }

        switch (word.getType()) {
            case comma:
                word = lexer.getNextWordAndShow();
                addToSymbolTable();
                if (word.getType() == Type.identifier) {
                    word = lexer.getNextWordAndShow();
                    parseMoreIdentifiers();
                } else {
                    exceptionCollector.addAndShow(new SyntaxException("标识符", word.getName(), word.getRow(), word.getColumn()));
                    skipErrors(identifier);
                    addToSymbolTable();

                    if (isDelimiter(word.getType())) {
                        return;
                    }
                    word = lexer.getNextWordAndShow();
                    parseMoreIdentifiers();
                }
                break;
            case semicolon:
                break;
            default:
                exceptionCollector.addAndShow(new SyntaxException(",或;", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(comma, semicolon);
                if (isDelimiter(word.getType())) {
                    return;
                }
                parseMoreIdentifiers();
        }
    }

    /**
     * <语句> ::= <赋值语句> | <条件语句> | <当型循环语句> | <复合语句> | <空>
     * select:标识符
     * select：if
     * select：while
     * select：begin
     * select：#,;,end
     */
    private void parseStatement() {
        if (word == null) {
            return;
        }

        switch (word.getType()) {
            case identifier:
                parseAssignmentStatement();
                break;
            case ifsym:
                parseConditionStatement();
                break;
            case whilesym:
                parseWhileStatement();
                break;
            case beginsym:
                parseCompoundStatement();
                break;
            case pound:
            case semicolon:
            case endsym:
                break;
            default:
                exceptionCollector.addAndShow(new SyntaxException("标识符,if,while,begin,#,;,end", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(identifier, ifsym, whilesym, beginsym, pound, semicolon, endsym);
                if (isDelimiter(word.getType())) {
                    return;
                }
                parseStatement();
        }
    }

    /**
     * <赋值语句> ::= 标识符 := <表达式>
     * select:标识符
     */
    private void parseAssignmentStatement() {
        if (word == null) {
            return;
        }

        if (word.getType() == Type.identifier) {
            checkDefinition();

            word = lexer.getNextWordAndShow();
            if (word.getType() == Type.assignment) {
                word = lexer.getNextWordAndShow();
                parseExpression();
            } else {
                exceptionCollector.addAndShow(new SyntaxException("赋值号", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(assignment);

                if (isDelimiter(word.getType())) {
                    return;
                }
                word = lexer.getNextWordAndShow();
                parseExpression();
            }
        } else {
            exceptionCollector.addAndShow(new SyntaxException("标识符", word.getName(), word.getRow(), word.getColumn()));
            skipErrors(identifier);
            checkDefinition();

            if (isDelimiter(word.getType())) {
                return;
            }
            parseAssignmentStatement();
        }
    }

    /**
     * <复合语句> ::= BEGIN <语句> <更多语句> END
     * select:begin
     */
    private void parseCompoundStatement() {
        if (word == null) {
            return;
        }

        if (word.getType() == Type.beginsym) {
            word = lexer.getNextWordAndShow();
            parseStatement();
            parseMoreStatement();
            if (word.getType() == Type.endsym) {
                word = lexer.getNextWordAndShow();
            } else {
                exceptionCollector.addAndShow(new SyntaxException("end", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(endsym);
                word = lexer.getNextWordAndShow();
            }
        } else {
            exceptionCollector.addAndShow(new SyntaxException("begin", word.getName(), word.getRow(), word.getColumn()));
            skipErrors(beginsym);
            if (isDelimiter(word.getType())) {
                return;
            }
            parseCompoundStatement();
        }
    }

    /**
     * <更多语句> ::= ;<更多语句1>
     * select：;
     */
    private void parseMoreStatement() {
        if (word == null) {
            return;
        }

        switch (word.getType()) {
            case semicolon:
                word = lexer.getNextWordAndShow();
                parseMoreStatement1();
                break;
            default:
                exceptionCollector.addAndShow(new SyntaxException(";", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(semicolon);
                if (isDelimiter(word.getType())) {
                    return;
                }
                parseMoreStatement();
        }
    }

    /**
     * <更多语句1> ::= <语句> <更多语句> | <空>
     * select：标识符，if，while，begin，；
     * select：end
     */
    private void parseMoreStatement1() {
        if (word == null) {
            return;
        }

        switch (word.getType()) {
            case identifier:
            case ifsym:
            case whilesym:
            case beginsym:
            case semicolon:
                parseStatement();
                parseMoreStatement();
                break;
            case endsym:
                break;
            default:
                exceptionCollector.addAndShow(new SyntaxException("标识符，if，while，begin，;，end", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(identifier, ifsym, whilesym, beginsym, semicolon, endsym);
                if (isDelimiter(word.getType())) {
                    return;
                }
                parseMoreStatement1();
        }
    }

    /**
     * <条件> ::= <表达式> <关系运算符> <表达式>
     * select：+，-，标识符，整数，（
     */
    private void parseCondition() {
        if (word == null) {
            return;
        }

        switch (word.getType()) {
            case plus:
            case minus:
            case identifier:
            case number:
            case lparen:
                parseExpression();
                parseRelationalOperator();
                parseExpression();
                break;
            default:
                exceptionCollector.addAndShow(new SyntaxException("+，-，标识符，整数，（", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(plus, minus, identifier, number);
                if (isDelimiter(word.getType())) {
                    return;
                }
                parseCondition();
        }
    }

    /**
     * <表达式> ::= + <项> <更多项> | - <项> <更多项> | <项> <更多项>
     * select：+
     * select：-
     * select：标识符，整数，（
     */
    private void parseExpression() {
        if (word == null) {
            return;
        }

        switch (word.getType()) {
            case plus:
            case minus:
                word = lexer.getNextWordAndShow();
                parseItem();
                parseMoreItem();
                break;
            case identifier:
            case number:
            case lparen:
                parseItem();
                parseMoreItem();
                break;
            default:
                exceptionCollector.addAndShow(new SyntaxException("+,-,标识符,整数", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(plus, minus, identifier, lparen, number);
                if (isDelimiter(word.getType())) {
                    return;
                }
                parseExpression();
        }
    }

    /**
     * <更多项> ::= <加法运算符> <项> <更多项> | <空>
     * select：+，-
     * select：=，！=，<，<=，>，>=，），then，do，#，；，end
     */
    private void parseMoreItem() {
        if (word == null) {
            return;
        }

        switch (word.getType()) {
            case plus:
            case minus:
                parseAddingOperator();
                parseItem();
                parseMoreItem();
                break;
            case eq://=，！=，<，<=，>，>=，），then，do，#，；，end
            case neq:
            case less:
            case leq:
            case greater:
            case geq:
            case rparen:
            case thensym:
            case dosym:
            case pound:
            case semicolon:
            case endsym:
                break;
            default:
                //+，-，=，！=，<，<=，>，>=，），then，do，#，；，end
                exceptionCollector.addAndShow(new SyntaxException("+,-,=，！=，<，<=，>，>=，），then，do", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(plus, minus, eq, neq, less, leq, greater, geq, rparen, thensym, dosym, pound, semicolon, endsym);
                if (isDelimiter(word.getType())) {
                    return;
                }
                parseMoreItem();
        }
    }

    /**
     * <项> ::= <因子><更多因子>
     * select:标识符，整数，（
     */
    private void parseItem() {
        if (word == null) {
            return;
        }

        switch (word.getType()) {
            case identifier:
            case number:
            case lparen:
                parseFactor();
                parseMoreFactor();
                break;
            default:
                exceptionCollector.addAndShow(new SyntaxException("标识符，整数，（", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(identifier, number, lparen);
                if (isDelimiter(word.getType())) {
                    return;
                }
                parseItem();
        }
    }

    /**
     * <更多因子> ::= <空> | <乘法运算符> <因子>
     * select:+，-，=，！=，<，<=，>，>=，），then，do，#，；，end
     * select:*，/
     */
    private void parseMoreFactor() {
        if (word == null) {
            return;
        }

        switch (word.getType()) {
            case times:
            case division:
                parseMultiplyingOperator();
                parseFactor();
                break;
            case plus:
            case minus:
            case eq:
            case neq:
            case less:
            case leq://+，-，=，！=，<，<=，>，>=，），then，do
            case greater:
            case geq:
            case rparen:
            case thensym:
            case dosym:
            case pound:
            case semicolon:
            case endsym:
                break;
            default:
                //+，-，=，！=，<，<=，>，>=，），then，do，#，；，end
                exceptionCollector.addAndShow(new SyntaxException("+，-，*，/，=，！=，<，<=，>，>=，），then，do,*，/", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(plus, minus, times, division, eq, neq, less, leq, greater, geq, rparen, thensym, dosym, pound, semicolon, endsym);
                if (isDelimiter(word.getType())) {
                    return;
                }
                parseMoreFactor();
        }
    }

    /**
     * <因子>  ::=  标识符 | 无符号整数 | ‘（’ <表达式> ‘）’
     * select:标识符
     * select:整数
     * select:（
     */
    private void parseFactor() {
        if (word == null) {
            return;
        }

        checkDefinition();
        switch (word.getType()) {
            case identifier:
                word = lexer.getNextWordAndShow();
                break;
            case number:
                word = lexer.getNextWordAndShow();
                break;
            case lparen:
                word = lexer.getNextWordAndShow();
                parseExpression();
                if (word.getType() == Type.rparen) {
                    word = lexer.getNextWordAndShow();
                } else {
                    exceptionCollector.addAndShow(new SyntaxException("）", word.getName(), word.getRow(), word.getColumn()));
                    skipErrors(rparen);
                    word = lexer.getNextWordAndShow();
                }
                break;
            default:
                exceptionCollector.addAndShow(new SyntaxException("标识符,整数,（", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(identifier, number, lparen);
                if (isDelimiter(word.getType())) {
                    return;
                }
                parseFactor();
        }
    }

    /**
     * <加法运算符> ::= + | -
     * select:+ -
     */
    private void parseAddingOperator() {
        if (word == null) {
            return;
        }

        switch (word.getType()) {
            case plus:
                word = lexer.getNextWordAndShow();
                break;
            case minus:
                word = lexer.getNextWordAndShow();
                break;
            default:
                exceptionCollector.addAndShow(new SyntaxException("+,-", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(plus, minus);
                if (isDelimiter(word.getType())) {
                    return;
                }
                parseAddingOperator();
        }
    }

    /**
     * <乘法运算符> ::= * | /
     * select:*，/
     */
    private void parseMultiplyingOperator() {
        if (word == null) {
            return;
        }

        switch (word.getType()) {
            case times:
                word = lexer.getNextWordAndShow();
                break;
            case division:
                word = lexer.getNextWordAndShow();
                break;
            default:
                exceptionCollector.addAndShow(new SyntaxException("*,/", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(times, division);
                if (isDelimiter(word.getType())) {
                    return;
                }
                parseMultiplyingOperator();
        }
    }

    /**
     * <关系运算符> ::= = | != | < | <= | > | >= <hr>
     * select:=，！=，<，<=，>，>=
     */
    private void parseRelationalOperator() {
        if (word == null) {
            return;
        }

        switch (word.getType()) {
            case eq:
            case neq:
            case less:
            case leq:
            case greater:
            case geq:
                word = lexer.getNextWordAndShow();
                break;
            default:
                //=，！=，<，<=，>，>=
                exceptionCollector.addAndShow(new SyntaxException("=，！=，<，<=，>，>=", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(eq, neq, less, leq, greater, geq);
                if (isDelimiter(word.getType())) {
                    return;
                }
                parseRelationalOperator();
        }
    }

    /**
     * <条件语句>  ::= IF <条件> THEN <语句> <hr>
     * select:if
     */
    private void parseConditionStatement() {
        if (word == null) {
            return;
        }

        if (word.getType() == Type.ifsym) {
            word = lexer.getNextWordAndShow();
            parseCondition();
            if (word.getType() == Type.thensym) {
                word = lexer.getNextWordAndShow();
                parseStatement();
            } else {
                exceptionCollector.addAndShow(new SyntaxException("then", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(thensym);
                if (isDelimiter(word.getType())) {
                    return;
                }
                word = lexer.getNextWordAndShow();
                parseStatement();
            }
        } else {
            exceptionCollector.addAndShow(new SyntaxException("if", word.getName(), word.getRow(), word.getColumn()));
            skipErrors(ifsym);
            if (isDelimiter(word.getType())) {
                return;
            }
            parseConditionStatement();
        }
    }

    /**
     * <当型循环语句> ::= WHILE <条件> DO <语句> <hr>
     * select:while
     */
    private void parseWhileStatement() {
        if (word == null) {
            return;
        }

        if (word.getType() == Type.whilesym) {
            word = lexer.getNextWordAndShow();
            parseCondition();
            if (word.getType() == Type.dosym) {
                word = lexer.getNextWordAndShow();
                parseStatement();
            } else {
                exceptionCollector.addAndShow(new SyntaxException("do", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(dosym);
                if (isDelimiter(word.getType())) {
                    return;
                }
                word = lexer.getNextWordAndShow();
                parseStatement();
            }
        } else {
            exceptionCollector.addAndShow(new SyntaxException("while", word.getName(), word.getRow(), word.getColumn()));
            skipErrors(whilesym);
            if (isDelimiter(word.getType())) {
                return;
            }
            parseWhileStatement();
        }
    }

    private void skipErrors(Type... types) {
        if (word != null && !isDelimiter(word.getType())) {
            System.out.println("错误处理，忽略以下单词：" + "\033[37m");
        }

        while (word != null && !isDelimiter(word.getType())) {
            boolean matched = false;

            for (Type type : types) {
                if (type == word.getType()) {
                    matched = true;
                    break;
                }
            }

            if (matched) {
                System.out.println("\033[0m" + word);
                break;
            }

            System.out.println(word);
            word = lexer.getNextWord();
        }

        if (word != null && isDelimiter(word.getType())) {
            System.out.println("\033[0m" + word);
        }
        System.out.print("\033[0m");
    }

    private boolean isDelimiter(Type type) {
        return type == semicolon || type == endsym;
    }

    private void printErrorMsg(String msg) {
        System.out.println("\033[31;4m" + msg + "\033[0m");
    }


    private void printSemError(String msg, int row, int column) {
        String s = msg + " at " + row + ", " + column;
        semErrors.add(s);
        printErrorMsg(s);
    }

    private void addToSymbolTable() {
        if (isDelimiter(word.getType())) {
            return;
        }

        if (ReservedWords.contains(word.getName())) {
            printSemError("语义错误：" + word.getName() + " 与保留字冲突！", word.getRow(), word.getColumn());
            return;
        }

        if ((table.find(word.getName())) != null) {
            printSemError("语义错误：" + word.getName() + " 重复定义！", word.getRow(), word.getColumn());
        } else if (word.getType() != identifier) {
            printSemError("语义错误：" + word.getName() + " 不是合法的标识符！", word.getRow(), word.getColumn());
        } else {
            table.add(word.getName(), word.getType(), 0, ++relativeAddr);
        }
    }

    private void checkDefinition() {
        if (word.getType() == identifier) {
            if ((symbol = table.find(word.getName())) == null) {
                printSemError("语义错误：" + word.getName() + "未定义！", word.getRow(), word.getColumn());
            }
        }
    }
}
