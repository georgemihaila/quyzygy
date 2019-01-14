import React, {Component} from 'react';
import './App.css';
import Popup from "reactjs-popup";
import SignUp from './signup.js';
import $ from 'jquery';
import localStorage from 'localStorage';
import sha256 from 'sha256';

const contentStyle = {
    maxWidth: "600px",
    width: "90%"
};

class login extends Component {

    constructor(props) {
        super(props);
    }

    componentWillMount() {

        var endpoint_API = "http://quyzygy.us/";

        $(window).on('load', function () {
            $("#loginButton").on('click', function () {
                let hash = sha256($("#passwordBox").val());
                $.ajax({
                    url: endpoint_API + "login?email=" + $("#emailBox").val() + "&passwordHash=" + hash,
                    type: "POST",
                    crossDomain: true,
                    success: function (response) {
                        console.log(response);
                        updateLocalStorage(response);
                        localStorageDemo();

                        let StateUserType = localStorage.getItem('userType');
                        if (StateUserType === 'Student') {
                            window.location.replace('/studentPage');
                        }
                        if (StateUserType === 'Professor') {
                            window.location.replace('/profPage');
                        }
                    },
                    error: function (xhr, status) {
                        alert("error");
                    }
                });
            });
            localStorageDemo();
        });

        function updateLocalStorage(loginData) {
            localStorage.setItem('sk', loginData["secretKey"]);
            localStorage.setItem('userType', loginData['userType']);
        }

        function getLocalStorage() {
            var x = {};
            x['sk'] = localStorage.getItem('sk');
            x['userType'] = localStorage.getItem('userType');

            return x;
        }

        function localStorageDemo() {
            $("#localStorageDemo").html(JSON.stringify(getLocalStorage()));
        }
    }

    render() {

        return (
            <div className="LogIn">
                <div id='loginForm'>

                    <form>
                        <div className='block'>
                            <label htmlFor="exampleEmail">Email</label>
                            <input id="emailBox" type="text" placeholder="name@csie.ase.ro"/>
                        </div>
                        <div className='block'>
                            <label htmlFor="examplePassword" >Password</label>
                            <input id="passwordBox" type="password" placeholder="password"/>
                        </div>
                            <input className='btnClk logBtn' id='loginButton' type="button" value="Login" />
                    </form>

                    <p>Don't have an account?</p>

                    <Popup
                        trigger={<button className="btnClk logBtn"> Register! </button>}
                        modal
                        contentStyle={contentStyle}>
                        {close => (
                            <div>
                                <SignUp/>
                                <a className="close" onClick={close}>
                                    &times;
                                </a>
                            </div>
                        )}
                    </Popup>
                </div>
            </div>

        )
    }
}

export default login;
