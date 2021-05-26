package guet.libuyan.com.compile_design.compile;

import guet.libuyan.com.compile_design.constants.SystemConstants;
import guet.libuyan.com.compile_design.pojo.AllSymbol;
import guet.libuyan.com.compile_design.pojo.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author libuyan
 * @date 2021/5/17 22:59
 * @description syntactic analysis
 */
public class Parser {
    private List<Token> tokenList;
    //符号表管理
    private AllSymbol allSymbol;
    private static Map<String, Short> keywordsMap = SystemConstants.keywordsMap;
    private static Map<Short, String> keywordStrMap = SystemConstants.keywordStrMap;
    private static Map<String, Short> separatorMap = SystemConstants.separatorMap;
    private static Map<String, Short> operatorMap = SystemConstants.operatorMap;
    /**
     * 指向当前token的指针
     */
    private int tokenPtr = 0;

    private Token curToken;
    /**
     * 是否发生错误
     */
    private boolean errorHappen = false;
    /**
     * 保存错误信息的集合
     */
    private List<String> errorMessage;

    private int level = 0;
    private int address = 0;
    private int addIncrement = 1;


    public Parser(List<Token> tokenList) {
        this.tokenList = tokenList;
        allSymbol = new AllSymbol();
        errorMessage = new ArrayList<>();
    }

    private Token getCurToken() {
        curToken = tokenList.get(tokenPtr);
        return curToken;
    }

    public void parser() {
        mainProgram();
        printParserError();
    }

    private void printParserError() {
        if (errorMessage.isEmpty()) {
            System.out.println("\033[1;92m" + "语法分析成功!");
        } else {
            errorMessage.forEach(errorMessage -> System.out.println("\033[31m" + errorMessage));
        }
    }

    /**
     * <主程序>::=<分程序>.
     */
    private void mainProgram() {
        block();
        parsePoint();
    }

    private void parsePoint() {
        matchToken(separatorMap.get("."), ".");
        if (!matchToken(SystemConstants.END_CHAR, "$")) {
            errorHandle(SystemConstants.REDUNDANT_CODE, getCurToken(), "");
        }
    }

    /**
     * <分程序>::=[<常量说明部分>][<变量说明部分>][<过程说明部分>]<语句>
     */
    private void block() {
        //常量说明部分
        if (getCurToken().getType() == keywordsMap.get("var")) {
            varDeclare();
        }
        parseStatement();
    }

    /**
     * <变量说明部分> ::= var<标识符> {, <标识符>}
     */
    private void varDeclare() {
        matchToken(keywordsMap.get("var"), "var");
        parseId();
        parseSelectableId();
    }

    /**
     * <变量说明部分> --- <标识符>
     */
    private void parseId() {
        String tokenText = getCurToken().getText();
        if (matchToken(SystemConstants.ID, null)) {
            checkAndAddSymbol(tokenText);
        }
    }

    private void checkAndAddSymbol(String tokenText) {
        if (allSymbol.isNowExists(tokenText, level)) {
            errorHandle(SystemConstants.ID_EXIST, curToken, tokenText);
        } else {
            allSymbol.enterVar(tokenText, level, address);
        }
        address += addIncrement;
    }

    /**
     * <变量说明部分> --- ... {, <标识符>} ...
     */
    private void parseSelectableId() {
        while (getCurToken().getType() == separatorMap.get(",") ||
                getCurToken().getType() == SystemConstants.ID) {
            if (getCurToken().getType() == separatorMap.get(",")) {
                tokenPtr++;
            } else {
                errorHandle(separatorMap.get(","), curToken, ".");
            }
            parseId();
        }
        matchToken(separatorMap.get(";"), ";");
    }

    private boolean matchToken(Short expectedType, String expectedTokenText) {
        Token token = getCurToken();
        if (token.getType() != expectedType) {
            errorHandle(expectedType, token, expectedTokenText);
            return false;
        }
        tokenPtr++;
        return true;
    }

    /**
     * <语句> ::= <赋值语句> | <条件语句> | <当循环语句> | <过程调用语句> | <复合语句> | <读语句> |
     * <写语句> | <复合语句> | <空语句>
     */
    private void parseStatement() {
        //<条件语句> ::= if <条件> then <语句> else <语句>
        if (getCurToken().getType() == keywordsMap.get("if")) {
            tokenPtr++;
            parseConditional();
            matchToken(keywordsMap.get("then"), "then");
            parseStatement();
            matchToken(keywordsMap.get("else"), "else");
            parseStatement();
        } else if (getCurToken().getType() == keywordsMap.get("while")) {
            //<当循环语句> ::= while <条件> do <语句>
            tokenPtr++;
            parseConditional();
            matchToken(keywordsMap.get("do"), "do");
            parseStatement();
        } else if (getCurToken().getType() == keywordsMap.get("begin")) {
            //<复合语句> ::= begin <语句> {;<语句>} end
            parseCompoundStatement();
        } else if (getCurToken().getType() == SystemConstants.ID) {
            //<赋值语句> ::= <标识符> := <表达式>
            judgeTokenExist();
            tokenPtr++;
            matchToken(operatorMap.get(":="), ":=");
            expression();
        } else {
            errorHandle(SystemConstants.ID, curToken, "");
            tokenPtr++;
        }

        if (getCurToken().getType() == separatorMap.get(";")) {
            tokenPtr++;
        }
    }

