import React, {Component} from 'react';
import {ToastsContainer, ToastsStore} from 'react-toasts';
import Dashboard from './Dashboard';
import ReactDOM from 'react-dom';


const serverName = "http://tm-stint-api.herokuapp.com/";
const s3Url = "https://task-management-app.s3.amazonaws.com/";
const initialState = {
    email:'',
    password:''
};
let snackbarMessage = '';
class SignInForm extends Component
{
    constructor()
    {
      super();
      if(sessionStorage.length > 0 && sessionStorage.getItem("token").length>0)
      {
        ReactDOM.render(<Dashboard />, document.getElementById('root'));
      }
      this.state = initialState;
      this.handleSubmit = this.handleSubmit.bind(this);
      this.handleChange = this.handleChange.bind(this);
    }
    handleSubmit = (event) =>
    {
        event.preventDefault();
        fetch(serverName+'login', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                email: this.state.email,
                password: this.state.password
            })
        }).then(response => {
            if (response.ok) {
                response.json().then(json => {
                  sessionStorage.setItem("token",json.token);
                  sessionStorage.setItem("userId",json.user_id);
                  localStorage.setItem("userName", json.name);
                  localStorage.setItem("email", json.email);
                  localStorage.setItem("image", json.image);
                  if(json.image)
                  {
                      localStorage.setItem("profilePhoto", s3Url+json.user_id);
                  }
                  else
                  {
                      localStorage.setItem("profilePhoto", "");
                  }  
                  localStorage.setItem("emailNotification", json.emailNotification);
                  localStorage.setItem("pushNotification", json.pushNotification);
                  snackbarMessage = "Success loggin in";
                  ToastsStore.success(snackbarMessage);
                  // navigate to dashboard
                  ReactDOM.render(<Dashboard />, document.getElementById('root'));
                });
              }
            else{
                response.json().then(json => {
                  snackbarMessage = json.errorMessage;
                  ToastsStore.error(snackbarMessage);
                  });
            }
        })
        .catch(error =>{
          snackbarMessage = error;
          ToastsStore.error(snackbarMessage);
        });
    }
    handleChange = (event) =>
    {
        let target = event.target;
        let value = target.type === 'checkbox' ? target.checked : target.value;
        let name = target.name;
        if(name === "email")
        {
            this.setState({email:value});
        }
        else if(name === "password")
        {
          this.setState({password:value});
        }
    }
    render()
    {
      return(
        <div>
        <div>
            <form className="FormCenter">
                <div className="FormField">
                    <label className="FormField__Label2" htmlFor="email">
                      Email
                    </label>
                    <input type="text" id="email" name="email" className="FormField__Input" placeholder="Enter your Email" onChange={this.handleChange}></input>
                </div>

                <div className="FormField">
                  <label className="FormField__Label2" htmlFor="password">
                    Password
                  </label>
                  <input type="Password" id="password" name="password" className="FormField__Input" placeholder="Enter your Password" onChange={this.handleChange}></input>
                </div>
                <br></br>
                <br></br>
                <div className="FormField">
                    <button className="FormField__Button mr-20" onClick={this.handleSubmit}>Log In</button>
                </div>
              </form> 
          </div>
          <div>
              <ToastsContainer store={ToastsStore}/>
          </div>
          </div>
      );
    }
}
export default SignInForm;
