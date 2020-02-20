import React, { Component } from 'react';
import Card from '@material-ui/core/Card';
import {  ToastsStore } from 'react-toasts';
import CardContent from '@material-ui/core/CardContent';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';
import InsideBoard from './InsideBoard';
import ReactDOM from 'react-dom';
// import { inherits } from 'util';

const serverName = "http://tm-stint-api.herokuapp.com/";
var snackbarMessage = '';
const cardsData = [];
var editFlag = false;
const s3Url = "https://task-management-app.s3.amazonaws.com/";
const colors = ['#FAD0C9FF', '#89ABE3FF', '#603F83FF', '#2BAE66FF'];

class Board extends Component {
  constructor() {
    super();
    this.state = {
      cards : [],
      cardsStyle:[],
      shadow: 0,
      color:""
    };
    this.handleChange = this.handleChange.bind(this);
    this.handleAddCard = this.handleAddCard.bind(this);
    this.handleChangeTextToInput = this.handleChangeTextToInput.bind(this);
    this.renderCards = this.renderCards.bind(this);
    this.handleNavigation = this.handleNavigation.bind(this);
    this.handleInputclick = this.handleInputclick.bind(this);
    this.handleHover = this.handleHover.bind(this);
    this.handleHoverOut = this.handleHoverOut.bind(this);
  }
  componentWillMount()
  {
    this.fetchBoards();
  }