    private void judgeTokenExist() {
        String name = getCurToken().getText();
        if (!allSymbol.isPreExists(name, level)) {
            errorHandle(SystemConstants.ID_NOT_EXIST, curToken, "");
        }
    }

    /**
     *<复合语句> ::= begin <语句> {;<语句>} end
     */
    private void parseCompoundStatement() {
        if (getCurToken().getType() == keywordsMap.get("begin")) {
            tokenPtr++;
            parseStatement();
            parseSelectableStatement();
            matchToken(keywordsMap.get("end"), "end");
        }
    }

    /**
     *<复合语句> --- ... {;<语句>} ...
     */
    private void parseSelectableStatement() {
        while (isHeadOfStatement()) {
//            if (getCurToken().getType() == keywordsMap.get("end")) {
//                errorHandle(21, "");
//                break;
//            }
            parseStatement();
        }
    }


    private boolean isHeadOfStatement() {
        return (getCurToken().getType() == keywordsMap.get("if") ||
                getCurToken().getType() == keywordsMap.get("while") ||
                getCurToken().getType() == keywordsMap.get("begin") ||
                getCurToken().getType() == SystemConstants.ID);
    }


    /**
     * <条件> ::= <表达式> <关系运算符> <表达式> | odd <表达式>
     */
    private void parseConditional() {
        expression();
        parseRelational();
        expression();
    }

    /**
     * <表达式>::=[+|-]<项>{<加法运算符><项>}
     * <加法运算符>::=+|-
     */
    private void expression() {
        short tokenType = getCurToken().getType();
        if (tokenType == operatorMap.get("+") || tokenType == operatorMap.get("-")) {
            tokenPtr++;
        }
        parseTerm();
        parseSelectableTerm();
    }

    /**
     * <表达式> ::=  ...{<加法运算符><项>}
     */
    private void parseSelectableTerm() {
        while (isAddOrSubOperator()) {
            tokenPtr++;
            parseTerm();
        }
    }

    private boolean isAddOrSubOperator() {
        return getCurToken().getType() == operatorMap.get("+") ||
                getCurToken().getType() == operatorMap.get("-");
    }

    /**
     * <关系运算符> ::= = | > | >= | < | <=
     */
    private void parseRelational() {
        if (!isRelationalOperator(getCurToken().getType())) {
            errorHandle(SystemConstants.COMPARISION_OPERATOR_ERROR, curToken, "");
        }
        tokenPtr++;
    }

    /**
     * 判断是否是关系运算符
     *
     * @return boolean
     */
    private boolean isRelationalOperator(short tokenType) {
        return tokenType == operatorMap.get("=") || tokenType == operatorMap.get(">") ||
                tokenType == operatorMap.get(">=") || tokenType == operatorMap.get("<") ||
                tokenType == operatorMap.get("<=");
    }

    /**
     * <项> ::= <因子> {<乘法运算符> <因子>}
     * <乘法运算符> ::= * | /
     */
    private void parseTerm() {
        parseFactor();
        while (getCurToken().getType() == operatorMap.get("*") ||
                getCurToken().getType() == operatorMap.get("/")) {
            tokenPtr++;
            parseFactor();
        }
    }

    /**
     * <因子> ::= <标识符> | <无符号整数> | '('<表达式>')'
     */
    private void parseFactor() {
        if (getCurToken().getType() == SystemConstants.ID) {
            tokenPtr++;
        } else if (getCurToken().getType() == SystemConstants.INT) {
            tokenPtr++;
        } else if (getCurToken().getType() == separatorMap.get("(")) {
            tokenPtr++;
            expression();
            matchToken(separatorMap.get(")"), ")");
        } else {
            errorHandle(SystemConstants.ID, curToken, "");
        }
    }

    private void errorHandle(short type, Token token, String expectedTokenText) {
        String error = "";
        if (type == SystemConstants.ID) {
            error = "Error happened in line: [" + token.getRow() + ":" + token.getColumn() + "] " +
                    "Identifier `"+ token.getText() + "` illegal";
        } else if (keywordStrMap.containsKey(type)) {
            error = "Error happened in line: [" + token.getRow() + ":" + token.getColumn() + "] " +
                    "require a `" + expectedTokenText + "`, but found a `" + token.getText() + "`";
        } else if (type == SystemConstants.ID_EXIST) {
            error = "Error happened in line: [" + token.getRow() + ":" + token.getColumn() + "] " +
                    "Identifier `"+ token.getText() + "` already exist";
        } else if (type == SystemConstants.ID_NOT_EXIST) {
            error = "Error happened in line: [" + token.getRow() + ":" + token.getColumn() + "] " +
                    "Identifier `"+ token.getText() + "` undefined";
        } else if (type == SystemConstants.COMPARISION_ERROR) {
            error = "Error happened in line: [" + token.getRow() + ":" + token.getColumn() + "] " +
                    "comparision operator `"+ token.getText() + "` undefined";
        } else if (type == SystemConstants.REDUNDANT_CODE) {
            error = "Error happened in line: [" + token.getRow() + ":" + token.getColumn() + "] " +
                    "redundant code: `" + token.getText() + "`";
        }
        errorMessage.add(error);
    }
}
