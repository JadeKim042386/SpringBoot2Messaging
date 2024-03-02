var eventSource = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

/**
 * 연결
 */
function connect() {
    eventSource = new EventSource("/api/v1/alarm/subscribe");
    eventSource.addEventListener('alarm', function (event) {
        console.log(event);
        showGreeting(event.data);
    });
}

/**
 * 종료(닫기)
 */
function disconnect() {
    eventSource.close();
    setConnected(false);
}

/**
 * 메시지 전송
 */
function sendName() {
    var name = $("#name").val();
    var receiverName = 'admin';

    var formData = new FormData();
    formData.append('content', name);
    formData.append('receiverName', receiverName);

    $.ajax({
        url:"/api/v1/alarm/send",
        data: formData,
        processData: false,
        contentType: false,
        type: "POST",
        success: function (result) {
            console.log("success");
        },
        error: function (result) {
            console.log("fail");
        }
    })
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $("#connect").click(() => connect());
    $("#disconnect").click(() => disconnect());
    $("#send").click(() => sendName());
});
