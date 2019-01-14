import React, { Component } from 'react';
import logo from '../assets/logo2.png';
import './App.css';
import { Redirect } from 'react-router';

import {
    Collapse,
    Navbar,
    NavbarToggler,
    NavbarBrand,
    Nav,
    NavItem,
    NavLink,
} from 'reactstrap';

class profpage extends Component {
    
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
    
  render() {
      
      if (this.state.toStudentGrades === true) {
      return <Redirect to='/StudentQuiz' />
    }
     if (this.state.toMyQiuzzes === true) {
      return <Redirect to='/editQuizes' />
    }
    if (this.state.toStartQuiz === true) {
      return <Redirect to='/startQuizPage' />
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
                
                <div className='mainSection'>
                
                    <div>
                   <input className='btnClk btnProf' type="button" value="START QUIZ" onClick={() => {
     
                          this.setState({ toStartQuiz: true});
                        }} />
                    </div>
                    <div>
                   <input className='btnClk btnProf' type="button" value="CREATE NEW QUIZ" onClick={() => {
     
                           this.setState({ toCreateNewQuiz: true});
                        }} />

                   <input className='btnClk btnProf' type="button" value="EDIT MY QUIZZES" onClick={() => {
     
                           this.setState({ toMyQiuzzes: true});
                        }} />
                    </div>
                     <div>
                   <input className='btnClk btnProf' type="button" value="SEE STUDENT GRADES" onClick={() => {
     
                           this.setState({ toStudentGrades: true});
                        }} />
                </div>
        </div>
     
      </div>
    );
  }
}

export default profpage;
