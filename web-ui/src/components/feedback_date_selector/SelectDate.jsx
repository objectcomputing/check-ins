import React, { useCallback, useEffect, useRef } from 'react';
import { styled } from '@mui/material/styles';
import DateFnsUtils from '@date-io/date-fns';
import PropTypes from 'prop-types';
import { TextField } from '@mui/material';
import { MobileDatePicker } from '@mui/x-date-pickers';

const dateUtils = new DateFnsUtils();
const PREFIX = 'SelectDate';
const classes = {
  pickerContain: `${PREFIX}-pickerContain`,
  picker: `${PREFIX}-picker`
};

const Root = styled('div')({
  [`& .${classes.pickerContain}`]: {
    marginLeft: '2em',
    marginTop: '2em'
  },
  [`& .${classes.picker}`]: {
    marginBottom: '0.5em',
    marginTop: '1em',
    minWidth: '60%',
    maxWidth: '80%',
    display: 'block'
  }
});

const propTypes = {
  changeQuery: PropTypes.func.isRequired,
  sendDateQuery: PropTypes.string,
  dueDateQuery: PropTypes.string
};

const SelectDate = ({ changeQuery, sendDateQuery, dueDateQuery }) => {
  const hasPushedInitialValues = useRef(false);
  let todayDate = new Date();
  const sendDate = sendDateQuery
    ? dateUtils.parse(sendDateQuery.toString(), 'MM/dd/yyyy', new Date())
    : todayDate;
  const dueDate = dueDateQuery
    ? dateUtils.parse(dueDateQuery?.toString(), 'MM/dd/yyyy', new Date())
    : null;

  useEffect(() => {
    if (
      !hasPushedInitialValues.current &&
      sendDate !== null &&
      sendDate !== undefined &&
      dueDate !== undefined
    ) {
      changeQuery('send', dateUtils.format(sendDate, 'MM/dd/yyyy'));
      if (dueDate !== null) {
        changeQuery('due', dateUtils.format(dueDate, 'MM/dd/yyyy'));
      }
      hasPushedInitialValues.current = true;
    }
  });

  const handleDueDateChange = useCallback(
    date => {
      const dueDate = date ? dateUtils.format(date, 'MM/dd/yyyy') : null;
      changeQuery('due', dueDate ? dueDate : undefined);
    },
    [changeQuery]
  );

  const handleSendDateChange = useCallback(
    date => {
      const sendDate = dateUtils.format(date, 'MM/dd/yyyy');
      changeQuery('send', sendDate);
    },
    [changeQuery]
  );

  return (
    <Root>
      <div className={classes.pickerContain}>
        <MobileDatePicker
          renderInput={props => (
            <TextField className={classes.picker} {...props} />
          )}
          disableToolbar
          format="MM/dd/yyyy"
          margin="normal"
          id="set-send-date"
          label="Send Date:"
          value={sendDate}
          minDate={dateUtils.date()}
          onChange={handleSendDateChange}
        />
        <br />
        <MobileDatePicker
          renderInput={props => (
            <TextField
              className={classes.picker}
              placeholder="No due date"
              {...props}
            />
          )}
          disableToolbar
          format="MM/dd/yyyy"
          margin="normal"
          id="set-due-date"
          label="Due Date:"
          value={dueDate}
          minDate={sendDate}
          minDateMessage="Due date must not be prior to the send date"
          clearable={true}
          onChange={handleDueDateChange}
        />
      </div>
    </Root>
  );
};

SelectDate.propTypes = propTypes;

export default SelectDate;
