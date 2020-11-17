import React from "react";

import { Avatar, Chip, Card, CardHeader } from "@material-ui/core";

const PendingSkillsCard = ({ pendingSkill }) => {
  const { name, members } = pendingSkill;

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
          {`Submitted By: ${chip(first)}, ${chip(
            second
          )} and ${length} others. `}
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
    <Card className="pending-skill">
      <CardHeader
        subheader={members[0] ? submittedBy(members) : ""}
        title={name}
      />
    </Card>
  );
};

export default PendingSkillsCard;
