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

  return (
    <div>
      <div className="container">
        {/* {permissions && <Permissions/>}
              {roles && <Roles/> }
              {users && <Users/>} */}
      </div>
      <div className="bottom-nav">
        <Button className="button" onClick={handlePermissions}>
          <h3>Permissions</h3>
        </Button>
        <Button className="button" onClick={handleRoles}>
          <h3>Roles</h3>
        </Button>
        <Button className="button" onClick={handleUsers}>
          <h3>Users</h3>
        </Button>
      </div>
    </div>
  );
};

export default AdminPage;
