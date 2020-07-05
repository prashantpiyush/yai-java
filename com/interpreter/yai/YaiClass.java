package com.interpreter.yai;

import java.util.List;
import java.util.Map;

class YaiClass implements YaiCallable {
    final String name;
    private final Map<String, YaiFunction> methods;

    YaiClass(String name, Map<String, YaiFunction> methods) {
        this.name = name;
        this.methods = methods;
    }

    YaiFunction findMethod(String name) {
        if(methods.containsKey(name)) {
            return methods.get(name);
        }
        return null;
    }

    @Override
    public int arity() {
        YaiFunction initializer = findMethod("init");
        if(initializer != null) {
            return initializer.arity();
        }
        return 0;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        YaiInstance instance = new YaiInstance(this);
        YaiFunction initializer = findMethod("init");
        if(initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }
        return instance;
    }

    @Override
    public String toString() {
        return "<class " + name + ">";
    }
}