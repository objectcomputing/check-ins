import React, { useContext, useState } from "react";

import { AppContext } from "../../context/AppContext";
import { selectNormalizedMembers } from "../../context/selectors";

import { Card, CardContent, CardHeader } from "@material-ui/core";

const Roles = () => {
  const { state, dispatch } = useContext(AppContext);
  const { csrf, roles, userProfile } = state;

  const [addUser, setAddUser] = useState(false);
  const [editRole, setEditRole] = useState(false);
  const [searchText, setSearchText] = useState("");
  console.log({ test });

  let normalizedMembers = selectNormalizedMembers(state, searchText);
  normalizedMembers = normalizedMembers.map((member, i) => {
    let temp = roles.find((role) => role.memberid === member.id);
    console.log({ member, temp });
    // if (temp) {
    //   member.role = temp;
    // }
  });
  const people = [
    { id: 1, name: "John" },
    { id: 2, name: "Alice" },
  ];
  const address = [
    { id: 1, peopleId: 1, address: "Some street 1" },
    { id: 2, peopleId: 2, address: "Some street 2" },
  ];

  let op = people.map((e, i) => {
    let temp = address.find((element) => element.id === e.id);
    if (temp.address) {
      e.address = temp.address;
    }
    return e;
  });
  console.log({ normalizedMembers, roles });
  // const admin =
  //   normalizedMembers &&
  //   normalizedMembers.filter((member) => member.role.includes("ADMIN"));
  // const pdl =
  //   normalizedMembers &&
  //   normalizedMembers.filter((member) => member.role.includes("PDL"));
  // const member =
  //   normalizedMembers &&
  //   normalizedMembers.filter((member) => member.role.includes("MEMBER"));
  // console.log({ admin, member, pdl });

  const createUserCards = normalizedMembers.map((member, index) => {});
  return (
    <div>
      {/* {roles.map((role) => {
        return (
          <Card className="role">
            <CardHeader
              subheader="description"
              title={role.name}
              titleTypographyProps={{ variant: "h5", component: "h2" }}
            />
            <CardContent className="role-container"></CardContent>
          </Card>
        );
      })} */}
    </div>
  );
};

export default Roles;
