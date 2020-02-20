import React from 'react'
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';
import CheckIcon from '@material-ui/icons/Check';
import ClearIcon from '@material-ui/icons/Clear';

export default class CustomCardForm extends React.Component
{
    constructor()
    {
        super();
        this.handleAddCard = this.handleAddCard.bind(this);
    }
    handleAddCard(){
        this.props.onAdd({
            title: document.getElementById("title").value, 
            description: document.getElementById("description").value,
            dueDateTime: null,
            notificationDateTime:null
        });
    }
   

    render() {
      const {onCancel} = this.props
        return (
          <Card>
            <CardContent>
            <div>
              <TextField id="title" label="Title" margin="normal"></TextField><br/>
            </div>
            <div>      
              <TextField
                  id="description"
                  label="Description"
                  multiline
                  rows="8"
                  margin="normal"
                  variant="outlined"
              />
            </div>
            <div>
              <table>
                <tr>
                  <td>
                  <Button variant="outlined" color="primary" startIcon={<CheckIcon />} onClick={this.handleAddCard}>Add</Button> 
                  </td>
                  <td>
                  <Button variant="outlined" color="secondary" startIcon={<ClearIcon />} onClick={onCancel}>Cancel</Button> 
                  </td>
                </tr>
              </table>
            </div>
            </CardContent>
          </Card>
        )
    }
  }