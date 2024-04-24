import React, { useCallback, useContext, useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import {
  Avatar,
  Card,
  CardHeader,
  Collapse,
  Divider,
  IconButton,
  List,
  ListItem,
  ListItemAvatar,
  ListItemIcon,
  ListItemText,
  Menu,
  MenuItem,
  Tooltip,
  Typography
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import RemoveIcon from '@mui/icons-material/Remove';
import ExpandLessIcon from '@mui/icons-material/ExpandLess';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import HighlightOffIcon from '@mui/icons-material/HighlightOff';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import { getAvatarURL } from '../../api/api';

import MemberSelectorDialog, {
  FilterType
} from './member_selector_dialog/MemberSelectorDialog';
import DownloadIcon from '@mui/icons-material/FileDownload';
import { reportSelectedMembersCsv } from '../../api/member.js';
import { AppContext } from '../../context/AppContext.jsx';
import { selectCsrfToken } from '../../context/selectors.js';
import fileDownload from 'js-file-download';
import { UPDATE_TOAST } from '../../context/actions.js';

import './MemberSelector.css';

const propTypes = {
  /** An array of filters to apply to the member list in the dialog. */
  initialFilters: PropTypes.arrayOf(
    PropTypes.shape({
      type: PropTypes.oneOf(Object.values(FilterType)),
      value: PropTypes.oneOfType([
        PropTypes.string,
        PropTypes.number,
        PropTypes.bool
      ])
    })
  ),
  /** The members that are currently selected. Use to make this a controlled component. */
  selected: PropTypes.arrayOf(PropTypes.object),
  /** Listener for whenever the list of selected members changes. Passes the updated list as an argument. */
  onChange: PropTypes.func,
  /** Optional title for the card. Default is "Selected Members". */
  title: PropTypes.string,
  /** Set to true to use the outlined variant of the card. Default is the elevated variant. */
  outlined: PropTypes.bool,
  /** If true, include a button to export the list of members to a CSV file. False by default. */
  exportable: PropTypes.bool,
  /** Adjusts the height of the scrollable list of selected members (in pixels) */
  listHeight: PropTypes.number,
  /** If true, members cannot be added to or removed from the current selection. False by default. */
  disabled: PropTypes.bool,
  /** A custom class name to additionally apply to the top-level card */
  className: PropTypes.string,
  /** Custom style properties to apply to the top-level card */
  style: PropTypes.object
};

const MemberSelector = ({
  initialFilters = [],
  selected,
  onChange,
  title = 'Selected Members',
  outlined = false,
  exportable = false,
  listHeight = 400,
  disabled = false,
  className,
  style
}) => {
  const isControlled = !!selected && Array.isArray(selected);

  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const [selectedMembers, setSelectedMembers] = useState(
    isControlled ? selected : []
  );
  const [dialogOpen, setDialogOpen] = useState(false);
  const [expanded, setExpanded] = useState(true);
  const [menuAnchor, setMenuAnchor] = useState(null);
  const [filters, setFilters] = useState(initialFilters);

  const isFilteredByRole = filters.some(
    filter => filter.type === FilterType.ROLE
  );
  const roleFilter = filters.find(filter => filter.type === FilterType.ROLE);
  const memberDescriptor = isFilteredByRole ? roleFilter.value : 'members';

  // When the selected members change, fire the onChange event
  useEffect(() => {
    if (onChange) {
      onChange(selectedMembers);
    }
  }, [selectedMembers, onChange]);

  // If the selector is disabled, make sure the selector dialog is closed
  useEffect(() => {
    if (disabled) {
      setDialogOpen(false);
    }
  }, [disabled]);

  const addMembers = useCallback(
    membersToAdd => {
      const selected = [...selectedMembers, ...membersToAdd];
      setSelectedMembers(selected);
      setDialogOpen(false);
    },
    [selectedMembers]
  );

  const removeMember = useCallback(
    member => {
      const selected = [...selectedMembers];
      const indexToRemove = selected.findIndex(
        selectedMember => selectedMember.id === member.id
      );
      selected.splice(indexToRemove, 1);
      setSelectedMembers(selected);
    },
    [selectedMembers]
  );

  const downloadMemberCsv = useCallback(() => {
    if (!exportable) {
      return;
    }

    const memberIds = selectedMembers.map(member => member.id);
    reportSelectedMembersCsv(memberIds, csrf).then(res => {
      if (res && !res.error) {
        fileDownload(res.payload.data, 'members.csv');
        dispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: 'success',
            toast: 'Member export has been saved'
          }
        });
      } else {
        dispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: 'error',
            toast: 'Failed to export members to CSV'
          }
        });
      }
    });
  }, [selectedMembers, csrf, dispatch]);

  const clearMembers = useCallback(() => {
    setSelectedMembers([]);
  }, []);

  return (
    <>
      <Card
        variant={outlined ? 'outlined' : 'elevation'}
        className={'member-selector-card' + (className ? ` ${className}` : '')}
        style={style}
      >
        <CardHeader
          avatar={
            <IconButton onClick={() => setExpanded(!expanded)}>
              {expanded ? <ExpandLessIcon /> : <ExpandMoreIcon />}
            </IconButton>
          }
          title={
            <div className="member-selector-card-title-container">
              <Typography
                className="member-selector-card-title"
                variant="h5"
                noWrap
              >
                {title}
              </Typography>
              <Typography
                className="member-selector-card-count"
                variant="h6"
                color="gray"
              >
                ({selectedMembers.length})
              </Typography>
            </div>
          }
          action={
            <>
              <Tooltip title={`Add ${memberDescriptor}`} arrow>
                <IconButton
                  style={{ margin: '4px 8px 0 0' }}
                  onClick={() => setDialogOpen(true)}
                  disabled={disabled}
                >
                  <AddIcon />
                </IconButton>
              </Tooltip>
              <IconButton
                style={{ margin: '4px 8px 0 0' }}
                onClick={event => setMenuAnchor(event.currentTarget)}
              >
                <MoreVertIcon />
              </IconButton>
              <Menu
                anchorEl={menuAnchor}
                open={!!menuAnchor}
                onClose={() => setMenuAnchor(null)}
              >
                <MenuItem
                  onClick={() => {
                    setMenuAnchor(null);
                    clearMembers();
                  }}
                  disabled={disabled || !selectedMembers.length}
                >
                  <ListItemIcon>
                    <HighlightOffIcon fontSize="small" />
                  </ListItemIcon>
                  <ListItemText>Remove all</ListItemText>
                </MenuItem>
                {exportable && (
                  <MenuItem
                    onClick={() => {
                      setMenuAnchor(null);
                      downloadMemberCsv();
                    }}
                    disabled={!selectedMembers.length}
                  >
                    <ListItemIcon>
                      <DownloadIcon fontSize="small" />
                    </ListItemIcon>
                    <ListItemText>Download</ListItemText>
                  </MenuItem>
                )}
              </Menu>
            </>
          }
        />
        <Collapse in={expanded}>
          <Divider />
          <List
            dense
            role="list"
            sx={{ maxHeight: listHeight, overflow: 'auto' }}
          >
            {selectedMembers.length ? (
              selectedMembers.map(member => (
                <ListItem
                  key={member.id}
                  role="listitem"
                  secondaryAction={
                    <Tooltip title="Deselect member" arrow>
                      <IconButton
                        onClick={() => removeMember(member)}
                        disabled={disabled}
                      >
                        <RemoveIcon />
                      </IconButton>
                    </Tooltip>
                  }
                >
                  <ListItemAvatar>
                    <Avatar src={getAvatarURL(member.workEmail)} />
                  </ListItemAvatar>
                  <ListItemText
                    primary={
                      <Typography fontWeight="bold">{member.name}</Typography>
                    }
                    secondary={
                      <Typography color="textSecondary" component="h6">
                        {member.title}
                      </Typography>
                    }
                  />
                </ListItem>
              ))
            ) : (
              <ListItem>
                <ListItemText style={{ color: 'gray' }}>
                  No {memberDescriptor} selected
                </ListItemText>
              </ListItem>
            )}
          </List>
        </Collapse>
      </Card>
      <MemberSelectorDialog
        open={dialogOpen}
        initialFilters={filters}
        memberDescriptor={memberDescriptor}
        selectedMembers={selectedMembers}
        onClose={() => setDialogOpen(false)}
        onSubmit={membersToAdd => addMembers(membersToAdd)}
      />
    </>
  );
};

MemberSelector.propTypes = propTypes;

export default MemberSelector;
