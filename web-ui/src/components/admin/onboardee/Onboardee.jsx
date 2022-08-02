import React, { useContext, useState } from "react";
import { styled } from '@mui/material/styles';
import AddOnboardee from "../../../components/modal/AddOnboardeeModal";
import { createMember } from "../../../api/member";
import { AppContext } from "../../../context/AppContext";
import { UPDATE_MEMBER_PROFILES } from "../../../context/actions";
import {
  selectNormalizedMembers,
  selectNormalizedMembersAdmin,
} from "../../../context/selectors";

const Onboardee = () => {
  const { state, dispatch } = useContext(AppContext);
  const { csrf, onboardeeProfiles, onboardeeProfile } = state;
  const [open, setOpen] = useState(false);
  const [serchText, setSearchText] = useState("");
  
  const handleOpen = () => setOpen(true)

  const handleClose = () => setOpen(false);

  return (
    <Root>
      <div className="onboardee-page">
        <Grid container spacing={3}>
          <Grid item xs={12} >
            <Button onClick={handleOpen}>
              Add Onboardee
            </Button>
            <AddOnboardee>
              onboard
            </AddOnboardee>
          </Grid>
        </Grid>
      </div>
    </Root>
  )


}