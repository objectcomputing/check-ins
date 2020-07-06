import React from "react";

import "./Profile.css";

const InputComponent = ({ disabled, label, rows = 1, value, setValue }) => {
  return (
    <div className="input-component">
      <label htmlFor={label}>{label}</label>
      {rows > 1 ? (
        <textarea
          disabled={disabled}
          id={label}
          onChange={(e) => setValue(e.target.value)}
          value={value}
        ></textarea>
      ) : (
        <input
          disabled={disabled}
          id={label}
          onChange={(e) => setValue(e.target.value)}
          value={value}
        ></input>
      )}
    </div>
  );
};

export default InputComponent;
