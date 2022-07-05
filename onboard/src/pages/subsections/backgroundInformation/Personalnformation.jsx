import React from "react";
import TextField from "@mui/material/TextField";
import { styled } from "@mui/material/styles";
import Grid from "@mui/material/Grid";
import Paper from "@mui/material/Paper";
import Box from "@mui/material/Box";

const Item = styled(Paper)(({ theme }) => ({
  backgroundColor: theme.palette.mode === "dark" ? "#1A2027" : "#fff",
  ...theme.typography.body2,
  padding: theme.spacing(1),
  textAlign: "center",
  color: theme.palette.text.secondary,
}));

function PersonalInformation() {
  return (
    <>
      <Box sx={{ width: "100%" }}>
        <Grid container rowSpacing={1} columnSpacing={{ xs: 1, sm: 2, md: 3 }}>
          <Grid item xs={4}>
            <Item>
              <TextField
                id="outlined-basic"
                label="First Name"
                variant="outlined"
              />
            </Item>
          </Grid>
          <Grid item xs={4}>
            <Item>
              <TextField
                id="outlined-basic"
                label="Last Name"
                variant="outlined"
              />
            </Item>
          </Grid>
          <Grid item xs={4}>
            <Item>
              <TextField
                id="outlined-basic"
                label="Middle Initial"
                variant="outlined"
              />
            </Item>
          </Grid>
          <Grid item xs={4}>
            <Item>
              <TextField
                type="number"
                id="outlined-basic"
                label="SSN"
                variant="outlined"
              />
            </Item>
          </Grid>
          <Grid item xs={4}>
            <Item>
              <TextField
                type="date"
                id="outlined-basic"
                label="Birthdate"
                variant="outlined"
              />
            </Item>
          </Grid>
          <Grid item xs={4}>
            <Item>
              <TextField
                id="outlined-basic"
                label="Current Address"
                variant="outlined"
              />
            </Item>
          </Grid>
          <Grid item xs={4}>
            <Item>
              <TextField
                id="outlined-basic"
                label="Permanent Address"
                variant="outlined"
              />
            </Item>
          </Grid>
          <Grid item xs={4}>
            <Item>
              <TextField
                type="number"
                id="outlined-basic"
                label="Primary Phone Number"
                variant="outlined"
              />
            </Item>
          </Grid>
          <Grid item xs={4}>
            <Item>
              <TextField
                type="number"
                id="outlined-basic"
                label="Secondary Phone Number"
                variant="outlined"
              />
            </Item>
          </Grid>
        </Grid>
      </Box>
    </>
  );
}

export default PersonalInformation;
