import React, { Component } from 'react';
import logo from '../assets/logo2.png';
import './App.css';
import { Redirect } from 'react-router';
import $ from 'jquery'

import {
    Collapse,
    Navbar,
    NavbarToggler,
    NavbarBrand,
    Nav,
    NavItem,
    NavLink,
} from 'reactstrap';

class startQuizPage extends Component {
    
      constructor(props) {
        super(props);

        this.toggle = this.toggle.bind(this);
        this.state = {
            isOpen: false,
            toStudentGrades: false,
            toMyQiuzzes: false,
            toStartQuiz: false,
            toCreateNewQuiz: false
        };
    }
    toggle() {
        this.setState({
            isOpen: !this.state.isOpen
        });
    }
    
    componentDidMount () {
        var endpoint = "http://quyzygy.us/";

        $(window).on('load', function(){
            refreshQuizzes();
        });

        function refreshQuizzes(){
            $.get(endpoint + 'myQuizzes?' + getParamCredentials() , function(data){
                console.log(data);
                let html = "<ol>";
                for (let i = 0; i < data.length; i++){
                    html += "<li>";
                    html += "Course: <b>" + data[i].CourseName + "</b></br>";
                    html += "Quiz name: " + data[i].QuizName;
                    html += "</br>Duration: " + parseInt(data[i].Duration) / 60 + "m";
                    html += "</br><button id='" + "act" + data[i].id + "'>Activate quiz</button>";
                    html += "<button id='" + "start" + data[i].id + "'> Start quiz</button>";
                    html += "</li>";
                    html += "<div id='lq" + data[i].id  + "'></div>"
                }
                html += "</ol>"
                $("#mainSection").html(html);
                for (let i = 0; i < data.length; i++){
                    $("#act" + data[i].id).on('click', function(){
                        var id = $(this).attr('id').replace('act', '');
                        $.post(endpoint + "activateQuiz?" + getParamCredentials() + "&quizID=" + id, function(data){
                            $("#act" + id).attr('disabled', true);
                            console.log(data);
                            $("#lq" + id).html("<h4>Access code: " + data.AccessCode + "</h4>");
                            let ws = new WebSocket("ws://79.115.188.173:8081");
                            ws.onopen = function(openiningData){
                                $("#lq" + id).append("Waiting for students to join...");
                                $("#lq" + id).append("<div id='wssarea'></div>");
                            };

                            ws.onclose = function(closingData){
                                console.log("Something happened and connection ws#" + id + " was closed.");
                            };

                            ws.onmessage = function(message){
                                try{
                                    let res = JSON.parse(message.data);
                                    let liveData = res.Data;
                                    console.log(liveData);
                                    $("#wssarea").html("");
                                    for (let i = 0; i < Object.keys(liveData).length; i++){
                                        let cc = Object.keys(liveData)[i];
                                        let cd = liveData[cc];
                                        console.log(cd);
                                        let ci = parseInt(cd.Answers.length);
                                        let ti = ci + parseInt(cd.LeftQuestions.length);
                                        let percent = ci * 100 / ti;
                                        $("#wssarea").append("<div class='myProgress'><div class='myBar' style='width:" + percent +"%'>" + ci + "/" + ti + "</div></div>");
                                    }
                                }
                                catch(e){
                                    console.log(e);
                                }
                            };
                        });
                    });
                    $("#start" + data[i].id).on('click', function(){
                        var id = $(this).attr('id').replace('start', '');
                        $.post(endpoint + "startQuiz?" + getParamCredentials() + "&quizID=" + id, function(data){
                            $("#start" + id).attr('disabled', true);
                            console.log(data);
                        });
                    });
                }
            });
        }

        function getParamCredentials(){
            return "sk=" + localStorage.getItem('sk');
        }
    }

  render() {
      
      if (this.state.toStudentGrades === true) {
      return <Redirect to='/StudentQuiz' />
    }
     if (this.state.toMyQiuzzes === true) {
      return <Redirect to='/editQuizes' />
    }
    if (this.state.toStartQuiz === true) {
      return <Redirect to='/StudentQuiz' />
    }
     if (this.state.toCreateNewQuiz === true) {
      return <Redirect to='/createQuiz' />
    }
      
    return (
      <div className="ProfPage" id='profPage'>
     
     <Navbar id='navbar' dark expand="md" fixed="top" height='40px'>
                    <NavbarBrand href="/">
                        <img src={logo} alt="logo" height='30px'/>
                    </NavbarBrand>
                    <NavbarToggler onClick={this.toggle} />
                    <Collapse isOpen={this.state.isOpen} navbar>
                        <Nav className="ml-auto" navbar>
                            <NavItem>
                                <NavLink href="/">Log out</NavLink>
                            </NavItem>
                        </Nav>
                    </Collapse>
                </Navbar>
                
                <div id='mainSection' className='mainSection'>
                
                </div>
     
      </div>
    );
  }
}

export default startQuizPage;
