import React, {useContext, useState} from 'react';
import { Input, Typography } from '@mui/material';
import { createLabelId } from '../../../helpers/strings.js';
import { AppContext } from '../../../context/AppContext';
import { updateSetting } from '../../../api/settings';
import { debounce } from 'lodash/function';
import {selectCsrfToken} from "../../../context/selectors.js";

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
  value,
  placeholder,
}) => {

  const {state} = useContext(AppContext);
  const [settingsValue, setSettingsValue] = useState(value)
  const csrf = selectCsrfToken(state);

  const labelId = createLabelId(name);

  const realStoreSetting = (name, value, csrf) => updateSetting(name, value, csrf);

  const storeSetting = debounce(realStoreSetting, 1500);


  const handleStringChange = (event) => {
    if (!csrf) {
        return;
    }

    const {value} = event.target;
    setSettingsValue(value);
    let name = event.target.id?.toUpperCase();

    storeSetting(name, value, csrf);

  };


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
        type="text"
        value={settingsValue ?? value}
        placeholder={placeholder ?? `Enter ${name}`}
        onChange={() => handleStringChange(event)}
      />
    </div>
  );
};

export default SettingsString;
