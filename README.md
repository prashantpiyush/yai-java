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
### Nil
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

## Comments
Yai has single and multiline comments both. Single line comments start with `//`. And, multiline starts with `/*` and ends with `*/`.

```
// This is a single line comment

var a = 5; // var a stores 5

/*
 I am a multiline comment.
*/
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

## Functions
Functions in Yai can be defined using the keyword `fun`. And, like every other programming language, a function can be called by placing `()` after the function name and passing suitable arguments, if it accepts any.

Since Yai is a dynamically typed language, there is no need to specify a return type when defining a function.

More generally, function declarations have the following components:
- The `fun` keyword.
- A function name, an identifier.
- Comma-separated parameters list in parentheses. Parameters are optional, but parentheses are required.
- A block as the function's body.

```
fun foo() {
    print "in foo";
}
foo(); // prints: in fo
```

Functions can accept parameters also.

```
fun foo(a, b, c) {
    print a + b + c;
}
foo(1, 2, 10);  // prints: 12
```

A first-class object is an entity that can be dynamically created, destroyed, passed to a function, returned as a value, and pretty much behaves like any other variable in the language. In Yai, like in Python and JavaScript, functions are considered first-class objects.

```
// Assign to variables
fun foo() { print "foo"; }

var bar = foo;

bar();      // prints: foo


// Functions can be passed to other functions also
fun baz(f) {
    f();
}

baz(foo);   // prints: foo
```

They can even be declared inside loops or other functions.
```
for(var i = 0; i < 1; i = i + 1) {
    fun foo() {
        print "foo";
    }
    foo();
}
// prints: foo

// But, calling foo outside loop will give error
// foo is limited to the scope of loop only
foo();  // error
```

## Closures
Yai supports closures. A closure is the combination of a function bundled together with references to its surrounding state. A closure gives you access to an outer function’s scope from an inner function.

This means that the inner function will have access to the variables in the outer function scope, even after the outer function has returned.

A classic example:
```
fun addMaker(x) {
    fun adder(y) {
        return x + y;
    }
    return adder;
}

var add5 = addMaker(5);
var add10 = addMaker(10);

print add5(12);     // 17
print add10(8);     // 18
```

Closures only capture variables that are visible to their declaration, meaning they are lexically scoped. They run in the scope in which they are defined, not the scope from which they are executed. Thus, we can view closures as a combination of function definitions and the scope chain that was in effect when the function was defined.

```
var a = "outer";
fun foo() {
    var a = "inner";
    
    fun bar() {
        print a;
    }

    return bar;
}

var f = foo();
f();    // prints: innner
```

In the above example, only the inner variable is visible to the `bar` function, since it is shadowing the outer one. Due to this, the bar function forms a closure over the inner variable and stores a reference and, prints it when called.

```
var a = "global";
{
    fun assign() {
        a = "assigned";
    }

    var a = "inner";
    assign();
    print a;    // prints: inner
}
print a;        // prints: assigned
```

Many functions can be nested to create closures.

```
// 
fun foo() {
    var a = "a";
    fun bar() {
        var b = "b";
        fun baz() {
            var c = "c";
            fun bat() {
                print a + " " + b + " " + c;
            }
            return bat;
        }
        return baz();
    }
    return bar();
}
foo()();    // prints: a b c
```

## Classes
Object-Oriented Programming is a concept to bundle data and functionalities together. Languages that support OOP typically use inheritance for code reuse and follow either class-based or prototype-based programming style. And, Yai has classes. Classes are a nice way to create new namespaces and avoid clashes in the global scope. Creating a new class creates a new type of object, allowing new instances of that type to be made. Each instance has fields and methods attached to maintain and modify its state.

### Class definition syntax
Class definitions, like function definitions, must be executed before they have any effect. And, a class definition can be placed anywhere, like inside a function on in a while loop.

When creating a new class, a class-name must be preceded by the `class` keyword and followed by a block that only contains method definition.

```
class Foo {
    // empty block
}
```

Methods are not that different from functions. They just don't require the `fun` keyword. Since a class definition can only contain methods, it is assumed that all the substatements are going to be function definitions.

```
class Bar {
    methodLikeThis() {
        // do something
    }

