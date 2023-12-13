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
  const [showAllPermissions, setShowAllPermissions] = useState(true);
  const [updatePermissions, setUpdatePermissions] = useState(false);
  const [deletePermissions, setDeletePermissions] = useState(false);
  const [viewPermissions, setViewPermissions] = useState(false);

  const allPermissions = selectPermissions(state);

  const handleClick = () => setShowAllPermissions(!showAllPermissions);
  const handleClickUpdate = () => setUpdatePermissions(!updatePermissions);
  const handleClickDelete = () => setDeletePermissions(!deletePermissions);
  const handleClickView = () => setViewPermissions(!viewPermissions);

  useEffect(() => {
    console.log("Permissions");
    console.log(allPermissions, permissionsList);
  }, [allPermissions, permissionsList]);

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
            <input
              onClick={handleClick}
              id="all-permissions"
              type="checkbox"
              value={showAllPermissions}
            />
            <label htmlFor="all-permissions">Show all permissions</label>
          </div>
        </div>
      </div>

      <div className="edit-permissions-list">
        <h2>Edit Permissions Below:</h2>
        <div className="edit-permissions">
          <input
            onClick={handleClickUpdate}
            id="update-permissions"
            type="checkbox"
            value={updatePermissions}
          />
          <label htmlFor="update-permissions">Update permissions</label>
        </div>
        <div className="edit-permissions">
          <input
            onClick={handleClickDelete}
            id="delete-permissions"
            type="checkbox"
            value={deletePermissions}
          />
          <label htmlFor="delete-permissions">Delete permissions</label>
        </div>
        <div className="edit-permissions">
          <input
            onClick={handleClickView}
            id="view-permissions"
            type="checkbox"
            value={viewPermissions}
          />
          <label htmlFor="view-permissions">View permissions</label>
        </div>
      </div>
    </div>
  );
};

export default EditPermissionsPage;
