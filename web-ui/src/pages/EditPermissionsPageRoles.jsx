import React from "react";
import { Checkbox } from "@mui/material";

const EditPermissionsPageRoles = ({
  title,
  selectAdmin,
  admin,
  selectPDL,
  pdl,
  selectMember,
  member,
}) => {
  return (
    <div className="permissions">
      <h4>{title}</h4>
      <Checkbox
        checked={admin}
        id="admin-field"
        onChange={selectAdmin}
        inputProps={{ "aria-label": `admin checkbox ${title}` }}
      />

      <label htmlFor="admin-field">Admin</label>
      <Checkbox
        checked={pdl}
        id="admin-field"
        onChange={selectPDL}
        inputProps={{ "aria-label": `pdl checkbox ${title}` }}
      />

      <label htmlFor="pdl-field">PDL</label>
      <Checkbox
        checked={member}
        id="admin-field"
        onChange={selectMember}
        inputProps={{ "aria-label": `member checkbox ${title}` }}
      />
      <label htmlFor="member-field">Member</label>
    </div>
  );
};

export default EditPermissionsPageRoles;
