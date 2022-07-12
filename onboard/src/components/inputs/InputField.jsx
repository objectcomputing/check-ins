import React, { useState } from "react";
import ClickAwayListener from "@mui/material/ClickAwayListener";
import FormHelperText from "@mui/material/FormHelperText";
import IconButton from "@mui/material/IconButton";
import InputAdornment from "@mui/material/InputAdornment";
import { FormLabel } from "@mui/material";
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
      <FormLabel>{title} </FormLabel>
      {type === "password" ? (
        <ClickAwayListener onClickAway={handleClickAway}>
          <OutlinedInput
            id={id}
            name={id}
            value={value}
            autoFocus={autoFocus}
            error={error}
            onChange={onChangeHandler}
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
