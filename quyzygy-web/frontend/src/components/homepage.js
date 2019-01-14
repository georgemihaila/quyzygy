import React, { Component } from 'react';
import './App.css';
import logo from '../assets/logo2.png';
import LogIn from './login.js';
import {
    Collapse,
    Navbar,
    NavbarToggler,
    NavbarBrand,
    Nav,
    NavItem,
    NavLink,
} from 'reactstrap';
import { Link } from 'react-router-dom'

class homepage extends Component {
    
    
    constructor(props) {
        super(props);

        this.toggle = this.toggle.bind(this);
        this.state = {
            isOpen: false
        };
    }
    toggle() {
        this.setState({
            isOpen: !this.state.isOpen
        });
    }
    render() {
        return (
            <div className="HomePage">
                <Navbar id='navbar' dark expand="md" fixed="top" height='40px'>
                    <NavbarBrand href="/">
                        <img src={logo} alt="logo" height='30px'/>
                    </NavbarBrand>
                    <NavbarToggler onClick={this.toggle} />
                    <Collapse isOpen={this.state.isOpen} navbar>
                        <Nav className="ml-auto" navbar>
                            <NavItem>
                                <NavLink href="#UtilitySection">Utility</NavLink>
                            </NavItem>
                            <NavItem>
                                <NavLink href="#SignUpSection">Sign Up</NavLink>
                            </NavItem>
                            <NavItem>
                                <NavLink href="#AboutSection">About us</NavLink>
                            </NavItem>
                            <NavItem>
                                <NavLink href="#DonateSection">Donate</NavLink>
                            </NavItem>
                        </Nav>
                    </Collapse>
                </Navbar>
                
                
                <div id="UtilitySection">
                    Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's
                    standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a
                    type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting,
                    remaining
                    essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum
                    passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.
                    
                    
                    <li><Link to='/studentPage'>Student Page</Link></li>
                    <li><Link to='/profPage'>Proff Page</Link></li>
                  </div>
                
                  <div id="SignUpSection">
                  <LogIn />
                </div>
                
                  <div id="AboutSection">
                    It is a long established fact that a reader will be distracted by the readable content of a page when looking at its
                    layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to
                    using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web
                    page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web
                    sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on
                    purpose (injected humour and the like).
                  </div>
                  <div id="DonateSection">
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna
                    aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
                    Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur
                    sint
                    occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
                  </div>

      </div>
    );
  }
}

export default homepage;