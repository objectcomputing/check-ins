import React, { useState, useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import InputLabel from "@mui/material/InputLabel";
import OutlinedInput from "@mui/material/OutlinedInput";
import FormHelperText from "@mui/material/FormHelperText";
import FormControl from "@mui/material/FormControl";
import LoadingButton from "@mui/lab/LoadingButton";
import NotStartedIcon from "@mui/icons-material/NotStarted";
import ReactCodeInput from "react-verification-code-input";
import postCode from "../../../../../api/postCode";

// This is the Activation dialog.
function Activation({ activating, setActivating }) {
  const dispatch = useDispatch();
  const loginData = useSelector((state) => state.login);

  const [val, setVal] = useState("");
  const [loading, setLoading] = useState(false);
  const [errorStatus, setErrorStatus] = useState([]);
  const [errorMsg, setErrorMsg] = useState("");
  const [stateBustingKey, setStateBustingKey] = useState(1);
  const [expiredCode, setExpiredCode] = useState(false);

  const [email, setEmail] = useState("");

  const [emailError, setEmailError] = useState(false);
  const [emailHelperMsg, setEmailHelperMsg] = useState("Enter your email.");
  const [checkEmail, setCheckEmail] = useState(false);

  useEffect(() => {
    console.log(val);
  }, [val]);

  // useEffect(() => {
  //   setTimeout(() => {
  //     if (errorStatus?.status === "error") {
  //       setErrorMsg("Code Failure Error. Try Again.");
  //       setLoading(false);
  //       setStateBustingKey(stateBustingKey + 1);
  //     } else if (errorStatus?.status === "expired") {
  //       setErrorMsg("Code expired. Request new one:");
  //       setLoading(false);
  //       setStateBustingKey(stateBustingKey + 1);
  //       setExpiredCode(true);
  //       setErrorStatus([]);
  //     } else {
  //       setErrorMsg("");
  //     }
  //   }, 500);
  // }, [errorStatus]);

  useEffect(() => {
    if (loginData?.status === "error") {
      setEmailError(true);
      setErrorMsg("Code Failure Error. Try Again.");
    }
  }, [loginData]);

  const handleChange = (newVal) => {
    console.log(newVal);
    setVal(newVal);
  };

  function handleActivation(e) {
    e.preventDefault();
    setLoading(true);

    // if (val === "123456") {
    //   setTimeout(() => {
    //     setLoading(false);
    //     setActivating(false);
    //   }, 300);
    // } else if (val === "111111") {
    //   setErrorStatus({ status: "expired" });
    //   setCheckEmail(false);
    // } else {
    //   setErrorStatus({ status: "error" });
    //   setCheckEmail(false);
    // }

    // TODO:
    // Create action that hooks up with api (api not present yet)
    // dispatch(activateCode(errorStatus, setErrorStatus, val));
    dispatch(
      postCode(email, val)
    );
  }

  const resetEmailHelperErrorsState = () => {
    setEmailError(false);
    setEmailHelperMsg("Enter your email.");
  };

  const EMAIL_RE = /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/;
  const passesEmailCheck = (emailVal) => EMAIL_RE.test(emailVal);

  // Event handler for the email field
  const handleChangeEmail = (event) => {
    let val = event.target.value;
    setEmail(val);
    if (val.length >= 4) {
      resetEmailHelperErrorsState();
    }

    if (passesEmailCheck(val) && val.length >= 5) {
      setEmailError(false);
      setEmailHelperMsg("Enter your email");
    } else {
      setEmailError(true);
      setEmailHelperMsg("Email not valid.");
    }
  };

  const handleSignInClick = (e) => {
    // Change the cursor to "wait" for
    // all children of the form and the body.
    const form = e.target;

    e.preventDefault();
    setLoading(true);
    setErrorStatus([]);

    setTimeout(() => {
      setExpiredCode(false);
      setLoading(false);
      setCheckEmail(true);
      setEmail("");
    }, 500);

    // dispatch(postUser(email));
  };

  return (
    <Box sx={{ width: "100%", maxWidth: "360px" }}>
      {expiredCode ? (
        <>
          <Typography children={"Code Has Expired"} variant={"h4"} />
          <Typography
            sx={{ marginTop: 3, marginBottom: 3 }}
            children={"Please enter your email to request a new code."}
            variant={"h5"}
          />
          <form autoComplete="off" onSubmit={handleSignInClick}>
            <FormControl
              sx={{ marginTop: 1, marginBottom: 1, minWidth: "100%" }}
            >
              <InputLabel htmlFor="outlined-adornment-amount">Email</InputLabel>
              <OutlinedInput
                id="email"
                value={email}
                autoFocus={true}
                error={emailError}
                onChange={handleChangeEmail}
                label="Email"
                placeholder="Enter Email"
                type="string"
                autoComplete="email"
              />
              <FormHelperText>{emailHelperMsg}</FormHelperText>
            </FormControl>
            <LoadingButton
              disabled={!email || emailError}
              loading={loading}
              variant="contained"
              color="primary"
              startIcon={<NotStartedIcon />}
              sx={{ marginTop: 2, marginRight: 3, marginBottom: 2 }}
              type="submit"
            >
              Request
            </LoadingButton>
          </form>
        </>
      ) : (
        <>
          <Typography children={"Welcome to OCI Onboarding"} variant={"h4"} />
          <Typography
            sx={{ marginTop: 3, marginBottom: 3 }}
            children={
              checkEmail
                ? "Please check your email for your new activation code. Enter below."
                : "Please enter your activation code. You should have an email with it from your administrator."
            }
            variant={"h5"}
          />
          <form autoComplete="off" onSubmit={handleActivation}>
            <FormControl
              sx={{ marginTop: 1, marginBottom: 1, minWidth: "100%" }}
            >
              <InputLabel htmlFor="outlined-adornment-amount">Email</InputLabel>
              <OutlinedInput
                id="email"
                value={email}
                autoFocus={true}
                error={emailError}
                onChange={handleChangeEmail}
                label="Email"
                placeholder="Enter Email"
                type="string"
                autoComplete="email"
              />
              <FormHelperText>{emailHelperMsg}</FormHelperText>
            </FormControl>
            <ReactCodeInput
              type="text"
              fields={5}
              values={[]}
              onChange={handleChange}
              loading={loading}
              key={stateBustingKey}
            />
            {errorMsg && (
              <Typography
                sx={{ marginTop: 3, color: "red" }}
                children={errorMsg}
                variant={"h6"}
              />
            )}
            <LoadingButton
              disabled={(val.length < 5 || !email || emailError)}
              loading={loading}
              variant="contained"
              color="primary"
              startIcon={<NotStartedIcon />}
              sx={{ marginTop: 3, marginRight: 3, marginBottom: 3 }}
              type="submit"
            >
              Activate
            </LoadingButton>
          </form>
        </>
      )}
    </Box>
  );
}
export default Activation;
