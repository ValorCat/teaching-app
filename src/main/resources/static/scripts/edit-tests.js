
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