import React from 'react'
import mobiscroll from './calendar/@mobiscroll/react';
import './calendar/@mobiscroll/react/dist/css/mobiscroll.min.css';
import Navbar from './Navbar';
import { ToastsStore } from 'react-toasts';
import { thisExpression } from '@babel/types';

const serverName = "http://tm-stint-api.herokuapp.com/";
const s3Url = "https://task-management-app.s3.amazonaws.com/";
let snackbarMessage = "";
const colors = ['#FAD0C9FF', '#89ABE3FF', '#603F83FF', '#2BAE66FF'];


let _isMounted = false;

export default class CustomCalendar extends React.Component {
    constructor(props) {
        super(props);

        var now = new Date();

        this.state = {
            myEvents: [],
            token:sessionStorage.getItem("token")
        };
        this.getUrlParameter = this.getUrlParameter.bind(this);
        this.fetchCalendarData = this.fetchCalendarData.bind(this);

    }
    getUrlParameter(name) {
        name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
        var regex = new RegExp('[\\?&]' + name + '=([^&#]*)');
        var results = regex.exec(window.location.search);
        return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
    };
    componentWillUnmount() {
        this._isMounted = false;
      }

    componentDidMount(){
        let tokenFromURL = this.getUrlParameter('token');
        if(tokenFromURL!=null && tokenFromURL.length>0)
        {
            this.setState({token:tokenFromURL}, ()=>{
                this.fetchCalendarData();
            });
        }
        else
        {
            this.fetchCalendarData();
        }


    }
    fetchCalendarData()
    {
        this._isMounted = true;
        let jsondata = {};
        fetch(serverName + 'getCalendar/type/android', {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization' : this.state.token
            }
            }).then(response => {
                if (response.ok) {
                    response.json().then(json => {
                        if (this._isMounted) {
                            for(let i=0; i<json.length;i++)
                            {
                            var now = new Date();
                            jsondata = {
                                d:  new Date(json[i].taskDueDate),
                                text: json[i].taskTitle,
                                color: colors[Math.floor(Math.random() * colors.length)],

                            }
                            this.setState({myEvents :  this.state.myEvents.concat(jsondata)});
                            }
                        }

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
    render() {
        return (
            <div>
                <div>
                    {sessionStorage.getItem("token")!= undefined && sessionStorage.getItem("token").length>0?< Navbar/>:<div></div>}
                </div>
                <div>
                 <mobiscroll.Eventcalendar
                    theme="ios"
                    themeVariant="light"
                    display="inline"
                    calendarHeight={513}
                    view={{
                        calendar: {
                            labels: true,
                            popover: true
                        },
                        eventList: { type: 'month', scrollable: true }

                    }}
                    onEventSelect={this.onEventSelect}
                    data={this.state.myEvents}
                />
                </div>
            </div>
        );
    }
}
