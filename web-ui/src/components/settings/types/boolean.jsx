import React from 'react';
import { Switch, Typography } from '@mui/material';
import { createLabelId } from '../../../helpers/strings.js';

/**
 * A component for rendering a boolean input field in the settings.
 *
 * @component
 * @param {Object} props
 * @param {string} props.name - The label for the settings component.
 * @param {string} [props.description] - The description for the settings component.
 * @param {boolean} props.value - The value of the settings component.
 * @param {Function} props.handleChange - The function to handle the change event of the settings component.
 * @returns {JSX.Element} The rendered boolean settings component.
 */
const SettingsBoolean = ({ name, description, value, handleChange }) => {
  const labelId = createLabelId(name);

  return (
    <div className="settings-type">
      <label htmlFor={labelId}>
        <Typography variant="h5" gutterBottom>
          {name}
        </Typography>
      </label>
      {description ?? <p>{description}</p>}
      <Switch
        id={labelId}
        className="settings-control"
        type="checkbox"
        checked={value}
        onChange={handleChange}
      />
    </div>
  );
};

export default SettingsBoolean;
