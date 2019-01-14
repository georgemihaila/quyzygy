import React, { Component } from 'react'
import UserStore from '../stores/UserStore'
import SignUp from './signup.js';


class UserList extends Component {
  constructor(){
    super()
    this.store = new UserStore()
    this.state = {
      Users : [],
      selectedUser : null
    }
    this.add = (User) => {
      this.store.addOne(User)
    }
    this.delete = (id) => {
      this.store.deleteOne(id)
    }
    this.save = (id, User) => {
      this.store.saveOne(id, User)
    }
    this.select = (id) => {
      let selected = this.state.Users.find((e) => e.id === id)
      this.setState({
        showGradesFor : id,
        selectedUser : selected
      })
    }
  }
  componentDidMount(){
    this.store.getAll()
    this.store.emitter.addListener('GET_ALL_SUCCESS', () => {
      this.setState({
        Users : this.store.content
      })
    })
  }
  render() {
      return (
        <div>
          {
            this.state.Users.map((e, i) => <User item={e} key={i} onDelete={this.delete} onSave={this.save} onSelect={this.select} />)
          }
          <SignUp onAdd={this.add} />
        </div>
      )
  }
}

export default UserList
