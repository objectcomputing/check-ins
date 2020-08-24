import React from "react";
import "./TeamMember.css";

const MemberIcon = (props) => {
  const { profile, onSelect, onSelectPDL } = props;
  const { image_url } = profile;
  let image = image_url ? image_url : "/default_profile.jpg";

  return (
    <div
      onClick={() => {
        onSelectPDL(profile.pdl);
        onSelect(profile);
      }}
      className="image-div"
    >
      <img alt="Profile" className="member-image" src={image} />
    </div>
  );
};

export default MemberIcon;
