package com.interpreter.yai;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Tree-walk interpreter
 * 
 * For sysexit codes:
 * https://www.freebsd.org/cgi/man.cgi?query=sysexits&apropos=0&sektion=0&manpath=FreeBSD+4.3-RELEASE
 * 
 */
public class Yai {
    static boolean hadError = false;
    static boolean hadRunTimeError = false;

    private static final Interpreter interpreter = new Interpreter();

    public static void main(final String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: yai [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(final String path) throws IOException {
        final byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        // Indicate an error in the exit code
        if(hadError) System.exit(65);
        if(hadRunTimeError) System.exit(70);
    }

    private static void runPrompt() throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        while (true) {
            System.out.print("> ");
            run(bufferedReader.readLine());
            hadError = false;
        }

        // try {
        // bufferedReader.close();
        // } catch(Exception exception) {}
        // try {
        // inputStreamReader.close();
        // } catch(Exception exception) {}
    }

    private static void run(final String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        // Stop if there was syntax error
        if(hadError) return;

        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);

        // Stop if there was a resolution error
        if(hadError) return;

        interpreter.interpret(statements);
    }

    private static void report(int line, String where, String message) {
        System.err.println(
            "[line " + line + "] Error " + where + ": " + message);
        hadError = true;
    }

    static void error(int line, String errorMessage) {
        report(line, "", errorMessage);
    }

    static void error(Token token, String errorMessage) {
        if(token.type == TokenType.EOF) {
            report(token.line, " at end", errorMessage);
        } else {
            report(token.line, "'" + token.lexeme + "'", errorMessage);
        }
    }

    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() + "\n[Line " + error.token.line + "]");
        hadRunTimeError = true;
    }
}