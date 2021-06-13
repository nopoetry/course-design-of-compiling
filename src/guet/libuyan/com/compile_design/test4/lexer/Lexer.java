package guet.libuyan.com.compile_design.test4.lexer;

import guet.libuyan.com.compile_design.test4.commons.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author lan
 * @create 2021-06-11-13:47
 */
public class Lexer {
    private CharList charList;

    private LexerInfoCollector infoCollector = new LexerInfoCollector();

    private int index;
    private int row = 1;
    private int column;

    public boolean analyse() {
        while (hasCharacter()) {
            Word word;

            while ((word = _getNextWord()) == null && hasCharacter()) ;

            if (word != null) {
                infoCollector.addWord(word);
            }
        }

        return infoCollector.getErrors().size() == 0;
    }

    public void showAllWords() {
        infoCollector.showAllWords();
    }

    public Lexer(String program) {
        this.charList = new CharList(program);
    }

    public Lexer(File file) {
        FileReader fr = null;
        StringBuilder builder = null;
        try {
            fr = new FileReader(file);
            builder = new StringBuilder(1024);//预设大小，尽量避免出现数组扩充

            int len;
            char[] buf = new char[1024];
            while ((len = fr.read(buf)) != -1) {
                builder.append(buf, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (builder != null) {
                charList = new CharList(builder.toString());
            }
        }
    }

    /**
     * 从源程序中获取下一个单词
     *
     * @return 单词
     */
    private Word _getNextWord() {
        Word word = null;
        StringBuilder wordName = new StringBuilder();

        Character ch;

        while ((ch = charList.pop()) != null) {
            if (ch == ' ' || ch == '\t') {//跳过空格与制表符
                column++;
            } else if (ch == '\n' || ch == '\r') {//跳过换行
                row++;
                column = 0;
            } else {
                column++;
                break;
            }
        }
        int initialRow = row;
        int initialColumn = column;

        wordName.append(ch);
        if (isLetter(ch)) {
            //字母开头
            while ((ch = charList.top()) != null && (isLetter(ch) || isNumber(ch))) {
                updateRowAndColumn(ch);
                wordName.append(charList.pop());
            }

            if (!ReservedWords.contains(wordName.toString())) {
                try {
                    word = new Word(wordName.toString(), Type.identifier, initialRow, initialColumn);
                } catch (RuntimeException e) {
                    infoCollector.addError(e.getMessage());
                    printErrorMsg(e.getMessage());
                }
            } else {
                word = new Word(wordName.toString(), Type.getTypeByName(wordName.toString()), initialRow, initialColumn);
            }

        } else if (isNumber(ch)) {
            //数字开头
            while ((ch = charList.top()) != null && isNumber(ch)) {
                updateRowAndColumn(ch);
                wordName.append(charList.pop());
            }

            try {
                word = new Word(wordName.toString(), Type.number, Integer.parseInt(wordName.toString()), initialRow, initialColumn);
            } catch (RuntimeException e) {
                infoCollector.addError(e.getMessage());
                printErrorMsg(e.getMessage());
            }
        } else if (ch == ':') {
            //冒号开头
            if ((ch = charList.top()) != null && ch == '=') {
                updateRowAndColumn(ch);
                wordName.append(charList.pop());
                word = new Word(wordName.toString(), Type.assignment, initialRow, initialColumn);
            }
        } else if (ch == '<') {
            //小于号开头
            if ((ch = charList.top()) != null && ch == '=') {
                updateRowAndColumn(ch);
                wordName.append(charList.pop());
                word = new Word(wordName.toString(), Type.leq, initialRow, initialColumn);
            } else {
                word = new Word(wordName.toString(), Type.less, initialRow, initialColumn);
            }
        } else if (ch == '>') {
            //大于号开头
            if ((ch = charList.top()) != null && ch == '=') {
                updateRowAndColumn(ch);
                wordName.append(charList.pop());
                word = new Word(wordName.toString(), Type.geq, initialRow, initialColumn);
            } else {
                word = new Word(wordName.toString(), Type.greater, initialRow, initialColumn);
            }
        } else if (ch == '=') {
            //等于号开头
            word = new Word(wordName.toString(), Type.eq, initialRow, initialColumn);
        } else if (ch == '!') {
            //感叹号开头
            if ((ch = charList.top()) != null && ch == '=') {
                updateRowAndColumn(ch);
                wordName.append(charList.pop());
                word = new Word(wordName.toString(), Type.neq, initialRow, initialColumn);
            } else {
                String errorMsg = "词法错误：非法符号 " + wordName.toString() + " at " + initialRow + " , " + initialColumn;
                infoCollector.addError(errorMsg);
                printErrorMsg(errorMsg);
//                word = new Word(wordName.toString(), Type.unknown, initialRow, initialColumn);
            }
        } else if (ch == ';') {
            //分号开头
            word = new Word(wordName.toString(), Type.semicolon, initialRow, initialColumn);
        } else if (ch == ',') {
            //逗号开头
            word = new Word(wordName.toString(), Type.comma, initialRow, initialColumn);
        } else if (ch == '+') {
            //加号开头
            word = new Word(wordName.toString(), Type.plus, initialRow, initialColumn);
        } else if (ch == '-') {
            //减号开头
            word = new Word(wordName.toString(), Type.minus, initialRow, initialColumn);
        } else if (ch == '*') {
            //星号开头
            word = new Word(wordName.toString(), Type.times, initialRow, initialColumn);
        } else if (ch == '(') {
            //左括号开头
            word = new Word(wordName.toString(), Type.lparen, initialRow, initialColumn);
        } else if (ch == ')') {
            //右括号开头
            word = new Word(wordName.toString(), Type.rparen, initialRow, initialColumn);
        } else if (ch == '#') {
            //结束符
            word = new Word(wordName.toString(), Type.pound, initialRow, initialColumn);
        } else if (ch == '/') {
            //斜线开头
            boolean isCommentsMatched = false;
            if ((ch = charList.top()) != null && ch == '*') {
                updateRowAndColumn(ch);
                wordName.append(charList.pop());

                while ((ch = charList.top()) != null) {
                    boolean finishedLoop = false;

                    while ((ch = charList.top()) != null && ch != '*') {
                        updateRowAndColumn(ch);
                        wordName.append(charList.pop());
                    }
                    //while循环遇到*跳出，但未弹出*，需要使用pop弹出
                    wordName.append(charList.pop());

                    if ((ch = charList.top()) != null && ch == '/') {
                        updateRowAndColumn(ch);
                        wordName.append(charList.pop());
                        finishedLoop = true;
                    }

                    if (finishedLoop) {
                        isCommentsMatched = true;
                        break;
                    }
                }
                if (isCommentsMatched) {
                    String comment = wordName.toString();
                    printCommentMsg("注释：" + comment);
                    infoCollector.addComment(comment);
                } else {
                    String errorMsg = "词法错误：注释符未匹配 at " + initialRow + ", " + initialColumn;
                    infoCollector.addError(errorMsg);
                    printErrorMsg(errorMsg);
                }

            } else if ((ch = charList.top()) != null && ch == '/') {
                updateRowAndColumn(ch);
                wordName.append(charList.pop());

                while ((ch = charList.top()) != null && !isNewLine(ch)) {
                    wordName.append(charList.pop());
                }

                String comment = wordName.toString();
                printCommentMsg("注释：" + comment);
                infoCollector.addComment(comment);
            } else {
                word = new Word(wordName.toString(), Type.division, initialRow, initialColumn);
            }
        } else {
            String errorMsg = "词法错误：非法符号 " + wordName.toString() + " at " + initialRow + " , " + initialColumn;
            infoCollector.addError(errorMsg);
            printErrorMsg(errorMsg);
//            word = new Word(wordName.toString(), Type.unknown, initialRow, initialColumn);
        }

        return word;
    }

    public Word getNextWordAndShow() {
        Word word = infoCollector.getWord(index++);
        if (word != null) {
            System.out.println(word);
        }

        return word;
    }

    public Word getNextWord() {
        return infoCollector.getWord(index++);
    }

    /**
     * 返回词法分析器是否还有字符未处理
     *
     * @return true:是   false:否
     */
    private boolean hasCharacter() {
        return charList.top() != null;
    }

    public boolean hasWord() {
        return infoCollector.hasWord(index);
    }

    /**
     * 判断字符是否是字母
     *
     * @param ch
     * @return true:是   false:否
     */
    private boolean isLetter(Character ch) {
        return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z';
    }

    /**
     * 判断字符是否是数字
     *
     * @param ch
     * @return true:是   false:否
     */
    private boolean isNumber(Character ch) {
        return ch >= '0' && ch <= '9';
    }

    /**
     * 判断字符是否是换行符
     *
     * @param ch
     * @return true:是   false:否
     */
    private boolean isNewLine(Character ch) {
        return ch == '\r' || ch == '\n';
    }

    private void updateRowAndColumn(Character ch) {
        if (ch == '\r' || ch == '\n') {
            row++;
            column = 0;
        } else {
            column++;
        }
    }

    public LexerInfoCollector getInfoCollector() {
        return infoCollector;
    }

    private void printErrorMsg(String msg) {
        System.out.println("\033[31;4m" + msg + "\033[0m");
    }

    private void printCommentMsg(String msg) {
        System.out.println("\033[32;4m" + msg + "\033[0m");
    }
}