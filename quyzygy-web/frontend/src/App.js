import React, { Component } from 'react';
import HomePage from './components/homepage.js'
import { Route } from 'react-router-dom'
import StudentPage from "./components/studentpage.js";
import StudentQuiz from "./components/StudentQuiz.js";
import ProfPage from './components/profpage.js'
import createQuiz from './components/createQuiz.js'
import editQuizes from './components/editQuizes.js'
import startQuizPage from './components/startQuizPage.js'

class App extends Component {
  render() {
    return (
      <div className="App">
     
      <Route exact path='/' component={HomePage}/>
      <Route path='/studentPage' component={StudentPage}/>
      <Route path='/StudentQuiz' component={StudentQuiz}/>
      <Route path='/profPage' component={ProfPage}/>
      <Route path='/createQuiz' component={createQuiz}/>
      <Route path='/editQuizes' component={editQuizes}/>
      <Route path='/startQuizPage' component={startQuizPage}/>

      </div>
    );
  }
}

export default App;

