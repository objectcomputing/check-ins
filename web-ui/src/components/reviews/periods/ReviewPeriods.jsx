import React, {useCallback, useContext, useEffect, useState} from "react";
import { styled } from '@mui/material/styles';
import PropTypes from "prop-types";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Typography from "@mui/material/Typography";
import Divider from '@mui/material/Divider';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import ListItemAvatar from '@mui/material/ListItemAvatar';
import Modal from '@mui/material/Modal';
import Skeleton from '@mui/material/Skeleton';
import TextField from '@mui/material/TextField';
import Tooltip from '@mui/material/Tooltip';
import Avatar from '@mui/material/Avatar';
import WorkIcon from '@mui/icons-material/Work';
import IconButton from '@mui/material/IconButton';
import ArchiveIcon from '@mui/icons-material/Archive';
import UnarchiveIcon from '@mui/icons-material/Unarchive';
import DeleteIcon from '@mui/icons-material/Delete';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import { getReviewPeriods, createReviewPeriod, updateReviewPeriod, removeReviewPeriod } from "../../../api/reviewperiods.js";
import { findSelfReviewRequestsByPeriodAndTeamMember } from "../../../api/feedback.js";
import { AppContext } from "../../../context/AppContext";
import {
  UPDATE_REVIEW_PERIODS,
  ADD_REVIEW_PERIOD,
} from "../../../context/actions";
import {
  selectCsrfToken,
  selectUserProfile,
  selectCurrentUserId,
  selectReviewPeriod,
  selectReviewPeriods,
} from "../../../context/selectors";

const propTypes = {
  message: PropTypes.string,
  onSelect: PropTypes.func,
};
const displayName = "ReviewPeriods";

const PREFIX = displayName;
const classes = {
  actionButtons: `${PREFIX}-actionButtons`,
  headerContainer: `${PREFIX}-headerContainer`,
  periodModal: `${PREFIX}-periodModal`,
};

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

const Root = styled('div')(({theme}) => ({
  [`& .${classes.actionButtons}`]: {
    margin: "0.5em 0 0 1em",
    ['@media (max-width:820px)']: { // eslint-disable-line no-useless-computed-key
      padding: "0",
    },
  },
  [`& .${classes.headerContainer}`]: {
    display: "flex",
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    margin: "0 2em 20px 2em",
  },
}));

