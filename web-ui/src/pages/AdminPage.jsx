import React, { useState } from "react";
import { Button } from "@material-ui/core";

import "./AdminPage.css";

const AdminPage = () => {
  const [permissions, setPermissions] = useState(false);
  const [roles, setRoles] = useState(false);
  const [users, setUsers] = useState(true);

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
  // uncommment lines when components are ready
  return (
    <div>
      <div className="container">
        {/* {permissions && <Permissions/>}
              {roles && <Roles/> }
              {users && <Users/>} */}
      </div>
      <div className="bottom-nav">
        <Button
          //   className={permissions ? "button-selected" : "button"}
          onClick={handlePermissions}
        >
          <h3>Permissions</h3>
        </Button>
        <Button
          //   className={roles ? "button-selected" : "button"}
          onClick={handleRoles}
        >
          <h3>Roles</h3>
        </Button>
        <Button
          //   className={users ? "button-selected" : "button"}
          onClick={handleUsers}
        >
          <h3>Users</h3>
        </Button>
      </div>
    </div>
  );
};

export default AdminPage;
