import React, {Component} from 'react';
import {ToastsContainer, ToastsStore} from 'react-toasts';
const serverName = "http://tm-stint-api.herokuapp.com/";
const homePage = "http://tm-stint-webapp.herokuapp.com/sign-in";

const initialState = {
    fullName:'',
    email:'',
    phone:'',
    password:'',
    confirmPassword:'',
    terms:false,
    nameError:'',
    emailError:'',
    phoneError:'',
    passwordError:'',
    confirmPasswordError:''
};
class RegisterForm extends Component
{
    constructor()
    {
        super();
        this.state = initialState;
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.validate = this.validate.bind(this);
    }
 
    validate() {
        let nameError = "";
        let emailError = "";
        let phoneError = "";
        let passwordError = "";
        let confirmPasswordError = "";

        if(this.state.fullName.length === 0)
        {
            nameError = "Enter Name";
        }
        if(nameError)
        {
            this.setState({nameError});
            this.setState({emailError: "", phoneError:"", passwordError:"", confirmPasswordError:"" });
            return false;
        }

        if(!this.state.email.includes("@"))
        {
            emailError = "Enter Valid Email";
        }
        if(emailError)
        {
            this.setState({emailError});
            this.setState({nameError: "", phoneError:"", passwordError:"", confirmPasswordError:"" });
            return false;
        }

        if(this.state.phone.length!==10)
        {
            phoneError = "Enter a 10 digit phone number";
        }
        if(phoneError)
        {
            this.setState({emailError: "", nameError:"", passwordError:"", confirmPasswordError:"" });
            this.setState({phoneError});
            return false;
        }

        if(this.state.password.length<5)
        {
            passwordError = "Enter password of length greater than 5";
        }
        if(passwordError)
        {
            this.setState({passwordError});
            this.setState({emailError: "", phoneError:"", nameError:"", confirmPasswordError:"" });
            return false;
        }

        if(this.state.confirmPassword!==this.state.password)
        {
            confirmPasswordError = "Passwords should match";
        }
        if(confirmPasswordError)
        {
            this.setState({confirmPasswordError});
            this.setState({emailError: "", phoneError:"", passwordError:"", nameError:"" });
            return false;
        }

        this.setState({emailError: "", phoneError:"", passwordError:"", confirmPasswordError:"", nameError:""});
        return true;

    }
    handleSubmit(e)
    {
        e.preventDefault();
        const isValid = this.validate();
        if(isValid && this.state.terms)
        {
            this.setState = initialState;
            fetch(serverName + 'registerUser', {
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    name: this.state.fullName,
                    email: this.state.email,
                    phone: this.state.phone,
                    password: this.state.password,
                    confirmPassword: this.state.confirmPassword
                })
            }).then(response => {
                if (response.ok) {
                    response.json().then(json => {
                      window.location.href = homePage + "sign-in";
                    });
                  }
                else{
                    response.json().then(json => {
                      ToastsStore.error(json.errorMessage);
                    });
                }
            })
            .catch(error =>{
              ToastsStore.error("Something Went Wrong!.. ");
            });
        }   
    }
    handleChange(e)
    {
        let target = e.target;
        let value = target.type === 'checkbox' ? target.checked : target.value;
        let name = target.name;
        const isValid = this.validate();
        this.setState({
            [name] : value
        });
    }
    render()
    {
        return(
          <div className="Main">
            <div className="FormCenter">
            <form className="FormFields">           
              <div className="FormField">
                <label className="FormField__Label" htmlFor="fullName">
                    Name
                </label>
                <input type="text" id="fullName" name="fullName" className="FormField__Input" placeholder="Enter your First Name" value={this.state.fullName} onChange={this.handleChange}></input>
                <div style={{color:"red", fontSize:12}}>{this.state.nameError}</div>
              </div>

              <div className="FormField">
                <label className="FormField__Label" htmlFor="email">
                  Email
                </label>
                <input type="text" id="email" name="email" className="FormField__Input" placeholder="Enter your Email" value={this.state.email} onChange={this.handleChange}></input>
                <div style={{color:"red", fontSize:12}}>{this.state.emailError}</div>
              </div>


              <div className="FormField">
                <label className="FormField__Label" htmlFor="phone">
                  Phone Number
                </label>
                <input type="Phone" id="phone" name="phone" className="FormField__Input" placeholder="Enter your Phone" value={this.state.phone} onChange={this.handleChange}></input>
                <div style={{color:"red", fontSize:12}}>{this.state.phoneError}</div>
              </div>


              <div className="FormField">
                <label className="FormField__Label" htmlFor="password">
                  Password
                </label>
                <input type="Password" id="password" name="password" className="FormField__Input" placeholder="Enter your Password" value={this.state.password} onChange={this.handleChange}></input>
                <div style={{color:"red", fontSize:12}}>{this.state.passwordError}</div>
              </div>


              <div className="FormField">
                <label className="FormField__Label" htmlFor="confirmPassword">
                  Confirm Password
                </label>
                <input type="Password" id="confirmPassword" name="confirmPassword" className="FormField__Input" placeholder="Confirm your password" value={this.state.confirmPassword} onChange={this.handleChange}></input>
                <div style={{color:"red", fontSize:12}}>{this.state.confirmPasswordError}</div>
              </div>
              
              <div className="FormField">
                <button className="FormField__Button mr-20" onClick={this.handleSubmit}>Register</button>
              </div>
            </form>
          </div>
          </div>
        );
    }
}
export default RegisterForm;