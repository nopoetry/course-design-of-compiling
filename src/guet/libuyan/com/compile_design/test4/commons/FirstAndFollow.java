package guet.libuyan.com.compile_design.test4.commons;

import com.sun.deploy.util.ArrayUtil;

import java.util.Arrays;

/**
 * @author lan
 * @create 2021-06-13-11:12
 */
public class FirstAndFollow {
    public static final Type[] first_MainProgram
            = {Type.pound, Type.varsym, Type.identifier, Type.ifsym, Type.whilesym, Type.beginsym};
    public static final Type[] first_SubProgram
            = {Type.pound, Type.varsym, Type.identifier, Type.ifsym, Type.whilesym, Type.beginsym};
    public static final Type[] first_Variable
            = {Type.varsym};
    public static final Type[] first_MoreIdentifiers
            = {Type.comma};
    public static final Type[] first_Statement
            = {Type.identifier, Type.ifsym, Type.whilesym, Type.beginsym};
    public static final Type[] first_AssignmentStatement
            = {Type.identifier};
    public static final Type[] first_CompoundStatement
            = {Type.beginsym};
    public static final Type[] first_MoreStatement
            = {Type.semicolon};
    public static final Type[] first_MoreStatement1
            = {Type.identifier, Type.ifsym, Type.whilesym, Type.beginsym, Type.semicolon};
    public static final Type[] first_Condition
            = {Type.plus, Type.minus, Type.identifier, Type.number, Type.lparen};
    public static final Type[] first_Expression
            = {Type.plus, Type.minus, Type.identifier, Type.number, Type.lparen};
    public static final Type[] first_MoreItem
            = {Type.plus, Type.minus};
    public static final Type[] first_Item
            = {Type.identifier, Type.number, Type.lparen};
    public static final Type[] first_MoreFactor
            = {Type.times, Type.division};
    public static final Type[] first_Factor
            = {Type.identifier, Type.number, Type.lparen};
    public static final Type[] first_PlusOperator
            = {Type.plus, Type.minus};
    public static final Type[] first_MultiplyingOperator
            = {Type.times, Type.division};
    public static final Type[] first_RelationalOperator
            = {Type.eq, Type.neq, Type.less, Type.leq, Type.greater, Type.geq};
    public static final Type[] first_ConditionStatement
            = {Type.ifsym};
    public static final Type[] first_WhileStatement
            = {Type.whilesym};

    public static final Type[] follow_MainProgram
            = {Type.pound};
    public static final Type[] follow_SubProgram
            = {Type.pound};
    public static final Type[] follow_Variable
            = {Type.pound, Type.identifier, Type.ifsym, Type.whilesym, Type.beginsym};
    public static final Type[] follow_MoreIdentifiers
            = {Type.semicolon};
    public static final Type[] follow_Statement
            = {Type.pound, Type.semicolon, Type.endsym};
    public static final Type[] follow_AssignmentStatement
            = follow_Statement;
    public static final Type[] follow_CompoundStatement
            = follow_Statement;
    public static final Type[] follow_MoreStatement
            = {Type.endsym};
    public static final Type[] follow_MoreStatement1
            = follow_MoreStatement;
    public static final Type[] follow_Condition
            = {Type.thensym, Type.dosym};
    public static final Type[] follow_Expression
            = {Type.eq, Type.neq, Type.less, Type.leq, Type.greater, Type.geq, Type.rparen, Type.thensym, Type.dosym, Type.pound, Type.semicolon, Type.endsym};
    public static final Type[] follow_MoreItem
            = follow_Expression;
    public static final Type[] follow_Item
            = {Type.plus, Type.minus, Type.eq, Type.neq, Type.less, Type.leq, Type.greater, Type.geq, Type.rparen, Type.thensym, Type.dosym, Type.pound, Type.semicolon, Type.endsym};
    public static final Type[] follow_MoreFactor
            = follow_Item;
    public static final Type[] follow_Factor
            = follow_MoreFactor;
    public static final Type[] follow_PlusOperator
            = {Type.identifier, Type.number, Type.lparen};
    public static final Type[] follow_MultiplyingOperator
            = {Type.identifier, Type.number, Type.lparen};
    public static final Type[] follow_RelationalOperator
            = {Type.identifier, Type.number, Type.lparen, Type.plus, Type.minus};
    public static final Type[] follow_ConditionStatement
            = {Type.pound, Type.semicolon, Type.endsym};
    public static final Type[] follow_WhileStatement
            = {Type.pound, Type.semicolon, Type.endsym};
}
