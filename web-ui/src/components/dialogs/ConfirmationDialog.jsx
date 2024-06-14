import PropTypes from 'prop-types';
import React, { useCallback, useState } from 'react';

import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle
} from '@mui/material';

const propTypes = {
  onNo: PropTypes.func,
  onYes: PropTypes.func.isRequired,
  open: PropTypes.bool.isRequired,
  question: PropTypes.string.isRequired,
  setOpen: PropTypes.func.isRequired,
  title: PropTypes.string
};

const ConfirmationDialog = ({
  onNo,
  onYes,
  open,
  question,
  setOpen,
  title = 'Confirm'
}) => {
  const handleNo = useCallback(() => {
    setOpen(false);
    if (onNo) onNo();
  }, [onNo, setOpen]);

  const handleYes = useCallback(() => {
    setOpen(false);
    onYes();
  }, [onYes, setOpen]);

  return (
    <Dialog open={open} onClose={handleNo}>
      <DialogTitle>{title}</DialogTitle>
      <DialogContent>
        <DialogContentText>{question}</DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleNo}>No</Button>
        <Button onClick={handleYes} autoFocus>
          Yes
        </Button>
      </DialogActions>
    </Dialog>
  );
};

ConfirmationDialog.propTypes = propTypes;
ConfirmationDialog.displayName = 'ConfirmationDialog';

export default ConfirmationDialog;
