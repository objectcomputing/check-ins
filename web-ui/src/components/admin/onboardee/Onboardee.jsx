import React, { useContext, useState } from "react";
import { styled } from "@mui/material/styles";
import AddOnboardee from "../../../components/modal/AddOnboardeeModal";
import { createOnboardee } from "../../../api/onboardeeMember";
import { AppContext } from "../../../context/AppContext";
import {
  UPDATE_MEMBER_PROFILES,
  UPDATE_ONBOARDEE_MEMBER_PROFILES,
} from "../../../context/actions";
import {
  selectNormalizedMembers,
  selectNormalizedMembersAdmin,
} from "../../../context/selectors";
import { createOnboardee } from "../../../api/onboardeeMember";
const Onboardee = () => {
  const { state, dispatch } = useContext(AppContext);
  const { csrf, onboardeeProfiles, onboardeeProfile } = state;
  const [open, setOpen] = useState(false);
  const [searchText, setSearchText] = useState("");

  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);

  return (
    <Root>
      <div className="onboardee-page">
        <Grid container spacing={3}>
          <Grid item xs={12}>
            <Button onClick={handleOpen}>Add Onboardee</Button>
            <AddOnboardee
              onboardee={{}}
              open={open}
              onClose={handleClose}
              onSave={async (onboardee) => {
                if (
                  onboardee.firstName &&
                  onboardee.lastName &&
                  onboardee.position &&
                  onboardee.email &&
                  onboardee.hireType &&
                  onboardee.pdl &&
                  csrf
                ) {
                  let res = await createOnboardee(onboardee, csrf);
                  let data =
                    res.payload && res.payload.data && !res.error
                      ? res.payload.data
                      : null;
                  if (data) {
                    dispatch({
                      type: UPDATE_ONBOARDEE_MEMBER_PROFILES,
                      payload: [...onboardeeProfiles, data],
                    });
                  }
                }
              }}
            />
          </Grid>
        </Grid>
      </div>
    </Root>
  );
};
