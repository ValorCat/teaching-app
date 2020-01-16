
/* INITIALIZE HTML ================================================= */

function setup() {
    var htmlList = document.getElementById('test-list')
    for (var test of testData) {
        // build accordion
        var header = document.getElementById('accordion-prototype').firstElementChild.cloneNode(true)
        header.innerHTML += collapse(test)
        header.addEventListener('click', function() {
            this.classList.toggle('open')
            var panel = this.nextElementSibling
            panel.style.maxHeight = panel.style.maxHeight ? null : panel.scrollHeight + "px"
            panel.classList.toggle('open')
        })
        htmlList.appendChild(header)

        // build content panel
        var panel = document.getElementById('panel-prototype').lastElementChild.cloneNode(true)
        htmlList.appendChild(panel)

        // add test snippet
        if (test.test) {
            toggleType(panel)
            panel.querySelector('.test-snippet input').value = test.test
        }

        // add inputs/outputs
        for (var loc in test.inputs) {
            var testLoc = convertIOLocation(loc, 'input')
            addInitialIO(panel, testLoc, 'input', loc, test.inputs[loc])
        }
        for (var loc in test.outputs) {
            var testLoc = convertIOLocation(loc, 'output')
            addInitialIO(panel, testLoc, 'output', loc, test.outputs[loc])
        }
    }
}

/* DROPDOWN BUTTONS ================================================= */

var lastDropdown = null

function toggleDropdown(element) {
    lastDropdown = element.nextElementSibling
    lastDropdown.classList.toggle('active-dropdown')
}

// close dropdowns when user clicks somewhere else
window.onclick = function(event) {
    for (var dropdown of document.getElementsByClassName('dropdown-content')) {
        if (dropdown !== lastDropdown && dropdown.classList.contains('active-dropdown')) {
            dropdown.classList.remove('active-dropdown')
        }
    }
    lastDropdown = null
}

/* MODIFY TEST PROPERTIES =================================================== */

 function toggleType(element) {
    var panel = element.closest('.panel')
    var testAllLine = panel.getElementsByClassName('test-all')[0]
    var testSnippetLine = panel.getElementsByClassName('test-snippet')[0]
    testAllLine.hidden = !testAllLine.hidden
    testSnippetLine.hidden = !testSnippetLine.hidden
}

function addInitialIO(panel, source, ioType, location, content) {
    var newLine = addIO(panel, source, ioType)
    var fields = newLine.getElementsByTagName('input')
    if (fields.length == 1) {
        fields[0].value = content
    } else if (fields.length == 2) {
        fields[0].value = location
        fields[1].value = content
    }
}

function addIO(element, source, ioType) {
    var panel = element.closest('.panel')
    var lines = panel.getElementsByClassName(ioType)[0]
    if (lines.firstElementChild.innerText === 'None') {
        lines.removeChild(lines.firstElementChild)
    }
    var prototype = document.getElementById('io-prototype')
    var newLine = prototype.getElementsByClassName(source)[0].cloneNode(true)
    var dropButton = prototype.getElementsByClassName('button')[0].cloneNode(true)
    newLine.appendChild(dropButton)
    lines.appendChild(newLine)
    if (panel.classList.contains('open')) {
        panel.style.maxHeight = panel.scrollHeight + "px"
    }
    return newLine
}

function dropTest(button) {
    var panel = button.parentElement
    var header = panel.previousElementSibling
    panel.remove()
    header.remove()
}

function dropIO(element) {
    var line = element.parentElement
    var section = line.parentElement
    section.removeChild(line)
    if (section.childElementCount === 0) {
        var none = document.createElement('i')
        none.innerHTML = 'None'
        section.appendChild(none)
    }
}

function convertIOLocation(location, ioType) {
    switch (location) {
        case '<stdin>':     return 'stdin'
        case '<stdout>':    return 'stdout'
        case '<return>':    return 'return'
        default:            return ioType === 'input' ? 'infile' : 'outfile'
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