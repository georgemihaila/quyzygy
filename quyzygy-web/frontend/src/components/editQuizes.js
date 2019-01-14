import React from 'react';
import { Table } from 'reactstrap';

export default class Example extends React.Component {
  render() {
    return (
      <div>
      <h2> My quizes</h2>
      
      <Table>
        <thead>
          <tr>
            <th>#</th>
            <th>Quiz name</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <th scope="row">1</th>
            <td>Quiz1</td>
            <button className="myListButton">view </button>
          </tr>
          <tr>
            <th scope="row">2</th>
            <td>Quiz2</td>
            <button className="myListButton">view </button>
          </tr>
          <tr>
            <th scope="row">3</th>
            <td>Quiz3</td>
            <button className="myListButton">view </button>
          </tr>
        </tbody>
      </Table>
      </div>
    );
  }
}