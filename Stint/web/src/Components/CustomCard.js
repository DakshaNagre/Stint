import React from 'react'
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import Button from '@material-ui/core/Button';
import IconButton from '@material-ui/core/IconButton';
import Typography from '@material-ui/core/Typography';
import EditIcon from '@material-ui/icons/Edit';
import { CardHeader } from '@material-ui/core';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import Slide from '@material-ui/core/Slide';
import TextField from '@material-ui/core/TextField';
import { MuiPickersUtilsProvider, KeyboardDatePicker, KeyboardTimePicker } from "@material-ui/pickers";
import DateFnsUtils from "@date-io/date-fns";
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import CheckIcon from '@material-ui/icons/Check';
import ClearIcon from '@material-ui/icons/Clear';
import DeleteIcon from '@material-ui/icons/Delete';
import ScheduleIcon from '@material-ui/icons/Schedule';
import { ToastsStore } from 'react-toasts';


const serverName = "http://tm-stint-api.herokuapp.com/";
const snackbarMessage = "";
const Transition = React.forwardRef(function Transition(props, ref) {
    return <Slide direction="up" ref={ref} {...props} />;
  });

export default class CustomCard extends React.Component
{

    constructor()
    {
        super();
        this.handleEditClick = this.handleEditClick.bind(this);
        this.handleClose = this.handleClose.bind(this);
        this.handleDateChange = this.handleDateChange.bind(this);
        this.handleSelectClose = this.handleSelectClose.bind(this);
        this.handleSelectOpen = this.handleSelectOpen.bind(this);
        this.handleSelectChange = this.handleSelectChange.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleSaveAction = this.handleSaveAction.bind(this);
        this.handleDeleteAction = this.handleDeleteAction.bind(this);
        this.handleTimeChange = this.handleTimeChange.bind(this);

        this.state ={
            open:false,
            selectedDate:'',
            selectOpen:false,
            title: '',
            description: '',
            dueDate: new Date(),
            dueDateTime: new Date(),
            notificationTime: '',
            position: -1
        };
    }
    handleSaveAction(e)
    {
        e.preventDefault();
        var month = this.state.dueDate.getMonth() + 1;
        var day = this.state.dueDate.getDate();
        var year = this.state.dueDate.getFullYear();
        var formattedDate = month + "/" + day + "/" + year;

        var hours = this.state.dueDateTime.getHours();
        var minutes = this.state.dueDateTime.getMinutes();
        var seconds = this.state.dueDateTime.getSeconds();
        var formattedTime = hours + ":" + minutes + ":" + seconds;
         fetch(serverName + 'editTask', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization' : sessionStorage.getItem("token")
            },
              body: JSON.stringify({
                title: this.state.title,
                laneId: this.props.laneId ,
                id: this.props.id,
                dueDateTime: formattedDate + " " + formattedTime,
                notificationTimeMinutes: this.state.notificationTime,
                description: this.state.description,
                position: this.props.position
              })
            }).then(response => {
                if (response.ok) {
                    response.json().then(json => {
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
    handleDeleteAction()
    {
        //
    }
    handleSelectClose(){
        this.setState({selectOpen:false})
    }
    handleSelectOpen(){
        this.setState({selectOpen:true})
    }
    handleSelectChange(data){
        this.setState({
            notificationTime: data.target.value
        });
    }
    handleDateChange(date)
    {
        this.setState({dueDate:date});
    }
    handleTimeChange(time)
    {
        this.setState({dueDateTime:time});
    }
    handleEditClick(e)
    {
       this.setState({open:true,
        title: this.props.title,
        description: this.props.description,
        dueDate: this.props.dueDateTime == null ? new Date() : new Date(this.props.dueDateTime),
        dueDateTime: this.props.dueDateTime == null ? new Date() : new Date(this.props.dueDateTime),
        notificationTime: this.props.notificationTimeMinutes == null ? "-1" : this.props.notificationTimeMinutes
        });
    }
    handleClose()
    {
        this.setState({open:false})
    }
    handleChange(e)
    {
        let target = e.target;
        let value = target.type === 'checkbox' ? target.checked : target.value;
        let name = target.name;
        this.setState({
            [name] : value
        });

    }
    render()
    {
        var formattedDate = "No due date and time";
        this.props.dueDateTime != undefined && this.props.dueDateTime != ""?
          formattedDate = this.props.dueDateTime : formattedDate = "No due date and time"

        return(
            <div>
            <div style={{paddingBottom:10}}>
                <Card>
                    <CardHeader title = {this.props.title}
                    action={
                        <IconButton onClick={this.handleEditClick} id={this.props.id}>
                            <EditIcon/>
                        </IconButton>
                    }
                    style={{paddingBottom:0}}><hr/>
                    </CardHeader>

                    <CardContent>
                            <Typography variant="body2" color="textSecondary" component="p">{this.props.description}</Typography>
                            <Button variant="contained" size="small" color="primary" onClick={this.handleEditClick} startIcon={<ScheduleIcon/>}>{formattedDate}</Button>
                    </CardContent>
                </Card>
            </div>
            <div>
            <Dialog
                open={this.state.open}
                TransitionComponent={Transition}
                keepMounted
                onClose={this.handleClose}
                aria-labelledby="alert-dialog-slide-title"
                aria-describedby="alert-dialog-slide-description"
                maxWidth='md'
                fullWidth = {true}
            >
            <DialogContent>
                <div>
                <TextField id="title" label="Title" margin="normal" style={{width:'100%'}} value={this.state.title} onChange = {this.handleChange} name="title"></TextField><br/>
                </div>
                <div>
                <TextField
                    id="description"
                    label="Description"
                    multiline
                    rows="8"
                    margin="normal"
                    variant="outlined"
                    style={{width:'100%'}}
                    value = {this.state.description}
                    onChange = {this.handleChange}
                    name = "description"
                />
                </div>
                <div>
                    <MuiPickersUtilsProvider utils={DateFnsUtils}>
                                <KeyboardDatePicker
                                    margin="normal"
                                    id="dueDate"
                                    label="Task Due Date"
                                    format="MM/dd/yyyy"
                                    value={this.state.dueDate}
                                    onChange={this.handleDateChange}
                                    KeyboardButtonProps={{
                                        'aria-label': 'change date',
                                    }}
                                    name="dueDate"
                                />
                                <KeyboardTimePicker
                                    margin="normal"
                                    id="dueDateTime"
                                    label="Task Due Time"
                                    value={this.state.dueDateTime}
                                    onChange={this.handleTimeChange}
                                    KeyboardButtonProps={{
                                        'aria-label': 'change time',
                                    }}
                                    name="dueDateTime"
                                />
                    </MuiPickersUtilsProvider>
                </div>
                <br/>

                <div>
                <DialogContentText>Select Notification Time: &nbsp;&nbsp;&nbsp;&nbsp;
                    <Select
                        labelId="demo-controlled-open-select-label"
                        id="notificationTime"
                        open={this.state.selectOpen}
                        onClose={this.handleSelectClose}
                        onOpen={this.handleSelectOpen}
                        value={this.state.notificationTime}
                        onChange={this.handleSelectChange}
                        style = {{width: '50%'}}
                        name = "notificationTime">
                        <MenuItem value={-1}>No Notifications</MenuItem>
                        <MenuItem value={0}>At Time of Due Date</MenuItem>
                        <MenuItem value={5}>5 Minutes Before</MenuItem>
                        <MenuItem value={10}>10 Minutes Before</MenuItem>
                        <MenuItem value={15}>15 Minutes Before</MenuItem>
                        <MenuItem value={60}>1 Hour Before</MenuItem>
                        <MenuItem value={120}>2 Hour Before</MenuItem>
                        <MenuItem value={1440}>1 Day Before</MenuItem>
                        <MenuItem value={2880}>2 Day Before</MenuItem>
                    </Select>
                </DialogContentText>

                </div>
            </DialogContent>
                <DialogActions>
                    <Button variant="contained" onClick={this.handleSaveAction} color="primary" startIcon={<CheckIcon/>}>
                        Save
                    </Button>
                    <Button variant="contained" onClick={this.handleClose} color="default" startIcon={<ClearIcon/>}>
                        Cancel
                    </Button>
                    <Button variant="contained" onClick={this.handleDeleteAction} color="secondary" startIcon={<DeleteIcon/>}>
                        Delete
                    </Button>
                </DialogActions>
        </Dialog>
        </div>
        </div>
        );
    }
}
