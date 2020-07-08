package com.interpreter.yai;

@SuppressWarnings("serial")
class FlowControl extends RuntimeException {
    final Token keyword;

    FlowControl(Token keyword) {
        super(null, null, false, false);
        this.keyword = keyword;
    }
}