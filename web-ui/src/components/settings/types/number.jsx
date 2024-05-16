import React from 'react';
import { Typography } from '@mui/material';

const SettingsNumber = ({ label, description, value, handleChange }) => {
  return (
    <div className='settings-type'>
      <label htmlFor={label}>
        <Typography variant="h5" gutterBottom>{label}</Typography>
        {description ?? (<p>{description}</p>)}
      </label>
      <input
        type="text"
        value={value}
        onChange={handleChange}
      />
    </div>
  );
}

export default SettingsNumber;