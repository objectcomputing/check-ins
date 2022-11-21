import React, { useContext, useState, useCallback } from "react";
import { AppContext } from "../../context/AppContext";
import {
  selectOrderedMemberFirstName,
} from "../../context/selectors";
import { Modal, TextField, Box } from "@mui/material";
import Autocomplete from "@mui/material/Autocomplete";
import { Button } from "@mui/material";
import { UPDATE_TOAST } from "../../context/actions";

const modalStyles = {
  position: "absolute",
  minWidth: "400px",
  maxWidth: "600px",
  backgroundColor: "background.paper",
  top: "50%",
  left: "50%",
  padding: "1rem",
  transform: "translate(-50%, -50%)",
  border: "2px solid #fff",
};

const modalActionStyles = {
  marginTop: "1rem",
  width: "calc(100% - 1rem)",
  display: "flex",
  flexDirection: "row",
  justifyContent: "flex-end",
};

const SelectUserModal = ({ userLabel, open, onSelect, onClose }) => {
  const { state, dispatch } = useContext(AppContext);
  const [member, setMember] = useState(null);
  const sortedMembers = selectOrderedMemberFirstName(state);
  const onUserChange = (event, newValue) => {
    setMember(newValue);
  };

  const handleSelectMember = useCallback(async () => {
    if (!member) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast:
            "You must select a team member.",
        },
      });
    } else {
      onSelect(member);
    }
  }, [
    onSelect,
    dispatch,
    member,
  ]);

  return (
    <Modal open={open} onClose={onClose}>
      <Box sx={modalStyles}>
        <Autocomplete
          options={["", ...sortedMembers]}
          value=""
          onChange={onUserChange}
          getOptionLabel={(option) => option.name || ""}
          renderInput={(params) => (
            <TextField
              {...params}
              className="fullWidth"
              label={userLabel}
              placeholder="Select a team member..."
            />
          )}
        />
        <div className="fullWidth" style={modalActionStyles}>
          <Button onClick={onClose} color="secondary">
            Cancel
          </Button>
          <Button onClick={handleSelectMember} color="primary">
            Save {userLabel}
          </Button>
        </div>
      </Box>
    </Modal>
  );
};

export default SelectUserModal;
