var mountNode = document.getElementById('mountNode')
var server = "localhost:9000"

var Message = React.createClass({
    render: function() {
        var obj = this.props.message
        return  <div>
                    <span className="messageUsername">{obj.username}</span><br />
                    <span className="messageText">{obj.message}</span><br />
                    <span className="messageTime">{obj.messageTime}</span><hr />
                </div>;
    }
});

var MessageList = React.createClass({
    render: function() {
        return <ul>{this.props.messageList.map(function(item) {
        return <Message message={item} />
        })}</ul>;
    }
});

var RenderAccount = React.createClass({
    checkPost: function() {
        var postTxt = document.getElementById('postTxt')
        var postButton = document.getElementById('postButton')
        var postCount = document.getElementById('postCount')
        if(postTxt.value != ''){
            postButton.value = 'Post'
            postButton.className = 'specialButton'
            postButton.disabled = false;
        } else{
            postButton.value = 'Start Typing'
            postButton.className = 'specialButtonDisabled'
            postButton.disabled = true;
        }

        if(postTxt.value.length >= 140){
            postTxt.value = postTxt.value.substring(0,140);
            postCount.style.color = "red";
        } else{
            postCount.style.color = "#999999";
        }
        postCount.innerHTML = postTxt.value.length.toString().concat("/140");
    },

    postSubmit: function(e) {
        e.preventDefault()
        window.postMessage(document.getElementById('postTxt').value)
    },

    render: function() {
        var username = this.props.username
        var email = this.props.username
        return  <div id="accountScreen" className="displayNone">
                    <h2><strong>Username: </strong><span dangerouslySetInnerHTML={{__html: username}}></span></h2>
                    <h2><strong>Email: </strong><span dangerouslySetInnerHTML={{__html: email}}></span></h2>

                    <form onSubmit={this.postSubmit}>
                        <p>
                            <textarea name="postTxt" rows="3" cols="40" id="postTxt" className="postbox" placeholder="Post Something" onChange={this.checkPost}></textarea>
                            <input type="submit" id="postButton" value="Start Typing" className="specialButtonDisabled" disabled="true"/> <span id="postCount">0/140</span>
                        </p>
                    </form>
                    <h2>My Posts</h2>
                    <MessageList messageList={window.received} />
                </div>;
}
});

var RenderBlankAccount = React.createClass({
    render: function() {
        return  <div id="accountScreen" className="displayNone">
                    <h3>You are not currently logged </h3>
                </div>;
    }
});

var MessageApp = React.createClass({
    getInitialState: function() {
        return {
            server: "localhost:9000",
            t: "",
            post: ""
        }
    },

    setServer: function(e) {
        var s = this.state
        s["server"] = e.target.value
        this.setState(s)
    },

    autoSearch: function(e) {
        var s = this.state
        s["t"] = e.target.value
        this.setState(s)
        window.receivedSearch = []
        if(document.getElementById('search').value != ''){
            searchwebsocket.send(document.getElementById('search').value)
        }
    },

    handleSubmit: function(e) {
        e.preventDefault()
        window.getMessage(this.state)
    },

    render: function() {
        var username = document.getElementById('usernameVar').value
        var email = document.getElementById('emailVar').value
        var account;
        if(username != ''){
            account = <RenderAccount username={username} email={email}/>
        } else{
            account = <RenderBlankAccount />
        }
        return (
        <div>
            {account}

            <div id="searchScreen">
                <h2>Search For Users or Posts</h2>
                <form onSubmit={this.handleSubmit}>
                    <p>
                        <input type="text" className="textbox" value={this.state.t} id="search" onChange={this.autoSearch} placeholder="Search"/>
                    </p>
                </form>
                <MessageList messageList={window.receivedSearch} />
            </div>

      </div>
    );
  }
});


var rerender = function() {
  React.render(<MessageApp />, mountNode);
}
rerender();

document.getElementById("search").focus();



