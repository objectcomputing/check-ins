import React from 'react';
import './TeamMember.css';

const MemberIcon = props => {
  const { profile, onSelect } = props;
  const { imageUrl } = profile;
  let image = imageUrl ? imageUrl : '/default_profile.jpg';

  return (
    <div
      onClick={() => {
        onSelect(profile);
      }}
      className="image-div"
    >
      <img alt="Profile" className="member-image" src={image} />
    </div>
  );
};

export default MemberIcon;
