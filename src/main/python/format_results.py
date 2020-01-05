map_inputs = {
    '<stdin>':  " with the console containing '{content}'",
    '*':        " with the file '{location}' containing '{content}'"
}

map_outputs = {
    '<stdout>': " I should see '{content}'",
    '<return>': " I should get {content}",
    '*':        " the file '{location}' should contain '{content}'"
}


def _translate(location: str, content: str, mapping: dict):
    translation = mapping.get(location) or mapping['*']
    return translation.format(location=location, content=content)


def _translate_all(inputs: dict, mapping: dict):
    return ' and'.join(_translate(location, content, mapping) for location, content in inputs.items())


def build_pass_msg(case: dict):
    test = case.get('test', 'the code')
    inputs = _translate_all(case['inputs'], map_inputs)
    with open('C:/Users/Anthony/Desktop/output.txt', 'a') as f:
        print(case, file=f)
    outputs = _translate_all(case['outputs'], map_outputs)
    return f"When I run {test}{inputs},{outputs}."


def build_fail_msg(case: dict, location: str, result):
    test = case.get('test', 'the code')
    inputs = _translate_all(case['inputs'], map_inputs)
    output = _translate(location, case['outputs'][location], map_outputs)
    return f"When I run {test}{inputs},{output} (found {result!r})."


def build_error_msg(case: dict, line: int, error):
    test = case.get('test', 'the code')
    inputs = _translate_all(case['inputs'], map_inputs)
    outputs = _translate_all(case['outputs'], map_outputs)
    return f"When I run {test}{inputs},{outputs} (line {line} threw {type(error).__name__}: {error})."
