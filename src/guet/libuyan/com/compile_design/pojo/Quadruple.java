package guet.libuyan.com.compile_design.pojo;

/**
 * 四元式
 *
 * @author lan
 * @create 2021-05-21-20:28
 */
public class Quadruple {
    //x = y op z    (op, y, z, x )
    String op;
    String arg1;
    String arg2;
    String result;

    public Quadruple() {
    }

    public Quadruple(String op, String arg1, String arg2, String result) {
        this.op = op;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.result = result;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getArg1() {
        return arg1;
    }

    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    public String getArg2() {
        return arg2;
    }

    public void setArg2(String arg2) {
        this.arg2 = arg2;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Quadruple{" +
                "op='" + op + '\'' +
                ", arg1='" + arg1 + '\'' +
                ", arg2='" + arg2 + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