const ReviewPeriods = ({ onPeriodSelected, mode }) => {
  const { state, dispatch } = useContext(AppContext);
  const userProfile = selectUserProfile(state);
  const currentUserId = selectCurrentUserId(state);
  const csrf = selectCsrfToken(state);
  const periods = selectReviewPeriods(state);
  const [selfReviews, setSelfReviews] = useState(null);
  const [periodToAdd, setPeriodToAdd] = useState({ name: "", open: true });
  const isAdmin = userProfile?.role?.includes("ADMIN");
  const [open, setOpen] = useState(false)
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [toDelete, setToDelete] = useState(null);

  const handleOpen = useCallback(() => setOpen(true), [setOpen]);
  const handleClose = useCallback(() => setOpen(false), [setOpen]);

  const findPeriodByName = useCallback((name) => periods.find((period)=>period?.name.toUpperCase() === name.toUpperCase()), [periods]);

  const addReviewPeriod = useCallback(async (name) => {
    if (!csrf) {
      return;
    }
    const alreadyExists = findPeriodByName(periodToAdd?.name);
    if (!alreadyExists) {
      handleOpen();
      const res = await createReviewPeriod(
        periodToAdd,
        csrf
      );
      const data =
        res && res.payload && res.payload.data ? res.payload.data : null;
      data && dispatch({ type: ADD_REVIEW_PERIOD , payload: data});
    }
    handleClose();
    setPeriodToAdd({ name: "", open: true });
  }, [csrf, dispatch, periodToAdd, handleOpen, handleClose, setPeriodToAdd, findPeriodByName]);

  const toggleReviewPeriod = useCallback(async (id) => {
    if (!csrf) {
      return;
    }
    const toUpdate = selectReviewPeriod(state, id);
    toUpdate.open = !toUpdate?.open;
    const res = await updateReviewPeriod(
      toUpdate,
      csrf
    );
    const data =
      res && res.payload && res.payload.data ? res.payload.data : null;
    data && dispatch({ type: UPDATE_REVIEW_PERIODS, payload: [...periods]});
  }, [csrf, state, periods, dispatch]);

  const getSecondaryLabel = useCallback((periodId) => {
      if(mode === 'self') {
        if(selectReviewPeriod(state, periodId)?.open) {
          if(!selfReviews || !selfReviews[periodId] || selfReviews[periodId] === null) {
            return "Click to start your review."
          } else {
            if(selfReviews[periodId].status.toUpperCase() === 'SUBMITTED') {
              return "Your review has been submitted. Thank you!"
            } else {
              return "Click to finish your review."
            }
          }
        } else {
          return "This review period is closed.";
        }
      }
    }, [selfReviews, state, mode]);

  const handleConfirmClose = useCallback(() => {
    setToDelete(null);
    setConfirmOpen(false);
  }, [setToDelete, setConfirmOpen]);

  const deleteReviewPeriod = useCallback(async () => {
    if (!csrf) {
      return;
    }

    await removeReviewPeriod(
      toDelete,
      csrf
    );
    dispatch({ type: UPDATE_REVIEW_PERIODS, payload: periods.filter((period) => period?.id !== toDelete) });
    handleConfirmClose();
  }, [csrf, periods, dispatch, toDelete, handleConfirmClose]);

  useEffect(() => {
    const getAllReviewPeriods = async () => {
      setLoading(true);
      const res = await getReviewPeriods(csrf);
      const data =
        res &&
        res.payload &&
        res.payload.data &&
        res.payload.status === 200 &&
        !res.error
          ? res.payload.data
          : null;
      if (data) {
        dispatch({ type: UPDATE_REVIEW_PERIODS, payload: data});
        setLoading(false);
      }
    };
    if (csrf) {
      getAllReviewPeriods();
    }
  }, [csrf, dispatch, setLoading]);

  useEffect(() => {
      const getSelfReviews = async () => {
        let reviews = {};
        Promise.all(periods.map(async (period) => {
            const res = await findSelfReviewRequestsByPeriodAndTeamMember(period, currentUserId, csrf);
            const data =
              res &&
              res.payload &&
              res.payload.data &&
              res.payload.status === 200 &&
              !res.error
                ? res.payload.data
                : null;
            if (data) {
              reviews[period.id] = data[0];
            }
        })).then(()=> setSelfReviews(reviews));
      };
      if (csrf && periods && periods.length > 0 && currentUserId && selfReviews == null) {
        getSelfReviews();
      }
    }, [csrf, periods, currentUserId, selfReviews]);

  const onPeriodClick = useCallback((id) => {
    if(selectReviewPeriod(state, id)?.open) {
      onPeriodSelected(id);
    }
  }, [state, onPeriodSelected]);

  const confirmDelete = useCallback((id) => {
    setToDelete(id);
    setConfirmOpen(true);
  }, [setToDelete, setConfirmOpen]);

  return (
    <Root>
      <div className={classes.headerContainer}>
        <Typography variant="h5">Review Periods</Typography>
        {isAdmin ?
           (<Button onClick={handleOpen} className={classes.actionButtons} variant="contained" color="primary">
              Add Review Period
            </Button>) : null
        }
      </div>
      <List sx={{ width: '100%', bgcolor: 'background.paper' }}>
      {loading ? (
          <>
          <ListItem key="skeleton-period">
            <ListItemAvatar>
              <Skeleton animation="wave" variant="circular" width={40} height={40} />
            </ListItemAvatar>
            <ListItemText primary={(<Skeleton variant="text" sx={{ fontSize: '1rem' }} />)} secondary={(<Skeleton variant="text" sx={{ fontSize: '1rem' }} />)} />
          </ListItem>
          <Divider key="skeleton-divider"/>
          </>
        ) : periods.sort((a, b) => {
          return (!!a.open === !!b.open) ? ('' + a.name).localeCompare(b.name) : !!a.open ? -1 : 1;
        }).map(({name, open, id}, i) => (
          <>
          <ListItem
            secondaryAction={ isAdmin &&
              (<>
              <Tooltip title={open ? "Archive" :  "Unarchive"}>
                <IconButton onClick={() => toggleReviewPeriod(id)} aria-label={open ? "Archive" :  "Unarchive"}>
                  {open ? (<ArchiveIcon />) : (<UnarchiveIcon />)}
                </IconButton>
              </Tooltip>
              <Tooltip title="Delete">
                <IconButton onClick={() => confirmDelete(id)} edge="end" aria-label="Delete">
                  <DeleteIcon />
                </IconButton>
              </Tooltip>
              </>)
            }
            key={`period-${id}`}
          >
            <ListItemAvatar key={`period-lia-${id}`} onClick={() => onPeriodClick(id)}>
              <Avatar>
                <WorkIcon />
              </Avatar>
            </ListItemAvatar>
            <ListItemText key={`period-lit-${id}`} onClick={() => onPeriodClick(id)} primary={name + (open ? " - Open" : "")} secondary={getSecondaryLabel(id)} />
          </ListItem>
          <Divider key={`divider-${id}`}/>
          </>
        ))
      }
      </List>
      <Modal open={open} onClose={handleClose}>
        <Box sx={modalStyles}>
          <TextField
            className="fullWidth"
            id="reviewPeriod-name-input"
            label="Name"
            placeholder="Period Name"
            required
            value={periodToAdd.name ? periodToAdd.name : ""}
            onChange={(e) =>
              setPeriodToAdd({ ...periodToAdd, name: e.target.value })
            }
          />
          <TextField
            className="fullWidth"
            id="reviewPeriod-template-input"
            label="Template ID"
            placeholder="Template UUID"
            required
            value={periodToAdd.reviewTemplateId ? periodToAdd.reviewTemplateId : ""}
            onChange={(e) =>
              setPeriodToAdd({ ...periodToAdd, reviewTemplateId: e.target.value })
            }
          />
          <TextField
            className="fullWidth"
            id="reviewPeriod-self-template-input"
            label="Self Review Template ID"
            placeholder="Template UUID"
            required
            value={periodToAdd.selfReviewTemplateId ? periodToAdd.selfReviewTemplateId : ""}
            onChange={(e) =>
              setPeriodToAdd({ ...periodToAdd, selfReviewTemplateId: e.target.value })
            }
          />
          <div className="fullWidth" style={modalActionStyles}>
            <Button onClick={handleClose} color="secondary">
              Cancel
            </Button>
            <Button onClick={() => addReviewPeriod(periodToAdd.name)} color="primary">
              Save Review Period
            </Button>
          </div>
        </Box>
      </Modal>
      <Dialog
        open={confirmOpen}
        onClose={handleConfirmClose}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">
          {"Delete this review period?"}
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            Are you sure that you would like to delete period {selectReviewPeriod(state, toDelete)?.name}?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleConfirmClose}>No</Button>
          <Button onClick={deleteReviewPeriod} autoFocus>
            Yes
          </Button>
        </DialogActions>
      </Dialog>
    </Root>
  );
};

ReviewPeriods.propTypes = propTypes;
ReviewPeriods.displayName = displayName;

export default ReviewPeriods;
