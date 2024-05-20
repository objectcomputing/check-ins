import React from 'react';
import { Button, Typography } from '@mui/material';
import { AddCircle } from '@mui/icons-material';
import { createLabelId } from '../../../helpers/strings.js';

/**
 * A component for handling file settings.
 *
 * @component
 * @param {Object} props - The component props.
 * @param {string} props.label - The label of the file settings.
 * @param {string} [props.description] - The description of the file settings.
 * @param {Object} props.fileRef - The reference to the file input element.
 * @param {Function} props.handleFile - The function to handle file upload.
 * @returns {JSX.Element} The rendered component.
 */
const SettingsFile = ({ fileRef, handleFile, label, description }) => {
  const labelId = createLabelId(label);

  const handleClick = event => {
    fileRef.current.click();
  };

  const handleChange = event => {
    const fileUploaded = event.target.files[0];
    handleFile(fileUploaded);
  };

  return (
    <div className="settings-type">
      <label htmlFor={labelId}>
        <Typography variant="h5" gutterBottom>
          {label}
        </Typography>
      </label>
      {description ?? <p>{description}</p>}
      <div className="settings-control">
        <Button onClick={handleClick}>
          <AddCircle />
          &nbsp; Upload
        </Button>
        <input
          id={labelId}
          type="file"
          ref={fileRef}
          onChange={handleChange}
          className="hidden"
        />
      </div>
    </div>
  );
};

export default SettingsFile;
