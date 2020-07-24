import React, { useRef, useState } from "react";
import Avatar from "@material-ui/core/Avatar";
import Button from "@material-ui/core/Button";

import "./EditPDL.css";

const EditPDL = ({ onDeselect, onEdit, onSelect, profile }) => {
  const { image_url, name, pdl, role } = profile;
  const [disabled, setDisabled] = useState(true);
  const inputRef = useRef();

  let image = image_url ? image_url : "https://i.imgur.com/TkSNOpF.jpg";
  return (
    <div style={{ border: "1px solid black", margin: "10px" }}>
      <div className="edit-pdl">
        <input
          onChange={() => {
            profile.selected ? onDeselect(profile) : onSelect(profile);
            setDisabled(!disabled);
          }}
          checked={profile.selected}
          type="checkbox"
        ></input>
        <Avatar alt="Profile" src={image} style={{ marginLeft: "20px" }} />
        <p>Name: {name}</p>
        <p>Role: {role}</p>
        <div>
          <label htmlFor={name}>PDL:</label>
          <input
            id={name}
            disabled={disabled}
            defaultValue={pdl}
            ref={inputRef}
          ></input>
        </div>
        <Button
          onClick={() => {
            disabled ? onSelect(profile) : onDeselect(profile);
            setDisabled(!disabled);
            if (!disabled) {
              onEdit(inputRef.current.value);
            }
          }}
          style={{ backgroundColor: "green", color: "white" }}
        >
          {disabled ? "Edit PDL" : "Update"}
        </Button>
      </div>
    </div>
  );
};

export default EditPDL;
