import React, { useState, useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogContent from '@mui/material/DialogContent';
import LinearProgress from '@mui/material/LinearProgress';
import Typography from '@mui/material/Typography';

import initialErrorState from './subcomponents/Registration/json/initialErrorState.json';

import Activation from './subcomponents/Activation/Activation';
import Registration from './subcomponents/Registration/Registration';

function RegisterModal({ open, onClose, activating, setActivating }) {
  const dispatch = useDispatch();
  const loginData = useSelector((state) => state.login);

  const [message, setMessage] = useState('');
  const [registering, setRegistering] = useState(false);
  const [errorState, setErrorState] = useState(initialErrorState);

  useEffect(() => {
    const { status } = loginData;
    if (status !== 200) {
      setMessage(status);
    } else {
      setRegistering(false); // closes dialog
    }
  }, [loginData]);

  function closePopup() {
    setRegistering(false);
    setMessage('');

    // Remove the error state from all of the inputs.
    setErrorState(initialErrorState);
  }

  return (
    <>
      <Dialog open={open} onClose={onClose} scroll="body">
        <DialogContent>
          {activating ? (
            <Activation activating={activating} setActivating={setActivating} />
          ) : (
            <Registration
              onClose={onClose}
              errorState={errorState}
              setErrorState={setErrorState}
              setRegistering={setRegistering}
              registering={registering}
            />
          )}
        </DialogContent>
      </Dialog>

      <Dialog
        className="create-account-dialog"
        open={registering}
        scroll="body"
      >
        <DialogContent>
          <Typography children={'Creating New Account'} variant={'h4'} />
          {message ? (
            <Typography
              children={
                'An error occurred registering this account. Do you already have an account?'
              }
              paragraph
            />
          ) : (
            <LinearProgress />
          )}
          {message && (
            <Button variant="contained" color="primary" onClick={closePopup}>
              Close
            </Button>
          )}
        </DialogContent>
      </Dialog>
    </>
  );
}

export default RegisterModal;
