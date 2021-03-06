package com.interpreter.yai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.interpreter.yai.Stmt.Break;
import com.interpreter.yai.Stmt.Continue;
import com.interpreter.yai.Stmt.Expression;
import com.interpreter.yai.Stmt.Function;
import com.interpreter.yai.Stmt.If;
import com.interpreter.yai.Stmt.Print;
import com.interpreter.yai.Stmt.Var;
import com.interpreter.yai.Stmt.While;

class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    final Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expr, Integer> locals = new HashMap<>();

    Interpreter() {
        globals.define("clock", new YaiCallable() {
            @Override
            public int arity() { return 0; }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return (double)System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString() { return "<native fn clock>"; }
        });

        globals.define("str", new YaiCallable() {
            @Override
            public int arity() { return 1; }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return stringify(arguments.get(0));
            }

            @Override
            public String toString() { return "<native fn str>"; }
        });
    }

    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Yai.runtimeError(error);
        } catch (FlowControl flowError) {
            Yai.runtimeError(new RuntimeError(flowError.keyword,
                "'" + flowError.keyword.lexeme + "' not properly in loop."));
        }
    }

    @Override
    public Void visitBlockStmt(Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        Object superclass = null;
        if(stmt.superclass != null) {
            superclass = evaluate(stmt.superclass);
            if(!(superclass instanceof YaiClass)) {
                throw new RuntimeError(stmt.superclass.name, "Superclass must be a class.");
            }
        }

        environment.define(stmt.name.lexeme, null);

        if(stmt.superclass != null) {
            environment = new Environment(environment);
            environment.define("super", superclass);
        }

        Map<String, YaiFunction> methods = new HashMap<>();
        for(Stmt.Function method : stmt.methods) {
            boolean isInit = method.name.lexeme.equals("init");
            YaiFunction function = new YaiFunction(method, environment, isInit);
            methods.put(method.name.lexeme, function);
        }

        YaiClass klass = new YaiClass(stmt.name.lexeme, (YaiClass)superclass, methods);
        
        if(superclass != null) {
            environment = environment.enclosing;
        }
        environment.assign(stmt.name, klass);
        return null;
    }

    @Override
    public Void visitExpressionStmt(Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Function stmt) {
        YaiFunction function = new YaiFunction(stmt, environment, false);
        environment.define(stmt.name.lexeme, function);
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if(stmt.value != null) {
            value = evaluate(stmt.value);
        }
        throw new Return(value);
    }

    @Override
    public Void visitIfStmt(If stmt) {
        if(isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        } else if(stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitVarStmt(Var stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }
        environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitWhileStmt(While stmt) {
        while(isTruthy(evaluate(stmt.condition))) {
            try {
                execute(stmt.body);
            } catch(FlowControl flowControl) {
                if(flowControl.keyword.type == TokenType.BREAK) {
                    break;
                } else if(flowControl.keyword.type == TokenType.CONTINUE) {
                    if(stmt.increment != null) {
                        /*
                        execute the increment statement after wrapping it in a
                        block because, if there were no break/continue stmt the
                        increment statement would be executed inside the while
                        block and it will maintain the envrionment chain which
                        resolver assumes.
                        not running it inside a block will break that assumed
                        chain length. It won't find the required vars at correct
                        distance in the environment.
                        */
                        execute(new Stmt.Block(Arrays.asList(stmt.increment)));
                    }
                    continue;
                }
            }
        }
        return null;
    }

    @Override
    public Void visitBreakStmt(Break stmt) {
        throw new FlowControl(stmt.keyword);
    }

    @Override
    public Void visitContinueStmt(Continue stmt) {
        throw new FlowControl(stmt.keyword);
    }

    @Override
    public Object visitAssignExpr(Assign expr) {
        Object value = evaluate(expr.value);

        Integer distance = locals.get(expr);
        if(distance != null) {
            environment.assignAt(distance, expr.name, value);
        } else {
            globals.assign(expr.name, value);
        }

        return value;
    }

    @Override
    public Object visitBinaryExpr(Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        // TODO: different from book,
        // in book this check is before each operation in below switch
        // I am creating a new switch
        switch (expr.operator.type) {
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

        switch (expr.operator.type) {
            case GREATER:
                return (double) left > (double) right;
            case GREATER_EQUAL:
                return (double) left >= (double) right;
            case LESS:
                return (double) left < (double) right;
            case LESS_EQUAL:
                return (double) left <= (double) right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case MINUS:
                return (double) left - (double) right;
            case PLUS:
                // TODO: what about the case of (String + double)
                // Does yai handles this?
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }
                if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }
                throw new RuntimeError(
                    expr.operator,
                    "Operands must be two Numbers or two Strings"
                );
            case SLASH:
                return (double) left / (double) right;
            case STAR:
                return (double) left * (double) right;
            // TODO: the default is not in book
            default:
                break;
        }

        // Unreachable
        return null;
    }

    @Override
    public Object visitCallExpr(Call expr) {
        Object callee = evaluate(expr.callee);

        List<Object> arguments = new ArrayList<>();
        for(Expr argument : expr.arguments) {
            arguments.add(evaluate(argument));
        }
        if(!(callee instanceof YaiCallable)) {
            throw new RuntimeError(expr.paren, "Can only call functions and classes.");
        }
        
        YaiCallable function = (YaiCallable)callee;
        if(arguments.size() != function.arity()) {
            throw new RuntimeError(expr.paren, "Expected "
                + function.arity() + " arguments but got "
                + arguments.size() + ".");
        }
        return function.call(this, arguments);
    }

    @Override
    public Object visitGetExpr(Get expr) {
        Object object = evaluate(expr.object);
        if(object instanceof YaiInstance) {
            return ((YaiInstance)object).get(expr.name);
        }
        throw new RuntimeError(expr.name, "Only instances have properties.");
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
    public Object visitSetExpr(Set expr) {
        Object object = evaluate(expr.object);
        
        if(!(object instanceof YaiInstance)) {
            throw new RuntimeError(expr.name, "Only instances have fields.");
        }

        Object value = evaluate(expr.value);
        ((YaiInstance)object).set(expr.name, value);
        return value;
    }

    @Override
    public Object visitSuperExpr(Super expr) {
        int distance = locals.get(expr);
        YaiClass superclass = (YaiClass)environment.getAt(distance, "super");

        // "this" is always one level nearer than "super"'s environment
        YaiInstance object = (YaiInstance)environment.getAt(distance - 1, "this");
        
        YaiFunction method = superclass.findMethod(expr.method.lexeme);
        if(method == null) {
            throw new RuntimeError(expr.method,
                "Undefined property '" + expr.method.lexeme + "'.");
        }
        return method.bind(object);
    }

    @Override
    public Object visitThisExpr(This expr) {
        return lookupVariable(expr.keyword, expr);
    }

    @Override
    public Object visitLogicalExpr(Logical expr) {
        Object left = evaluate(expr.left);

        if(expr.operator.type == TokenType.OR) {
            if(isTruthy(left)) return left;
        } else { // AND
            if(!isTruthy(left)) return left;
        }

        return evaluate(expr.right);
    }

    @Override
    public Object visitUnaryExpr(Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double) right;
            // TODO: this default is not in book
            default:
                break;
        }

        // Unreachable
        return null;
    }

    @Override
    public Object visitVariableExpr(Variable expr) {
        return lookupVariable(expr.name, expr);
    }

    private Object lookupVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if(distance != null) {
            return environment.getAt(distance, name.lexeme);
        } else {
            return globals.get(name);
        }
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment pervious = this.environment;
        try {
            this.environment = environment;

            for(Stmt statement: statements) {
                execute(statement);
            }
        } finally {
            this.environment = pervious;
        }
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
        // NaN != NaN
        if(left.equals(Double.NaN) && right.equals(Double.NaN)) {
            return false;
        }
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

    void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }
}