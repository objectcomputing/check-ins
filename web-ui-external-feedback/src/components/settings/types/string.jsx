import React from 'react';
import {
  Select,
  MenuItem,
  ListItemText,
  Input,
  Typography
} from '@mui/material';
import { createLabelId } from '../../../helpers/strings.js';

/**
 * A component for rendering a number input field in the settings.
 *
 * @component
 * @param {Object} props
 * @param {string} props.name - The name for the input field.
 * @param {string} [props.description] - The description for the input field (optional).
 * @param {string} props.value - The current value of the input field.
 * @param {string} [props.placeholder] - The placeholder text for the input field (optional).
 * @param {function} props.handleChange - The function to handle input field changes.
 * @returns {JSX.Element} - The rendered component.
 */
const SettingsString = ({
  name,
  description,
  values,
  value,
  placeholder,
  handleChange
}) => {
  const labelId = createLabelId(name);

  return (
    <div className="settings-type">
      <label htmlFor={labelId}>
        <Typography variant="h5" gutterBottom>
          {name}
        </Typography>
      </label>
      {description && <p>{description}</p>}
      {values && values.length > 0 ?
      <Select
        labelId={labelId}
        value={value}
        onChange={handleChange}
      >
        {values.map((option) => (
          <MenuItem key={option} value={option}>
            <ListItemText primary={option} />
          </MenuItem>
        ))}
      </Select> :
      <Input
        id={labelId}
        className="settings-control"
        type="text"
        value={value}
        placeholder={placeholder ?? `Enter ${name}`}
        onChange={handleChange}
      />}
    </div>
  );
};

export default SettingsString;
