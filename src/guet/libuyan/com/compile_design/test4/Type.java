package guet.libuyan.com.compile_design.test4;

/**
 * @author lan
 * @create 2021-06-11-15:44
 */
public enum Type {
    unknown("未知"), identifier("标识符"), number("数字"),
    plus("加号"), minus("减号"), times("乘号"),
    less("小于"), greater("大于"), comma("逗号"),
    eq("等于号"), neq("不等于号"), leq("小于等于"),
    geq("大于等于"), semicolon("分号"), ifsym("if"),
    thensym("then"), whilesym("while"), dosym("do"),
    callsym("call"), constsym("const"), varsym("var"),
    proceduresym("procedure"), beginsym("begin"), endsym("end"),
    assignment("赋值号"), lparen("左括号"), rparen("右括号");

    private final String description;

    Type(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static Type getTypeByName(String name) {
        return valueOf(name + "sym");
    }
}
