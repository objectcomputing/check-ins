import React, {useContext, useEffect, useState} from "react";
import { styled } from '@mui/material/styles';

import { AppContext } from "../context/AppContext";

import {Button, DialogTitle, Typography} from "@mui/material";
import SkillCategoryCard from "../components/skill-category-card/SkillCategoryCard";

import "./SkillCategoriesPage.css";
import Dialog from "@mui/material/Dialog";
import {selectCsrfToken} from "../context/selectors";
import {getSkillCategories} from "../api/skillcategory";

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
  const { state } = useContext(AppContext);
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
      }))
    }
  }, [csrf]);

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
          name={category.name}
          description={category.description}
          skills={[]}
        />
      )}
      <Dialog
        open={dialogOpen}
        onClose={() => setDialogOpen(false)}
      >
        <DialogTitle>New Category</DialogTitle>
      </Dialog>
    </Root>
  );
};

export default SkillCategoriesPage;