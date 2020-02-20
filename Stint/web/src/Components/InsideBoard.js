import React, { Component } from 'react'
import Board from 'react-trello'
import Button from '@material-ui/core/Button';
import { ToastsStore } from 'react-toasts';
import isEmpty from "lodash/isEmpty";
import NewCard from './CustomCardForm';
import CustomCard from './CustomCard';
import Navbar from './Navbar'
import DeleteIcon from '@material-ui/icons/Delete'
import { ImagePicker } from 'react-file-picker'
import PublishIcon from '@material-ui/icons/Publish'
import { Typography } from '@material-ui/core';
import Box from '@material-ui/core/Box';

var snackbarMessage = '';
var addNewLanePosition = 0;
var editLanePosition = [];
const serverName = "http://tm-stint-api.herokuapp.com/";
const s3Url = "https://task-management-app.s3.amazonaws.com/";

let oldCount = 0;
const components = {
    NewCardForm: NewCard,
    Card: CustomCard
  };
class InsideBoard extends Component {
     state = {
        data: {},
        backgroundImage: ''
      };
    constructor()
    {
        super();
        this.handleNewData = this.handleNewData.bind(this);
        this.handleAddNewCard = this.handleAddNewCard.bind(this);
        this.handleDeleteCard = this.handleDeleteCard.bind(this);
        this.handleCardDrag = this.handleCardDrag.bind(this);
        this.handleAddNewLane = this.handleAddNewLane.bind(this);
        this.handleUpdateLane = this.handleUpdateLane.bind(this);
        this.handleImagePicker = this.handleImagePicker.bind(this);
        this.handleImagePickerError = this.handleImagePickerError.bind(this);
    }
    componentDidMount()
    {
        fetch(serverName + 'getSwimLanes/boardId/' + localStorage.getItem("boardId"), {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization' : sessionStorage.getItem("token")
            },
            }).then(response => {
                if (response.ok) {
                    response.json().then(json => {
                        var dataFromDb = {
                            lanes: []
                        }
                        for(var i = 0; i<json.length;i++)
                        {
                            var temp = {
                                id: json[i].laneId,
                                title: json[i].title,
                                cards: json[i].cards
                            }
                            dataFromDb.lanes.push(temp);
                        }
                        this.setState({data:dataFromDb});
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
    handleNewData(nextData)
    {
        addNewLanePosition = nextData.lanes.length
        editLanePosition = []
        for(var i = 0; i<nextData.lanes.length; i++)
        {
            var temp = {
                laneId: nextData.lanes[i].id,
                position: i,
                cards: nextData.lanes[i].cards
            }
            editLanePosition.push(temp);
        }
    }
    handleAddNewCard(card, laneId){
        var position=0;
        for(var i=0; i< editLanePosition.length; i++)
        {
            if(editLanePosition[i].laneId === laneId)
            {
                for(var j=0; j<editLanePosition[i].cards.length; j++)
                {
                    if(card.id === editLanePosition[i].cards[j].id)
                    {
                        position = j;
                        break;
                    }
                }
            }
        }
        fetch(serverName + 'createTask', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization' : sessionStorage.getItem("token")
            },
              body: JSON.stringify({
                title: card.title,
                description: card.description,
                laneId: laneId,
                position: position,
                notificationTimeMinutes : -1
              })
            }).then(response => {
                if (response.ok) {
                    response.json().then(json => {
                       this.componentDidMount();
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
    handleDeleteCard(data){

    }
    handleCardDrag(fromLaneId, toLaneId, cardId, index){
        var description = '';
        var title = '';
        var dueDateTime = '';
        var notificationTimeMinutes = '';
        var notificationDateTime = '';
        for(var i=0; i< editLanePosition.length; i++)
        {
            if(editLanePosition[i].laneId === toLaneId)
            {
                for(var j=0; j<editLanePosition[i].cards.length; j++)
                {
                    if(cardId === editLanePosition[i].cards[j].id)
                    {
                        title = editLanePosition[i].cards[j].title;
                        description = editLanePosition[i].cards[j].description;
                        dueDateTime = editLanePosition[i].cards[j].dueDateTime;
                        notificationTimeMinutes = editLanePosition[i].cards[j].notificationTimeMinutes;
                        break;
                    }
                }
            }
        }

        fetch(serverName + 'editTask', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization' : sessionStorage.getItem("token")
            },
              body: JSON.stringify({
                title: title,
                laneId: toLaneId,
                id: cardId,
                dueDateTime: dueDateTime,
                notificationTimeMinutes: notificationTimeMinutes,
                description: description,
                position: index
              })
            }).then(response => {
                if (response.ok) {
                    response.json().then(json => {
                      this.componentDidMount();
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
    handleAddNewLane(data){
        fetch(serverName + 'createSwimLane', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization' : sessionStorage.getItem("token")
            },
              body: JSON.stringify({
                boardId: localStorage.getItem("boardId"),
                position: addNewLanePosition,
                title: data.title
              })
            }).then(response => {
                if (response.ok) {
                    response.json().then(json => {
                      this.componentDidMount();
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
    handleUpdateLane(laneId, data){
        var position=0;
        for(var i=0; i< editLanePosition.length; i++)
        {
            if(editLanePosition[i].laneId === laneId)
            {
                position = editLanePosition[i].position;
                break;
            }
        }
        fetch(serverName + 'editSwimLane', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization' : sessionStorage.getItem("token")
            },
              body: JSON.stringify({
                boardId: localStorage.getItem("boardId"),
                title: data.title,
                position: position,
                id: laneId
              })
            }).then(response => {
                if (response.ok) {
                    response.json().then(json => {
                        snackbarMessage = "Edit success";
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
    }
    handleImagePicker(base64)
    {
        this.setState({backgroundImage: base64})
        fetch(serverName + 'editBoard', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization' : sessionStorage.getItem("token")
            },
              body: JSON.stringify({
                board_id: localStorage.getItem("boardId"),
                name: localStorage.getItem("boardName"),
                description: localStorage.getItem("boardDescription") === "" ? null : localStorage.getItem("boardDescription"),
                image:base64,
                color:localStorage.getItem("Color")
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

    }
    handleImagePickerError(error)
    {
        snackbarMessage = error;
        ToastsStore.error(snackbarMessage);
    }

    render() {
        const { data } = this.state;
        return (
            <div>
                <Navbar/>
                {!isEmpty(data)?
                <div style ={{
                backgroundColor: this.state.backgroundImage == '' && localStorage.getItem("boardImage")!= "true" ? localStorage.getItem("Color") : "",
                backgroundImage:this.state.backgroundImage == ''?
                localStorage.getItem("boardImage") == "true" ? "url("+s3Url+localStorage.getItem("boardId")+")" : ""
                :"url("+this.state.backgroundImage+")"}}>
                    <div style= {{
                        backgroundColor:'rgba(0, 0, 0, 0.4)',

                    }}>
                    <div style = {{display:"flex",paddingRight:20, paddingTop:5, paddingBottom:5}}>
                        <Typography>
                            <Box style={{color:"white", marginLeft:20}} fontWeight="fontWeightBold" m={1}>
                            {localStorage.getItem("boardName")}
                            </Box>
                        </Typography>
                        <div style = {{marginLeft: "auto", float: "right"}}>
                        <table>
                            <tr>
                                <td>
                                    <ImagePicker
                                    onChange={this.handleImagePicker}
                                    onError={this.handleImagePickerError}
                                    dims={{minWidth: 100, maxWidth: 4000, minHeight: 100, maxHeight: 4000}}>

                                        <Button variant="contained" startIcon={<PublishIcon/>}>Background Image</Button>
                                    </ImagePicker>
                                </td>
                                <td>
                                    <Button color="secondary" variant="contained" startIcon={<DeleteIcon/>}>Delete Board</Button>
                                </td>
                            </tr>
                        </table>
                        </div>
                    </div>
                    <Board canAddLanes editable editLaneTitle
                        data = {data}
                        style = {{
                            backgroundColor:'rgba(0, 0, 0, 0)'
                        }}
                        customCardLayout
                        components={components}
                        onDataChange ={this.handleNewData}
                        onCardAdd = {this.handleAddNewCard}
                        onCardDelete = {this.handleDeleteCard}
                        onCardMoveAcrossLanes = {this.handleCardDrag}
                        onLaneAdd = {this.handleAddNewLane}
                        onLaneDelete = {this.handleDeleteLane}
                        onLaneUpdate = {this.handleUpdateLane}

                        />
                    </div>

                </div>
                : <div><Typography>
                    <Box style={{color:"black", position: 'absolute', left: '50%', top: '50%', transform: 'translate(-50%, -50%)'}} fontWeight="fontWeightBold" m={1}>
                      Loading...
                    </Box>
                </Typography></div>}
            </div>
        )
    }
}
export default InsideBoard
