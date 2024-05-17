import React from 'react';
import { Input, Typography } from '@mui/material';

/**
 * A component for rendering a number input field in the settings.
 *
 * @component
 * @param {Object} props
 * @param {string} props.label - The label for the input field.
 * @param {string} [props.description] - The description for the input field (optional).
 * @param {string} props.value - The current value of the input field.
 * @param {string} [props.placeholder] - The placeholder text for the input field (optional).
 * @param {function} props.handleChange - The function to handle input field changes.
 * @returns {JSX.Element} - The rendered component.
 */
const SettingsString = ({
  label,
  description,
  value,
  placeholder,
  handleChange
}) => {
  const labelId = label.replace(/\s/g, '-').toLowerCase();

  return (
    <div className="settings-type">
      <label htmlFor={labelId}>
        <Typography variant="h5" gutterBottom>
          {label}
        </Typography>
      </label>
      {description ?? <p>{description}</p>}
      <Input
        id={labelId}
        className="settings-control"
        type="text"
        value={value}
        placeholder={placeholder ?? `Enter ${label}`}
        onChange={handleChange}
      />
    </div>
  );
};

export default SettingsString;
