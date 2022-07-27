import React, { useState } from "react";
import Grid from "@mui/material/Grid";
import Box from "@mui/material/Box";
import { FormControl } from "@mui/material";
import InputField from "../components/inputs/InputField";

function EditOnboardee() {
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [position, setPosition] = useState("");
  const [hireType, setHireType] = useState("");
  const [pdl, setPdl] = useState("");

  function handleSaveInformation(e) {
    e.preventDefault();
    console.log("TODO: Submit data to backend!");
  }

  return (
    <Box sx={{ width: "100%", textAlign: "left" }}>
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
              <InputField
                autocomplete="first-name"
                title="First Name:"
                id="firstName"
                value={firstName}
                autoFocus={true}
                //onChangeHandler={handleChange}
                type="text"
              />
            </FormControl>
          </Grid>
        </Grid>
      </form>
    </Box>
  );
}

export default EditOnboardee;
