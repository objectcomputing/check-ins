import React from "react";
import Roles from "./Roles";

export default {
  component: Roles,
  title: "Check Ins/Roles",
};

const Template = (args) => {
  return <Roles {...args} />;
};

const rolesArgs = {
  state: {
    memberProfiles: [
      { id: 1, name: "Señior Test" },
      { id: 2, name: "Señora Test" },
      { id: 3, name: "Herr Test" },
    ],
    roles: [
      { id: 1, role: "ADMIN", memberid: 1 },
      { id: 2, role: "PDL", memberid: 2 },
    ],
  },
};

export const RolesComponent = Template.bind({});
RolesComponent.args = { ...rolesArgs };
