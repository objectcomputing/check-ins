import React, {useState, useEffect} from "react";

import {
  DatePicker,
} from '@material-ui/pickers';
import { makeStyles } from "@material-ui/core/styles";


const useStyles = makeStyles({
pickerContain: {
  marginLeft: '2em',
  marginTop:'2em',
},
 picker: {
  minWidth: '60%',
  maxWidth: "80%",
  display:'block',
 }
});

const SelectDate = (props) =>{
  const classes = useStyles();
  const [sendDateQuery, setSendDateQuery] = useState(props.sendDateProp)
  const [dueDateQuery, setDueDateQuery] = useState(props.dueDateProp)


  useEffect(() => {
    props.handleQueryChange("dueDate", dueDateQuery)
    props.handleQueryChange("sendDate", sendDateQuery)

  },[])

const handleDueDateChange = (date) => {
  setDueDateQuery(date.toString());
  props.handleQueryChange("dueDate", date.toString())
};

const handleSendDateChange = (date) => {
  setSendDateQuery(date.toString());
  props.handleQueryChange("sendDate", date.toString())


};

return (
<React.Fragment>
  <div className = {classes.root}>
  <div className={classes.pickerContain}>
       <DatePicker
       className= {classes.picker}
               disableToolbar
               format="MM/dd/yyyy"
               margin="normal"
               id="set-send-date"
               label="Send Date:"
                value={sendDateQuery}
               onChange={handleSendDateChange}
               KeyboardButtonProps={{
                 'aria-label': 'change date',
               }}
             />
            <DatePicker
            className= {classes.picker}
                disableToolbar
                format="MM/dd/yyyy"
                margin="normal"
                id="set-due-date"
                label="Due Date:"
                emptyLabel="No due date"
                value={dueDateQuery}
                onChange={handleDueDateChange}
                KeyboardButtonProps={{
                   'aria-label': 'change date',
                }}
             />
             </div>
             </div>
</React.Fragment>

    );
};
export default SelectDate;
