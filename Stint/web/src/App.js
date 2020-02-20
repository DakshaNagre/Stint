import React, {Component} from 'react';
import {BrowserRouter as Router, Route, NavLink } from 'react-router-dom';
import './css/router.css';
import RegisterForm from './Components/RegisterForm';
import SignInForm from './Components/SignInForm';
import CustomCalendar from './Components/CustomCalendar';
// import Dashboard from './Components/Dashboard';

class App extends Component{
  render()
  {
    //get link
    // if link contains 
    if(!window.location.pathname.includes('calendar'))
    {
      return(
        <Router>
         <div className="App">
           <div className="App__Aside"></div>
           <div className="App__Form">
   
             <div className = "PageSwitcher">
               <NavLink  to="/register" activeClassName="PageSwitcher__Item--Active" className="PageSwitcher__Item">Sign up</NavLink >
               <NavLink  to="/sign-in" activeClassName="PageSwitcher__Item--Active" className="PageSwitcher__Item">Sign In</NavLink >
             </div>
   
             <Route path="/register" exact component={RegisterForm}></Route>
             <Route path="/sign-in" exact component={SignInForm}></Route>
   
   
           </div>
         </div>
        </Router>
       );
    }
    else
    {
      return(
        <CustomCalendar/>
      );
    }
    
  }
}
export default App;
