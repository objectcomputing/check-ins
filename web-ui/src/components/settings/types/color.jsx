import React from 'react';
import { Typography } from '@mui/material';

/**
 * A component for rendering a color picker field in the settings.
 *
 * @component
 * @param {Object} props
 * @param {string} props.label - The label for the setting.
 * @param {string} [props.description] - The description for the setting (optional).
 * @param {string} props.value - The current value of the setting.
 * @param {function} props.handleChange - The function to handle changes to the setting value (optional).
 * @returns {JSX.Element} The rendered component.
 */
const SettingsColor = ({ label, description, value, handleChange }) => {
  const labelId = label.replace(/\s/g, '-').toLowerCase();

  return (
    <div className="settings-type">
      <label htmlFor={labelId}>
        <Typography variant="h5" gutterBottom>
          {label}
        </Typography>
      </label>
      {description ?? <p>{description}</p>}
      <input
        id={labelId}
        className="settings-control"
        type="color"
        value={value}
        onChange={handleChange}
      />
    </div>
  );
};

export default SettingsColor;
