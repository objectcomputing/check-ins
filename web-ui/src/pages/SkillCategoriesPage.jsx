import React, {useCallback, useContext, useEffect, useState} from "react";
import { styled } from '@mui/material/styles';

import { AppContext } from "../context/AppContext";

import {Button, DialogActions, DialogContent, DialogContentText, DialogTitle, Typography} from "@mui/material";
import SkillCategoryCard from "../components/skill-category-card/SkillCategoryCard";

import "./SkillCategoriesPage.css";
import {selectCsrfToken} from "../context/selectors";
import {
  createSkillCategory,
  deleteSkillCategory,
  getSkillCategories
} from "../api/skillcategory";
import SkillCategoryNewDialog from "../components/skill-category-new-dialog/SkillCategoryNewDialog";
import {UPDATE_TOAST} from "../context/actions";
import Dialog from "@mui/material/Dialog";

const PREFIX = 'SkillCategoriesPage';
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

const SkillCategoriesPage = () => {
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const [skillCategories, setSkillCategories] = useState([]);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [categoryToDelete, setCategoryToDelete] = useState(null);

  const retrieveCategories = useCallback(async () => {
    if (csrf) {
      const res = await getSkillCategories(csrf);
      const data = res.error ? [] : res.payload.data;
      setSkillCategories(data);
    }
  }, [csrf]);

  useEffect(() => {
    retrieveCategories();
  }, [retrieveCategories]);

  const deleteCategory = useCallback(async () => {
    if (categoryToDelete) {
      const res = await deleteSkillCategory(categoryToDelete.id, csrf);
      if (res.payload.status !== 200) {
        dispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "error",
            toast: "Failed to remove skill from category"
          }
        });
      }

      retrieveCategories().then(() => {
        setCategoryToDelete(null);
      });
    }
  }, [categoryToDelete, csrf, dispatch, retrieveCategories]);

  const createNewSkillCategory = async (categoryName, categoryDescription) => {
    const newSkillCategory = {
      name: categoryName.trim(),
      description: categoryDescription
    };

    let res = await createSkillCategory(newSkillCategory, csrf);
    if (!res.error) {
      retrieveCategories();
    } else {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: "Error: Could not save category"
        }
      });
    }
  }

  return (
    <Root className={classes.root}>
      <div className="skill-categories-header">
        <Typography variant="h4">Skill Categories</Typography>
        <Button
          variant="contained"
          onClick={() => setDialogOpen(true)}
        >
          New Category
        </Button>
      </div>
      {skillCategories.map(category =>
        <SkillCategoryCard
          key={category.id}
          id={category.id}
          name={category.name}
          description={category.description}
          skills={category.skills}
          onDelete={() => setCategoryToDelete(category)}
        />
      )}
      <SkillCategoryNewDialog
        isOpen={dialogOpen}
        onClose={() => setDialogOpen(false)}
        onConfirm={(categoryName, categoryDescription) => {
          createNewSkillCategory(categoryName, categoryDescription)
            .then(() => setDialogOpen(false));
        }}
      />
      {categoryToDelete &&
        <Dialog
          open={!!categoryToDelete}
          onClose={() => setCategoryToDelete(null)}>
          <DialogTitle>Delete Category?</DialogTitle>
          <DialogContent>
            <DialogContentText>Are you sure you want to delete the category "{categoryToDelete.name}"? The skills in this category will not be deleted.</DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setCategoryToDelete(null)} color="primary">
              Cancel
            </Button>
            <Button onClick={deleteCategory} color="error" autoFocus>
              Delete
            </Button>
          </DialogActions>
        </Dialog>
      }
    </Root>
  );
};

export default SkillCategoriesPage;