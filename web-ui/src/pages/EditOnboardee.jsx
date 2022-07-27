import React, { useState } from "react";
import Grid from "@mui/material/Grid";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import { FormControl, Autocomplete, TextField } from "@mui/material";
import InputField from "../components/inputs/InputField";
import IconButton from "@mui/material/IconButton";
import AddBoxIcon from "@mui/icons-material/AddBox";
import Stack from "@mui/material/Stack";

function EditOnboardee() {
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  //const [position, setPosition] = useState("");
  //const [hireType, setHireType] = useState("");
  const [email, setEmail] = useState("");
  //const [pdl, setPdl] = useState("");

  const posOptions = ["dummy1", "dummy2", "dummy3"];
  const hireOptions = ["dummy4", "dummy5", "dummy6"];
  const pdlOptions = ["dummy7", "dummy8", "dummy9"];

  function handleChange(event) {
    const e = event;
    const val = e.target.value;
    const name = e.target.name;

    // Event handler for the fields
    if (name === "firstName") {
      setFirstName(val);
    } else if (name === "lastName") {
      setLastName(val);
      // } else if (name === "position") {
      //   setPosition(val);
      // } else if (name === "hireType") {
      //   setHireType(val);
    } else if (name === "email") {
      setEmail(val);
      // } else if (name === "pdl") {
      //   setPdl(val);
    }
  }

  function handleSaveInformation(e) {
    e.preventDefault();
    console.log("TODO: Submit data to backend!");
  }

  return (
    <Box sx={{ width: "100%", textAlign: "left" }}>
      <Typography variant="h3" component="div" gutterBottom>
        Edit Onboardee
      </Typography>
      <form autoComplete="off" onSubmit={handleSaveInformation}>
        <Grid
          container
          rowSpacing={1}
          columnSpacing={{ xs: 1, sm: 2, md: 3 }}
          sx={{ marginTop: 3 }}
        >
          <Grid item xs={12} sm={12} md={12} lg={6}>
            <FormControl
              sx={{
                my: 1,
                marginLeft: 3,
                width: "90%",
                maxWidth: "500px",
              }}
            >
              <Typography id="description" sx={{ mt: 2 }}>
                Position:
              </Typography>
              <Autocomplete
                disablePortal
                options={posOptions}
                sx={{ width: "75%" }}
                renderInput={(option) => (
                  <TextField variant="outlined" {...option} />
                )}
              />
            </FormControl>
          </Grid>

          <Grid item xs={12} sm={12} md={12} lg={6}>
            <FormControl
              sx={{
                my: 1,
                marginLeft: 3,
                width: "90%",
                maxWidth: "500px",
              }}
            >
              <Typography id="description" sx={{ mt: 2 }}>
                Hire Type:
              </Typography>
              <Autocomplete
                disablePortal
                options={hireOptions}
                sx={{ width: "75%" }}
                renderInput={(option) => (
                  <TextField variant="outlined" {...option} />
                )}
              />
            </FormControl>
          </Grid>
          <Grid item xs={12} sm={12} md={12} lg={6}>
            <FormControl
              sx={{
                my: 1,
                marginLeft: 3,
                width: "90%",
                maxWidth: "500px",
              }}
            >
              <InputField
                autocomplete="first-name"
                title="First Name:"
                id="firstName"
                value={firstName}
                autoFocus={true}
                onChangeHandler={handleChange}
                type="text"
              />
            </FormControl>
          </Grid>
          <Grid item xs={12} sm={12} md={12} lg={6}>
            <FormControl
              sx={{
                my: 1,
                marginLeft: 3,
                width: "90%",
                maxWidth: "500px",
              }}
            >
              <InputField
                title="Last Name:"
                id="lastName"
                value={lastName}
                autoFocus={true}
                onChangeHandler={handleChange}
                type="text"
              />
            </FormControl>
          </Grid>

          <Grid item xs={12} sm={12} md={12} lg={6}>
            <FormControl
              sx={{
                my: 1,
                marginLeft: 3,
                width: "90%",
                maxWidth: "500px",
              }}
            >
              <InputField
                title="Email"
                id="email"
                value={email}
                autoFocus={true}
                onChangeHandler={handleChange}
                type="text"
              />
            </FormControl>
          </Grid>

          <Grid item xs={12} sm={12} md={12} lg={6}>
            <FormControl
              sx={{
                my: 1,
                marginLeft: 3,
                width: "90%",
                maxWidth: "500px",
              }}
            >
              <Typography id="description" sx={{ mt: 2 }}>
                PDL/Manager:
              </Typography>
              <Autocomplete
                disablePortal
                options={pdlOptions}
                sx={{ width: "75%" }}
                renderInput={(option) => (
                  <TextField variant="outlined" {...option} />
                )}
              />
            </FormControl>
          </Grid>

          <Grid item xs={12} sm={12} md={12} lg={6}>
            <FormControl
              sx={{
                my: 1,
                marginLeft: 3,
                width: "90%",
                maxWidth: "500px",
              }}
            >
              <Stack direction="row" alignItems="center" spacing={2}>
                <Box variant="contained" component="label">
                  Offer Letter
                </Box>
                <IconButton
                  color="primary"
                  aria-label="upload picture"
                  component="label"
                >
                  <input hidden accept="documents/*" type="file" />
                  <AddBoxIcon />
                </IconButton>
              </Stack>
            </FormControl>
                  </Grid>
                  
                  <Grid item xs={12} sm={12} md={12} lg={6}>
            <FormControl
              sx={{
                my: 1,
                marginLeft: 3,
                width: "90%",
                maxWidth: "500px",
              }}
            >
              <Stack direction="row" alignItems="center" spacing={2}>
                <Box variant="contained" component="label">
                  Employee Agreement
                </Box>
                <IconButton
                  color="primary"
                  aria-label="upload picture"
                  component="label"
                >
                  <input hidden accept="documents/*" type="file" />
                  <AddBoxIcon />
                </IconButton>
              </Stack>
            </FormControl>
          </Grid>
        </Grid>
      </form>
    </Box>
  );
}

export default EditOnboardee;
