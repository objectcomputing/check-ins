import React from 'react';
import { FormLabel } from '@mui/material';
import OutlinedInput from '@mui/material/OutlinedInput';
import FormHelperText from '@mui/material/FormHelperText';

function InputTextfield({
  title,
  id,
  value,
  autoFocus,
  error,
  onChangeHandler,
  placeholder,
  type,
  autocomplete,
  helperMessage
}) {
  return (
    <>
      <FormLabel>{title} </FormLabel>
      <OutlinedInput
        id={id}
        name={id}
        value={value}
        autoFocus={autoFocus}
        error={error}
        onChange={onChangeHandler}
        placeholder={placeholder}
        type={type}
        autoComplete={autocomplete}
        multiline
        rows={4}
      />
      <FormHelperText>{helperMessage}</FormHelperText>
    </>
  );
}

export default InputTextfield;