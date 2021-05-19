package guet.libuyan.com.compile_design.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author libuyan
 * @date 2021/5/16 17:57
 */
public class SystemConstants {
    /**
     * 标识符
     */
    public static final short ID = Short.valueOf("10");
    /**
     * 整型
     */
    public static final short INT = Short.valueOf("11");

    /* 错误常量 */
    /**
     * 含有非法字符
     */
    public static final Short ILLEGAL_CHAR = Short.valueOf("-2");
    /**
     * 超出标识符长度 <= 9
     */
    public static final Short OVER_LENGTH = Short.valueOf("-3");
    /**
     * int 超出65535
     */
    public static final Short OVER_INT = Short.valueOf("-4");

    /**
     * 注释格式错误
     */
    public static final Short COMMENT_ERROR = Short.valueOf("-5");

    /**
     * 注释格式错误
     */
    public static final Short COMPARISION_ERROR = Short.valueOf("-6");

    /**
     * 标识符重复
     */
    public static final Short ID_EXIST = Short.valueOf("-7");

    /**
     * 标识符未定义
     */
    public static final Short ID_NOT_EXIST = Short.valueOf("-8");

    /**
     * 标识符未定义
     */
    public static final Short COMPARISION_OPERATOR_ERROR = Short.valueOf("-9");

    /**
     * 冗余代码
     */
    public static final Short REDUNDANT_CODE = Short.valueOf("-10");



    /**
     * int 的最大限制, 65535
     */
    public static final Integer MAX_INT = Integer.valueOf("65535");
    /**
     * 注释
     */
    public static final Short COMMENT = Short.valueOf("0");
    /**
     * ID最大长度
     */
    public static final Integer ID_MAX_LENGTH = Integer.valueOf("8");

    /**
     * 结束符 $
     */
    public static final Short END_CHAR = Short.valueOf("-1");

    public static Map<String, Short> keywordsMap = new HashMap<>();
    public static Map<String, Short> operatorMap = new HashMap<>();
    public static Map<String, Short> separatorMap = new HashMap<>();
    public static Map<Short, String[]> errorMap = new HashMap<>();
    public static Map<Short, String> keywordStrMap = new HashMap<>();
    static {
        String[] keywords = new String[] {"begin", "if", "then", "else", "do", "while", "var", "end", "do"};
        String[] operators = new String[] {"+", "-", "*", "/", ">", "<", "=", ":", ">=", "<=", "<>", ":="};
        String[] separators = new String[] {";", "(", ")", ",", "."};
        for (int i = 0; i < keywords.length; i++) {
            keywordsMap.put(keywords[i], (short) (i + 1));
            keywordStrMap.put((short) (i + 1), "关键字");
        }
        keywordStrMap.put(ID, "标识符");
        keywordStrMap.put(INT, "整型");

        for (int i = 0; i < operators.length; i++) {
            operatorMap.put(operators[i], (short) (i + 101));
            keywordStrMap.put((short) (i + 101), "操作符");
        }

        for (int i = 0; i < separators.length; i++) {
            separatorMap.put(separators[i], (short) (i + 201));
            keywordStrMap.put((short) (i + 201), "分隔符");
        }

        errorMap.put(ILLEGAL_CHAR, new String[]{"非法字符", "非法字符"});
        errorMap.put(OVER_LENGTH, new String[]{"标识符超长", "标识符长度超过长度限制, 应小于等于8"});
        errorMap.put(OVER_INT, new String[]{"数值越界", "整型应小于65535"});
        errorMap.put(COMMENT_ERROR, new String[]{"注释错误", "注释格式错误, 应为 /**/ 或 //"});
        errorMap.put(COMPARISION_ERROR, new String[]{"比较符错误", "比较符"});
    }
}
