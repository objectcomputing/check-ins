import React, {useContext,  useState} from "react";

import {AppContext} from "../../context/AppContext";
import {UPDATE_GUILDS, UPDATE_TOAST} from "../../context/actions";
import EditGuildModal from "./EditGuildModal";

import {
    Card, 
    CardActions, 
    CardContent, 
    CardHeader, 
    Dialog, 
    DialogContentText, 
    DialogTitle, 
    DialogContent, 
    DialogActions, 
    Button
} from "@material-ui/core";
import PropTypes from "prop-types";
import {deleteGuild, updateGuild} from "../../api/guild.js";
import SplitButton from "../split-button/SplitButton";

import {makeStyles} from "@material-ui/core/styles";
const useStyles = makeStyles((theme) => ({
  card: {
    width: "340px",
    display: "flex",
    flexDirection: "column",
    justifyContent: "space-between",
  },
  header: {
    width: "100%",
  },
  title: {
    overflow: "hidden",
    textOverflow: "ellipsis",
    whiteSpace: "nowrap",
  }
}));

const propTypes = {
    guild: PropTypes.shape({
        id: PropTypes.string,
        name: PropTypes.string,
        description: PropTypes.string,
    }),
};

const displayName = "GuildSummaryCard";

const GuildSummaryCard = ({guild, index}) => {
    const classes = useStyles();
    const {state, dispatch} = useContext(AppContext);
    const {guilds, userProfile, csrf} = state;
    const [open, setOpen] = useState(false);
    const [openDelete, setOpenDelete] = useState(false);
    const isAdmin =
        userProfile && userProfile.role && userProfile.role.includes("ADMIN");

    let leads =
        guild.guildMembers == null
            ? null
            : guild.guildMembers.filter((guildMember) => guildMember.lead);
    let nonLeads =
        guild.guildMembers == null
            ? null
            : guild.guildMembers.filter((guildMember) => !guildMember.lead);

    const isGuildLead =
        leads === null
            ? false
            : leads.some((lead) => lead.memberId === userProfile.memberProfile.id);

    const handleOpen = () => setOpen(true);
    const handleClose = () => setOpen(false);

    const handleOpenDeleteConfirmation = () => setOpenDelete(true);
    const handleCloseDeleteConfirmation = () => setOpenDelete(false);

    const deleteAGuild = async (id) => {
        if (id && csrf) {
            const result = await deleteGuild(id, csrf);
            if (result && result.payload && result.payload.status === 200) {
                window.snackDispatch({
                    type: UPDATE_TOAST,
                    payload: {
                        severity: "success",
                        toast: "Guild deleted",
                    },
                });
                let newGuilds = guilds.filter((guild) => {
                    return guild.id !== id;
                });
                dispatch({
                    type: UPDATE_GUILDS,
                    payload: newGuilds,
                });
            }
        }
    };

    const options =
        isAdmin || isGuildLead ? ["Edit Guild", "Delete Guild"] : ["Edit Guild"];

    const handleAction = (e, index) => {
        if (index === 0) {
            handleOpen();
        } else if (index === 1) {
            handleOpenDeleteConfirmation();
        }
    };

    return (
        <Card className={classes.card}>
            <CardHeader classes={{
              content: classes.header,
              title: classes.title,
              subheader: classes.title,
            }} title={guild.name} subheader={guild.description}/>
            <CardContent>
                {guild.guildMembers == null ? (
                    <React.Fragment>
                    <strong>Guild Leads: </strong>None Assigned
                    <br/>
                    <strong>Guild Members: </strong>None Assigned
                    </React.Fragment>
                    ) : (
                    <React.Fragment>
                    <strong>Guild Leads: </strong>
                {leads.map((lead, index) => {
                    return index !== leads.length - 1 ? `${lead.name}, ` : lead.name;
                })}
                    <br/>
                    <strong>Guild Members: </strong>
                {nonLeads.map((member, index) => {
                    return index !== nonLeads.length - 1
                    ? `${member.name}, `
                    : member.name;
                })}
                    </React.Fragment>

                )}
            </CardContent>
            <CardActions>
                {(isAdmin || isGuildLead) && (
                <>
                    <SplitButton options={options} onClick={handleAction} />
                    <Dialog
                    open={openDelete}
                    onClose={handleCloseDeleteConfirmation}
                    aria-labelledby="alert-dialog-title"
                    aria-describedby="alert-dialog-description"
                    >
                        <DialogTitle id="alert-dialog-title">Delete guild?</DialogTitle>
                        <DialogContent>
                            <DialogContentText id="alert-dialog-description">
                            Are you sure you want to delete the guild?
                            </DialogContentText>
                        </DialogContent>
                        <DialogActions>
                            <Button onClick={handleCloseDeleteConfirmation} color="primary">
                            Cancel
                            </Button>
                            <Button disabled onClick={deleteAGuild} color="primary" autoFocus>
                            Yes
                            </Button>
                        </DialogActions>
                    </Dialog>
                </>
                )}
            </CardActions>
            <EditGuildModal
                guild={guild}
                open={open}
                onClose={handleClose}
                onSave={async (editedGuild) => {
                  let res = await updateGuild(editedGuild, csrf);
                  let data = res.payload && res.payload.data && !res.error
                               ? res.payload.data
                               : null;
                  if(data) {
                    const copy = [...guilds];
                    copy[index] = data;
                    dispatch({
                        type: UPDATE_GUILDS,
                        payload: copy,
                    });
                  }
                }}
                headerText='Edit Your Guild'
            />
        </Card>
    );
};

GuildSummaryCard.displayName = displayName;
GuildSummaryCard.propTypes = propTypes;

export default GuildSummaryCard;