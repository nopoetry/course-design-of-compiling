package guet.libuyan.com.compile_design.compile;


import guet.libuyan.com.compile_design.constants.SystemConstants;
import guet.libuyan.com.compile_design.pojo.Token;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author libuyan
 * @date 2021/5/16 17:18
 * @description to do lexical analysis
 */
public class Lexer {
    private static Map<String, Short> keywordsMap = SystemConstants.keywordsMap;
    private static Map<Short, String> keywordStrMap = SystemConstants.keywordStrMap;
    private static Map<Short, String[]> errorMap = SystemConstants.errorMap;
    private static Map<String, Short> operatorMap = SystemConstants.operatorMap;
    private static Map<String, Short> separatorMap = SystemConstants.separatorMap;

    private int row = 0;
    private int column = 0;
    private char ch;
    private short type = -1;
    private char[] chars;
    private List<Token> tokenList = new ArrayList<>();
    private String line;

    /**
     *
     * @param file which the source code resides
     * @return A list of tokens resulting from lexical analysis
     * @throws IOException read or write exception
     */
    public List<Token> lexer(File file) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            while ((line = bufferedReader.readLine()) != null) {
                row++;
                if (line.isEmpty()) {
                    continue;
                }
                chars = line.toCharArray();
                column = 0;

                while (column < line.length()) {
                    while (column < line.length() && isWhiteSpace()) {
                        column++;
                    }
                    type = -1;
                    if (getToken()) {
                        break;
                    }
                }
            }
        }
        return tokenList;
    }

    private boolean getToken() {
        if (Character.isLetter(ch)) {
            getKeyWord();
        } else if (Character.isDigit(ch)) {
            getNumber();
        } else if (operatorMap.containsKey(String.valueOf(ch))) {
            getOperator();
        } else if (separatorMap.containsKey(String.valueOf(ch))) {
            getSeparator();
        } else if (ch == '$') {
            tokenList.add(new Token((short) -1, "结束符", null, row, column));
            System.out.println("\033[1;93m" + "词法分析成功!");
            return true;
        } else if (isSpecialChar(ch)) {
            getSpecialChar();
        }
        return false;
    }

    /**
     * 特殊字符
     */
    private void getSpecialChar() {
        int startColumn = column + 1;
        column++;
        type = SystemConstants.ILLEGAL_CHAR;
        Token token = setToken(String.valueOf(ch), null, startColumn);
        printToken(token);
    }

    private boolean isWhiteSpace() {
        return (ch = chars[column]) == ' ' || ch == '\t';
    }

    private void getSeparator() {
        int startColumn = column + 1;
        column++;
        type = separatorMap.get(String.valueOf(ch));
        Token token = setToken(String.valueOf(ch), null, startColumn);
        printToken(token);
    }

    /**
     * 获取操作符
     */
    private void getOperator() {
        StringBuilder tokenText = new StringBuilder();
        tokenText.append(ch);
        int startColumn = column + 1;
        column++;
        if (isOperator()) {
            type = operatorMap.get(String.valueOf(ch));
            //说明是注释
            if ((ch = chars[column]) == '*') {
                type = SystemConstants.COMMENT;
                char nextCh;
                do {
                    ch = chars[column++];
                    nextCh = chars[column++];
                    tokenText.append(ch).append(nextCh);
                } while (isCommentEnd(nextCh));

                if (column < chars.length && (ch = chars[column++]) == '/') {
                    tokenText.append(ch);
                } else if (ch != '*' || nextCh != '/'){
                    type = SystemConstants.COMMENT_ERROR;
                }
            }
        } else if (isComparisonOrSemicolon()) {
            type = operatorMap.get(String.valueOf(ch));
            char preCh = ch;
            if ((ch = chars[column++]) == '=') {
                tokenText.append(ch);
                type = operatorMap.get(tokenText.toString());
            } else if (ch == '>') {
                if (preCh != '<') {
                    type = SystemConstants.ILLEGAL_CHAR;
                } else {
                    tokenText.append(ch);
                    type = operatorMap.get(tokenText.toString());
                }
            }
        } else if (ch == '=') {
            type = SystemConstants.ILLEGAL_CHAR;
        }

        Token token = setToken(tokenText.toString(), null, startColumn);
        printToken(token);
    }

    private boolean isCommentEnd(char nextCh) {
        return (column < line.length() - 1 && (ch != '*' || nextCh != '/'));
    }

    private boolean isComparisonOrSemicolon() {
        return ch == '>' || ch == '<' || ch == ':';
    }

    private boolean isOperator() {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    /**
     * 获得标识符(关键字)
     */
    private void getKeyWord() {
        StringBuilder tokenText = new StringBuilder();
        tokenText.append(ch);
        ch = chars[++column];
        int idlength = 0;
        while (hasNextCharInIdWord()) {
            ch = chars[column++];
            tokenText.append(ch);
            idlength++;
        }

        String tokenTextStr = tokenText.toString();
        if (idlength > SystemConstants.ID_MAX_LENGTH) {
            type = SystemConstants.OVER_LENGTH;
        } else {
            type = keywordsMap.getOrDefault(tokenTextStr, SystemConstants.ID);
        }

        int startColumn = column - tokenTextStr.length() + 1;
        Token token = setToken(tokenTextStr, null, startColumn);
        printToken(token);
    }

    private boolean isIllegalChar() {
        return isSpecialChar(ch);
    }

    private boolean hasNextCharInIdWord() {
        return column < line.length() && Character.isLetterOrDigit(ch = chars[column]);
    }

    private Token setToken(String text, String value, int startColumn) {
        Token token = new Token();
        token.setRow(row);
        token.setColumn(startColumn);
        token.setText(text);
        token.setValue(value);
        token.setType(type);
        if (type != SystemConstants.COMMENT && type != SystemConstants.COMMENT_ERROR) {
            tokenList.add(token);
        }
        return token;
    }

    /**
     * 获取数字
     */
    private void getNumber() {
        StringBuilder tokenNumber = new StringBuilder();
        tokenNumber.append(ch);
        int startColumn = column + 1;
        int numberValue;
        ch = chars[++column];
        while (column < line.length() && Character.isDigit(ch)) {
            tokenNumber.append(ch);
            ch = chars[++column];
        }

        String tokenNumberStr = tokenNumber.toString();
        numberValue = Integer.parseInt(tokenNumberStr);
        if (numberValue > SystemConstants.MAX_INT) {
            type = SystemConstants.OVER_INT;
        } else {
            type = SystemConstants.INT;
        }

        Token token = setToken(tokenNumberStr, tokenNumberStr, startColumn);
        printToken(token);
    }

    /**
     * 打印token的相关信息以及错误信息
     *
     * @param token 词法分析得到的二元组
     */
    private void printToken(Token token) {
        if (token.getType() > 0) {
            System.out.print("\033[30m" + "Get a ");
            System.out.print("\033[34m" + token.getText() + " ");
            System.out.print("\033[30m" + "belong to ");
            System.out.println("\033[34m" + keywordStrMap.get(token.getType()));
        } else if(token.getType() < 0) {
            String[] errorStrings = errorMap.get(token.getType());
            System.out.println("\033[31m" + "Error type [" + errorStrings[0] + "] at line [" + token.getRow() + ":" +
                    token.getColumn() + "]: " + errorStrings[1] + ": `" + token.getText() + "`");
        } else {
            System.out.print("\033[30m" + "Get a comment like this: ");
            System.out.println("\033[32m" + token.getText());
        }
    }

    /**
     * 判断是否含有特殊字符
     *
     * @param str 字符串
     * @return true为包含，false为不包含
     */
    private static boolean isSpecialStr(String str) {
        String regEx = "[ _`~!@#%^&|{}':;',\\[\\]./?~！@#￥%……&*（）——|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    private static boolean isSpecialChar(char ch) {
        return isSpecialStr(String.valueOf(ch));
    }
}
