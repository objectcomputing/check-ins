import React from "react";
import "./TeamMember.css";

const MemberIcon = (props) => {
  const { profile, onSelect } = props;
  let image = profile.image_url
    ? profile.image_url
    : "https://i.imgur.com/TkSNOpF.jpg";

  return (
    <div onClick={() => onSelect(profile)} className="image-div">
      <img
        alt="Profile"
        src={image}
        style={{ maxWidth: "156px", maxHeight: "156px", marginBottom: "20px" }}
      />
    </div>
  );
};

export default MemberIcon;
