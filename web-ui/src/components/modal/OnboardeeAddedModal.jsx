import React, { useState } from "react";
import { Modal, Box, Typography, Grid, Button } from "@mui/material";

const OnboardeeAddedModal = () => {
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
      <Button onClick={handleOpen}>Submit</Button>
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
              Onboardee added!
            </Typography>
          </div>
          <div>
            <Grid container sx={{ mt: 5 }}>
              <Grid item xs={12} align="center">
                <Button
                  variant="contained"
                  onClick={handleClose}
                  style={{
                    display: "flex",
                    justifyContent: "centered",
                    gap: "10px",
                  }}
                >
                  Okay
                </Button>
              </Grid>
            </Grid>
          </div>
        </Box>
      </Modal>
    </React.Fragment>
  );
};
export default OnboardeeAddedModal;
