import React, {useContext, useEffect, useState} from "react";

import MemberSummaryCard from "../components/member-directory/MemberSummaryCard";
import {createMember} from "../api/member";
import {AppContext, UPDATE_MEMBER_PROFILES} from "../context/AppContext";

import {Button, TextField} from "@material-ui/core";
import PersonIcon from "@material-ui/icons/Person";

import "./DirectoryPage.css";
import MemberModal from "../components/member-directory/MemberModal";

const DirectoryPage = () => {
    const {state, dispatch} = useContext(AppContext);
    const {csrf, memberProfiles, userProfile} = state;

    const [members, setMembers] = useState(
        memberProfiles &&
        memberProfiles.sort((a, b) => {
            const aPieces = a.name.split(" ").slice(-1);
            const bPieces = b.name.split(" ").slice(-1);
            return aPieces.toString().localeCompare(bPieces);
        })
    );

    const [open, setOpen] = useState(false);
    const [searchText, setSearchText] = useState("");

    const date = member.startDate ? new Date(member.startDate) : new Date();

    const isAdmin =
        userProfile && userProfile.role && userProfile.role.includes("ADMIN");

    useEffect(() => {
        setMembers(memberProfiles);
    }, [memberProfiles]);

    useEffect(() => {
        if (!member.startDate) {
            member.startDate = date;
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

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
                    value={searchText}
                    onChange={(e) => {
                        setSearchText(e.target.value);
                    }}
                />
                {isAdmin && (
                    <div className="add-member">
                        <Button startIcon={<PersonIcon/>} onClick={handleOpen}>Add Member</Button>

                        <MemberModal
                            member={member}
                            open={open}
                            onClose={handleClose}

                            onSave={async (member) => {
                                if (
                                    member.location &&
                                    member.name &&
                                    member.startDate &&
                                    member.title &&
                                    member.workEmail &&
                                    csrf
                                ) {
                                    let res = await createMember(member, csrf);

                                    let data =
                                        res.payload && res.payload.data && !res.error
                                            ? res.payload.data
                                            : null;
                                    if (data) {
                                        dispatch({
                                            type: UPDATE_MEMBER_PROFILES,
                                            payload: [...memberProfiles, data],
                                        });
                                    }
                                    handleClose();
                                }
                            }}
                        />
                    </div>
                )}
            </div>
            <div className="members">{createMemberCards}</div>
        </div>
    );
};

export default DirectoryPage;
