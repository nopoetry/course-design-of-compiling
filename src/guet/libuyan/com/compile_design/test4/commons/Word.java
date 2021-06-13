package guet.libuyan.com.compile_design.test4.commons;

import guet.libuyan.com.compile_design.test4.commons.Type;

/**
 * @author lan
 * @create 2021-06-11-15:52
 */
public class Word {
    private String name;
    private Type type;
    private int value;

    private int row;
    private int column;

    public Word() {
    }

    public Word(String name, Type type, int row, int column) {
        if (name.length() > 8 && type == Type.identifier) {
            throw new RuntimeException("错误：标识符最长为8个字符, " + name + " at " + row + "," + column);
        }

        this.name = name;
        this.type = type;
        this.row = row;
        this.column = column;
    }

    public Word(String name, Type type, int value, int row, int column) {
        if (name.length() > 8 && type == Type.identifier) {
            throw new RuntimeException("错误：标识符最长为8个字符, " + name + " at " + row + "," + column);
        }

        if (value > 65536) {
            throw new RuntimeException("错误：无符号整数的值不能超过65536 at " + row + ", " + column);
        }
        this.value = value;
        this.name = name;
        this.type = type;
        this.row = row;
        this.column = column;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value, int row, int column) {
        if (value > 65536) {
            throw new RuntimeException("错误：无符号整数的值不能超过65536 at " + row + ", " + column);
        }
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

    public void setRowAndColumn(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public String toString() {
        return "Word{" +
                "name=" + String.valueOf(name) +
                " type=" + type +
                " value=" + value +
                " row=" + row +
                " column=" + column +
                '}';
    }
}
