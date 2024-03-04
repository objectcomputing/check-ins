import React, {useContext, useEffect, useState} from "react";
import { styled } from '@mui/material/styles';

import { AppContext } from "../context/AppContext";

import {Button, Typography} from "@mui/material";
import SkillCategoryCard from "../components/skill-category-card/SkillCategoryCard";

import "./SkillCategoriesPage.css";
import {selectCsrfToken} from "../context/selectors";
import {createSkillCategory, getSkillCategories} from "../api/skillcategory";
import SkillCategoryNewDialog from "../components/skill-category-new-dialog/SkillCategoryNewDialog";
import {UPDATE_TOAST} from "../context/actions";

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

  useEffect(() => {
    const retrieveSkillCategories = async () => {
      const res = await getSkillCategories(csrf);
      return res.error ? [] : res.payload.data;
    }

    if (csrf) {
      retrieveSkillCategories().then((data => {
        setSkillCategories(data);
      }));
    }
  }, [csrf]);

  const createNewSkillCategory = async (categoryName, categoryDescription) => {
    const newSkillCategory = {
      name: categoryName.trim(),
      description: categoryDescription
    };

    let res = await createSkillCategory(newSkillCategory, csrf);
    if (!res.error) {
      let newCategory = res.payload.data;
      if (newCategory) {
        const withNewCategory = [...skillCategories, newCategory];
        setSkillCategories(withNewCategory);
      }
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
          name={category.name}
          description={category.description}
          skills={category.skills}
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
    </Root>
  );
};

export default SkillCategoriesPage;