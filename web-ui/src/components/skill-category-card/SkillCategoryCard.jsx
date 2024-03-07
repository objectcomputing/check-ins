import React from "react";
import {Card, CardContent, CardHeader, Chip, IconButton, Tooltip} from "@mui/material";
import PropTypes from "prop-types";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import Typography from "@mui/material/Typography";
import {Link} from "react-router-dom";

const propTypes = {
  id: PropTypes.string.isRequired,
  name: PropTypes.string.isRequired,
  description: PropTypes.string,
  skills: PropTypes.arrayOf(PropTypes.object),
};

const SkillCategoryCard = ({ id, name, description, skills }) => {
  return (
    <Card>
      <CardHeader
        title={name}
        subheader={description}
        action={<div
          style={{display: "flex", gap: "1rem", margin: "0 1rem"}}>
          <Tooltip title="Edit" arrow>
            <Link to={`/admin/skill-categories/${id}`}>
              <IconButton>
                <EditIcon />
              </IconButton>
            </Link>
          </Tooltip>
          <Tooltip title="Delete" arrow>
            <IconButton>
              <DeleteIcon />
            </IconButton>
          </Tooltip>
        </div>}
      />
      <CardContent style={{display: "flex", gap: "0.5rem"}}>
        {(skills && skills.length > 0) ? (
          skills.map((skill) => <Chip key={skill.id} label={skill.name}/>)
        ) : (
          <Typography style={{fontStyle: "italic"}} variant="body2" color="textSecondary">This category contains no skills.</Typography>
        )}
      </CardContent>
    </Card>
  );
};

SkillCategoryCard.propTypes = propTypes;

export default SkillCategoryCard;