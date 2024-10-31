import fileDownload from 'js-file-download';
import React, { useContext, useState } from 'react';

import DownloadIcon from '@mui/icons-material/FileDownload';
import PersonIcon from '@mui/icons-material/Person';
import { Button, Grid, TextField } from '@mui/material';
import { styled } from '@mui/material/styles';

import AdminMemberCard from '../../member-directory/AdminMemberCard';
import MemberModal from '../../member-directory/MemberModal';
import {
  createMember,
  reportAllMembersCsv
} from '../../../api/member';
import { AppContext } from '../../../context/AppContext';
import { UPDATE_MEMBER_PROFILES, UPDATE_TOAST } from '../../../context/actions';
import {
  selectHasProfileReportPermission,
  selectNormalizedMembers,
  selectNormalizedMembersAdmin,
  selectHasCreateMembersPermission,
} from '../../../context/selectors';
import { useQueryParameters } from '../../../helpers/query-parameters';

import './Users.css';

const PREFIX = 'Users';
const classes = {
  page: `${PREFIX}-page`,
  search: `${PREFIX}-search`,
  searchInput: `${PREFIX}-searchInput`,
  members: `${PREFIX}-members`
};

const Root = styled('div')({
  '& .MuiGrid-spacing-xs-3 > .MuiGrid-item': {
    padding: '12px'
  },
  '& .MuiGrid-root.MuiGrid-container': {
    width: 'calc(100% + 24px)',
    margin: '-12px'
  },
  [`& .${classes.search}`]: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center'
  },
  [`& .${classes.searchInput}`]: {
    width: '20em'
  },
  [`& .${classes.members}`]: {
    display: 'flex',
    flexWrap: 'wrap',
    justifyContent: 'space-evenly',
    width: '100%'
  }
});

const Users = () => {
  const { state, dispatch } = useContext(AppContext);
  const { csrf, memberProfiles, userProfile } = state;
  const [open, setOpen] = useState(false);
  const [searchText, setSearchText] = useState('');
  const [includeTerminated, setIncludeTerminated] = useState(false);
  const handleIncludeTerminated = () => {
    setIncludeTerminated(!includeTerminated);
  };

  const isAdmin =
    userProfile && userProfile.role && userProfile.role.includes('ADMIN');

  const normalizedMembers =
    isAdmin && includeTerminated
      ? selectNormalizedMembersAdmin(state, searchText)
      : selectNormalizedMembers(state, searchText);

  useQueryParameters([
    {
      name: 'addUser',
      default: false,
      value: open,
      setter: setOpen
    },
    {
      name: 'includeTerminated',
      default: false,
      value: includeTerminated,
      setter: setIncludeTerminated
    },
    {
      name: 'search',
      default: '',
      value: searchText,
      setter: setSearchText
    }
  ]);

  const handleOpen = () => setOpen(true);

  const handleClose = () => setOpen(false);

  const createMemberCards = normalizedMembers.map((member, index) => {
    return (
      <AdminMemberCard
        key={`${member.name}-${member.id}`}
        index={index}
        member={member}
      />
    );
  });

  const downloadMembers = async () => {
    let res = await reportAllMembersCsv(csrf);
    if (res?.error) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'error',
          toast: 'Hmm...Something went wrong.'
        }
      });
    } else {
      fileDownload(res?.payload?.data, 'members.csv');

      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'success',
          toast: `Member export has been saved!`
        }
      });
    }
  };

  return (
    <Root>
      <div className="user-page">
        <Grid container spacing={3}>
          <Grid item xs={12} className={classes.search}>
            <TextField
              className={classes.searchInput}
              label="Select employees..."
              placeholder="Member Name"
              value={searchText}
              onChange={e => {
                setSearchText(e.target.value);
              }}
            />
            {selectHasCreateMembersPermission(state) && (
              <div className="add-member">
                <Button startIcon={<PersonIcon />} onClick={handleOpen}>
                  Add Member
                </Button>
                {selectHasProfileReportPermission(state) && (
                  <Button
                    startIcon={<DownloadIcon />}
                    onClick={downloadMembers}
                  >
                    Download Members
                  </Button>
                )}

                <MemberModal
                  member={{}}
                  open={open}
                  onClose={handleClose}
                  onSave={async member => {
                    if (
                      member.location &&
                      member.firstName &&
                      member.lastName &&
                      member.startDate &&
                      member.title &&
                      member.workEmail &&
                      csrf
                    ) {
                      const res = await createMember(member, csrf);
                      const data =
                        res.payload?.data && !res.error
                          ? res.payload.data
                          : null;
                      if (data) {
                        dispatch({
                          type: UPDATE_MEMBER_PROFILES,
                          payload: [...memberProfiles, data]
                        });
                      }
                      handleClose();
                    }
                  }}
                />
              </div>
            )}
          </Grid>
          <div className="checkbox-row">
            <label htmlFor="includeterminated">
              Include Terminated Members
            </label>
            <input
              id="includeterminated"
              checked={includeTerminated}
              onChange={handleIncludeTerminated}
              type="checkbox"
            />
          </div>
          <Grid item className={classes.members}>
            {createMemberCards}
          </Grid>
        </Grid>
      </div>
    </Root>
  );
};

export default Users;
