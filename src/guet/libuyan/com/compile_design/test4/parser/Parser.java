package guet.libuyan.com.compile_design.test4.parser;

import guet.libuyan.com.compile_design.test4.commons.FirstAndFollow;
import guet.libuyan.com.compile_design.test4.lexer.Lexer;
import guet.libuyan.com.compile_design.test4.commons.Type;
import guet.libuyan.com.compile_design.test4.commons.Word;
import guet.libuyan.com.compile_design.test4.exception.SyntaxException;

import java.util.Arrays;

import static guet.libuyan.com.compile_design.test4.commons.FirstAndFollow.*;

/**
 * @author lan
 * @create 2021-06-11-21:08
 */
public class Parser {
    private ParserExceptionCollector exceptionCollector;
    private Lexer lexer;
    private Word word;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        exceptionCollector = new ParserExceptionCollector(20);
    }

    /**
     * 语法分析入口
     */
    public void startParsing() {
        word = lexer.getNextWordAndShow();
        parseMainProgram();
        if (!exceptionCollector.hasSyntaxException()) {
            System.out.println("语法分析结束，没有语法错误");
        } else {
            System.out.println("语法分析结束");
        }
    }

    /**
     * <主程序> ::= <分程序>
     * select: #，var，标识符，if，while，begin
     */
    private void parseMainProgram() {
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
                skipErrors(follow_MainProgram);
        }
    }


    /**
     * <分程序> ::= <变量说明部分> <语句>
     * select: #，var，标识符，if，while，begin
     */
    private void parseSubProgram() {
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
                skipErrors(follow_SubProgram);
        }
    }

    /**
     * <变量说明部分> ::= VAR 标识符 <更多标识符> ; | <空>
     * select:var
     * select:#，标识符，if，while，begin
     */
    private void parseVariable() {
        switch (word.getType()) {
            case varsym:
                word = lexer.getNextWordAndShow();
                if (word.getType() == Type.identifier) {
                    word = lexer.getNextWordAndShow();
                    parseMoreIdentifiers();

                    if (word.getType() == Type.semicolon) {
                        word = lexer.getNextWordAndShow();
                    } else {
                        exceptionCollector.addAndShow(new SyntaxException(";", word.getName(), word.getRow(), word.getColumn()));
                        skipErrors(follow_Variable);
                    }
                } else {
                    exceptionCollector.addAndShow(new SyntaxException("var，#，标识符，if，while，begin", word.getName(), word.getRow(), word.getColumn()));
                    skipErrors(follow_Variable);
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
                skipErrors(follow_Variable);
        }
    }

    /**
     * <更多标识符> ::= , 标识符 <更多标识符> | <空>
     * select:,
     * select:;
     */
    private void parseMoreIdentifiers() {
        switch (word.getType()) {
            case comma:
                word = lexer.getNextWordAndShow();
                if (word.getType() == Type.identifier) {
                    word = lexer.getNextWordAndShow();
                    parseMoreIdentifiers();
                } else {
                    exceptionCollector.addAndShow(new SyntaxException("标识符", word.getName(), word.getRow(), word.getColumn()));
                    skipErrors(follow_MoreIdentifiers);
                }
                break;
            case semicolon:
                break;
            default:
                exceptionCollector.addAndShow(new SyntaxException(";", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(follow_MoreIdentifiers);
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
                skipErrors(follow_Statement);
        }
    }

    /**
     * <赋值语句> ::= 标识符 := <表达式>
     * select:标识符
     */
    private void parseAssignmentStatement() {
        if (word.getType() == Type.identifier) {
            word = lexer.getNextWordAndShow();
            if (word.getType() == Type.assignment) {
                word = lexer.getNextWordAndShow();
                parseExpression();
            } else {
                exceptionCollector.addAndShow(new SyntaxException("赋值号", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(follow_AssignmentStatement);
            }
        } else {
            exceptionCollector.addAndShow(new SyntaxException("标识符", word.getName(), word.getRow(), word.getColumn()));
            skipErrors(follow_AssignmentStatement);
        }
    }

    /**
     * <复合语句> ::= BEGIN <语句> <更多语句> END
     * select:begin
     */
    private void parseCompoundStatement() {
        if (word.getType() == Type.beginsym) {
            word = lexer.getNextWordAndShow();
            parseStatement();
            parseMoreStatement();
            if (word.getType() == Type.endsym) {
                word = lexer.getNextWordAndShow();
            } else {
                exceptionCollector.addAndShow(new SyntaxException("end", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(follow_CompoundStatement);
            }
        } else {
            exceptionCollector.addAndShow(new SyntaxException("begin", word.getName(), word.getRow(), word.getColumn()));
            skipErrors(follow_CompoundStatement);
        }
    }

    /**
     * <更多语句> ::= ;<更多语句1>
     * select：;
     */
    private void parseMoreStatement() {
        switch (word.getType()) {
            case semicolon:
                word = lexer.getNextWordAndShow();
                parseMoreStatement1();
                break;
            default:
                exceptionCollector.addAndShow(new SyntaxException(";", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(follow_MoreStatement);
        }
    }

    /**
     * <更多语句1> ::= <语句> <更多语句> | <空>
     * select：标识符，if，while，begin，；
     * select：end
     */
    private void parseMoreStatement1() {
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
                skipErrors(follow_MoreStatement1);
        }
    }

    /**
     * <条件> ::= <表达式> <关系运算符> <表达式>
     * select：+，-，标识符，整数，（
     */
    private void parseCondition() {
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
                skipErrors(follow_Condition);
        }
    }

    /**
     * <表达式> ::= + <项> <更多项> | - <项> <更多项> | <项> <更多项>
     * select：+
     * select：-
     * select：标识符，整数，（
     */
    private void parseExpression() {
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
                skipErrors(follow_Expression);
        }
    }

    /**
     * <更多项> ::= <加法运算符> <项> <更多项> | <空>
     * select：+，-
     * select：=，！=，<，<=，>，>=，），then，do，#，；，end
     */
    private void parseMoreItem() {
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
                exceptionCollector.addAndShow(new SyntaxException("+,-,=，！=，<，<=，>，>=，），then，do", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(follow_MoreItem);
        }
    }

    /**
     * <项> ::= <因子><更多因子>
     * select:标识符，整数，（
     */
    private void parseItem() {
        switch (word.getType()) {
            case identifier:
            case number:
            case lparen:
                parseFactor();
                parseMoreFactor();
                break;
            default:
                exceptionCollector.addAndShow(new SyntaxException("标识符，整数，（", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(follow_Item);
        }
    }

    /**
     * <更多因子> ::= <空> | <乘法运算符> <因子>
     * select:+，-，=，！=，<，<=，>，>=，），then，do，#，；，end
     * select:*，/
     */
    private void parseMoreFactor() {
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
                exceptionCollector.addAndShow(new SyntaxException("+，-，=，！=，<，<=，>，>=，），then，do,*，/", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(follow_MoreFactor);
        }
    }

    /**
     * <因子>  ::=  标识符 | 无符号整数 | ‘（’ <表达式> ‘）’
     * select:标识符
     * select:整数
     * select:（
     */
    private void parseFactor() {
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
                    skipErrors(follow_Factor);
                }
                break;
            default:
                exceptionCollector.addAndShow(new SyntaxException("标识符,整数,（", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(follow_Factor);
        }
    }

    /**
     * <加法运算符> ::= + | -
     * select:+ -
     */
    private void parseAddingOperator() {
        switch (word.getType()) {
            case plus:
                word = lexer.getNextWordAndShow();
                break;
            case minus:
                word = lexer.getNextWordAndShow();
                break;
            default:
                exceptionCollector.addAndShow(new SyntaxException("+,-", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(follow_PlusOperator);
        }
    }

    /**
     * <乘法运算符> ::= * | /
     * select:*，/
     */
    private void parseMultiplyingOperator() {
        switch (word.getType()) {
            case times:
                word = lexer.getNextWordAndShow();
                break;
            case division:
                word = lexer.getNextWordAndShow();
                break;
            default:
                exceptionCollector.addAndShow(new SyntaxException("*,/", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(follow_MultiplyingOperator);
        }
    }

    /**
     * <关系运算符> ::= = | != | < | <= | > | >= <hr>
     * select:=，！=，<，<=，>，>=
     */
    private void parseRelationalOperator() {
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
                exceptionCollector.addAndShow(new SyntaxException("=，！=，<，<=，>，>=", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(follow_RelationalOperator);
        }
    }

    /**
     * <条件语句>  ::= IF <条件> THEN <语句> <hr>
     * select:if
     */
    private void parseConditionStatement() {
        if (word.getType() == Type.ifsym) {
            word = lexer.getNextWordAndShow();
            parseCondition();
            if (word.getType() == Type.thensym) {
                word = lexer.getNextWordAndShow();
                parseStatement();
            } else {
                exceptionCollector.addAndShow(new SyntaxException("then", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(follow_ConditionStatement);
            }
        } else {
            exceptionCollector.addAndShow(new SyntaxException("if", word.getName(), word.getRow(), word.getColumn()));
            skipErrors(follow_ConditionStatement);
        }
    }

    /**
     * <当型循环语句> ::= WHILE <条件> DO <语句> <hr>
     * select:while
     */
    private void parseWhileStatement() {
        if (word.getType() == Type.whilesym) {
            word = lexer.getNextWordAndShow();
            parseCondition();
            if (word.getType() == Type.dosym) {
                word = lexer.getNextWordAndShow();
                parseStatement();
            } else {
                exceptionCollector.addAndShow(new SyntaxException("while", word.getName(), word.getRow(), word.getColumn()));
                skipErrors(follow_WhileStatement);
            }
        } else {
            exceptionCollector.addAndShow(new SyntaxException("while", word.getName(), word.getRow(), word.getColumn()));
            skipErrors(follow_WhileStatement);
        }
    }

    private void skipErrors(Type... types) {
        System.out.println("错误处理，忽略以下单词：" + "\033[37m");
        System.out.println(word);
        while ((word = lexer.getNextWord()) != null) {
            System.out.print("\033[37m");
            boolean matched = false;
            for (Type type : types) {
                if (type == word.getType()) {
                    matched = true;
                    break;
                }
            }

            if (matched) {
                System.out.println("\033[0m" + word);//正常输出
                break;
            } else {
                System.out.println(word);
            }
        }
        System.out.print("\033[0m");
    }
}
