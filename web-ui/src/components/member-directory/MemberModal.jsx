import React, { useContext, useState } from "react";
import { AppContext } from "../../context/AppContext";
import { selectOrderedPdls } from "../../context/selectors";

import { Modal, TextField } from "@material-ui/core";
import Autocomplete from "@material-ui/lab/Autocomplete";
import {
  KeyboardDatePicker,
  MuiPickersUtilsProvider,
} from "@material-ui/pickers";
import DateFnsUtils from "@date-io/date-fns";
import { format } from "date-fns";
import { Button } from "@material-ui/core";

import "./MemberModal.css";

const MemberModal = ({ member = {}, open, onSave, onClose }) => {
  const { state } = useContext(AppContext);
  const { memberProfiles } = state;
  const [editedMember, setMember] = useState(member);
  const sortedPdls = selectOrderedPdls(state);
  const onSupervisorChange = (event, newValue) => {
    setMember({
      ...editedMember,
      supervisorid: newValue ? newValue.id : "",
    });
  };

  if (!editedMember.startDate) {
    setMember({ ...editedMember, startDate: new Date() });
  }

  const terminationDate =
    editedMember && editedMember.terminationDate
      ? new Date(editedMember.terminationDate)
      : null;
  const startDate = new Date(editedMember.startDate);

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
          label="First Name"
          required
          className="halfWidth"
          placeholder="First Name"
          value={editedMember.firstName ? editedMember.firstName : ""}
          onChange={(e) =>
            setMember({ ...editedMember, firstName: e.target.value })
          }
        />
        <TextField
          id="member-name-input"
          label="Middle Name"
          className="halfWidth"
          placeholder="Middle Name"
          value={editedMember.middleName ? editedMember.middleName : ""}
          onChange={(e) =>
            setMember({ ...editedMember, middleName: e.target.value })
          }
        />
        <TextField
          id="member-name-input"
          label="Last Name"
          required
          className="halfWidth"
          placeholder="Last Name"
          value={editedMember.lastName ? editedMember.lastName : ""}
          onChange={(e) =>
            setMember({ ...editedMember, lastName: e.target.value })
          }
        />
        <TextField
          id="member-name-input"
          label="Suffix"
          className="halfWidth"
          placeholder="Suffix"
          value={editedMember.suffix ? editedMember.suffix : ""}
          onChange={(e) =>
            setMember({ ...editedMember, suffix: e.target.value })
          }
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
          options={sortedPdls && ["", ...sortedPdls]}
          value={
            (sortedPdls &&
              sortedPdls.find((pdl) => pdl?.id === editedMember.pdlId)) ||
            ""
          }
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
          value={
            memberProfiles.find(
              (memberProfile) => memberProfile.id === editedMember.supervisorid
            ) || ""
          }
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
            id="start-datepicker-dialog"
            required
            label="Start Date"
            format="MM/dd/yyyy"
            value={startDate}
            onChange={(e) => {
              setMember({ ...editedMember, startDate: e });
            }}
            KeyboardButtonProps={{
              "aria-label": "Change Date",
            }}
          />
          <KeyboardDatePicker
            margin="normal"
            id="termination-datepicker-dialog"
            label="Termination Date"
            clearable
            format="MM/dd/yyyy"
            value={terminationDate}
            placeholder={format(new Date(), "MM/dd/yyy")}
            onChange={(date) => {
              setMember({ ...editedMember, terminationDate: date });
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
