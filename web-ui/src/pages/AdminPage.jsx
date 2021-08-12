import React, { useState } from "react";

import Roles from "../components/admin/roles/Roles";
import UserPage from "../components/admin/users/User";

import { Button } from "@material-ui/core";

import "./AdminPage.css";

const AdminPage = () => {
  const [permissions, setPermissions] = useState(false);
  const [roles, setRoles] = useState(true);
  const [users, setUsers] = useState(false);

  const handlePermissions = () => {
    setPermissions(true);
    setRoles(false);
    setUsers(false);
  };
  const handleRoles = () => {
    setPermissions(false);
    setRoles(true);
    setUsers(false);
  };
  const handleUsers = () => {
    setPermissions(false);
    setRoles(false);
    setUsers(true);
  };
  return (
    <div>
      <div className="admin-container">
        {permissions ? <div /> : roles ? <Roles /> : users && <UserPage />}
      </div>
      <div className="bottom-nav">
        <Button
          className={permissions ? "button-selected" : "button"}
          onClick={handlePermissions}
        >
          <h3>Permissions</h3>
        </Button>
        <Button
          className={roles ? "button-selected" : "button"}
          onClick={handleRoles}
        >
          <h3>Roles</h3>
        </Button>
        <Button
          className={users ? "button-selected" : "button"}
          onClick={handleUsers}
        >
          <h3>Users</h3>
        </Button>
      </div>
    </div>
  );
};

export default AdminPage;
