import React, { useState } from "react";
import "./SidebarItem.css";
import { Checkbox } from "@mui/material";
import { lightBlue } from "@mui/material/colors";

const SidebarItem = (props) => {
  // const [isCompleted, setIsCompleted] = useState(false);
  const label = { inputProps: { "aria-label": "Checkbox demo" } };
  return (
    <div className="sidebar__item">
      <div className="sidebar__check">
        {!isCompleted && (
          <Checkbox
            {...label}
            sx={{
              color: lightBlue[800],
              "&.Mui-checked": {
                color: lightBlue[600],
              },
            }}
            disabled
          />
        )}
        {isCompleted && (
          <Checkbox
            {...label}
            sx={{
              color: lightBlue[800],
              "&.Mui-checked": {
                color: lightBlue[600],
              },
            }}
            disabled
            Check
          />
        )}
      </div>

      <div className="sidebar__title">
        <p>{props.title}</p>
      </div>
    </div>
  );
};

export default SidebarItem;
