import React, { useState } from 'react';
import PropTypes from 'prop-types';
import {
  Button,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField
} from '@mui/material';
import Dialog from '@mui/material/Dialog';

const propTypes = {
  isOpen: PropTypes.bool.isRequired,
  onClose: PropTypes.func,
  onConfirm: PropTypes.func
};

const SkillCategoryNewDialog = ({ isOpen, onClose, onConfirm }) => {
  const [categoryName, setCategoryName] = useState('');
  const [categoryDescription, setCategoryDescription] = useState('');

  const reset = () => {
    setCategoryName('');
    setCategoryDescription('');
  };

  const isDisabled = () => {
    return categoryName.trim().length === 0;
  };

  return (
    <Dialog
      open={isOpen}
      onClose={() => {
        reset();
        onClose();
      }}
    >
      <DialogTitle>New Category</DialogTitle>
      <DialogContent>
        <div style={{ display: 'flex', flexDirection: 'column' }}>
          <TextField
            label="Name"
            fullWidth
            value={categoryName}
            onChange={event => setCategoryName(event.target.value)}
          />
          <TextField
            label="Description"
            fullWidth
            multiline
            maxRows={3}
            value={categoryDescription}
            onChange={event => setCategoryDescription(event.target.value)}
            style={{ marginTop: '1rem' }}
          />
        </div>
      </DialogContent>
      <DialogActions>
        <Button
          style={{ color: 'gray' }}
          onClick={() => {
            reset();
            onClose();
          }}
        >
          Cancel
        </Button>
        <Button
          color="primary"
          disabled={isDisabled()}
          onClick={() => {
            reset();
            onConfirm(categoryName, categoryDescription);
          }}
        >
          Create
        </Button>
      </DialogActions>
    </Dialog>
  );
};

SkillCategoryNewDialog.propTypes = propTypes;

export default SkillCategoryNewDialog;
