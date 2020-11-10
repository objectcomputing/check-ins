import React, { useContext, useEffect, useState } from "react";

import { getAllPDLs, getMember, updateMember } from "../../api/member";
import { AppContext } from "../../context/AppContext";

import { Modal, TextField } from "@material-ui/core";
import Autocomplete from "@material-ui/lab/Autocomplete";
import {
  KeyboardDatePicker,
  MuiPickersUtilsProvider,
} from "@material-ui/pickers";
import DateFnsUtils from "@date-io/date-fns";
import { Button } from "@material-ui/core";

import "./MemberModal.css";

const MemberModal = ({ member = {}, open, onSave, onClose }) => {
  const { state } = useContext(AppContext);
  const { csrf } = state;
  const [editedMember, setMember] = useState(member);
  const [pdls, setPdls] = useState([]);

  const getPdls = async () => {
    let res = await getAllPDLs(csrf);
    let promises = res.payload.data.map((member) => getMember(member.memberid));
    const results = await Promise.all(promises);
    const pdlArray = results.map((res) => res.payload.data);
    setPdls(pdlArray);
  };

  useEffect(() => {
    if (open && csrf) {
      getPdls();
    }
  }, [open, csrf, getPdls]);

  let date = new Date(editedMember.startDate);

  const onPdlChange = (event, newValue) => {
    setMember({
      ...editedMember,
      pdlId: newValue ? newValue.id : "",
    });
  };

  return (
    <Modal open={open} onClose={onClose}>
      <div className="member-modal">
        <TextField
          id="member-email-input"
          label="Member Email"
          required
          className="halfWidth"
          placeholder="Creative Email"
          value={editedMember.workEmail ? editedMember.workEmail : ""}
          onChange={(e) =>
            setMember({ ...editedMember, workEmail: e.target.value })
          }
        />
        <TextField
          id="member-title-input"
          label="Member title"
          required
          className="halfWidth"
          placeholder="Glorious title"
          value={editedMember.title ? editedMember.title : ""}
          onChange={(e) =>
            setMember({ ...editedMember, title: e.target.value })
          }
        />
        <TextField
          id="member-location-input"
          label="Member location"
          required
          className="halfWidth"
          placeholder="Somewhere by the beach"
          value={editedMember.location ? editedMember.location : ""}
          onChange={(e) =>
            setMember({ ...editedMember, location: e.target.value })
          }
        />
        <TextField
          id="member-insperityId-input"
          label="InsperityId"
          required
          className="halfWidth"
          placeholder="Somewhere by the beach"
          value={editedMember.insperityId ? editedMember.insperityId : ""}
          onChange={(e) =>
            setMember({ ...editedMember, insperityId: e.target.value })
          }
        />
        <Autocomplete
          options={["", ...pdls]}
          value={pdls.find((pdl) => pdl.id === editedMember.pdlId) || ""}
          onChange={onPdlChange}
          getOptionLabel={(option) => option.name || ""}
          renderInput={(params) => (
            <TextField
              {...params}
              className="fullWidth"
              label="PDLs"
              placeholder="Change PDL"
            />
          )}
        />
        <MuiPickersUtilsProvider utils={DateFnsUtils}>
          <KeyboardDatePicker
            margin="normal"
            id="member-datepicker-dialog"
            required
            label="Start Date"
            format="MM/dd/yyyy"
            value={date}
            onChange={(e) => {
              setMember({ ...editedMember, startDate: e });
            }}
            KeyboardButtonProps={{
              "aria-label": "change date",
            }}
          />
        </MuiPickersUtilsProvider>
        {/* need supervisor property on member */}
        {/* <TextField
          id="member-supervisor-input"
          label="supervisor"
          required
          className="halfWidth"
          placeholder="Somewhere by the beach"
          value={editedMember.supervisor ? editedMember.supervisor : ""}
          onChange={(e) =>
            setMember({ ...editedMember, supervisor: e.target.value })
          }
        /> */}
        <div className="member-modal-actions fullWidth">
          <Button onClick={onClose} color="secondary">
            Cancel
          </Button>
          <Button
            onClick={async () => {
              onSave(editedMember);
              await updateMember(editedMember);
            }}
            color="primary"
          >
            Save Member
          </Button>
        </div>
      </div>
    </Modal>
  );
};

export default MemberModal;
