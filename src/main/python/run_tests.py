import json
import sandbox
import traceback

from format_results import build_pass_msg, build_fail_msg, build_error_msg

"""
Input Model:
    [
        {
            "test": "",
            "inputs": {
                "<stdin>": "",
                "file": ""
            },
            "outputs": {
                "<stdout>": "",
                "<return>": "",
                "<error>": "",
                "file": ""
            }
        }
    ]
    
Output Model:
    COMPILE
    NO_COMPILE <line#> <col#> <message>
    PASS <message>
    FAIL <message>
"""


def run_tests(source_code, tests):
    # parse json tests
    tests = json.loads(tests)

    # check for syntax errors
    try:
        code = compile(source_code, 'attempt.py', 'exec', dont_inherit=True, optimize=0)
        report_compilation()
    except SyntaxError as e:
        return report_compilation(e)

    # check each test case
    for test in tests:
        inputs, outputs = test['inputs'].copy(), test['outputs'].copy()
        stdin_buffer = inputs.pop('<stdin>', '')
        test_output = None

        # switch to sandbox env
        with sandbox.Sandbox(stdin_buffer, inputs) as env:
            exec(code, env.vars)
            if 'test' in test:
                test_output = eval(test['test'], env.vars)

        case_results = {}
        case_success = True

        # if the sandbox exited with an exception
        if env.exception:
            success = False
            error_name = type(env.exception).__name__
            if '<error>' in outputs:
                expected_err = outputs.pop('<error>')
                success = expected_err == error_name
            if success:
                case_results['<error>'] = (True, error_name)
            else:
                report_error(test, env.exception)
                continue  # skip to next test case
        elif '<error>' in outputs:
            # we expected an error but didn't find one
            outputs.pop('<error>')
            case_results['<error>'] = (False, None)
            case_success = False

        # if we need to check the return value
        if '<return>' in outputs:
            expected = eval(outputs.pop('<return>'), env.vars)
            success = expected == test_output
            case_results['<return>'] = (success, test_output)
            case_success = case_success and success

        # if we need to check stdout
        if '<stdout>' in outputs:
            expected = outputs.pop('<stdout>')
            actual = env.stdout.getvalue().rstrip()
            success = expected == actual
            case_results['<stdout>'] = (success, actual)
            case_success = case_success and success

        # check all other outputs
        for location, expected in outputs:
            actual = env.file_system.get(location, '<no such file>').rstrip()
            success = actual == expected
            case_results[location] = (success, actual)
            case_success = case_success and success

        # output results
        if case_success:
            report_pass(test)
        else:
            for location in case_results:
                success, result = case_results[location]
                if success:
                    report_pass(test)
                else:
                    report_fail(test, location, result)


def report_pass(case_data):
    print(f'PASS {build_pass_msg(case_data)}')


def report_fail(case_data, location, result):
    print(f'FAIL {build_fail_msg(case_data, location, result)}')


def report_error(case_data, err: BaseException):
    # get line number in user's code that raised the error
    err_line = -1
    for frame, line in traceback.walk_tb(err.__traceback__):
        if frame.f_code.co_filename == 'attempt.py':
            err_line = line
    print(f'FAIL {build_error_msg(case_data, err_line, err)}')


def report_compilation(err: SyntaxError = None):
    if not err:
        print('COMPILE')
    else:
        print(f'NO_COMPILE {err.lineno} {err.offset} SyntaxError: {err.msg}')


if __name__ == '__main__':
    source_code = input().replace('\0', '\n')
    test_cases = input().replace('\0', '\n')
    run_tests(source_code, test_cases)
