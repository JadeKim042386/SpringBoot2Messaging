var socket = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

/**
 * 웹 소켓 연결
 */
function connect() {
    var ws = new SockJS("/myHandler");

    /**
     * 소켓이 열릴 경우 수행
     */
    ws.onopen = () => {
        setConnected(true);
        console.log('Open');
    };

    /**
     * 소켓을 통해 메시지가 전송될 경우 수행
     */
    ws.onmessage = (event) => {
        console.log('On Message: ', event.data);
        showGreeting(JSON.parse(event.data).name);
    };

    /**
     * 소켓이 닫힐 경우 수행
     */
    ws.onclose = () => {
        console.log('Close');
    }

    socket = ws;
}

/**
 * 웹 소켓 종료(닫기)
 */
function disconnect() {
    socket.close();
    setConnected(false);
}

/**
 * 메시지 전송
 */
function sendName() {
    var name = $("#name").val();
    var receiverId = '1';
    socket.send(JSON.stringify({'name': name, 'receiverId': receiverId}));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $( "#connect" ).click(() => connect());
    $( "#disconnect" ).click(() => disconnect());
    $( "#send" ).click(() => sendName());
});
