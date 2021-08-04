import React, {useCallback, useEffect, useRef} from "react";
import {DatePicker} from '@material-ui/pickers';
import { makeStyles } from "@material-ui/core/styles";
import DateFnsUtils from "@date-io/date-fns";
import PropTypes from "prop-types";

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

const propTypes = {
  changeQuery: PropTypes.func.isRequired,
  sendDateQuery: PropTypes.string,
  dueDateQuery: PropTypes.string
};

const SelectDate = ({changeQuery, sendDateQuery, dueDateQuery}) =>{
    const classes = useStyles();
    const hasPushedInitialValues = useRef(false);
    let todayDate = new Date();
    const sendDate = sendDateQuery ? dateUtils.parse(sendDateQuery.toString(), "yyyy-MM-dd") : todayDate;
    const dueDate = dueDateQuery ? dateUtils.parse(dueDateQuery?.toString(), "yyyy-MM-dd") : null;

    useEffect(() => {
      if (!hasPushedInitialValues.current && sendDate !== null && sendDate !== undefined && dueDate !== undefined) {
        changeQuery("send", dateUtils.format(sendDate, "yyyy-MM-dd"));
        if (dueDate !== null) {
          changeQuery("due", dateUtils.format(dueDate, "yyyy-MM-dd"));
        }
        hasPushedInitialValues.current = true;
      }
    });

    const handleDueDateChange = useCallback((date) => {
      const dueDate = date ? dateUtils.format(date, "yyyy-MM-dd") : null;
      changeQuery("due", dueDate ? dueDate : undefined);
    },[changeQuery]);

    const handleSendDateChange = useCallback((date) => {
      const sendDate = dateUtils.format(date, "yyyy-MM-dd");
      changeQuery("send", sendDate);
    },[changeQuery]);

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
          minDateMessage="Due date must not be prior to the send date"
          clearable={true}
          onChange={handleDueDateChange}
        />
      </div>
    </React.Fragment>);
};

SelectDate.propTypes = propTypes;

export default SelectDate;
