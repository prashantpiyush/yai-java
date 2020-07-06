"""
Script to test Yai agains the test cases present in folder "tests"

"""

import os
import subprocess


def print_in_color(color):
    cmd = {'red':'\033[91m', 'green':'\033[92m', 'cyan':'\033[96m'}
    def print_func(*args, **kwargs):
        for arg in args:
            print(cmd[color], arg, '\033[0m ', sep='', end='')
        print(**kwargs)
    return print_func


class Test:
    
    def __init__(self, filepath):
        self.filepath = filepath

        with open(filepath, 'r') as file:
            code = file.readlines()[:-1]
            stdout = False
            stderr = False
            self.expected_stdout = []
            self.expected_stderr = []
            for line in code:
                if line.startswith('// stdout'):
                    stdout = True
                    continue
                elif line.startswith('// stderr'):
                    stdout = False
                    stderr = True
                    continue
                if stdout:
                    self.expected_stdout.append(line)
                elif stderr:
                    self.expected_stderr.append(line)
    

    def run(self):
        process = subprocess.run(
            [cmd, self.filepath],
            capture_output=True,
            bufsize=1,
            universal_newlines=True)
        self.stdout = process.stdout
        self.stderr = process.stderr
        self.passed = False
        if(self.match('out') and self.match('err')):
            self.passed = True
    

    def match(self, check_type):
        expeced = self.expected_stdout
        got = self.stdout
        if check_type == 'err':
            expeced = self.expected_stderr
            got = self.stderr
        got = got.strip().split('\n')

        if len(expeced) == 0 and len(got) == 1 and got[0] == '':
            return True
        
        if(len(expeced) != len(got)): return False

        for e, g in zip(expeced, got):
            if e.replace('\n', '') != g:
                return False
        return True


def main():
    total_tests = 0
    tests_passed = 0
    tests_failed = 0
    tests = []
    
    # get all the test files and create objects
    for file in os.listdir(test_directory):
        base = os.path.join(test_directory, file)
        if os.path.isdir(base):
            for test in os.listdir(base):
                path = os.path.join(base, test)
                tests.append(Test(path))
        else:
            tests.append(Test(base))
    
    # run all the tests and print their status
    total_tests = len(tests)
    for test in tests:
        # print(''.join(test.expected_stdout))
        # print(''.join(test.expected_stderr))
        print('{0: <40}'.format(test.filepath), sep='', end=' ')
        test.run()
        if test.passed:
            tests_passed += 1
            green('PASS')
        else:
            red('FAIL')
    tests_failed = total_tests - tests_passed

    print()
    print('Total tests:', total_tests)
    green('Tests passed:', tests_passed)
    red('Tests failed:', tests_failed)
    if total_tests == tests_passed:
        green('All passed (OK)')


if __name__ == '__main__':
    test_directory = 'tests'
    red = print_in_color('red')
    green = print_in_color('green')
    cmd = './yai'
    main()