import React from "react";
import Profile from "../components/profile/Profile";

const testProfile = [
  { name: "holmes" },
  { name: "homie" },
  { name: "homie g" },
];

let teamProfile = (profile) => {
  let team = profile.map((e) => {
    return (
      <Profile
        key={e.name}
        name={e.name}
        image_url={e.image_url}
        team={true}
      ></Profile>
    );
  });
  return team;
};

const HomePage = () => {
  let team = teamProfile(testProfile);
  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
        flexDirection: "column",
      }}
    >
      <h3>Professional Development @ OCI</h3>
      <div
        style={{
          display: "flex",
          justifyContent: "center",
          flexDirection: "row",
        }}
      >
        {team}
      </div>
    </div>
  );
};

export default HomePage;
