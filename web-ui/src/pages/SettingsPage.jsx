import React, { useRef } from "react";
import SettingsFile from "../components/settings/types/file";
import { SettingsString, SettingsNumber } from "../components/settings";
import './SettingsPage.css';

const displayName = "SettingsPage";

const SettingsPage = () => {
  const hiddenFileInput = React.useRef(null);
   const handleFile = file => {
    if (csrf) {
      setFiles([...files, file]);
      addFile(file);
    }
  };
  return (
    <div className="settings-page">
      <SettingsFile title="Branding" description="Upload your logo file" fileRef={hiddenFileInput} handleFile />
      <SettingsString title="Company Name" description="The name of your company" />
    </div>
  );
}

SettingsPage.displayName = displayName;

export default SettingsPage;