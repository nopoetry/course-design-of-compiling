package guet.libuyan.com.compile_design.test4.commons;

import guet.libuyan.com.compile_design.test4.commons.Type;

import java.util.*;

/**
 * 符号表数据结构
 *
 * @author lan
 * @create 2021-06-13-19:53
 */
public class SymbolTable {
    private Map<String, Symbol> table = new LinkedHashMap<>(32);


    public void add(String name, Type type, int value, int addr) {
        table.put(name, new Symbol(name, type, value, addr));
    }

    public Symbol find(String name) {
        return table.get(name);
    }

    public void showTable() {
        table.values().forEach(System.out::println);
    }


    public static class Symbol {
        private String name;
        private Type type;
        private int value;
        private int addr;

        public Symbol(String name, Type type, int value, int addr) {
            this.name = name;
            this.type = type;
            this.value = value;
            this.addr = addr;
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

        public void setValue(int value) {
            this.value = value;
        }

        public int getAddr() {
            return addr;
        }

        public void setAddr(int addr) {
            this.addr = addr;
        }

        @Override
        public String toString() {
            return "Symbol{" +
                    "name='" + name + '\'' +
                    ", type=" + type +
                    ", value=" + value +
                    ", addr=" + addr +
                    '}';
        }
    }
}
