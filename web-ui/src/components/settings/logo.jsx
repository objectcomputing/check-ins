import React from 'react';
import { Avatar, Button, Typography } from '@mui/material';
import AddCircleIcon from '@mui/icons-material/AddCircle';
import PaletteIcon from '@mui/icons-material/Palette';

const SettingsLogo = ({ companyLogo, fileRef, handleFile }) => {
  const handleClick = event => {
    fileRef.current.click();
  };

  // Call a function (passed as a prop from the parent component) to handle the user-selected file
  const handleChange = event => {
    const fileUploaded = event.target.files[0];
    handleFile(fileUploaded);
  };

  return (
    <>
      <Typography variant="h5" gutterBottom>Company Logo</Typography>
      <div className="settings-grid-2col grid-items-start">
        <div>
          {companyLogo ? (
            <Avatar alt="Company Logo" src={companyLogo} sx={{ width: 100, height: 100 }} />
          ) : (
            <Avatar sx={{ width: 100, height: 100 }}>
              <PaletteIcon sx={{ fontSize: 60 }} />
            </Avatar>
            )}
        </div>
        <div>
          <p>Upload your logo</p>
          <Button onClick={handleClick}>
            <AddCircleIcon />&nbsp; Logo Upload
          </Button>
          <input
            type="file"
            ref={fileRef}
            onChange={handleChange}
            style={{ display: 'none' }} /* Make the file input element invisible */
            />
        </div>
      </div>
    </>
  );
}

export default SettingsLogo;