package guet.libuyan.com.compile_design.pojo;

/**
 * @author libuyan
 * @date 2021/5/16 17:09
 * @description 词法分析 单词的二元组
 */
public class Token {
    /**
     * 类型码
     */
    private short type;

    /**
     * 标志位/保留字的text
     */
    private String text;

    /**
     * 数字的值
     */
    private String value;

    /**
     * 行号
     */
    private int row;

    /**
     * 列号
     */
    private int column;

    public Token() {
    }

    public Token(short type, String text, String value, int row, int column) {
        this.type = type;
        this.text = text;
        this.value = value;
        this.row = row;
        this.column = column;
    }
    public short getType() {
        return type;
    }
    public void setType(short type) {
        this.type = type;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public int getRow() {
        return row;
    }
    public void setRow(int row) {
        this.row = row;
    }
    public int getColumn() {
        return column;
    }
    public void setColumn(int column) {
        this.column = column;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", text='" + text + '\'' +
                ", value='" + value + '\'' +
                ", row=" + row +
                ", column=" + column +
                '}';
    }
}
