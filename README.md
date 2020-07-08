# Yai-Java
Yai is a tree-walk interpreter written in Java, modeled after the "Lox" language presented in the book [Crafting interpreters](http://www.craftinginterpreters.com/). But, Yai contains a few extra features in comparison, like, break and continue statements.

Yai uses the recursive descent parsing method to interpret a given program. In this technique, the interpreter interpreters the statements and expressions by directly walking the Abstract Syntax Tree (AST) generated from the code.

It also provides a REPL interface, like Python, to quickly evaluate and see outputs a few statements.

# Language
## Primitive datatypes
### Number
```
// valid literals
124
3534.93
1.0

// invalid literals - no trailing or leading decimal allowed
.1534
534.
```
### String
```
"I am a string"

"multiline
strings
are
also
supported"
```
### Boolean
```
// like boolean in Java, C++
true
false
```
### nil
`nil` represents a null value. It is like `null` in Java or `None` in Python. Just the keyword `nil` is sufficient to represent a null value.
```
nil
```

## Variables
Yai is a dynamically typed language, meaning you don't need to specify the data type while declaring the variables. Although, you have to use the `var` keyword.
```
var int = 1;
var num = 953.12;

var bool = true;
var imFalse = false;

var string = "this a str";

var iAnNil;
var alsoNil = nil;
```

## Print statement
Instead of a function, like in most languages, `print` is a statement in Yai, like it is in Python2. If you want to print anything just use the following syntax `print <expression>;`.
```
var a = 1;
print a; // 1

var b = true;
print b; // true
```

## Operators
The following are the arithmetic and comparison operators supported by Yai.

### Arithmetic `+ - / *`
```
print 1 + 2;   // 3
print 6 - 2;   // 4
print 10 / 10; // 1
print 1 * 53;  // 53
```
Plus `+` operator is overloaded to support string concatination:
```
print "str" + "ing"; // string
```
However, unlike Java and, like Python, implicit conversion of a Number to String is not supported when adding a String and Number.
```
print "num = " + 353; // error
```

### Comparison `< <= > >=` <br>
Only numbers can be compared using these operators.
```
print 1 <= 2;    // true
print 3 > 3;     // false
print 53 >= 8;   // false
print 5 > 2;     // true

print "str" > "abc"; // error
```

### Equality `== !=`
```
print true == false;  // false
print 3 == "3"        // false
print 0 == 0.0;       // true

print "str" == "str"; // true
print "abc" == "str"; // false
print "abc" != "str"; // true

print 353 != "def";   // true

```
`nil` is only equal to `nil`.
```
print nil == nil;     // true

print nil == 0;       // false
print "str" == nil;   // false

print 353.1 != nil;   // true
```
Special case for NaN: NaN is not equal to itself.
```
var nan = 0 / 0;

print nan == nan;     // false
```
In Java, however, NaNs are equal when compared using the `equals` method.
```
Double nan = Double.NaN;
boolean result = nan.equals(Double.NaN);
System.out.println(result); // true

System.out.println(nan == Double.NaN); // false
```

### Not `!`
The result of the `!` operator is `true` if the expression's truthiness is false and vice-versa.

When checking for the truthiness of a variable or expression remember that, in Yai, only `nil` and `false` evaluates to "not true". Everything else, including `0` and `""` (empty strings), is true.

```
print !true;   // false
print !false;  // true
print !!true;  // true

print !nil;    // true
print !0;      // false
print !353.5;  // false
print !"str";  // false
```

### Logical `and or`
These operators short-circuit and the result of the expression is not necessarily `true/false`, instead the result will have proper truthiness.
```
print true or true;             // true
print true and false;           // false
print false or true and false;  // false


fun foo() {
    return "good";
}

print true and foo();           // good
print false or foo();           // good


// short-circut examples

fun bar() {
    print "never executed";
}

print true or bar();            // true
print false or bar();           // false
```

## Associativity and precedence order
Yai follows the same precedence and associativity order as C.
|Operator|Associativity|Precedence|
|:--------:|:---------:|:--------:|
|! - (unary) | Right | Highest
|/ * | Left
|- + | Left
|> >= < <= | Left
|== != | Left
|or and | Left | Lowest



## Blocks
Yai supports block creation and shadowing of variables within it. A block starts with `{` and ends with `}`.
```
{
    // this is an empty block
}
```
Shadowing variables from outer scope:
```
var a = "global";
{
    print a;         // global
    var a = "local";
    print a;         // local
}
print a;             // global
```
Redeclaring variables are not allowed in the local scope.
```
{
    var num = 5;
    var num = 63; // error
}
```
Note: Redeclaration of variables is not allowed in the local scope (block), but they can be redeclared in the global scope.

## If - else
Yai supports `if-else` statements. But, there is no `else if` or `elif` like other languages.

Any expression can be used as a condition of if statement.
```
if(true) {
    print "ok";
}
// prints: ok


if(false) {
    print "bad";
} else {
    print "good";
}
// prints: good


if(0) print "it is zero";
// prints: it is zero


if(353 < -9935.3) {
    print "bad";
} else {
    print "OK"; 
}
// prints: OK
```

Like Java, declaring a variable in a single statement `if` body is not allowed.
```
// in Yai
if(true) var x = 0; // error

// in Java
// error: variable declaration not allowed here
if(true) var x = 0;
```

## Loops
Yai has standard C-style `for` and `while` loops.

### while
```
var i = 0;
while(i < 3) {
    print i;
    i = i + 1;
}

var i = 0;
while(i < 3) {
    i = i + 1;
    continue;
    print "nope";
}

var i = 0;
while(i < 3) {
    break;
    print "nope";
}
```

### for
```
for(var i=0; i<3; i=i+1) {
    print i;
}

for(var i=0; i<3; i=i+1) {
    if(i>1) break;
}

for(var i=0; i<3; i=i+1) {
    continue;
    print "nope";
}
```

## Grammar
**Vocab: **
- Rules are called production because they produce string which follows the grammar
- Terminal: A literal value. It is called so because they don't have any more rules as they subexpression.
- Non-Terminal: Named reference to another rule in the grammar.

```
program : declaration* EOF ;

declaration : varDecl | funDecl | classDecl | statement ;

varDecl : "var" IDENTIFIER ("=" expression)? ";" ;

funDecl : "fun" function ;

function : IDENTIFIER "(" paramerters? ")" block ;

parameters : IDENTIFIER ("," IDENTIFIER)* ;

classDecl : "class" IDENTIFIER ("<" IDENTIFIER)? "{" function* "}" ;

statement : block | exprStmt | printStmt | ifStmt | whileStmt | forStmt
    | returnStmt | breakStmt | continueStmt ;

block : "{" declaration* "}" ;

exprStmt : expression ";" ;

printStmt : "print" expression ";" ;

returnStmt : "return" expression? ";" ;

breakStmt : "break" ";" ;

continueStmt : "continue" ";" ;

ifStmt : "if" "(" expression ")" statement ("else" statement)? ;

whileStmt : "while" "(" expression ")" statement ;

forStmt : "for" "(" (varDecl | exprStmt | ";") expression? ";" expression? ")" statement ;

expression : assignment ;

assignment : (call ".")? IDENTIFIER "=" assignment | logicOr ;

call : primary ("(" arguments? ")" | "." IDENTIFIER)* ;

arguments : expression ("," expression)* ;

logicOr : logicAnd ("or" logicAnd)* ;

logicAnd : equality ("and" equality)* ;

equality : comparison ( ("==" | "!=") comparison)* ;

comparison : addition ( ("+" | "-") addition)* ;

addition : multiplication ( ("*" | "/") multiplication)* ;

multiplication : unary ( ("!" | "-") unary)* ;

unary : ("!" | "-") unary | call ;

primary : NUMBER | STRING | "false" | "true" | "nil" | "(" expression ")"
    | IDENTIFIER | "super" "." IDENTIFIER ;
```

# Concepts
## Tree-walk interpreter
In a tree-walk interpreter, the interpreter interpreters the statements and expressions by directly walking the Abstract Syntax Tree (AST) generated from the code.

The parser creates an AST, where each node of the AST contains either a statement or an expression.  The interpreter evaluates these nodes by traversing the syntax tree in a post-order traversal manner. In which, the current node gets evaluated after all of its children are visited.

Since the interpreter starts from the top-most grammar rule and works its way down into the nested subexpressions before finally reaching the leaves of the tree, the recursive descent parsing is also called top-down parsing.

This method tends to be generally less efficient than the other methods of interpreting, like LL or LAIR parsing techniques. However, recursive descent can parse any LL grammar language and provides better error handling. One of the famous compilers to use this technique is the GCC compiler.

## Visitor Pattern
ASTs contain different types of program statements as their nodes, and each node has its way in which it gets interpreted. It becomes quite tough to add a universal method across each grammar class in an efficient way. The problem here is distributing a new operation to each node class.

If someday you want to add a new semantic analysis routine to the interpreter, then you would have to change each node class and add a method that performs the required operation.

It would be better if we can add new methods separately, and the node classes remain independent of the operations that apply to them. The visitor pattern lets you exactly do this. You can add new functions on objects without changing the classes of the elements on which it operates.

Additionally, it becomes easy to define related operations in a separate class, making the code easy to understand and manage.
