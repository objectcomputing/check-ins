import React, {useCallback, useContext, useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import {AppContext} from "../context/AppContext";
import {styled} from "@mui/material/styles";

import {
  Button,
  Card,
  CardHeader, DialogActions, DialogContent, DialogContentText, DialogTitle,
  IconButton,
  List,
  ListItem,
  ListItemText,
  TextField,
  Tooltip,
  Typography
} from "@mui/material";
import RemoveIcon from "@mui/icons-material/Remove";
import {
  createSkillCategorySkills,
  deleteSkillCategorySkill,
  getSkillCategory,
  updateSkillCategory
} from "../api/skillcategory";
import {selectCsrfToken, selectOrderedSkills} from "../context/selectors";
import {Add} from "@mui/icons-material";
import SelectSkillsDialog from "../components/select-skills-dialog/SelectSkillsDialog";
import {UPDATE_TOAST} from "../context/actions";
import Dialog from "@mui/material/Dialog";

import "./SkillCategoryEditPage.css";

const PREFIX = 'SkillCategoryEditPage';
const classes = {
  root: `${PREFIX}-root`,
};

const Root = styled('div')({
  [`&.${classes.root}`]: {
    backgroundColor: "transparent",
    margin: "4rem 2rem 2rem 2rem",
    height: "100%",
    maxWidth: "100%",
    '@media (max-width: 800px)': {
      display: "flex",
      flexDirection: "column",
      overflowX: "hidden",
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
  const [skillToRemove, setSkillToRemove] = useState(null);

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

  const refreshSkillCategory = useCallback(async () => {
    retrieveSkillCategory(categoryId).then(data => {
      if (data) {
        setCategory(data);
      } else {
        dispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "error",
            toast: "Failed to refresh category"
          }
        });
      }
    });
  }, [categoryId, dispatch, retrieveSkillCategory]);

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

      refreshSkillCategory().then(() => {
        setAddSkillsDialogOpen(false);
      });
    }
  }, [categoryId, csrf, dispatch, refreshSkillCategory]);

  const removeSkillFromCategory = useCallback(async () => {
    if (skillToRemove) {
      const res = await deleteSkillCategorySkill(categoryId, skillToRemove.id, csrf);
      if (res.payload.status !== 200) {
        dispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "error",
            toast: "Failed to remove skill from category"
          }
        });
      }

      refreshSkillCategory().then(() => {
        setSkillToRemove(null);
      });
    }
  }, [categoryId, csrf, dispatch, refreshSkillCategory, skillToRemove]);

  const updateCategoryDetails = useCallback(async () => {
    const res = await updateSkillCategory(category, csrf);
    if (res.error) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: "Failed to update category"
        }
      });
    }
  }, [category, csrf, dispatch]);

  return (
    <Root className={classes.root}>
      <Typography variant="h4">Edit Skill Category</Typography>
      <div className="edit-skill-category-fields">
        <TextField
          label="Name"
          style={{ width: "250px" }}
          value={category ? category.name : ""}
          onChange={(event) => {
            setCategory({
              ...category,
              name: event.target.value
            });
          }}
          onBlur={() => updateCategoryDetails()}
        />
        <TextField
          label="Description"
          style={{ width: "400px" }}
          value={category ? category.description : ""}
          onChange={(event) => {
            setCategory({
              ...category,
              description: event.target.value
            });
          }}
          onBlur={() => updateCategoryDetails()}
        />
      </div>
      <Card>
        <CardHeader
          title="Category Skills"
          action={
            <Tooltip title="Add skills to this category" arrow>
              <IconButton onClick={() => setAddSkillsDialogOpen(true)}><Add/></IconButton>
            </Tooltip>
          }
        />
        <List
          dense
          role="list"
          >
          {category && (
            category.skills.length ? (
              category.skills.map(skill =>
                <ListItem
                  key={skill.id}
                  role="listitem"
                  secondaryAction={
                    <Tooltip title="Remove skill from category" arrow>
                      <IconButton onClick={() => setSkillToRemove(skill)}><RemoveIcon/></IconButton>
                    </Tooltip>
                  }
                >
                  <ListItemText
                    primary={skill.name}
                    secondary={<Typography color="textSecondary" component="h6">{skill.description}</Typography>}
                  />
                </ListItem>
              )
            ) : (
              <ListItem><ListItemText>This category contains no skills</ListItemText></ListItem>
            )
          )}
        </List>
      </Card>
      {addSkillsDialogOpen &&
        <SelectSkillsDialog
          isOpen={addSkillsDialogOpen}
          onClose={() => setAddSkillsDialogOpen(false)}
          selectableSkills={getSelectableSkills()}
          onSave={saveCategorySkillIds}
        />
      }
      {skillToRemove &&
        <Dialog
          open={!!skillToRemove}
          onClose={() => setSkillToRemove(null)}>
          <DialogTitle>Remove Skill?</DialogTitle>
          <DialogContent>
            <DialogContentText>Are you sure you want to remove "{skillToRemove.name}" from {category.name}? The skill itself will not be deleted.</DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setSkillToRemove(null)} color="primary">
              Cancel
            </Button>
            <Button onClick={removeSkillFromCategory} color="error" autoFocus>
              Remove
            </Button>
          </DialogActions>
        </Dialog>
      }
    </Root>
  )
};

export default SkillCategoryEditPage;