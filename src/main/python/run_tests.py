import json
import sandbox
import sys
import traceback

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

    CASE <case#> PASS
    CASE <case#> FAIL <#elements>
        ELEMENT <location> PASS
        ELEMENT <location> FAIL <result>
    CASE <case#> ERROR <line#> <message>
"""


def run_tests(source_code, test_cases):
    # parse json tests
    test_cases = json.loads(test_cases)

    # check for syntax errors
    try:
        code = compile(source_code, 'attempt.py', 'exec', dont_inherit=True, optimize=0)
        output_compile_test()
    except SyntaxError as e:
        return output_compile_test(e)

    # check each test case
    for case_id, case_data in test_cases.items():
        test_snippet = case_data.get('test')
        test_output = None
        inputs, outputs = case_data['inputs'], case_data['outputs']
        stdin_buffer = inputs.pop('<stdin>', '')

        # switch to sandbox env
        with sandbox.Sandbox(stdin_buffer, inputs) as env:
            exec(code, env.vars)
            if test_snippet:
                test_output = eval(test_snippet, env.vars)

        # if the sandbox exited with an exception
        if env and env.exception:
            output_test_error(case_id, env.exception)
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
            output_test_pass(case_id)
        else:
            output_test_fail(case_id, case_results)


def output_test_pass(case_id):
    print(f'CASE {case_id} PASS')


def output_test_fail(case_id, case_results):
    print(f'CASE {case_id} FAIL {len(case_results)}')
    for location in case_results:
        success, result = case_results[location]
        if success:
            print(f'ELEMENT {location} PASS')
        else:
            print(f'ELEMENT {location} FAIL {repr(result)}')


def output_test_error(case_id, err: BaseException):
    # get line number in user's code that raised the error
    err_line = '?'
    for frame, line in traceback.walk_tb(err.__traceback__):
        if frame.f_code.co_filename == 'attempt.py':
            err_line = line
    print(f'CASE {case_id} ERROR {err_line} {type(err).__name__}: {err}')


def output_compile_test(err: SyntaxError = None):
    if not err:
        print('COMPILE')
    else:
        print(f'NO_COMPILE {err.lineno} {err.offset} SyntaxError: {err.msg}')


if __name__ == '__main__':
    run_tests(sys.argv[1], sys.argv[2])
