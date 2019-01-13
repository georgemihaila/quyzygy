var endpoint_API = "http://quyzygy.us/"

$(window).on('load', function(){
    $("#loginButton").on('click', function(){
        let hash = sha256($("#passwordBox").val());
        $.ajax({
            url: endpoint_API + "login?email=" + $("#emailBox").val() + "&passwordHash=" + hash,
            type: "POST",
            crossDomain: true,
            success: function (response) {
                console.log(response);
                updateLocalStorage(response);
                localStorageDemo();
            },
            error: function (xhr, status) {
                alert("error");
            }
        });
    });
    $("#getQB").on('click', function(){
        $.get(endpoint_API + "myQuizzes?sk=" + localStorage.getItem('sk'), function(data){
            $("#quizList").html(JSON.stringify(data));
        });
    });
    localStorageDemo();
});

function updateLocalStorage(loginData){
    localStorage.setItem('sk', loginData["secretKey"]);
    localStorage.setItem('userType', loginData['userType']);
}

function getLocalStorage(){
    var x = {};
    x['sk'] = localStorage.getItem('sk');
    x['userType'] = localStorage.getItem('userType');
    return x;
}

function localStorageDemo(){
    $("#localStorageDemo").html(JSON.stringify(getLocalStorage()));
}