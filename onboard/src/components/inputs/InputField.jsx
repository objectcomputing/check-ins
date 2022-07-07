import React, { useState } from "react";
import ClickAwayListener from "@mui/material/ClickAwayListener";
import FormHelperText from "@mui/material/FormHelperText";
import IconButton from "@mui/material/IconButton";
import InputAdornment from "@mui/material/InputAdornment";
import InputLabel from "@mui/material/InputLabel";
import OutlinedInput from "@mui/material/OutlinedInput";
import Visibility from "@mui/icons-material/Visibility";
import VisibilityOff from "@mui/icons-material/VisibilityOff";

function InputField({
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
  const [showPassword, setShowPassword] = useState(false);

  const handleClick = () => {
    setShowPassword((prev) => !prev);
  };

  const handleClickAway = () => {
    setShowPassword(false);
  };

  return (
    <>
      <InputLabel htmlFor={`outlined-adornment-${id}`}>{title}</InputLabel>
      {type === "password" ? (
        <ClickAwayListener onClickAway={handleClickAway}>
          <OutlinedInput
            id={id}
            name={id}
            value={value}
            autoFocus={autoFocus}
            error={error}
            onChange={onChangeHandler}
            label={label}
            placeholder={placeholder}
            type={showPassword ? "text" : "password"}
            autoComplete={autocomplete}
            endAdornment={
              <InputAdornment position="end">
                <IconButton
                  aria-label="toggle password visibility"
                  onClick={handleClick}
                >
                  {showPassword ? <Visibility /> : <VisibilityOff />}
                </IconButton>
              </InputAdornment>
            }
          />
        </ClickAwayListener>
      ) : (
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
        />
      )}
      <FormHelperText>{helperMessage}</FormHelperText>
    </>
  );
}

export default InputField;
