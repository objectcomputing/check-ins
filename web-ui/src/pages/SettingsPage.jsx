import React, { useRef } from "react";
import SettingsLogo from "../components/settings/logo";
import './SettingsPage.css';

const displayName = "SettingsPage";

const SettingsPage = () => {
  const hiddenFileInput = React.useRef(null);
  return (
    <div className="settings-page">
      <SettingsLogo fileRef={hiddenFileInput} />
    </div>
  );
}

SettingsPage.displayName = displayName;

export default SettingsPage;