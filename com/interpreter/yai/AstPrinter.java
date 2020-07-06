package com.interpreter.yai;

import java.util.List;

import com.interpreter.yai.Expr.Assign;
import com.interpreter.yai.Expr.Binary;
import com.interpreter.yai.Expr.Call;
import com.interpreter.yai.Expr.Get;
import com.interpreter.yai.Expr.Grouping;
import com.interpreter.yai.Expr.Literal;
import com.interpreter.yai.Expr.Logical;
import com.interpreter.yai.Expr.Set;
import com.interpreter.yai.Expr.Super;
import com.interpreter.yai.Expr.This;
import com.interpreter.yai.Expr.Unary;
import com.interpreter.yai.Expr.Variable;
import com.interpreter.yai.Stmt.Block;
import com.interpreter.yai.Stmt.Expression;
import com.interpreter.yai.Stmt.Function;
import com.interpreter.yai.Stmt.If;
import com.interpreter.yai.Stmt.Print;
import com.interpreter.yai.Stmt.Var;
import com.interpreter.yai.Stmt.While;

/**
 * Prints string representation of AST nodes
 */
class AstPrinter implements Expr.Visitor<String>, Stmt.Visitor<String> {

    String print(Expr expr) {
        return expr.accept(this);
    }

    String print(List<Stmt> statements) {
        StringBuilder stringBuilder = new StringBuilder();
        for(Stmt stmt : statements) {
            stringBuilder.append(print(stmt)).append("\n");
        }
        return stringBuilder.toString();
    }

    String print(Stmt stmt) {
        return stmt.accept(this);
    }

    @Override
    public String visitBlockStmt(Block stmt) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(block ");
        for(Stmt statement : stmt.statements) {
            stringBuilder.append(statement.accept(this));
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    @Override
    public String visitClassStmt(Stmt.Class stmt) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(class " + stmt.name.lexeme);
        if(stmt.superclass != null) {
            stringBuilder.append(" < " + print(stmt.superclass));
        }
        for(Stmt.Function method : stmt.methods) {
            stringBuilder.append(" " + print(method));
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    @Override
    public String visitExpressionStmt(Expression stmt) {
        return parenthesize(";", stmt.expression);
    }

    @Override
    public String visitFunctionStmt(Function stmt) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(fun " + stmt.name.lexeme + "(");
        for(Token param : stmt.params) {
            if(param != stmt.params.get(0)) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(param.lexeme);
        }
        stringBuilder.append(")");
        for(Stmt body : stmt.body) {
            stringBuilder.append(body.accept(this));
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    @Override
    public String visitIfStmt(If stmt) {
        if(stmt.elseBranch == null) {
            return parenthesize2("if", stmt.condition, stmt.thenBranch);
        }
        return parenthesize2("if-else", stmt.condition, stmt.thenBranch, stmt.elseBranch);
    }

    @Override
    public String visitPrintStmt(Print stmt) {
        return parenthesize("print", stmt.expression);
    }

    @Override
    public String visitReturnStmt(Stmt.Return stmt) {
        if(stmt.value == null) return "(return)";
        return parenthesize("return", stmt.value);
    }

    @Override
    public String visitVarStmt(Var stmt) {
        if(stmt.initializer == null) {
            return parenthesize2("var", stmt.name);
        }
        return parenthesize2("var", stmt.name, "=", stmt.initializer);
    }

    @Override
    public String visitWhileStmt(While stmt) {
        return parenthesize2("while", stmt.condition, stmt.body);
    }

    @Override
    public String visitAssignExpr(Assign expr) {
        return parenthesize2("=", expr.name.lexeme, expr.value);
    }

    @Override
    public String visitBinaryExpr(Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitCallExpr(Call expr) {
        return parenthesize2("call", expr.callee, expr.arguments);
    }

    @Override
    public String visitGetExpr(Get expr) {
        return parenthesize2(".", expr.object, expr.name.lexeme);
    }

    @Override
    public String visitGroupingExpr(Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Literal expr) {
        if (expr.value == null) {
            return "nil";
        }
        return expr.value.toString();
    }

    @Override
    public String visitLogicalExpr(Logical expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitSetExpr(Set expr) {
        return parenthesize2("=", expr.object, expr.name.lexeme, expr.value);
    }

    @Override
    public String visitSuperExpr(Super expr) {
        return parenthesize2("super", expr.method);
    }

    @Override
    public String visitThisExpr(This expr) {
        return "this";
    }

    @Override
    public String visitUnaryExpr(Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    @Override
    public String visitVariableExpr(Variable expr) {
        return expr.name.lexeme;
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("(").append(name);
        for (Expr expr : exprs) {
            stringBuilder.append(" ");
            stringBuilder.append(expr.accept(this));
        }
        stringBuilder.append(")");

        return stringBuilder.toString();
    }

    private String parenthesize2(String name, Object... parts) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(").append(name);
        for(Object part: parts) {
            stringBuilder.append(" ");
            if(part instanceof Expr) {
                stringBuilder.append(((Expr)part).accept(this));
            } else if(part instanceof Stmt) {
                stringBuilder.append(((Stmt)part).accept(this));
            } else if(part instanceof Token) {
                stringBuilder.append(((Token)part).lexeme);
            } else {
                stringBuilder.append(part);
            }
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}