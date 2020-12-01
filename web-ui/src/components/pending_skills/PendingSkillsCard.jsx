import React, { useContext, useEffect, useState } from "react";

import { getSkillMembers } from "../../api/memberskill";
import { AppContext, selectProfileMap } from "../../context/AppContext";

import { Avatar, Chip, Card, CardHeader, CardContent } from "@material-ui/core";

const PendingSkillsCard = ({ pendingSkill }) => {
  const { state } = useContext(AppContext);
  const { csrf } = state;

  const { description, id, name } = pendingSkill;
  const [members, setMembers] = useState([]);

  useEffect(() => {
    const getMembers = async () => {
      let res = await getSkillMembers(id, csrf);
      if (res && res.payload && res.payload.data) {
        const memberIds = res.payload.data.map((m) => m.memberid);
        setMembers(memberIds);
      }
    };
    if (csrf) {
      getMembers();
    }
  }, [csrf, id]);

  const chip = (position) => {
    return (
      <Chip
        avatar={<Avatar src={position.imageURL}></Avatar>}
        label={position.name}
      ></Chip>
    );
  };

  const submittedBy = (members) => {
    const [first, second, ...rest] = members;
    const firstProfile = selectProfileMap(state)[first];
    if (second) {
      const secondProfile = selectProfileMap(state)[second];
      return (
        <div>
          Submitted By: {chip(firstProfile)} {chip(secondProfile)}
          {rest && ` and ${rest.length} others`}.
        </div>
      );
    } else return firstProfile && <div>Submitted by: {chip(firstProfile)}</div>;
  };

  return (
    <Card className="pending-skills-card">
      <CardHeader subheader={members && submittedBy(members)} title={name} />
      <CardContent>
        <div>{description}</div>
      </CardContent>
    </Card>
  );
};

export default PendingSkillsCard;