  fetchBoards(){
    fetch(serverName + 'getBoards', {
      method: 'GET',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'Authorization' : sessionStorage.getItem("token")
      },
      async:false
    }).then(response => {
      if (response.ok) {
        response.json().then(json => {
          this.renderCards(json);
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
  renderCards(json)
  {
    cardsData.splice(0, cardsData.length);
    for (let i = 0; i < json.length; i++){
      let temp = {
        id: '',
        cardText: '',
        cardDescription: '',
        backgroundImage: '',
        color: ''
      }
      var randomColor = colors[Math.floor(Math.random() * colors.length)];
      let cardStyleFromDatabase = {
        width: 200,
        height: 140,
        backgroundColor: json[i].image ? "rgba(0,0,0,0.5)" : (json[i].color == null ? randomColor : json[i].color),
        display: "inline-block",
      };

      temp.id = json[i].board_id;
      temp.cardText = json[i].name;
      temp.cardDescription = json[i].description;
      temp.backgroundImage = json[i].image;
      temp.color = json[i].color == null ? randomColor : json[i].color;
      cardsData.push(temp);
      this.setState({
        cardsStyle: this.state.cardsStyle.concat(cardStyleFromDatabase)
      })
    }

    this.setState({cards: []});
    for (var i = 0; i < cardsData.length; i++) {
      if(json[i]){
        var inputId = 'ip'+cardsData[i].id;
        var cardId = 'card'+cardsData[i].id;
        var item =
        <div style = {{backgroundImage: json[i].image ? "url("+s3Url+json[i].board_id+") "  : "",
        backgroundPosition:  json[i].image ? "center center" : "",
        backgroundRepeat:  json[i].image ? "no-repeat" : "",
        backgroundSize : json[i].image ? "contain" : "",
        width:200, height:140, float:'left', display: "inline-block",
        marginTop: 20,
        marginRight: 10}}>
        <Card id={cardId} className="card" style={this.state.cardsStyle[i]} onClick={this.handleNavigation} onMouseOver={this.handleHover} onMouseOut={this.handleHoverOut}>
        <CardContent id={cardsData[i].id}><Typography id={inputId} style={{color:'white'}} onClick={this.handleChangeTextToInput}>{cardsData[i].cardText}</Typography>
        </CardContent>
        </Card>
        </div>

        this.setState({cards: this.state.cards.concat(item)});
      }
    }
  }

  handleNavigation(e)
  {
    var cardDescription = '';
    var cardImage = '';
    var cardColor = '';
    var cardName = '';
    var boardId = e.target.id;
    if(boardId.substring(0, 4) === "card"){
        boardId = boardId.substring(4);
    }
    for(var i = 0; i<cardsData.length;i++)
    {
      if(cardsData[i].id === boardId)
      {
        cardDescription = cardsData[i].description;
        cardImage = cardsData[i].backgroundImage;
        cardColor = cardsData[i].color;
        cardName = cardsData[i].cardText;
        break;
      }
    }
    localStorage.setItem("boardId",boardId);
    localStorage.setItem("boardName",cardName);
    localStorage.setItem("boardColor",cardColor);
    localStorage.setItem("boardImage",cardImage);
    localStorage.setItem("boardDescription",cardDescription);
    localStorage.setItem("Color", cardColor);

    ReactDOM.render(<InsideBoard />, document.getElementById('root'));
  }

  handleChange(e){
    if(e.key === 'Enter') {

      let value = document.getElementById(e.target.id).value;
      let parentId = e.target.parentNode.id;
      let mainParentId = document.getElementById(parentId).parentNode;

      if(editFlag)
      {
        var cardDescription = '';
        var cardImage = '';
        var cardColor = '';
        var cardId = e.target.id;
        cardId = cardId.substring(2);
        for(var i = 0; i<cardsData.length;i++)
        {
          if(cardsData[i].id === parentId)
          {
            cardDescription = "";
            if(cardsData[i].description){
              cardDescription = cardsData[i].description;
            }
            cardImage = "";
            if(cardsData[i].backgroundImage){
                cardImage = cardsData[i].backgroundImage;
            }
            cardColor = "#fefefe"
            if(cardsData[i].color){
                cardColor = cardsData[i].color;
            }
            break;
          }
        }
        fetch(serverName + 'editBoard', {
          method: 'POST',
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization' : sessionStorage.getItem("token")
          },
          body: JSON.stringify({
            board_id: cardId,
            name: value,
            description:cardDescription===""?null:cardDescription,
            image:cardImage===""?null:cardImage,
            color:cardColor===""?null:cardColor
          })
        }).then(response => {
          if (response.ok) {
            response.json().then(json => {
              document.getElementById(parentId).innerHTML = "<Typography id='"+e.target.id+"'>"+value+"</Typography>";
              editFlag = false;
              this.fetchBoards();
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

      }else{
        fetch(serverName + 'createBoard', {
          method: 'POST',
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization' : sessionStorage.getItem("token")
          },
          body: JSON.stringify({
            name: value,
            color: this.state.color
          })
        }).then(response => {
          if (response.ok) {
          this.fetchBoards();
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
    }

  }
  handleInputclick(e)
  {
    e.stopPropagation();
  }

  handleChangeTextToInput(e)
  {
    e.stopPropagation();
    if(editFlag)
    {
      alert("Can edit only one board at a time")
    }
    else{
      let parentid = e.target.parentNode.id;
      document.getElementById(parentid).innerHTML = "<input type='text' id='"+e.target.id+"' placeholder='Enter name'></input>";
      document.getElementById(e.target.id).onkeydown = this.handleChange;
      document.getElementById(e.target.id).onclick = this.handleInputclick;
      editFlag = true;
      document.getElementById("addBoardCard").disabled = true;
    }
  }
  handleHover(e)
  {
    e.stopPropagation();
    var cardId = e.target.id
    if(cardId.includes("card"))
    {
      document.getElementById(e.target.id).style.boxShadow = "0 0 10px black";
    }
    else if(cardId.includes("ip"))
    {
      document.getElementById(e.target.parentNode.parentNode.id).style.boxShadow = "0 0 10px black";
    }
    else{
      document.getElementById(e.target.parentNode.id).style.boxShadow = "0 0 10px black";
    }
  }
  handleHoverOut(e)
  {
    e.stopPropagation();
    var cardId = e.target.id
    if(cardId.includes("card"))
    {
      document.getElementById(e.target.id).style.boxShadow = "0px 0px 0px black";
    }
    else if(cardId.includes("ip"))
    {
      document.getElementById(e.target.parentNode.parentNode.id).style.boxShadow = "0px 0px 0px black";
    }
    else{
      document.getElementById(e.target.parentNode.id).style.boxShadow = "0px 0px 0px black";
    }
  }

  handleAddCard(e){
    e.preventDefault();
    var tempId = this.state.cards.length + 1;
    var inputId = 'ip'+tempId;
    var cardId = 'card'+tempId;
    this.setState({color: colors[Math.floor(Math.random() * colors.length)]},
    ()=> {
      var cardStyle =  {
        width: 200,
        height: 140,
        padding: 10,
        backgroundColor: this.state.color,
        display: "inline-block",
        marginTop: 20,
        marginRight: 10,
        backgroundImage: "https://www.google.com/url?sa=i&source=images&cd=&ved=2ahUKEwj01_rQzOXlAhWdCTQIHTBTCpkQjRx6BAgBEAQ&url=https%3A%2F%2Fwww.bbc.com%2Fnews%2Fscience-environment-49744435&psig=AOvVaw31X7Ep7t5XthQoEI4jvZ6y&ust=1573679907788618",
      };
      var item = <Card className="card" style={cardStyle} onClick={this.handleNavigation} onMouseOver={this.handleHover} onMouseOut={this.handleHoverOut} id={cardId}>
      <CardContent id={tempId}><input type="text" id={inputId} onKeyDown={this.handleChange} placeholder="Enter name" onClick={this.handleInputclick}></input>
      </CardContent>
      </Card>
      this.setState({
        cards: this.state.cards.concat(item)
      });
    });
  }

  render() {
    return  (
      <div>
      <div>
      {
        this.state.cards ? this.state.cards: <div><h1>Loading data</h1></div>
      }
      </div>
      <div style={{margin: "10"}}>
      <Button id="addBoardCard" variant="contained" color="primary" onClick={this.handleAddCard} style={{margin: "10", width: 200,
      height: 140,
      padding: 10,marginTop: 20,
      marginRight: 10}}>
      Add Board
      </Button>
      </div>
      </div>
    )
  }
}

export default Board
