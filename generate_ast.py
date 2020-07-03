"""
A little scrip to generate java code for AST classes

--------
Use this file to generate subclasses with overridden methods
in Expr.java present in com/interpreter/yai.

Add new sub-types here with what parameters they should accept
and run this code using `make generate_ast` and it will add the
new sub-types with the required code (fields, construction, 
overridden method) in com/interpreter/yai/Expr.java.

It doesn't changes the com/interpreter/yai/AstPrinter.java file.
You will have to manually override each of the new methods there.

"""

import os
import sys
from typing import (
    Dict,
    List,
    IO
)

TAB = ' '*4


def define_visitor(file: IO, basename: str, subclasses: Dict[str, List[str]]):
    file.write('\n')
    file.write(f'{TAB}interface Visitor<T> {{\n')

    basename_lower = basename.lower()

    for subclass, fields in subclasses.items():
        file.write(f'{TAB*2}T visit{subclass}{basename}({subclass} {basename_lower});\n')
    
    file.write(f'{TAB}}}\n')


def define_subtypes(file: IO, basename: str, subclasses: Dict[str, List[str]]):
    # define each subclass
    """
    @Override
    <T> T accept(Visitor<T> visitor) {
        return visitor.vistTypeExpr(this);
    }
    """
    for subclass, fields in subclasses.items():
        file.write('\n')
        file.write(f'{TAB}static class {subclass} extends {basename} {{\n')
        
        # fields
        for field in fields:
            file.write(f'{TAB*2}final {field};\n')
        
        parameters = ', '.join(fields)
        
        # constructor
        file.write('\n')
        file.write(f'{TAB*2}{subclass}({parameters}) {{\n')
        for field in fields:
            name = field.split(' ')[1]
            file.write(f'{TAB*3}this.{name} = {name};\n')
        file.write(f'{TAB*2}}}\n')

        # visitor pattern
        file.write('\n')
        file.write(f'{TAB*2}@Override\n')
        file.write(f'{TAB*2}<T> T accept(Visitor<T> visitor) {{\n')
        file.write(f'{TAB*3}return visitor.visit{subclass}{basename}(this);\n')
        file.write(f'{TAB*2}}}\n')

        file.write(f'{TAB}}}\n')


def define_ast(output_dir: str, basename: str, subclasses: Dict[str, List[str]]):
    filepath = os.path.join(output_dir, basename + '.java')

    with open(filepath, 'w') as file:
        file.write('package com.interpreter.yai;\n')
        file.write('\n')
        file.write('import java.util.List;\n')
        file.write('\n')
        file.write(f'abstract class {basename} {{\n')

        define_visitor(file, basename, subclasses)
        define_subtypes(file, basename, subclasses)

        # base accept() method
        file.write('\n')
        file.write(f'{TAB}abstract <T> T accept(Visitor<T> visitor);\n')

        file.write('}')


def main():
    args = sys.argv
    if len(args) != 2:
        print('Usage: generate_ast <output directory>')
        print('Example: generate_ast com/interpreter/yai')
        sys.exit(64)
    
    output_dir = args[1]

    define_ast(output_dir, 'Expr', {
        'Assign': ['Token name', 'Expr value'],
        'Binary': ['Expr left', 'Token operator', 'Expr right'],
        'Call': ['Expr callee', 'Token paren', 'List<Expr> arguments'],
        'Grouping': ['Expr expression'],
        'Literal': ['Object value'],
        'Logical': ['Expr left', 'Token operator', 'Expr right'],
        'Unary': ['Token operator', 'Expr right'],
        'Vairable': ['Token name']
    })

    define_ast(output_dir, 'Stmt', {
        'Block': ['List<Stmt> statements'],
        'Expression': ['Expr expression'],
        'Print': ['Expr expression'],
        'Function': ['Token name', 'List<Token> params', 'List<Stmt> body'],
        'Return': ['Token keyword', 'Expr value'],
        'If': ['Expr condition', 'Stmt thenBranch', 'Stmt elseBranch'],
        'Var': ['Token name', 'Expr initializer'],
        'While': ['Expr condition', 'Stmt body']
    })


if __name__ == '__main__':
    main()