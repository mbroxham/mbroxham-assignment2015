var searchwebsocket;
var userwebsocket;

window.message = [];

window.received = [];

window.receivedSearch = [];

window.getMessage = function(state) {
    var path, xhr;
    path = "http://" + state.server + "/api/tags/" + state.t;
    xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        var arr;
        if (xhr.readyState === 4) {

            arr = JSON.parse(xhr.responseText);
            window.message = arr;
            console.log(arr);
            console.log("Received API Result");
            return rerender();
        }
    };
    console.log("asking for message");
    xhr.open("GET", path, true);
    return xhr.send();
};

window.postMessage = function(message) {
    var path, xhr;
    var s = this.state
    path = "http://" + server + "/api/postmessage";
    xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        var arr;
        if (xhr.readyState === 4) {
            console.log("Post message received: " + xhr.responseText);
            return rerender();
        }
    };
    console.log("Sending message...");
    xhr.open("POST", path, true);
    xhr.setRequestHeader("Content-Type", "text/plain")
    return xhr.send(message);
};

window.getSearchSocket = function() {
    searchwebsocket = new WebSocket("ws://" + window.location.host + "/ws?socketType=search");
    console.log("Search Websocket Created:");
    return searchwebsocket.onmessage = function(msg) {
        var json;
        console.log("Received a message over the search websocket:");
        console.log(msg);
        console.log("---");
        json = JSON.parse(msg.data);
        for(var i = 0; i < json.length; i++) {
            //display order i had from A1 is different for user and tag posts, this switches the order or the array
            if(document.getElementById('search').value.substring(0,1) == '@' && json.length > 1){
                window.receivedSearch.push(json[i]);
            }else{
                window.receivedSearch.unshift(json[i]);
            }

        }
        return rerender();
    };
};

window.getUserSocket = function() {
    userwebsocket = new WebSocket("ws://" + window.location.host + "/ws?socketType=userpost");
    console.log("User Websocket Created:");
    return userwebsocket.onmessage = function(msg) {
        var json;
        console.log("Received a message over the user websocket:");
        console.log(msg);
        console.log("---");
        json = JSON.parse(msg.data);
        if(json.length == 1){
            window.received.unshift(json[0]);
        } else{
            for(var i = 0; i < json.length; i++) {
                window.received.push(json[i]);
            }
        }
        document.getElementById('postTxt').value = "";
        var postButton = document.getElementById('postButton');
        postButton.value = 'Start Typing';
        postButton.className = 'specialButtonDisabled';
        postButton.disabled = true;
        return rerender();
    };
};

window.getSearchSocket();
if(document.getElementById('usernameVar').value != ''){
    window.getUserSocket();
}

