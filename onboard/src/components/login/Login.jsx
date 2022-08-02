import React, { useState, useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { ACTIONS } from './../../redux/reducers/login';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Container from '@mui/material/Container';
import CssBaseline from '@mui/material/CssBaseline';
import Divider from '@mui/material/Divider';
import Typography from '@mui/material/Typography';
import Grid from '@mui/material/Grid';
import InputLabel from '@mui/material/InputLabel';
import OutlinedInput from '@mui/material/OutlinedInput';
import FormHelperText from '@mui/material/FormHelperText';
import FormControl from '@mui/material/FormControl';

import RegisterModal from './../modals/RegisterModal/RegisterModal.jsx';

import postUser from './../../api/postUser';
import styled from 'styled-components';

import LogoDisplay from './../../assets/LogoDisplay.jsx';
import backgroundImageLogin from './../../assets/img/background-img.jpg';

const LoggedOutRoot = styled.div`
  display: block;
  width: 100%;
  height: 100%;
  background-color: rgb(248, 248, 248);
  background-image: url(${backgroundImageLogin});
  background-size: cover;
  overflow: scroll;
`;

const ApplicationWrap = styled.div`
  margin: 0 auto;
  width: 100%;
  min-height: 100vh;
  overflow: scroll;
`;

const LoginBox = styled.div`
  display: flex;
  align-items: center;
  background-color: white;
  padding: 1.5em;
  border-radius: 20px;
  border: 1px solid #83afd7;
  box-shadow: 1px 2px 6px 1px rgb(22 52 80 / 78%);
  margin-top: 10vh;
  margin-bottom: 10vh;
`;

const SubmitButton = styled(Button)`
  background-color: #36b769;
  width: 100%;
  height: 45px;
  font-size: 20px;
  margin-top: 1rem;
  margin-bottom: 1rem;

  :hover {
    background-color: #000;
  }

  :disabled {
    background-color: rgba(54 183 105, 0.7);
    color: #fff;
  }
`;

const CreateButton = styled(Button)`
  background-color: transparent;
  color: #36b769;
  width: 100%;
  height: 35px;

  :hover {
    background-color: #36b769;
    color: #fff;
  }
`;

// This is our login page.
function Login(props) {
  const dispatch = useDispatch();

  const loginData = useSelector((state) => state.login);

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const [emailError, setEmailError] = useState(false);
  const [emailHelperMsg, setEmailHelperMsg] = useState('Enter your email.');

  const [passwordError, setPasswordError] = useState(false);
  const [passwordHelperMsg, setPasswordHelperMsg] = useState(
    'Enter your password.'
  );

  const [open, setOpen] = useState(false);
  const [activating, setActivating] = useState(true);

  const handleRegisterClick = () => {
    setOpen(true);
    setActivating(true);
  };

  const onClose = () => {
    setOpen(false);
  };

  useEffect(() => {
    if (loginData?.status === 'error') {
      setEmail('');
      setEmailError(true);
      setEmailHelperMsg('Entry did not succeed. Try again.');
      setPassword('');
      setPasswordError(true);
      setPasswordHelperMsg('Entry did not succeed. Try again.');
    }
    // if (loginData?.accessToken) {
    // }
  }, [loginData]);

  const resetEmailHelperErrorsState = () => {
    setEmailError(false);
    setEmailHelperMsg('Enter your email.');
  };

  const resetPasswordHelperErrorsState = () => {
    setPasswordError(false);
    setPasswordHelperMsg('Enter your password.');
  };

  // Event handler for the email field
  const handleChangeEmail = (event) => {
    let val = event.target.value;
    setEmail(val);
    if (val.length >= 4) {
      resetEmailHelperErrorsState();
    }
  };

  // Event handler for the password field
  const handleChangePassword = (event) => {
    let val = event.target.value;
    setPassword(val);
    if (val.length >= 4) {
      resetPasswordHelperErrorsState();
    }
  };

  const handleSignInClick = (e) => {
    // Change the cursor to "wait" for
    // all children of the form and the body.
    const form = e.target;
    for (const child of form.children) {
      child.style.cursor = 'wait';
    }
    document.body.style.cursor = 'wait';

    e.preventDefault();

    dispatch({ type: ACTIONS.RESET_USER });

    dispatch(postUser(email, password));
  };

  return (
    <LoggedOutRoot>
      <ApplicationWrap>
        <CssBaseline />
        <Container>
          <Box sx={{ marginTop: 2 }}>
            <Grid container sx={{ display: 'flex', justifyContent: 'center' }}>
              <Grid
                container
                item
                md={5}
                xs={12}
                sx={{ justifyContent: 'center' }}
              >
                <LoginBox>
                  <Box sx={{ width: '100%', mr: 1 }}>
                    <div
                      style={{
                        position: 'relative',
                        margin: '0 auto',
                        width: '120px'
                      }}
                    >
                      <LogoDisplay width="120px" height="120px" />
                    </div>
                    <Typography
                      children={'Onboarding'}
                      variant={'h4'}
                      sx={{ fontWeight: 400, textAlign: 'center' }}
                    />
                    <Typography
                      sx={{
                        marginTop: 1,
                        marginBottom: 2,
                        textAlign: 'center'
                      }}
                      children={'OCI Onboarding Portal'}
                      variant={'h6'}
                    />
                    <form autoComplete="off" onSubmit={handleSignInClick}>
                      <FormControl
                        sx={{ marginTop: 3, marginBottom: 1, minWidth: '100%' }}
                      >
                        <InputLabel htmlFor="outlined-adornment-amount">
                          Email
                        </InputLabel>
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
                      <FormControl sx={{ my: 1, minWidth: '100%' }}>
                        <InputLabel htmlFor="outlined-adornment-amount">
                          Password (Required)*
                        </InputLabel>
                        <OutlinedInput
                          id="password"
                          value={password}
                          error={passwordError}
                          onChange={handleChangePassword}
                          label="Password (Required)*"
                          placeholder="Enter Password"
                          type="password"
                          autoComplete="password"
                        />
                        <FormHelperText>{passwordHelperMsg}</FormHelperText>
                      </FormControl>
                      <SubmitButton
                        disabled={emailError || !email}
                        variant="contained"
                        type="submit"
                      >
                        Login
                      </SubmitButton>
                    </form>
                    <Divider
                      variant="fullWidth"
                      dark="true"
                      className="width100Percent"
                    />
                    <Typography
                      sx={{
                        marginTop: 1,
                        marginBottom: 2,
                        textAlign: 'center'
                      }}
                      children={"Don't have an account?"}
                      variant={'h6'}
                    />
                    <CreateButton onClick={handleRegisterClick} variant="text">
                      Activate Account
                    </CreateButton>
                  </Box>
                </LoginBox>
                <RegisterModal
                  open={open}
                  onClose={onClose}
                  activating={activating}
                  setActivating={setActivating}
                />
              </Grid>
            </Grid>
          </Box>
        </Container>
      </ApplicationWrap>
    </LoggedOutRoot>
  );
}
export default Login;
