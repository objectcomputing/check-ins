import React from "react";

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
      <input
        onClick={selectAdmin}
        id="admin-field"
        type="checkbox"
        value={admin}
      />
      <label htmlFor="admin-field">Admin</label>
      <input onClick={selectPDL} id="pdl-field" type="checkbox" value={pdl} />
      <label htmlFor="pdl-field">PDL</label>
      <input
        onClick={selectMember}
        id="member-field"
        type="checkbox"
        value={member}
      />
      <label htmlFor="member-field">Member</label>
    </div>
  );
};

export default EditPermissionsPageRoles;
