import React from 'react';
import { Input, Typography } from '@mui/material';
import { createLabelId } from '../../../helpers/strings.js';

/**
 * A component for rendering a number input field in the settings.
 *
 * @component
 * @param {Object} props
 * @param {string} props.name - The name for the number input field.
 * @param {string} [props.description] - The description for the input field (optional).
 * @param {number} props.value - The current value of the number input field.
 * @param {function} props.handleChange - The callback function to handle value changes.
 * @returns {JSX.Element} - The rendered component.
 */
const SettingsNumber = ({ name, description, value, handleChange }) => {
  const labelId = createLabelId(name);

  return (
    <div className="settings-type">
      <label htmlFor={labelId}>
        <Typography variant="h5" gutterBottom>
          {name}
        </Typography>
      </label>
      {description && <p>{description}</p>}
      <Input
        id={labelId}
        className="settings-control"
        type="number"
        value={value}
        onChange={handleChange}
      />
    </div>
  );
};

export default SettingsNumber;
