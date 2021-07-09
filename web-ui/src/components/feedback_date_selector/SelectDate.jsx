import React, {useCallback, useEffect, useRef} from "react";
import {
  DatePicker,
} from '@material-ui/pickers';
import { makeStyles } from "@material-ui/core/styles";
import queryString from "query-string";
import {useHistory, useLocation} from "react-router-dom";
import DateFnsUtils from "@date-io/date-fns";

const dateUtils = new DateFnsUtils();

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

const SelectDate = () =>{
    const classes = useStyles();
    const location = useLocation();
    const history = useHistory();
    const hasPushedInitialValues = useRef(false);
    const query = queryString.parse(location?.search);
    let todayDate = new Date();
    const sendDate = query.send ? dateUtils.parse(query.send?.toString(), "yyyy-MM-dd"): todayDate ;
    const dueDate = query.due ? dateUtils.parse(query.due?.toString(), "yyyy-MM-dd"): null;

    useEffect(() => {
    if (!hasPushedInitialValues.current && sendDate !== null && sendDate!== undefined && dueDate!==undefined) {
      query.send = dateUtils.format(sendDate, "yyyy-MM-dd");
      if (dueDate !== null) {
              query.due = dateUtils.format(dueDate, "yyyy-MM-dd");
      }
        history.push({...location, search: queryString.stringify(query)});
        hasPushedInitialValues.current=true;
    }
    })


    const handleDueDateChange = useCallback((date) => {
        query.due = dateUtils.format(date, "yyyy-MM-dd");
        history.push({...location, search: queryString.stringify(query)});
    },[location, history, query]);

    const handleSendDateChange = useCallback((date) => {
         query.send = dateUtils.format(date, "yyyy-MM-dd");
        history.push({...location, search: queryString.stringify(query)});
    },[location, history, query]);



    return (
    <React.Fragment>
      <div className={classes.pickerContain}>
           <DatePicker
           className= {classes.picker}
                   disableToolbar
                   format="MM/dd/yyyy"
                   margin="normal"
                   id="set-send-date"
                   label="Send Date:"
                   value={sendDate}
                   minDate={dateUtils.date()}
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
                    minDate={sendDate}
                    minDateMessage={'Date should not be before send Date'}
                    onChange={handleDueDateChange}
                    KeyboardButtonProps={{
                       'aria-label': 'change date',
                    }}
                 />
                 </div>
    </React.Fragment>);
};
export default SelectDate;
