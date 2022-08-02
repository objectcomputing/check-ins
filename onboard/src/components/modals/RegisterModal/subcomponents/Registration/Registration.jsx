import React, { useState, useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import { ACTIONS } from "../../../../../redux/reducers/login";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import LoadingButton from "@mui/lab/LoadingButton";
import Typography from "@mui/material/Typography";

import InputField from "../../../../inputs/InputField";
import PasswordModule from "./modules/PasswordModule";
import RequestAccess from "../../sharedModules/RequestAccess";

import registerUser from "../../../../../api/registerUser";
import "./modules/request.scss";

import initialState from "./json/initialState.json";
import initialErrorState from "./json/initialErrorState.json";
import fullErrorState from "./json/totalErrorState.json";
import formMessages from "./json/formMessages.json";
import formErrorMessages from "./json/formErrorMessages";
import inputArray from "./json/inputArray.json";
import { isArrayPresent } from "../../../../../utils/helperFunctions";

function Registration({
  onClose,
  errorState,
  setErrorState,
  registering,
  setRegistering,
}) {
  const dispatch = useDispatch();
  const loginData = useSelector((state) => state.login);

  const [state, setState] = useState(initialState);
  const [helperMessageState, setHelperMessageState] = useState(formMessages);

  function handleStateChange(id, val) {
    setState({
      ...state,
      [id]: val,
    });
  }

  function handleErrorChange(id, val) {
    setErrorState({
      ...errorState,
      [id]: val,
    });
  }

  function handleHelperMessageChange(id, val) {
    setHelperMessageState({
      ...helperMessageState,
      [id]: val,
    });
  }

  function handleChange(event) {
    const e = event;
    const val = e.target.value;
    setState({
      ...state,
      [e.target.name]: val,
    });
    if (e.target.name === "email") {
      if (passesEmailCheck(val) && val.length >= 5) {
        handleErrorChange("email", false);
        handleHelperMessageChange("email", formMessages.email);
      } else {
        handleErrorChange("email", true);
        handleHelperMessageChange("email", "Email not valid.");
      }
    } else if (e.target.name === "password") {
      if (val.length >= 12) {
        handleErrorChange("password", false);
        handleHelperMessageChange("password", formMessages.password);
      }
    } else if (e.target.name === "confirmPassword") {
      if (val.length >= 12) {
        handleErrorChange("confirmPassword", false);
        handleHelperMessageChange(
          "confirmPassword",
          formMessages.confirmPassword
        );
      }
    }
  }

  const EMAIL_RE = /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/;
  const passesEmailCheck = (emailVal) => EMAIL_RE.test(emailVal);

  let isValid = false;
  const MIN_LENGTH = 12;
  const SYMBOL_RE = /[-!$%^&*@#()_+|~=`{}[\]:";'<>?,./]/;

  let hasLength = state.password.length >= MIN_LENGTH;
  let containsUppercase = /[A-Z]/.test(state.password);
  let containsNumber = /[1-9]/.test(state.password);
  let containsSymbol = SYMBOL_RE.test(state.password);
  let matches =
    state.password.length && state.confirmPassword === state.password;

  // In the event that the password is totally cleared out, also clear out the password confirm
  useEffect(() => {
    if (!state.password) {
      handleStateChange("confirmPassword", "");
    }
  }, [state.password]);

  isValid =
    hasLength &&
    matches &&
    containsUppercase &&
    containsNumber &&
    containsSymbol;

  let formIsNotValid =
    !isValid ||
    errorState.email ||
    errorState.password ||
    errorState.confirmPassword ||
    !state.email ||
    !state.password ||
    !state.confirmPassword;

  let invalidFirstPasswordCheck =
    !hasLength || !containsUppercase || !containsNumber || !containsSymbol;

  const resetInitialValues = () => {
    setErrorState(initialErrorState);
    setState(initialState);
  };

  function handleSignInClick(e) {
    e.preventDefault();
    dispatch({ type: ACTIONS.RESET_USER });
    setRegistering(true);

    dispatch(
      registerUser(state.email, state.password, state.firstName, state.lastName)
    );
  }

  // Close the dialog and reset the form
  const handleCancel = () => {
    onClose();
    resetInitialValues();
  };

  useEffect(() => {
    if (loginData?.status === "error") {
      setErrorState(fullErrorState);
      setHelperMessageState(formErrorMessages);
    }
  }, [loginData]);

  const calcArray = () => {
    let fullInputArray = [];
    inputArray.forEach((input) => {
      fullInputArray.push({
        ...input,
        value: state[input.id],
        error: errorState[input.id],
        helperMessage: helperMessageState[input.id],
      });
    });
    return fullInputArray;
  };

  let fullArray = calcArray();

  return (
    <>
      <Typography children={"Register Account"} variant={"h3"} />
      <Typography
        sx={{ marginTop: 3 }}
        children={
          "Please enter your information in the fields below to register your account."
        }
        variant={"h4"}
      />
      <form autoComplete="off" onSubmit={handleSignInClick}>
        {isArrayPresent(fullArray) &&
          fullArray.map((arr, index) => {
            return (
              <FormControl
                key={index}
                sx={
                  index === 0
                    ? { marginTop: 3, marginBottom: 1, minWidth: "100%" }
                    : { my: 1, minWidth: "100%" }
                }
              >
                <InputField
                  title={arr.title}
                  id={arr.id}
                  value={arr.value}
                  autoFocus={arr.autoFocus}
                  error={arr.error}
                  onChangeHandler={handleChange}
                  label={arr.label}
                  placeholder={arr.placeholder}
                  type={arr.type}
                  autocomplete={arr.autocomplete}
                  helperMessage={arr.helperMessage}
                />
                {arr?.id === "password" && (
                  <PasswordModule
                    invalidFirstPasswordCheck={invalidFirstPasswordCheck}
                    hasLength={hasLength}
                    containsUppercase={containsUppercase}
                    containsNumber={containsNumber}
                    containsSymbol={containsSymbol}
                    minLength={MIN_LENGTH}
                    password={state.password}
                  />
                )}
                {arr?.id === "confirmPassword" && !matches && (
                  <div className="password-reset-form">
                    <div className="criteria-line">
                      <span className="criteria">matches</span>
                    </div>
                  </div>
                )}
              </FormControl>
            );
          })}
        <LoadingButton
          disabled={formIsNotValid || registering}
          loading={registering}
          variant="contained"
          color="primary"
          sx={{ marginTop: 3, marginRight: 3, marginBottom: 3 }}
          type="submit"
        >
          Register
        </LoadingButton>
        <Button
          variant="outlined"
          color="primary"
          sx={{ marginTop: 3, marginBottom: 3 }}
          onClick={handleCancel}
        >
          Cancel
        </Button>
      </form>
      <RequestAccess />
    </>
  );
}

export default Registration;
