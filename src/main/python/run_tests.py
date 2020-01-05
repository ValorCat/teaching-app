import json
import sandbox
import traceback

from format_results import build_pass_msg, build_fail_msg, build_error_msg

"""
Input Model:
    {
        "12345": {
            "test": "",
            "inputs": {
                "<stdin>": "",
                "file": ""
            },
            "outputs": {
                "<stdout>": "",
                "<return>": "",
                "file": ""
            }
        }
    }
    
Output Model:
    COMPILE
    NO_COMPILE <line#> <col#> <message>
    PASS <message>
    FAIL <message>
"""


def run_tests(source_code, test_cases):
    # parse json tests
    test_cases = json.loads(test_cases)

    # check for syntax errors
    try:
        code = compile(source_code, 'attempt.py', 'exec', dont_inherit=True, optimize=0)
        report_compilation()
    except SyntaxError as e:
        return report_compilation(e)

    # check each test case
    for case_id, case_data in test_cases.items():
        inputs, outputs = case_data['inputs'].copy(), case_data['outputs'].copy()
        stdin_buffer = inputs.pop('<stdin>', '')
        test_output = None

        # switch to sandbox env
        with sandbox.Sandbox(stdin_buffer, inputs) as env:
            exec(code, env.vars)
            if 'test' in case_data:
                test_output = eval(case_data['test'], env.vars)

        # if the sandbox exited with an exception
        if env and env.exception:
            report_error(case_data, env.exception)
            continue  # skip to next test case

        case_results = {}
        case_success = True

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
            report_pass(case_data)
        else:
            for location in case_results:
                success, result = case_results[location]
                if success:
                    report_pass(case_data)
                else:
                    report_fail(case_data, location, result)


def report_pass(case_data):
    print(f'PASS {build_pass_msg(case_data)}')


def report_fail(case_data, location, result):
    print(f'FAIL {build_fail_msg(case_data, location, result)}')


def report_error(case_data, err: BaseException):
    # get line number in user's code that raised the error
    err_line = '?'
    for frame, line in traceback.walk_tb(err.__traceback__):
        if frame.f_code.co_filename == 'attempt.py':
            err_line = line
    print(f'FAIL {err_line} {build_error_msg(case_data, err_line, err)}')


def report_compilation(err: SyntaxError = None):
    if not err:
        print('COMPILE')
    else:
        print(f'NO_COMPILE {err.lineno} {err.offset} SyntaxError: {err.msg}')


if __name__ == '__main__':
    source_code = input().replace('\0', '\n')
    test_cases = input().replace('\0', '\n')
    run_tests(source_code, test_cases)
