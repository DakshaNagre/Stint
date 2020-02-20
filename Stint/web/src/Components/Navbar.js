import React, { Component } from 'react'
import { AppBar, Toolbar, Typography} from '@material-ui/core'
import IconButton from '@material-ui/core/IconButton';
import MoreIcon from '@material-ui/icons/MoreVert';
import EventIcon from '@material-ui/icons/Event';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
// import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import Slide from '@material-ui/core/Slide';
import Switch from '@material-ui/core/Switch';
import FormGroup from '@material-ui/core/FormGroup';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import FormControl from '@material-ui/core/FormControl';
import { ImagePicker } from 'react-file-picker'
import Avatar from '@material-ui/core/Avatar';
import Grid from '@material-ui/core/Grid';
import PersonIcon from '@material-ui/icons/Person';
import FormLabel from '@material-ui/core/FormLabel';
import ReactDOM from 'react-dom';
// import SignIn from './SignInForm'
import App from './../App.js'
import { ToastsStore } from 'react-toasts';
import CustomCalendar from './CustomCalendar.js';


const serverName = "http://tm-stint-api.herokuapp.com/";
const s3Url = "https://task-management-app.s3.amazonaws.com/";
let snackbarMessage = "";



const Transition = React.forwardRef(function Transition(props, ref) {
    return <Slide direction="up" ref={ref} {...props} />;
  });


class Navbar extends Component {
    constructor()
    {
        super();
        this.state = {
            anchorEl : null,
            open: false,
            name:'',
            email:'',
            password:'',
            confPassword:'',
            emailNotification:false,
            pushNotification: false,
            profilePhoto:''
        }
        this.handleProfileMenuOpen = this.handleProfileMenuOpen.bind(this);
        this.handleProfileMenuClose = this.handleProfileMenuClose.bind(this);
        this.handleLogOut = this.handleLogOut.bind(this);
        this.handleEditUser = this.handleEditUser.bind(this);
        this.handleClose = this.handleClose.bind(this);
        this.handleUserChange = this.handleUserChange.bind(this);
        this.handleEmailNotificationChange = this.handleEmailNotificationChange.bind(this);
        this.handlePushNotificationChange = this.handlePushNotificationChange.bind(this);
        this.handleUpdateProfile = this.handleUpdateProfile.bind(this);
        this.handleProfilePhoto = this.handleProfilePhoto.bind(this);
        this.handleCalendarOpen = this.handleCalendarOpen.bind(this);
    }


    handleCalendarOpen() {
        ReactDOM.render(<CustomCalendar />, document.getElementById('root'));
    }

    handleProfileMenuOpen(event)
    {
        this.setState({anchorEl: event.currentTarget});
    }
    handleProfileMenuClose(event)
    {
        this.setState({anchorEl: null});
    }
    handleLogOut()
    {
        sessionStorage.setItem("token","");
        sessionStorage.setItem("userId","");
        localStorage.setItem("boardId","");
        localStorage.setItem("boardName","");
        localStorage.setItem("boardColor","");
        localStorage.setItem("boardImage","");
        localStorage.setItem("boardDescription","");
        localStorage.setItem("Color", "");
        ReactDOM.render(<App/>, document.getElementById('root'));
    }

