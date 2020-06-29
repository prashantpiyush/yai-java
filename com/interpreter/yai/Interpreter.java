package com.interpreter.yai;

import com.interpreter.yai.Expr.Binary;
import com.interpreter.yai.Expr.Grouping;
import com.interpreter.yai.Expr.Literal;
import com.interpreter.yai.Expr.Unary;

class Interpreter implements Expr.Visitor<Object> {

    void interpret(Expr expression) {
        try {
            Object value = evaluate(expression);
            System.out.println(stringify(value));
        } catch(RuntimeError error) {
            Yai.runtimeError(error);
        }
    }

    @Override
    public Object visitBinaryExpr(Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        // TODO: different from book,
        // in book this check is before each operation in below switch
        // I am creating a new switch
        switch(expr.operator.type) {
            case GREATER:
            case GREATER_EQUAL:
            case LESS:
            case LESS_EQUAL:
            case MINUS:
            case SLASH:
            case STAR:
                checkNumberOperands(expr.operator, left, right);
            default:
                break;
        }

        switch(expr.operator.type) {
            case GREATER:
                return (double)left > (double)right;
            case GREATER_EQUAL:
                return (double)left >= (double)right;
            case LESS:
                return (double)left < (double)right;
            case LESS_EQUAL:
                return (double)left <= (double)right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case MINUS:
                return (double)left - (double)right;
            case PLUS:
                // TODO: what about the case of (String + double)
                // Does yai handles this?
                if(left instanceof Double && right instanceof Double) {
                    return (double)left + (double)right;
                }
                if(left instanceof String && right instanceof String) {
                    return (String)left + (String)right;
                }
                throw new RuntimeError(
                    expr.operator,
                    "Operands must be two Numbers or two Strings"
                );
            case SLASH:
                return (double)left / (double)right;
            case STAR:
                return (double)left * (double) right;
            // TODO: the default is not in book
            default:
                break;
        }

        // Unreachable
        return null;
    }

    @Override
    public Object visitGroupingExpr(Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Unary expr) {
        Object right = evaluate(expr.right);

        switch(expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double)right;
            // TODO: this default is not in book
            default:
                break;
        }

        // Unreachable
        return null;
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private boolean isTruthy(Object object) {
        if(object == null) return false;
        if(object instanceof Boolean) return (boolean)object;
        return true;
    }

    private boolean isEqual(Object left, Object right) {
        // null is only equal to null
        if(left == null && right == null) return true;
        if(left == null || right == null) return false;
        return left.equals(right);
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if(operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if(left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be a number.");
    }

    private String stringify(Object object) {
        if(object == null) return "nil";

        // Work around Java adding ".0" to integer-valued doubles
        if(object instanceof Double) {
            String text = object.toString();
            if(text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }
}