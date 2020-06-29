package com.interpreter.yai;

import com.interpreter.yai.Expr.Binary;
import com.interpreter.yai.Expr.Grouping;
import com.interpreter.yai.Expr.Literal;
import com.interpreter.yai.Expr.Unary;

/**
 * Prints string representation of AST nodes
 */
class AstPrinter implements Expr.Visitor<String> {

    /**
     * For the purpose of testing AstPrinter,
     * without parser
     * 
     * Expteced result: (* (- 123) (group 45.67))
     */
    public static void main(String[] args) {
        Expr expression = new Binary(
            new Unary(
                new Token(TokenType.MINUS, "-", null, 1),
                new Literal(123)
            ),
            new Token(TokenType.STAR, "*", null, 1),
            new Grouping(new Literal(45.67))
        );
        System.out.println(new AstPrinter().print(expression));
    }

    String print(Expr expr) {
        return expr.accept(this);
    }

    private String parenthesize(String name, Expr ...exprs) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("(").append(name);
        for(Expr expr: exprs) {
            stringBuilder.append(" ");
            stringBuilder.append(expr.accept(this));
        }
        stringBuilder.append(")");

        return stringBuilder.toString();
    }

    @Override
    public String visitBinaryExpr(Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Literal expr) {
        if(expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }
}