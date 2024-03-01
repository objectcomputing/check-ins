import React, { useContext, useState } from "react";
import { styled } from '@mui/material/styles';

import { AppContext } from "../context/AppContext";

import { Button, Typography } from "@mui/material";

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
  return (
    <Root className={classes.root}>
      <div className="skill-categories-header">
        <Typography variant="h4">Skill Categories</Typography>
        <Button
          variant="contained">
          New Category
        </Button>
      </div>
    </Root>
  );
};

export default SkillCategoriesPage;