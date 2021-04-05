import React, { useContext, useEffect, useState } from "react";

import { getAllPDLs, getMember } from "../../api/member";
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
  const { csrf, memberProfiles } = state;
  const [editedMember, setMember] = useState(member);
  const [pdls, setPdls] = useState([]);

  const getPdls = async () => {
    let res = await getAllPDLs(csrf);
    let promises = res.payload.data.map((member) => getMember(member.memberid, csrf));
    const results = await Promise.all(promises);
    const pdlArray = results.map((res) => res.payload.data);
    setPdls(pdlArray);
  };

  const onSupervisorChange = (event, newValue) => {
    setMember({
      ...editedMember,
        supervisorid: newValue ? newValue.id : "",
      });
   };

  useEffect(() => {
    if (open && csrf) {
      getPdls();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open, csrf]);

  if(!editedMember.terminateDate) {
    setMember({ ...editedMember, terminateDate: new Date() });
  }
  const date = new Date(editedMember.terminateDate);

  const onPdlChange = (event, newValue) => {
    setMember({
      ...editedMember,
      pdlId: newValue ? newValue.id : "",
    });
  };

  return (
    <Modal open={open} onClose={onClose}>
      <div className="member-modal">
        <MuiPickersUtilsProvider utils={DateFnsUtils}>
          <KeyboardDatePicker
            margin="normal"
            id="member-datepicker-dialog"
            required
            label="Termination Date"
            format="MM/dd/yyyy"
            value={date}
            onChange={(e) => {
              setMember({ ...editedMember, terminateDate: e });
            }}
            KeyboardButtonProps={{
              "aria-label": "Change Date",
            }}
          />
        </MuiPickersUtilsProvider>
        <div className="member-modal-actions fullWidth">
          <Button onClick={onClose} color="secondary">
            Cancel
          </Button>
          <Button
            onClick={async () => {
              onSave(editedMember);
            }}
            color="primary"
          >
            Save
          </Button>
        </div>
      </div>
    </Modal>
  );
};

export default MemberModal;