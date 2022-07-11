import React from "react";
import FormHelperText from "@mui/material/FormHelperText";
import TextField from "@mui/material/TextField";

import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { DesktopDatePicker } from "@mui/x-date-pickers/DesktopDatePicker";

function DateInputField({
  title,
  id,
  value,
  autoFocus,
  error,
  onChangeHandler,
  label,
  autocomplete,
  helperMessage,
}) {
  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <DesktopDatePicker
        // id={id}
        // name={id}
        // autoFocus={autoFocus}
        label={label}
        // error={error}
        inputFormat="MM/dd/yyyy"
        value={value}
        onChange={onChangeHandler}
        renderInput={(params) => <TextField {...params} />}
      />

        <FormHelperText>{helperMessage}</FormHelperText>
    </LocalizationProvider>
  );
}

export default DateInputField;
