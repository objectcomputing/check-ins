import React from 'react';
import InputLabel from '@mui/material/InputLabel';
import OutlinedInput from '@mui/material/OutlinedInput';
import FormHelperText from '@mui/material/FormHelperText';

function InputTextfield({
  title,
  id,
  value,
  autoFocus,
  error,
  onChangeHandler,
  label,
  placeholder,
  type,
  autocomplete,
  helperMessage
}) {
  return (
    <>
      <InputLabel htmlFor={`outlined-adornment-${id}`}>{title}</InputLabel>
      <OutlinedInput
        id={id}
        name={id}
        value={value}
        autoFocus={autoFocus}
        error={error}
        onChange={onChangeHandler}
        label={label}
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