    methodWithParams(a, b, c) {
        // do something else
    }
}
```

### Class objects
To instantiate a class, Yai uses function notation like Python and JS. Just pretend that the class object is a function that returns a new instance of that class.

```
class Foo {
    // methods go here
}

var fooObject = Foo();
```

### Constructor
A class can define its own `init` method that will be invoked every time a new object of that class is created.

```
class Foo {
    init() {
        print "in Foo init";
    }
}

var fooObject = Foo();
// prints: in Foo init
```

The constructor function can also accept arguments required to initiate a class. And, these arguments are passed to the class while instantiating.

```
class Foo {
    init(param) {
        print "foo: " + param;
    }
}

Foo("obj");
// prints: foo: obj
```

### Keyword `this`
Instantiating a class creates an empty object, without any state. Classes can be instantiated with a default state by setting proper fields in the constructor method. Within a method, including constructor, `this` is a reference to the current object - the object whose method or constructor is being called. The constructor can use `this` to set attributes on the current object.

```
class Person {
    init(name) {
        this.name = name;
    }

    ...
}

var hito = Person("hito");
```

### Access instance properties
Any field or method on an instance can be accessed by placing dot `.` after the object and then the property name.

When looking for a property, Yai will first look for fields with the given name and then for a method. If a field and method have the same name, then the field will overshadow the method.

```
class Person {
    init(name) {
        this.name = name;
    }

    sayHello() {
        print "Hello " + this.name;
    }
}

var hito = Person("hito");

// Instance methods can be called like this
hito.sayHello();    // prints: Hello hito


// Access attributes like this
print hito.name;    // prints: hito


// Methods can be assigned to other variables and then called
var hello = hito.sayHello;

hello();            // prints: Hello hito
```

Inside a method body, a `this` expression evaluates to the instance that the method was called on. More specifically, since the methods are accessed and invoked as a two-step process, `this` will refer to the object that the method was accessed from.

```
class Foo {
    init(value) {
        this.value = value;
    }
    method() {
        print this.value;
    }
}

class Bar {
    init(value) {
        this.value = value;
    }
    method() {
        print this.value;
    }
}

var foo = Foo("foo");
var bar = Bar("bar");

bar.method = foo.method;

bar.method();   // prints: foo
```

### Inheritance
In Yai, like other OOP languages, classes can be derived from other classes, thereby inheriting methods.

```
class Foo {
    printFoo() {
        print "foo";
    }
}

class Bar < Foo {}

var bar = Bar();

// bar inheris Foo's printFoo function
bar.printFoo();
```

### Keyword `super`
The use of a `super` keyword is to access the superclass's method from a subclass. If a method overrides one of its superclass's method, then the overridden method can be invoked using the `super` keyword.

Only methods from a superclass can be accessed using `super`.

```
class Foo {
    method() {
        print "foo";
    }
}

class Bar < Foo {
    method() {
        super.method();
        print "bar";
    }
}

Bar().method();
// prints:
// foo
// bar
```

To understand how `super` works, consider the following example. An equivalent program in Java or C++ would print "A.method", and that is what Yai does.

```
class A {
    method() {
        print "A.method";
    }
}

class B < A {
    method() {
        print "B.method";
    }

    test() {
        super.method();
    }
}

class C < B {}

C().test();     // prints: A.method
```

The `super` keyword, followed by a dot and an identifier looks for a method with that name. Unlike calls on `this`, the search starts at the superclass containing the super expression.

## In-built function `str()`
The `str` function accepts only one argument and returns the string representation of the passed variable.

```
print "str = " + str(str);
// str = <native fn str>
```

## Grammar
<b>Vocab:</b>
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
