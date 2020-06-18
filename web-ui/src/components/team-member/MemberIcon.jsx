import React from "react";
import "./TeamMember.css";

const MemberIcon = (props) => {
  const { profile, onSelect } = props;

  return (
    <div onClick={() => onSelect(profile)} className="image-div">
      <img
        alt="Profile"
        src={
          profile.image_url
            ? profile.image_url
            : "https://i.imgur.com/TkSNOpF.jpg"
        }
        style={{ maxWidth: "156px", maxHeight: "156px" }}
      />
    </div>
  );
};

export default MemberIcon;
