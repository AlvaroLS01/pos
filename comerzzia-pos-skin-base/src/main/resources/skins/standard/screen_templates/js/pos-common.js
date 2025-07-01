function executeOperation(path, operation, params = null) {
    var url = "czzpos://method/" + path + "?operation=" + operation;

    if (params != null) {
        for (i = 0; i < params.length; i++) {
            url = url + "&" + params[i].name + "=" + params[i].value;
        }
    }

    safeGet(url);
}

function execute(path, params = null) {
    var url = "czzpos://method/" + path;

    if (params != null) {
        for (i = 0; i < params.length; i++) {
            if (i == 0) {
                url = url + "?";
            } else {
                url = url + "&";
            }

            url = url + params[i].name + "=" + params[i].value;
        }
    }

    safeGet(url);
}

function safeGet(url) {
    var e = document.createElement('a');
    e.id = 'safeMethod';
    e.href = url;
    document.getElementsByTagName('body')[0].appendChild(e);
    e.click();
    e.parentNode.removeChild(e);
  }