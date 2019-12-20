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
    SYNTAX_ERROR <line> <column> <message>
    TEST <case#> PASS <location>
    TEST <case#> FAIL <location> <result>
    TEST <case#> ERROR <line#> <message>
"""


def run_tests(source_code, test_cases):
    # parse json tests
    test_cases = json.loads(test_cases)

    # check for syntax errors
    try:
        code = compile(source_code, 'attempt.py', 'exec', dont_inherit=True, optimize=0)
    except SyntaxError as e:
        return output_fail_compile(e)

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

        # if we need to check the return value
        if '<return>' in outputs:
            expected = outputs.pop('<return>')
            output_test(case_id, '<return>', expected == test_output, test_output)

        # if we need to check stdout
        if '<stdout>' in outputs:
            expected = outputs.pop('<stdout>')
            actual = env.stdout.getvalue()
            output_test(case_id, '<stdout>', expected == actual, actual)

        # check all other outputs
        for location, expected in outputs:
            actual = env.file_system.get(location, '<no such file>')
            output_test(case_id, location, expected == actual, actual)


def output_test(case_id, location, success, result=None):
    keyword = 'PASS' if success else 'FAIL'
    message = f'TEST {case_id} {keyword} {location}'
    if not success:
        message += ' ' + str(result)
    print(message)


def output_test_error(case_id, err: BaseException):
    # get line number of top stack frame in traceback
    line = traceback.extract_tb(err.__traceback__)[-1].lineno
    print(f'TEST {case_id} ERROR {line} {type(err).__name__}: {err}')


def output_fail_compile(err: SyntaxError):
    print(f'SYNTAX_ERROR {err.lineno} {err.offset} {err.msg}')


if __name__ == '__main__':
    run_tests(sys.argv[1], sys.argv[2])
