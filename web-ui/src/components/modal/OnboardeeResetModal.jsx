import React from "react";
import { Modal, Box, Typography, Grid, Button } from "@mui/material";

const OnboardeeResetModal = ({ open, onClose, onSave }) => {
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

  return (
    <Modal
      open={open}
      onClose={onClose}
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
              <Button variant="contained" onClick={onSave}>
                Reset Onboardee
              </Button>
            </Grid>
          </Grid>
        </div>
      </Box>
    </Modal>
  );
};
export default OnboardeeResetModal;
