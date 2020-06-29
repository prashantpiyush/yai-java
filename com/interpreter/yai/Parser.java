package com.interpreter.yai;

import java.util.List;

/**
    expression     → equality ;
    equality       → comparison ( ( "!=" | "==" ) comparison )* ;
    comparison     → addition ( ( ">" | ">=" | "<" | "<=" ) addition )* ;
    addition       → multiplication ( ( "-" | "+" ) multiplication )* ;
    multiplication → unary ( ( "/" | "*" ) unary )* ;
    unary          → ( "!" | "-" ) unary
                | primary ;
    primary        → NUMBER | STRING | "false" | "true" | "nil"
                | "(" expression ")" ;
 */
class Parser {
    @SuppressWarnings("serial")
    private static class ParseError extends RuntimeException {}
    
    private final List<Token> tokens;
    private int current = 0;

    Parser(final List<Token> tokens) {
        this.tokens = tokens;
    }

    Expr parse() {
        try {
            return expression();
        } catch(ParseError parseError) {
            return null;
        }
    }

    private Expr expression() {
        return equality();
    }

    private Expr equality() {
        Expr expr = comparison();

        while(match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            final Token operator = previous();
            final Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        Expr expr = addition();
        
        while(match(TokenType.GREATER, TokenType.GREATER_EQUAL,
                    TokenType.LESS, TokenType.LESS_EQUAL)) {
            final Token operator = previous();
            final Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr addition() {
        Expr expr = multiplication();

        while(match(TokenType.PLUS, TokenType.MINUS)) {
            final Token operator = previous();
            final Expr right = multiplication();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr multiplication() {
        Expr expr = unary();

        while(match(TokenType.STAR, TokenType.SLASH)) {
            final Token operator = previous();
            final Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if(match(TokenType.BANG, TokenType.MINUS)) {
            final Token operator = previous();
            final Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    private Expr primary() {
        if(match(TokenType.FALSE)) return new Expr.Literal(false);
        if(match(TokenType.TRUE)) return new Expr.Literal(true);
        if(match(TokenType.NIL)) return new Expr.Literal(null);

        if(match(TokenType.NUMBER, TokenType.STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if(match(TokenType.LEFT_PAREN)) {
            final Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expect expression.");
    }

    private Token consume(final TokenType type, final String errorMessage) {
        if(check(type)) return advance();
        throw error(peek(), errorMessage);
    }

    private ParseError error(final Token token, final String errorMessage) {
        Yai.error(token, errorMessage);
        return new ParseError();
    }

    // private void synchronize() {
    //     advance();

    //     while(!isAtEnd()) {
    //         if(previous().type == TokenType.SEMICOLON) return;

    //         switch(peek().type) {
    //             case CLASS:
    //             case FUN:
    //             case VAR:
    //             case FOR:
    //             case IF:
    //             case WHILE:
    //             case PRINT:
    //             case RETURN:
    //                 return;
    //             // TODO: this default is not in book
    //             default:
    //                 break;
    //         }

    //         advance();
    //     }
    // }

    private boolean match(final TokenType... types) {
        for(final TokenType type: types) {
            if(check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(final TokenType type) {
        if(isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if(!isAtEnd()) ++current;
        return previous();
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token peek() {
        return tokens.get(current);
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }
}