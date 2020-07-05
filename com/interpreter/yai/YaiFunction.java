package com.interpreter.yai;

import java.util.List;

class YaiFunction implements YaiCallable {
    private final Stmt.Function declaration;
    private final Environment closure;
    private final boolean isInitializer;

    YaiFunction(Stmt.Function declaration, Environment closure, boolean isInitializer) {
        this.declaration = declaration;
        this.closure = closure;
        this.isInitializer = isInitializer;
    }

    YaiFunction bind(YaiInstance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);
        return new YaiFunction(declaration, environment, isInitializer);
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);

        for(int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme, arguments.get(i));
        }

        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch(Return returnValue) {
            if(isInitializer) {
                return closure.getAt(0, "this");
            }
            return returnValue.value;
        }

        if(isInitializer) {
            return closure.getAt(0, "this");
        }
        return null;
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }
}