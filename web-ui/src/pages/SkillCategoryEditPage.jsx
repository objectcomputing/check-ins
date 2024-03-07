import React, {useCallback, useContext, useEffect, useState} from "react";
import { useParams } from "react-router-dom";
import {AppContext} from "../context/AppContext";
import {styled} from "@mui/material/styles";

import {
  Card,
  CardHeader,
  IconButton,
  List,
  ListItem,
  ListItemText,
  TextField,
  Typography
} from "@mui/material";
import RemoveIcon from "@mui/icons-material/Remove";
import {getSkillCategory} from "../api/skillcategory";
import {selectCsrfToken, selectOrderedSkills} from "../context/selectors";
import {Add} from "@mui/icons-material";
import SelectSkillsDialog from "../components/select-skills-dialog/SelectSkillsDialog";

const PREFIX = 'SkillCategoryEditPage';
const classes = {
  root: `${PREFIX}-root`,
};

const Root = styled('div')({
  [`&.${classes.root}`]: {
    backgroundColor: "transparent",
    margin: "4rem 2rem 2rem 2rem",
    height: "100%",
    'max-width': "100%",
    '@media (max-width:800px)': {
      display: "flex",
      'flex-direction': "column",
      'overflow-x': "hidden",
      margin: "2rem 5% 0 5%",
    }
  }
});

const SkillCategoryEditPage = () => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const skills = selectOrderedSkills(state);
  const [category, setCategory] = useState(null);
  const [addSkillsDialogOpen, setAddSkillsDialogOpen] = useState(false);

  const { categoryId } = useParams();

  const getSelectableSkills = useCallback(() => {
    if (category && category.skills) {
      const result = skills.filter(skill => {
        const skillId = skill.id;
        for (let categorySkill of category.skills) {
          if (categorySkill.id === skillId) {
            return false;
          }
        }
        return true;
      });
      return result;
    }
    return [];
  }, [category, skills]);

  useEffect(() => {
    const retrieveSkillCategory = async (categoryId) => {
      const res = await getSkillCategory(categoryId, csrf);
      return !res.error ? res.payload.data : null;
    }

    if (categoryId) {
      retrieveSkillCategory(categoryId).then(data => setCategory(data));
    }
  }, [categoryId, csrf]);

  return (
    <Root className={classes.root}>
      <div>
        <Typography variant="h4">Edit Skill Category</Typography>
      </div>
      <TextField value={category ? category.name : ""} label="Name"/>
      <TextField value={category ? category.description : ""} label="Description"/>
      <Card>
        <CardHeader
          title="Selected Skills"
          action={<IconButton onClick={() => setAddSkillsDialogOpen(true)}><Add/></IconButton>}
        />
        <List
          dense
          role="list"
          >
          {category && category.skills && category.skills.map(skill =>
            <ListItem
              key={skill.id}
              role="listitem"
              secondaryAction={
                <IconButton><RemoveIcon/></IconButton>
              }
            >
              <ListItemText
                primary={skill.name}
                secondary={<Typography color="textSecondary" component="h6">{skill.description}</Typography>}
              />
            </ListItem>
          )}
        </List>
      </Card>
      <SelectSkillsDialog
        isOpen={addSkillsDialogOpen}
        onClose={() => setAddSkillsDialogOpen(false)}
        selectableSkills={getSelectableSkills()}
      />
    </Root>
  )
};

export default SkillCategoryEditPage;