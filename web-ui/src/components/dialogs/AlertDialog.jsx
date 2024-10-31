import PropTypes from 'prop-types';

import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle
} from '@mui/material';

const propTypes = {
  message: PropTypes.string.isRequired,
  open: PropTypes.bool.isRequired,
  setOpen: PropTypes.func.isRequired,
  title: PropTypes.string
};

const AlertDialog = ({ message, open, setOpen, title = 'Alert' }) => (
  <Dialog open={open}>
    <DialogTitle>{title}</DialogTitle>
    <DialogContent>
      <DialogContentText>{message}</DialogContentText>
    </DialogContent>
    <DialogActions>
      <Button onClick={() => setOpen(false)} autoFocus>
        Close
      </Button>
    </DialogActions>
  </Dialog>
);

AlertDialog.propTypes = propTypes;
AlertDialog.displayName = 'AlertDialog';

export default AlertDialog;
