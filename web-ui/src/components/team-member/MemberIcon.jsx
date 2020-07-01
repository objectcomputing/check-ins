import React from "react";
import "./TeamMember.css";

const MemberIcon = (props) => {
  const { profile, onSelect } = props;
  const { image_url } = profile;
  let image = image_url ? image_url : "https://i.imgur.com/TkSNOpF.jpg";

  return (
    <div onClick={() => onSelect(profile)} className="image-div">
      <img alt="Profile" className="member-image" src={image} />
    </div>
  );
};

export default MemberIcon;
