import React, { useContext, useEffect, useState } from "react";

import { getSkillMembers } from "../../api/memberskill";
import { AppContext } from "../../context/AppContext";

import { Avatar, Chip, Card, CardHeader, CardContent } from "@material-ui/core";

const PendingSkillsCard = ({ pendingSkill }) => {
  const { state } = useContext(AppContext);
  const { selectMemberProfileById } = AppContext;
  const { csrf, memberProfiles } = state;

  const { description, id, name } = pendingSkill;
  const [members, setMembers] = useState([]);

  useEffect(() => {
    const getMembers = async () => {
      let res = await getSkillMembers(id, csrf);
      const copy = [...members];
      if (res && res.payload && res.payload.data) {
        res.payload.data.map((m) => {
          const { memberid } = m;
          const member = selectMemberProfileById(state)(memberid);
          if (member) {
            copy.push(member);
          }
          return copy;
        });
        setMembers(copy);
      }
    };
    if (csrf) {
      getMembers();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [csrf, id, memberProfiles]);

  const chip = (position) => {
    return (
      <Chip
        avatar={<Avatar src={position.imageURL}></Avatar>}
        label={position.name}
      ></Chip>
    );
  };

  const submittedBy = (array) => {
    const [first, second, ...rest] = array;
    const length = rest.length;
    if (first && second && rest.length > 0) {
      return (
        <div>
          Submitted By: {chip(first)} {chip(second)} and {length} others.
        </div>
      );
    } else if (first && second) {
      return (
        <div>
          Submitted By: {chip(first)} and {chip(second)}
        </div>
      );
    } else return <div>Submitted by: {chip(first)}</div>;
  };

  return (
    <Card className="pending-skills-card">
      <CardHeader
        subheader={members && members[0] ? submittedBy(members) : ""}
        title={name}
      />
      <CardContent>
        <div>{description}</div>
      </CardContent>
    </Card>
  );
};

export default PendingSkillsCard;
