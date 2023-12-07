import React, { useEffect, useContext, useState } from "react";

import { AppContext } from "../context/AppContext";
import { selectPermissions } from "../context/selectors";

import { TextField } from "@mui/material";

import "./EditPermissionsPage.css";

const EditPermissionsPage = (props) => {
  const { state, dispatch } = useContext(AppContext);
  const { csrf } = state;
  const [permissionsList, setPermissionsList] = useState([]);

  const [searchText, setSearchText] = useState("");
  const [showAllSkills, setShowAllSkills] = useState(false);

  const allPermissions = selectPermissions(state);

  const handleClick = () => setShowAllSkills(!showAllSkills);

  useEffect(() => {

    console.log("Permissions");
    console.log(allPermissions, permissionsList);

  }, [allPermissions, permissionsList])

  return (
    <div className="edit-permissions-page">
      <div className="search">
        <div>
          <TextField
            label="Search Permissions"
            placeholder="Permission"
            fullWidth={true}
            value={searchText}
            onChange={(e) => {
              setSearchText(e.target.value);
            }}
          />
          <div className="show-all-permissions">
            <label htmlFor="all-permissions">Show all permissions</label>
            <input
              onClick={handleClick}
              id="all-permissions"
              type="checkbox"
              value={showAllSkills}
            />
          </div>
        </div>
      </div>

      <div className="edit-permissions-list">

      </div>
    </div>
  );
};

export default EditPermissionsPage;
