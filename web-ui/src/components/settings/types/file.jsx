import React from 'react';
import { Avatar, Button, Typography } from '@mui/material';
import { AddCircle, Palette } from '@mui/icons-material';

const SettingsFile = ({ branding, fileRef, handleFile, title, description }) => {
  const handleClick = event => {
    fileRef.current.click();
  };

  const handleChange = event => {
    const fileUploaded = event.target.files[0];
    handleFile(fileUploaded);
  };

  return (
    <>
      <Typography variant="h5" gutterBottom>{title}</Typography>
      <div className="settings-grid-2col grid-items-start">
        <div>
          {branding ? (
            <Avatar alt="Branding" src={branding} sx={{ width: 100, height: 100 }} />
          ) : (
            <Avatar sx={{ width: 100, height: 100 }}>
              <Palette sx={{ fontSize: 60 }} />
            </Avatar>
            )}
        </div>
        <div>
          <p>{description}</p>
          <Button onClick={handleClick}>
            <AddCircle />&nbsp; Upload
          </Button>
          <input
            type="file"
            ref={fileRef}
            onChange={handleChange}
            className="hidden"
          />
        </div>
      </div>
    </>
  );
}

export default SettingsFile;