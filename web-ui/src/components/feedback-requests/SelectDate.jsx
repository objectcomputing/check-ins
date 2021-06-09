import React, { useContext, useEffect, useState } from "react";
import {
  DatePicker,
} from '@material-ui/pickers';
import DateFnsUtils from '@date-io/date-fns';
//import { MuiPickersUtilsProvider } from '@material-ui/pickers';
// import AdapterDateFns from '@material-ui/lab/AdapterDateFns';
// import LocalizationProvider from '@material-ui/lab/LocalizationProvider'

const SelectDate = () =>{
const [dueDate, setDueDate] = React.useState(null);
const [sendDate, setSendDate] = React.useState(new Date());
console.log(sendDate)

const handleDueDateChange = (date) => {
  setDueDate(date);
};

const handleSendDateChange = (date) => {
  setSendDate(date);
};

return (
<React.Fragment>
       <DatePicker
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
                disableToolbar
                format="MM/dd/yyyy"
                margin="normal"
                id="set-due-date"
                label="Due Date:"
                value={dueDate}
                onChange={handleDueDateChange}
                KeyboardButtonProps={{
                   'aria-label': 'change date',
                }}
             />
</React.Fragment>

    );
};
export default SelectDate;
