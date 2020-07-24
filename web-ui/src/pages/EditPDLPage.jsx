import React, { useContext, useState } from "react";
import EditPDL from "../components/admin/EditPDL";
import { SkillsContext, UPDATE_PDLS } from "../context/SkillsContext";

const EditPDLPage = () => {
  const { state, dispatch } = useContext(SkillsContext);
  const { defaultTeamMembers } = state;

  const [selectedProfiles, setSelectedProfiles] = useState([]);

  const handleSelect = (profile) => {
    if (!selectedProfiles.find((p) => p.id === profile.id)) {
      setSelectedProfiles([...selectedProfiles, profile]);
      profile.selected = true;
    }
  };
  const handleDeselect = (profile) => {
    setSelectedProfiles(selectedProfiles.filter((p) => p.id !== profile.id));
    profile.selected = false;
  };
  const handleEdit = (pdl) => {
    dispatch({ type: UPDATE_PDLS, payload: { selectedProfiles, pdl } });
    setSelectedProfiles([]);
  };

  return (
    <div style={{ display: "flex", flexDirection: "column" }}>
      {defaultTeamMembers.map((profile) => (
        <EditPDL
          key={profile.id + profile.pdl}
          profile={profile}
          onDeselect={() => handleDeselect(profile)}
          onSelect={() => handleSelect(profile)}
          onEdit={handleEdit}
        ></EditPDL>
      ))}
    </div>
  );
};

export default EditPDLPage;
