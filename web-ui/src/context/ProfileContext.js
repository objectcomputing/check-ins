import React, { useContext } from "react";

const defaultValues = {
  defaultProfile: {
    bio: "It was all a dream, I used to read Word Up magazine",
    email: "Biggie@oci.com",
    name: "Christopher Wallace",
    pdl: "Tupac Shakur",
    role: "Lyrical Poet",
  },
  defaultTeamMembers: [
    {
      name: "jes",
      role: "engineer",
      pdlId: "fb6424a0-b429-4edf-8f05-6927689bec5f",
      location: "kihei",
      workEmail: "example email",
      startDate: 1573551461820,
      bioText: "example bio text",
    },
    {
      name: "pramukh",
      role: "engineer",
      pdlId: "fb6424a0-b429-4edf-8f05-6927689bec5f",
      location: "St. Louis",
      workEmail: "example email",
      insperityId: "example string of insperity",
      startDate: 1493051461820,
      bioText: "example bio text",
    },
  ],
};

export const useAppContext = () => useContext(ProfileContext);

const ProfileContext = React.createContext(defaultValues);

export default ProfileContext;
