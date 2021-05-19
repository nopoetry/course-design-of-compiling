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
/*        //缺少分号
        if (getCurToken().getType() != separatorMap.get(";")) {
            errorHandle(0, "");
        } else {
            tokenPtr++;
        }*/
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
/*            if (getCurToken().getType() == keywordsMap.get("then")) {
                tokenPtr++;
                parseStatement();
                if (getCurToken().getType() == keywordsMap.get("else")) {
                    tokenPtr++;
                    parseStatement();
                }
            } else {
                errorHandle(8, "");
            }*/
        } else if (getCurToken().getType() == keywordsMap.get("while")) {
            //<当循环语句> ::= while <条件> do <语句>
            tokenPtr++;
            parseConditional();
            matchToken(keywordsMap.get("do"), "do");
            parseStatement();
/*            if (getCurToken().getType() == keywordsMap.get("do")) {
                tokenPtr++;
                parseStatement();
            } else {
                errorHandle(9, "");
            }*/
        } else if (getCurToken().getType() == keywordsMap.get("begin")) {
            //<复合语句> ::= begin <语句> {;<语句>} end
            parseCompoundStatement();
        } else if (getCurToken().getType() == SystemConstants.ID) {
            //<赋值语句> ::= <标识符> := <表达式>
            judgeTokenExist();
            tokenPtr++;
            matchToken(operatorMap.get(":="), ":=");
            expression();

/*            if (getCurToken().getType() == operatorMap.get(":=")) {
                tokenPtr++;
                expression();
            } else {
                errorHandle(3, "");
            }*/
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
/*            if (getCurToken().getType() == keywordsMap.get("end")) {
                tokenPtr++;
            } else { //缺少end
                errorHandle(7, "");
            }*/
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

/*    private void parseCompoundStatement() {
        //<复合语句> ::= begin <语句> {;<语句>} end
        if (getCurToken().getType() == keywordsMap.get("begin")) {
            tokenPtr++;
            parseStatement();
            while (getCurToken().getType() == separatorMap.get(";") || isHeadOfStatement()) {
                if (getCurToken().getType() == separatorMap.get(";")) {
                    tokenPtr++;
                } else {
                    if (getCurToken().getType() != keywordsMap.get("end")) {
                        errorHandle(0, "");
                    }
                }
                if (getCurToken().getType() == keywordsMap.get("end")) {
                    errorHandle(21, "");
                    break;
                }
                parseStatement();
            }
            if (getCurToken().getType() == keywordsMap.get("end")) {
                tokenPtr++;
            } else { //缺少end
                errorHandle(7, "");
            }
        } else { //缺少begin
            errorHandle(6, "");
        }
    }*/

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
        factor();
        while (getCurToken().getType() == operatorMap.get("*") ||
                getCurToken().getType() == operatorMap.get("/")) {
            tokenPtr++;
            factor();
        }
    }

    /**
     * <因子> ::= <标识符> | <无符号整数> | '('<表达式>')'
     */
    private void factor() {
        if (getCurToken().getType() == SystemConstants.ID) {
            tokenPtr++;
        } else if (getCurToken().getType() == SystemConstants.INT) {
            tokenPtr++;
        } else if (getCurToken().getType() == separatorMap.get("(")) {
            tokenPtr++;
            expression();
            matchToken(separatorMap.get(")"), ")");
/*            if (getCurToken().getType() == separatorMap.get(")")) {
                tokenPtr++;
            } else {
                errorHandle(5, "");
            }*/
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

/*    private void errorHandle(int k, String name) {
        errorHappen = true;
        String error = "";
        switch (k) {
            case -1: //常量定义不是const开头，变量定义不是var开头
                error = "Error happened in line " + getCurToken().getRow() + ":" + "wrong token";
                break;
            case 0: //缺少分号
                if (getCurToken().getType() == separatorMap.get(";")) {
                    error = "Error happened in line " + getCurToken().getRow() + ":" + "Missing ; before " + getCurToken().getText();
                } else {
                    error = "Error happened in line " + getCurToken().getRow() + ":" + "Missing ; before " + getCurToken().getType();
                }
                break;
            case 1: //标识符不合法
                error = "Error happened in line " + getCurToken().getRow() + ":" + "Identifier illegal";
                break;
            case 2: //不合法的比较符
                error = "Error happened in line " + getCurToken().getRow() + ":" + "illegal compare symbol";
                break;
            case 3: //常量赋值没用=
                error = "Error happened in line " + getCurToken().getRow() + ":" + "Const assign must be =";
                break;
            case 4: //缺少（
                error = "Error happened in line " + getCurToken().getRow() + ":" + "Missing (";
                break;
            case 5: //缺少）
                error = "Error happened in line " + getCurToken().getRow() + ":" + "Missing )";
                break;
            case 6: //缺少begin
                error = "Error happened in line " + getCurToken().getRow() + ":" + "Missing begin";
                break;
            case 7: //缺少end
                error = "Error happened in line " + getCurToken().getRow() + ":" + "Missing end";
                break;
            case 8: //缺少then
                error = "Error happened in line " + getCurToken().getRow() + ":" + "Missing then";
                break;
            case 9: //缺少do
                error = "Error happened in line " + getCurToken().getRow() + ":" + "Missing do";
                break;
            case 10: //call, write, read语句中，不存在标识符
                error = "Error happened in line " + getCurToken().getRow() + ":" + "Not exist" + getCurToken().getText();
                break;
            case 11: //该标识符不是proc类型
                error = "Error happened in line " + getCurToken().getRow() + ":" + getCurToken().getText() + "is not " +
                        "a procedure";
                break;
            case 12: //read, write语句中，该标识符不是var类型
                error = "Error happened in line " + getCurToken().getRow() + ":" + getCurToken().getText() + "is not " +
                        "a variable";
                break;
            case 13: //赋值语句中，该标识符不是var类型
                error = "Error happened in line " + getCurToken().getRow() + ":" + name + "is not a varible";
                break;
            case 14: //赋值语句中，该标识符不存在
                error = "Error happened in line " + getCurToken().getRow() + ":" + "not exist " + name;
                break;
            case 15: //该标识符已存在
                error = "Error happened in line " + getCurToken().getRow() + ":" + "Already exist " + name;
                break;
            case 16: //调用函数参数错误
                error = "Error happened in line " + getCurToken().getRow() + ":" + "Number of parameters of" +
                        " procedure " + name + "is incorrect";
                break;
            case 17: //缺少end 后的结束符
                error = "Error happened in line " + getCurToken().getRow() + ":" + "Missing .(End 后的 '.')";
                break;
            case 18: //多余代码
                error = "Error happened in line " + getCurToken().getRow() + ":" + "too much code after .";
                break;
            case 19: //缺少until
                error = "Error happened in line " + getCurToken().getRow() + ":" + "Missing until";
                break;
            case 20: //赋值符应为：=
                error = "Error happened in line " + getCurToken().getRow() + ":" + "Assign must be :=";
                break;
            case 21: //end前多了；
                error = "Error happened in line " + getCurToken().getRow() + ":" + "; is no need before end";
                break;
            case 22: //until前多了；
                error = "Error happened in line " + getCurToken().getRow() + ":" + "; is no need before " +
                        "ubtil";
                break;
            case 23: //缺少,
                error = "Error happened in line " + getCurToken().getRow() + ":" + "Missing ,";
                break;
            default:
                break;
        }
        errorMessage.add(error);
    }*/
}
