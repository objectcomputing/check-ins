import React, { useContext } from 'react';
import Snackbar from '@mui/material/Snackbar';
import MuiAlert from '@mui/material/Alert';
import { AppContext } from '../../context/AppContext';
import { UPDATE_TOAST } from '../../context/actions';

const SnackBarWithContext = () => {
  const { state, dispatch } = useContext(AppContext);
  window.snackDispatch = dispatch;
  const { severity, toast } = state.toast;

  const closeToast = () => {
    dispatch({
      type: UPDATE_TOAST,
      payload: {
        severity: '',
        toast: ''
      }
    });
  };

  return (
    <Snackbar
      autoHideDuration={25000}
      anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      open={toast !== '' && severity !== ''}
      onClose={closeToast}
      style={{ bottom: '10%' }}
      toast={toast}
    >
      {severity === '' ? null : (
        <MuiAlert
          onClose={closeToast}
          severity={severity}
          elevation={6}
          variant="filled"
        >
          {toast.message || toast}
        </MuiAlert>
      )}
    </Snackbar>
  );
};

export default SnackBarWithContext;
