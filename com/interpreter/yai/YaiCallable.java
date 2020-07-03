package com.interpreter.yai;

import java.util.List;

interface YaiCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}