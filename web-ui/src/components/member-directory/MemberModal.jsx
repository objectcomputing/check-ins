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
    setPdls(pdlArray.sort(compare));
  };

  const onSupervisorChange = (event, newValue) => {
    setMember({
      ...editedMember,
        supervisorid: newValue ? newValue.id : "",
      });
   };

 function compare(a, b) {
     var splitA = a.name.split(" ");
     var splitB = b.name.split(" ");
     var lastA = splitA[splitA.length - 1];
     var lastB = splitB[splitB.length - 1];

     if (lastA < lastB) return -1;
     if (lastA > lastB) return 1;
     return 0;
 }

  useEffect(() => {
    if (open && csrf) {
      getPdls();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open, csrf]);

  if(!editedMember.startDate) {
    setMember({ ...editedMember, startDate: new Date() });
  }
  const date = new Date(editedMember.startDate);

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
            id="member-name-input"
            label="Name"
            required
            className="halfWidth"
            placeholder="Full Name"
            value={editedMember.name ? editedMember.name : ""}
            onChange={(e) =>
                setMember({ ...editedMember, name: e.target.value })}
        />
        <TextField
          id="member-email-input"
          label="Member Email"
          required
          className="halfWidth"
          placeholder="Company Email"
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
          placeholder="Official Title"
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
          placeholder="Physical Location"
          value={editedMember.location ? editedMember.location : ""}
          onChange={(e) =>
            setMember({ ...editedMember, location: e.target.value })
          }
        />
        <TextField
          id="member-employeeId-input"
          label="EmployeeId"
          required
          className="halfWidth"
          placeholder="Employee Identifier"
          value={editedMember.employeeId ? editedMember.employeeId : ""}
          onChange={(e) =>
            setMember({ ...editedMember, employeeId: e.target.value })
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
              label="PDL"
              placeholder="Change PDL"
            />
          )}
        />
        <Autocomplete
          options={["", ...memberProfiles]}
          value={memberProfiles.find((memberProfile) => memberProfile.id === editedMember.supervisorid) || ""}
          onChange={onSupervisorChange}
          getOptionLabel={(option) => option.name || ""}
          renderInput={(params) => (
            <TextField
              {...params}
              className="fullWidth"
              label="Supervisors"
              placeholder="Change Supervisor"
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
            Save Member
          </Button>
        </div>
      </div>
    </Modal>
  );
};

export default MemberModal;
