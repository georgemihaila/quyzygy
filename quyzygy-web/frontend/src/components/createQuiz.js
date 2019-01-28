import React, { Component } from 'react';
import './App.css';

class createQuiz extends Component {

  constructor(props){
    super(props);
    this.state={
      title: 'Create new quiz',
      act: 0,
      index: '',
      datas: []
    }
  } 

  componentDidMount(){
    this.refs.quizName.focus();
  }

  fSubmit = (e) =>{
    e.preventDefault();
    console.log('try');

    let datas = this.state.datas;
    let quizName = this.refs.quizName.value;
    let quizContent = this.refs.quizContent.value;
    let answer1  = this.refs.answer1.value;
    let answer2  = this.refs.answer2.value;
    let answer3  = this.refs.answer3.value;
    let answer4  = this.refs.answer4.value;

    if(this.state.act === 0){   //new
      let data = {
        quizName, quizContent,answer1,answer2,answer3,answer4
      }
      datas.push(data);
    }else{                      //update
      let index = this.state.index;
      datas[index].quizName = quizName;
      datas[index].quizContent = quizContent;
      datas[index].answer1 = answer1;
      datas[index].answer2 = answer2;
      datas[index].answer3 = answer3;
      datas[index].answer4 = answer4;

    }    

    this.setState({
      datas: datas,
      act: 0
    });

    this.refs.myForm.reset();
    this.refs.quizName.focus();
  }

  fRemove = (i) => {
    let datas = this.state.datas;
    datas.splice(i,1);
    this.setState({
      datas: datas
    });

    this.refs.myForm.reset();
    this.refs.quizName.focus();
  }

  fEdit = (i) => {
    let data = this.state.datas[i];
    this.refs.quizName.value = data.quizName;
    this.refs.quizContent.value = data.quizContent;
    this.refs.answer1.value = data.answer1;
    this.refs.answer2.value = data.answer2;
    this.refs.answer3.value = data.answer3;
    this.refs.answer4.value = data.answer4;
    

    this.setState({
      act: 1,
      index: i
    });

    this.refs.quizName.focus();
  }  

  render() {
    let datas = this.state.datas;
    return (
      <div className="createQuiz">
        <h2>Create a quiz</h2>
        <input type="text" ref="quizName" placeholder="quiz name" className="formField" />
        <form ref="myForm" className="myForm">
          <input type="text" ref="quizContent" placeholder="Question" className="formField" />
          <br/>
          <input type="text" ref="answer1" placeholder="answer" className="formField" />
          <br/>
          <input type="text" ref="answer2" placeholder="answer" className="formField" />
          <br/>
          <input type="text" ref="answer3" placeholder="answer" className="formField" />
          <br/>
          <input type="text" ref="answer4" placeholder="answer" className="formField" />
          <button onClick={(e)=>this.fSubmit(e)} className="myButton">add question </button>
        </form>
        <pre>
          {datas.map((data, i) =>
            <li key={i} className="myList">
              {i+1}. {data.quizContent}
              <br/>
              {data.answer1}
              <br/>
              {data.answer2}
              <br/>
              {data.answer3}
              <br/>
              {data.answer4}
              <br/>
              <button onClick={()=>this.fRemove(i)} className="myListButton">remove </button>
              <button onClick={()=>this.fEdit(i)} className="myListButton">edit </button>
            </li>
          )}
        </pre>
      </div>
    );
  }
}

export default createQuiz;