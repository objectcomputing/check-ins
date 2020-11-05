import React, { useContext, useEffect, useState } from "react";

import MemberSummaryCard from "../components/member-directory/MemberSummaryCard";
import { createMember } from "../api/member";
import {
  AppContext,
  UPDATE_MEMBER_PROFILES,
  UPDATE_TOAST,
} from "../context/AppContext";

import { Button, Modal, TextField } from "@material-ui/core";
import GroupIcon from "@material-ui/icons/Group";
import {
  KeyboardDatePicker,
  MuiPickersUtilsProvider,
} from "@material-ui/pickers";
import DateFnsUtils from "@date-io/date-fns";

import "./DirectoryPage.css";

const DirectoryPage = () => {
  const { state, dispatch } = useContext(AppContext);
  const { memberProfiles, userProfile } = state;

  const [members, setMembers] = useState(
    memberProfiles &&
      memberProfiles.sort((a, b) => {
        const aPieces = a.name.split(" ").slice(-1);
        const bPieces = b.name.split(" ").slice(-1);
        return aPieces.toString().localeCompare(bPieces);
      })
  );
  const [member, setMember] = useState({});
  const [open, setOpen] = useState(false);
  const [searchText, setSearchText] = useState("");

  const { location, name, startDate, title, workEmail } = member;

  const date = member.startDate ? new Date(member.startDate) : new Date();

  const isAdmin =
    userProfile && userProfile.role && userProfile.role.includes("ADMIN");

  useEffect(() => {
    setMembers(memberProfiles);
  }, [memberProfiles]);

  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);

  const createMemberCards = members.map((member, index) => {
    if (member.name.toLowerCase().includes(searchText.toLowerCase())) {
      return (
        <MemberSummaryCard
          key={`${member.name}-${member.id}`}
          index={index}
          member={member}
        />
      );
    } else return null;
  });

  return (
    <div className="directory-page">
      <div className="search">
        <TextField
          className="fullWidth"
          label="Search Members"
          placeholder="Member Name"
          style={{ marginBottom: "1rem" }}
          value={searchText}
          onChange={(e) => {
            setSearchText(e.target.value);
          }}
        />
        {isAdmin && (
          <div className="add-member">
            <GroupIcon />
            <Button onClick={handleOpen}>Add Member</Button>
          </div>
        )}
      </div>
      <Modal open={open} onClose={handleClose}>
        <div className="add-member-modal">
          <TextField
            id="add-member-name"
            label="Member Name"
            required
            className="halfWidth"
            placeholder="Name"
            value={member.name ? member.name : ""}
            onChange={(e) => setMember({ ...member, name: e.target.value })}
          />
          <TextField
            id="add-member-title"
            label="Member Title"
            required
            className="halfWidth"
            placeholder="Title"
            value={member.title ? member.title : ""}
            onChange={(e) => setMember({ ...member, title: e.target.value })}
          />
          <TextField
            id="add-member-email"
            label="Member Email"
            required
            className="halfWidth"
            placeholder="Email"
            value={member.workEmail ? member.workEmail : ""}
            onChange={(e) =>
              setMember({ ...member, workEmail: e.target.value })
            }
          />
          <TextField
            id="add-member-location"
            label="Member Location"
            required
            className="halfWidth"
            placeholder="Location"
            value={member.location ? member.location : ""}
            onChange={(e) => setMember({ ...member, location: e.target.value })}
          />
          {/* 
                    need to be able to find all PDLs
                    <TextField
                      id="add-member-pdl"
                      label="Member PDL"
                      required
                      className="halfWidth"
                      placeholder="PDL"
                      value={member.pdlid ? member.pdlid : ""}
                      onChange={(e) => setMember({ ...member, pdlid: e.target.value })}
                    /> */}
          <TextField
            id="add-member-insperityid"
            label="Member Insperity Id"
            className="halfWidth"
            placeholder="Insperity Id"
            value={member.insperityid ? member.insperityid : ""}
            onChange={(e) =>
              setMember({
                ...member,
                insperityid: e.target.value,
              })
            }
          />
          <TextField
            id="add-member-bioText"
            label="Member Bio Text"
            className="halfWidth"
            placeholder="Bio Text"
            value={member.bioText ? member.bioText : ""}
            onChange={(e) => setMember({ ...member, bioText: e.target.value })}
          />
          <MuiPickersUtilsProvider utils={DateFnsUtils}>
            <KeyboardDatePicker
              margin="normal"
              id="add-member-datepicker"
              required
              label="Start Date"
              format="MM/dd/yyyy"
              value={date}
              onChange={(e) => {
                setMember({ ...member, startDate: e });
              }}
              KeyboardButtonProps={{
                "aria-label": "change date",
              }}
            />
          </MuiPickersUtilsProvider>
          <div className="add-member-modal-actions fullWidth">
            <Button onClick={handleClose} color="secondary">
              Cancel
            </Button>
            <Button
              onClick={async () => {
                if (location && name && startDate && title && workEmail) {
                  let res = await createMember(member);
                  if (res && res.payload && res.payload.data) {
                    dispatch({
                      type: UPDATE_MEMBER_PROFILES,
                      payload: [...memberProfiles, member],
                    });
                  }
                  handleClose();
                } else {
                  dispatch({
                    type: UPDATE_TOAST,
                    payload: {
                      severity: "error",
                      toast: "Must fill in all required fields",
                    },
                  });
                }
              }}
              color="primary"
              startIcon={<GroupIcon />}
            >
              Add Member
            </Button>
          </div>
        </div>
      </Modal>
      <div className="members">{createMemberCards}</div>
    </div>
  );
};

export default DirectoryPage;
