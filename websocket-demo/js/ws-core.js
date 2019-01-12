var endpoint_LiveQuiz = "ws://127.0.0.1:8082";
var endpoint_QuizStatus = "ws://127.0.0.1:8081";

$(window).on('load', function(){
    //Student
    $("#jq-btn").on('click', function(){
        $("#jqstat1").html("Joining quiz...");
        var wsid = "";
        var ws = new WebSocket(endpoint_LiveQuiz);
                ws.onopen = function (openingData){
                    $("#jqstat1").html("Ready");
                };

                ws.onclose = function(closingData){
                    window.alert("Student ws closed!");
                };

                ws.onmessage = function(response){
                    var data = response.data;
                    console.log(data);
                    if (data.Success){
                        if (wsid == ""){
                            wsid = data.Data;
                        }
                        ws.send({Identity:{sk:localStorage.getItem('sk'),WSID:wsid},Action:"JoinQuiz",Data:""});
                    }
                    else{

                    }
                    
                    $("#jqstat1").html(data.Data);
                };
    });
    //Professor
    $("#actB").on('click', function(){
        $.ajax({
            url: endpoint_API + "activateQuiz?sk=" + localStorage.getItem('sk') + "&quizID=1",
            type: "POST",
            crossDomain: true,
            success: function (response) {
                $("#p11").html("Response: " + JSON.stringify(response));
                $("#p12").html("Subscribing to ws...");
                var ws = new WebSocket(endpoint_QuizStatus);
                ws.onopen = function (openingData){
                    $("#p12").html("Ready");
                };

                ws.onclose = function(closingData){
                    window.alert("Professor ws closed!");
                };

                ws.onmessage = function(response){
                    var data = response.data;
                    console.log(data);
                    $("#p12").html(data);
                };
            },
            error: function (xhr, status) {
                alert("error");
            }
        });
    });
});