import React from 'react';
import { Typography } from '@mui/material';

const SettingsString = ({ title, description, value, handleChange }) => {
  return (
    <>
      <Typography variant="h5" gutterBottom>{title}</Typography>
      <p>{description}</p>
      <input
        type="text"
        value={value}
        onChange={handleChange}
      />
    </>
  );
}

export default SettingsString;