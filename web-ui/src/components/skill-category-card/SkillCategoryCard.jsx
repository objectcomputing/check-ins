import React from "react";
import {Card, CardContent, CardHeader, Chip} from "@mui/material";
import PropTypes from "prop-types";

const propTypes = {
  name: PropTypes.string.isRequired,
  description: PropTypes.string,
  skills: PropTypes.arrayOf(PropTypes.string),
};

const SkillCategoryCard = ({ name, description, skills }) => {
  return (
    <Card>
      <CardHeader title={name} subheader={description} />
      <CardContent style={{display: "flex", gap: "0.5rem"}}>
        {skills && skills.map((name) =>
          <Chip label={name}/>)
        }
      </CardContent>
    </Card>
  );
};

SkillCategoryCard.propTypes = propTypes;

export default SkillCategoryCard;