import React, { useContext, useEffect, useState } from "react";
import {
  DatePicker,
} from '@material-ui/pickers';
import DateFnsUtils from '@date-io/date-fns';
import { makeStyles } from "@material-ui/core/styles";


const useStyles = makeStyles({
pickercontain: {
  marginLeft: '2em',
  marginTop:'2em',
},
 picker: {
  minWidth: '60%',
  maxWidth: "80%",
  display:'block',
 }
});

const SelectDate = () =>{
 const classes = useStyles();
const [dueDate, setDueDate] = React.useState(null);
const [sendDate, setSendDate] = React.useState(new Date());

const handleDueDateChange = (date) => {
  setDueDate(date);
};

const handleSendDateChange = (date) => {
  setSendDate(date);
};

return (
<React.Fragment className={classes.root}>
  <div className={classes.pickercontain}>
       <DatePicker
       className= {classes.picker}
               disableToolbar
               format="MM/dd/yyyy"
               margin="normal"
               id="set-send-date"
               label="Send Date:"
               value={sendDate}
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
                value={dueDate}
                onChange={handleDueDateChange}
                KeyboardButtonProps={{
                   'aria-label': 'change date',
                }}
             />
             </div>
</React.Fragment>

    );
};
export default SelectDate;
