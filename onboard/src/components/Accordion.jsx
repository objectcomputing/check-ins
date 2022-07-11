import React, { useState } from "react";
import Typography from "@mui/material/Typography";
import Grid from "@mui/material/Grid";
import { Divider } from "@mui/material";
import Box from "@mui/material/Box";
import { Button } from "@mui/material";
import ArrowDropDownIcon from "@mui/icons-material/ArrowDropDown";
import ArrowDropUpIcon from "@mui/icons-material/ArrowDropUp";
import  "./Accordion.css";

function Accordion({ title, open, index, content }) {
  const [isActive, setIsActive] = useState(open ? true : false);

  return (
    <Grid item xs={12} sx={{ marginTop: 7 }} key={index + Math.random()}>
      <Divider variant="fullWidth" light className="width100Percent" />
      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          minWidth: "100%",
          mb: 1,
          mt: 1,
        }}
        onClick={() => setIsActive(!isActive)}
      >
        <Box sx={{ width: "90%" }}>
          <Typography
            sx={{ marginTop: 1, marginBottom: 1 }}
            children={title}
            variant={"h4"}
            align="left"
          />
        </Box>
        <Box sx={{ width: "10%" }}>
          <Button>
            {isActive ? <ArrowDropUpIcon /> : <ArrowDropDownIcon />}
          </Button>
        </Box>
      </Box>
      {isActive && (
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            minWidth: "100%",
            mb: 1,
            mt: 1,
          }}
        >
          <Box sx={{ width: "100%"}}>{content}</Box>
        </Box>
      )}
    </Grid>
  );
}

export default Accordion;
