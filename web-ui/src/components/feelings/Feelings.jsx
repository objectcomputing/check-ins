import React from "react";
import PropTypes from "prop-types";

const propTypes = {
  message: PropTypes.string,
  onSelect: PropTypes.func,
};
const displayName = "Feelings";

const Feelings = ({ onSelect, message }) => {
  const inputs = [
    ["Terrible", "fa fa-frown-o fa-3x"],
    ["Bad"],
    ["Okay", "fa fa-meh-o fa-3x"],
    ["Good"],
    ["Great", "fa fa-smile-o fa-3x"],
  ];
  const onChange = (e) => {
    onSelect(e.target.value);
  };

  return (
    <div>
      <h4>{message}</h4>
      <div style={{ display: "flex" }}>
        {inputs.map(([text, icon], i) => (
          <div
            key={`feelings-${i}`}
            style={{
              margin: "10px",
              display: icon === undefined ? "flex" : "",
              alignItems: "flex-end",
            }}
          >
            <div>
              <i className={icon} aria-hidden="true"></i>
            </div>
            <input
              id={`feelings-input-${i}`}
              type="radio"
              name="feeling"
              onClick={onChange}
              value={text}
            />
            {text}
          </div>
        ))}
      </div>
    </div>
  );
};

Feelings.propTypes = propTypes;
Feelings.displayName = displayName;

export default Feelings;