    handleUpdateProfile()
    {
        
        fetch(serverName + 'editUser', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization' : sessionStorage.getItem("token")
            },
              body: JSON.stringify({
                name: this.state.name,
                email: this.state.email,
                emailNotification: this.state.emailNotification,
                pushNotification: this.state.pushNotification,
                image: this.state.profilePhoto,
                password: this.state.password,
                confirmPassword: this.state.confPassword,
              })
            }).then(response => {
                if (response.ok) {
                    response.json().then(json => {
                        localStorage.setItem("userName", this.state.name);
                        localStorage.setItem("email", this.state.email);
                        localStorage.setItem("emailNotification", this.state.emailNotification);
                        localStorage.setItem("pushNotification", this.state.pushNotification);
                        localStorage.setItem("profilePhoto", s3Url+sessionStorage.getItem("userId"));


                        snackbarMessage = "Update successful";
                        ToastsStore.success(snackbarMessage);
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

        this.handleClose();
        
    }

    handleEditUser(e)
    {
        this.setState({open:true,
            name: localStorage.getItem("userName"),
            email: localStorage.getItem("email"),
            emailNotification: JSON.parse(localStorage.getItem("emailNotification")),
            pushNotification: JSON.parse(localStorage.getItem("pushNotification")),
            
            });
    }

    handleProfilePhoto(image)
    {
        this.setState({profilePhoto:image})
    }

    handleClose()
    {
        this.setState({open:false})
    }

    handleEmailNotificationChange(e)
    {
        this.setState({emailNotification: e.target.checked});
    }

    handlePushNotificationChange(e)
    {
        this.setState({pushNotification: e.target.checked});
    }

    handleUserChange(e)
    {
        e.preventDefault();
        if(this.state.email !== '')
        {
            let target = e.target;
            let value = target.value;
            let name = target.id;
            this.setState({
                 [name] : value
            });
        }


    }

    render(){
        return(
            <div>
                <AppBar position="static" color="primary">
                    <div style={{ display: "flex" }}>
                        <Toolbar>
                            <Typography variant="h5" color="inherit">Stint</Typography>
                        </Toolbar>
                        <IconButton 
                            aria-label="calendar" 
                            color="inherit"  
                            onClick={this.handleCalendarOpen}
                            style={{ marginLeft: "auto", float: "right"}}
                            >
                            <EventIcon />
                        </IconButton>
                        <IconButton
                                aria-label="show more"
                                aria-haspopup="true"
                                onClick={this.handleProfileMenuOpen}
                                color="inherit"
                                style={{ float: "right", width:70, height:70}}
                                >
                                <MoreIcon />
                        </IconButton>
                        <Menu
                            id="simple-menu"
                            anchorEl={this.state.anchorEl}
                            keepMounted
                            open={Boolean(this.state.anchorEl)}
                            onClose={this.handleProfileMenuClose}
                            >
                            <MenuItem onClick={this.handleEditUser}>User Profile</MenuItem>
                            <MenuItem onClick={this.handleLogOut}>Logout</MenuItem>
                        </Menu>
                    </div>
                </AppBar>

                <div>
                    <Dialog open={this.state.open}
                            TransitionComponent={Transition}
                            keepMounted
                            onClose={this.handleClose}
                            aria-labelledby="alert-dialog-slide-title"
                            aria-describedby="alert-dialog-slide-description"
                            maxWidth='md'
                            fullWidth = {true}>
                                <Grid container justify="center" alignItems="center">
                                    <ImagePicker
                                    onChange={this.handleProfilePhoto}
                                    style={{marginTop:10}}
                                    dims={{minWidth: 100, maxWidth: 4000, minHeight: 100, maxHeight: 4000}}>
                                        <Avatar alt="Remy Sharp" src={this.state.profilePhoto=="" ? (localStorage.getItem("profilePhoto") !== "" ? localStorage.getItem("profilePhoto") : "") 
                                        :this.state.profilePhoto} style={{width:50, height:50}} ><PersonIcon/></Avatar>
                                    </ImagePicker>

                                    <DialogTitle id="form-dialog-title" style={{marginTop:10}}>User Profile</DialogTitle>
                                </Grid>

                        <DialogContent>
                            <div>
                                <TextField
                                    margin="dense"
                                    id="name"
                                    label="Name"
                                    type="name"
                                    name="name"
                                    value={this.state.name}
                                    onChange={this.handleUserChange}
                                    fullWidth
                                />
                            </div>

                            <div>
                                <TextField
                                    margin="dense"
                                    id="email"
                                    name="email"
                                    label="Email Address"
                                    type="email"
                                    value={this.state.email}
                                    onChange={this.handleUserChange}
                                    fullWidth
                                />
                            </div>

                            <div>
                                <TextField
                                    margin="dense"
                                    id="password"
                                    label="Password"
                                    type="password"
                                    name="password"
                                    value={this.state.password}
                                    onChange={this.handleUserChange}
                                    fullWidth
                                />
                            </div>

                            <div>
                                <TextField
                                    margin="dense"
                                    id="confPassword"
                                    label="Confirm Password"
                                    type="password"
                                    value={this.state.confPassword}
                                    onChange={this.handleUserChange}
                                    fullWidth
                                    style={{width: "100%"}}
                                />
                            </div>

                            <FormControl component="fieldset" style={{marginTop:15}}>
                                <FormLabel  component="legend">Notifications?</FormLabel>
                                <FormGroup aria-label="position" row>

                                    <FormControlLabel

                                    control={<Switch color="primary" id="emailNotification" checked={this.state.emailNotification} onChange={this.handleEmailNotificationChange} value={this.state.emailNotification}/>}
                                    label="Email"
                                    labelPlacement="start"
                                    />
                                    <FormControlLabel
                                    // id="pushNotification"
                                    control={<Switch color="primary" id="pushNotification" checked={this.state.pushNotification} onChange={this.handlePushNotificationChange} value={this.state.pushNotification}/>}
                                    label="Push"
                                    labelPlacement="start"
                                    />
                                </FormGroup>
                                </FormControl>

                        </DialogContent>
                        <DialogActions>
                        <Button onClick={this.handleUpdateProfile} color="primary">
                            Update
                        </Button>
                        <Button onClick={this.handleClose} color="secondary">
                            Cancel
                        </Button>
                        </DialogActions>
                    </Dialog>
                </div>

            </div>
        );
    }

}
export default Navbar;
