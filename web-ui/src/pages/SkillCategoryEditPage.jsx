import React, {useCallback, useContext, useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import {AppContext} from "../context/AppContext";
import {styled} from "@mui/material/styles";

import {Card, CardHeader, IconButton, List, ListItem, ListItemText, TextField, Typography} from "@mui/material";
import RemoveIcon from "@mui/icons-material/Remove";
import {createSkillCategorySkills, getSkillCategory} from "../api/skillcategory";
import {selectCsrfToken, selectOrderedSkills} from "../context/selectors";
import {Add} from "@mui/icons-material";
import SelectSkillsDialog from "../components/select-skills-dialog/SelectSkillsDialog";
import {UPDATE_TOAST} from "../context/actions";

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
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const skills = selectOrderedSkills(state);
  const [category, setCategory] = useState(null);
  const [addSkillsDialogOpen, setAddSkillsDialogOpen] = useState(false);

  const { categoryId } = useParams();

  const getSelectableSkills = useCallback(() => {
    if (category && category.skills) {
      const categorySkillIds = category.skills.reduce((map, skill) => {
        map[skill.id] = skill.name;
        return map;
      }, {});

      return skills.filter(skill => {
        return !categorySkillIds[skill.id];
      });
    }
    return [];
  }, [category, skills]);

  const retrieveSkillCategory = useCallback(async (categoryId) => {
    const res = await getSkillCategory(categoryId, csrf);
    return !res.error ? res.payload.data : null;
  }, [csrf]);

  useEffect(() => {
    if (categoryId) {
      retrieveSkillCategory(categoryId).then(data => setCategory(data));
    }
  }, [categoryId, csrf, retrieveSkillCategory]);

  const saveCategorySkillIds = useCallback(async (skillIds) => {
    if (categoryId) {
      const res = await createSkillCategorySkills(categoryId, skillIds, csrf);
      if (res.error) {
        dispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "error",
            toast: "Failed to add skill(s) to category"
          }
        });
      }

      retrieveSkillCategory(categoryId).then(data => {
        if (data) {
          setCategory(data);
        } else {
          dispatch({
            type: UPDATE_TOAST,
            payload: {
              severity: "error",
              toast: "Failed to retrieve category after saving skills"
            }
          });
        }
        setAddSkillsDialogOpen(false);
      });
    }
  }, [categoryId, csrf, dispatch, retrieveSkillCategory]);

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
        onSave={saveCategorySkillIds}
      />
    </Root>
  )
};

export default SkillCategoryEditPage;