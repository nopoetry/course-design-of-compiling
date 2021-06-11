package guet.libuyan.com.compile_design.test4;

import com.sun.istack.internal.NotNull;

/**
 * 将源程序转为字符流，存储到数组中
 *
 * @author lan
 * @create 2021-06-11-13:49
 */
public class CharList {
    private char[] chars;
    private int index;

    private int column;
    private int row;

    public CharList(@NotNull String programStr) {
        chars = programStr.toCharArray();
    }

    /**
     * 获取当前字符并且指针后移
     *
     * @return null：没有字符
     */
    public Character pop() {
        return index < chars.length ? chars[index++] : null;
    }

    /**
     * 获取当前字符并且指针不移动
     *
     * @return null：没有字符
     */
    public Character top() {
        return index < chars.length ? chars[index] : null;
    }
}
