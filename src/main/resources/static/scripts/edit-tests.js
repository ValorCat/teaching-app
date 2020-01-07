
/* INITIALIZE HTML ================================================= */

function setup() {
    var htmlList = document.getElementById('test-list')
    for (var test of testData) {
        var header = document.createElement('p')
        header.innerHTML = collapse(test)
        header.classList.add('accordion')
        header.addEventListener('click', function() {
            this.classList.toggle('open')
            var panel = this.nextElementSibling
            panel.style.maxHeight = panel.style.maxHeight ? null : panel.scrollHeight + "px"
        })
        htmlList.appendChild(header)

        var panel = document.createElement('div')
        panel.innerHTML = 'content'
        panel.classList.add('panel')
        htmlList.appendChild(panel)
    }
}

/* COLLAPSE TESTS ================================================= */

inputMap = {
    '<stdin>':  " with the console containing '{content}'",
    '*':        " with the file '{location}' containing '{content}'"
}

outputMap = {
    '<stdout>': " I should see '{content}'",
    '<return>': " I should get {content}",
    '*':        " the file '{location}' should contain '{content}'"
}

function translate(inputs, mapping) {
    var outputs = []
    for (var loc in inputs) {
        var translation = mapping[loc] || mapping['*']
        outputs.push(translation
            .replace('{location}', loc)
            .replace('{content}', inputs[loc])
        )
    }
    return outputs.join(' and')
}

function collapse(test) {
    var expr = test.test || 'the code'
    var inputs = translate(test.inputs, inputMap)
    var outputs = translate(test.outputs, outputMap)
    return 'When I run ' + expr + inputs + ',' + outputs + '.'
}