import React, { Component } from 'react';
import Navbar from './Navbar.js';
import Board from './Board.js';

class Dashboard extends Component {
  constructor() {
    super();
  }
   render(){
    return (
      <div>
          <Navbar />      
          <div>
            <Board />
          </div>
      </div>
    );
   }
}

export default Dashboard;
