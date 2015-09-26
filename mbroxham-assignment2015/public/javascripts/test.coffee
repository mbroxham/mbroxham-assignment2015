console.log("I was loaded")

window.message = []

window.received = []

window.getMessage = (state) ->

  path = "http://" + state.server + "/api/tags/?tag=" + state.t

  xhr = new XMLHttpRequest()
  xhr.onreadystatechange = () ->
    if xhr.readyState == 4
      arr = JSON.parse(xhr.responseText)
      window.message = arr
      console.log(arr)
      rerender();

  console.log("asking for message")
  xhr.open("GET", path, true)
  xhr.send()


window.getSocket = () ->
  websocket = new WebSocket("ws://#{window.location.host}/ws?topic=breaking");
  websocket.onmessage = (msg) ->
    console.log("Received a message over the websocket:")
    console.log(msg)
    console.log("---")
    json = JSON.parse(msg.data)
    window.received.push(json)
    rerender()

window.getSocket()