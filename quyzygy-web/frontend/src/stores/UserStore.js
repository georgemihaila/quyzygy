import axios from 'axios'
import {EventEmitter} from 'fbemitter'

const SERVER = 'http://quyzygy.us/register'

class UserStore{
  constructor(){
    this.content = []
    this.emitter = new EventEmitter()
  }
  async getAll(){
    try {
      let response = await axios(`${SERVER}/users`)
      this.content = response.data
      this.emitter.emit('GET_ALL_SUCCESS')
    } catch (e) {
      console.warn(e)
      this.emitter.emit('GET_ALL_ERROR')
    }
  }
  async addOne(user){
    try {
      await axios.post(`${SERVER}/users`, user)
      this.emitter.emit('ADD_SUCCESS')
      this.getAll()
    } catch (e) {
      console.warn(e)
      this.emitter.emit('ADD_ERROR')
    }
  }
  async deleteOne(id){
    try {
      await axios.delete(`${SERVER}/users/${id}`)
      this.emitter.emit('DELETE_SUCCESS')
      this.getAll()
    } catch (e) {
      console.warn(e)
      this.emitter.emit('DELETE_ERROR')
    }
  }
  async saveOne(id, user){
    try {
      await axios.put(`${SERVER}/users/${id}`, user)
      this.emitter.emit('SAVE_SUCCESS')
      this.getAll()
    } catch (e) {
      console.warn(e)
      this.emitter.emit('SAVE_ERROR')
    }
  }
}

export default UserStore