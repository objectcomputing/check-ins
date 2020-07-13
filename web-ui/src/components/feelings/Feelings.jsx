import React from "react";

const Feelings = (props) => {
  const { onSelect, message } = props;

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
            key={i}
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
              type="radio"
              name="feeling"
              onChange={onChange}
              value={text}
            />
            {text}
          </div>
        ))}
      </div>
    </div>
  );
};

export default Feelings;
