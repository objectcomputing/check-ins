import React, { useState } from "react";
import { Modal, Box, Typography, Grid, Button } from "@mui/material";

const OnboardeeResetModal = () => {
  const modalBoxStyleMini = {
    position: "absolute",
    top: "50%",
    left: "50%",
    transform: "translate(-50%, -50%)",
    width: "25%",
    backgroundColor: "#fff",
    border: "2px solid #000",
    boxShadow: 24,
    pt: 2,
    px: 4,
    pb: 3,
    m: 2,
  };
  const [open, setOpen] = useState(false);
  const handleOpen = () => {
    setOpen(true);
  };
  const handleClose = () => {
    setOpen(false);
  };
  return (
    <React.Fragment>
      onClick={handleOpen}
      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby="title"
        aria-describedby="description"
      >
        <Box sx={modalBoxStyleMini}>
          <div
            style={{
              textAlign: "center",
              marginLeft: "auto",
              marginRight: "auto",
              marginTop: "auto",
              marginBottom: "auto",
            }}
          >
            <Typography variant="p" component="h3">
              This action will restart their entire onboarding process. Are you
              sure you want to reset this onboardee?
            </Typography>
          </div>
          <div>
            <Grid container>
              <Grid
                item
                xs={6}
                style={{ display: "flex", justifyContent: "flex-start" }}
              >
                <Button variant="contained" onClick={onClose}>
                  Cancel
                </Button>
              </Grid>
              <Grid
                item
                xs={6}
                style={{ display: "flex", justifyContent: "flex-end" }}
              >
                <Button variant="contained" onClick={resetOnboardeeClick}>
                  Reset Onboardee
                </Button>
              </Grid>
            </Grid>
          </div>
        </Box>
      </Modal>
    </React.Fragment>
  );
};
export default OnboardeeResetModal;
