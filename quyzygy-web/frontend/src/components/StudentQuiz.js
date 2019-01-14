import React, { Component } from 'react';
import quizQuestions from "./../api/quizQuestions";
import Quiz from './Quiz';
import Result from './Result';
import $ from 'jquery'
import logo from './../svg/logo.svg';
import './App.css';

class StudentQuiz extends Component {
    constructor(props) {
        super(props);
    }

    componentDidMount () {
        var endpoint = "http://quyzygy.us/"

        $(window).on('load', function(){
            displayNextQuestion();
        });

        function displayNextQuestion(){
            $.get(endpoint + "nextQuestion?" + getParamCredentials(), function(data){
                console.log(data);
                if (data.Success){
                    let question = data.Data;
                    if (question == "Completed!"){
                        window.location = '/studentPage';
                        return;
                    }
                    $("#currentQuestionTitle").html(question.Text);
                    buildAnswers(question);                    
                }
                else{
                    $("#currentQuestionTitle").html("You shouldn't be seeing this :/");
                    $("#answer-area").html("");
                }
            });
        }

        function buildAnswers(question){
            var html = "";
            if (question.Type == "SingleAnswer"){
                html += '<ul class="answerOptions">';
                var qs = JSON.parse(question.Answers);
                for (let i = 0; i < qs.length; i++){
                    html += "<li class='answerOption'>";
                    html += '<input type="radio" class="radioCustomButton" name="radioGroup" id="ans' + i + '" value="' + qs[i] + '">';
                    html += '<label class="radioCustomLabel" for="ans' + i + '">' + qs[i] + '</label>';
                    html += "</li>";
                }
                html += '</ul>';
                $("#answer-area").html(html);
                //Events
                for (let i = 0; i < qs.length; i++){
                    var id = 'ans' + i;
                    $("#" + id).on('click', function(){
                        answerCurrentQuestion({questionID:question.id,answer:$("#" + id).value});
                    });
                }
            }
            else if (question.Type == "MultipleAnswer"){

            }
            else if (question.Type == "OpenAnswer"){
                html += '<ul class="answerOptions">';
                html += "<p>Answer:</p><input type='text' id='ans'/>"
                html+="<button id='ans-btn'>Answer</button>"
                html += '</ul>';
                $("#answer-area").html(html);
                $("#ans-btn" ).on('click', function(){
                    answerCurrentQuestion({questionID:question.id,answer:$("#ans").value});
                });
            }
            return html;
        }

        function answerCurrentQuestion(answer){
            $.ajax({
                url: endpoint + "postAnswer?" + getParamCredentials(),
                type: "POST",
                crossDomain: true,
                contentType:"application/json",
                data:JSON.stringify({questionID:answer.questionID, answer:answer.answer}),
                success: function (response) {
                    displayNextQuestion();
                },
                error: function (xhr) {
                    console.log(xhr);
                }
            });
        }

        function getParamCredentials(){
            var id = JSON.parse(localStorage.getItem('identity'));
            return "sk=" + id.SecretKey + "&ac=" + id.AccessCode + "&wsid=" + id.WSID;
        }
    }

    render() {
        return (
            <div className="StudentQuiz">
                <div className="App-header">
                    <img src={logo} className="App-logo" alt="logo" />
                    <h2>React Quiz</h2>
                </div>
               <div id='quiz-area'>
                    <center>
                        <h1 id="currentQuestionTitle"></h1>
                        <div id='answer-area'></div>
                    </center>

               </div>
            </div>
        );
    }
}

export default StudentQuiz;
