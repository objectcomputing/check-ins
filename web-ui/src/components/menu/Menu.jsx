import React, { useContext, useEffect, useRef, useState } from 'react';

import { postEmployeeHours } from '../../api/hours';
import {
  selectCanViewFeedbackAnswerPermission,
  selectCanViewFeedbackRequestPermission,
  selectCanViewReviewPeriodPermission,
  selectCsrfToken,
  selectHasAnniversaryReportPermission,
  selectHasBirthdayReportPermission,
  selectHasCheckinsReportPermission,
  selectHasReportPermission,
  selectHasSkillsReportPermission,
  selectHasTeamSkillsReportPermission,
  selectIsAdmin
} from '../../context/selectors';
import { UPDATE_TOAST } from '../../context/actions';

import { useLocation, Link } from 'react-router-dom';
import { AppContext } from '../../context/AppContext';
import { getAvatarURL } from '../../api/api';

import MenuIcon from '@mui/icons-material/Menu';
import { useTheme } from '@mui/material/styles';
import {
  AppBar,
  Avatar,
  Button,
  CssBaseline,
  Collapse,
  Drawer,
  IconButton,
  List,
  ListItemButton,
  ListItemText,
  Modal,
  Toolbar
} from '@mui/material';
import './Menu.css';

const PREFIX = 'Menu';
const classes = {
  root: `${PREFIX}-root`,
  drawer: `${PREFIX}-drawer`,
  appBar: `${PREFIX}-appBar`,
  menuButton: `${PREFIX}-menuButton`,
  drawerPaper: `${PREFIX}-drawerPaper`,
  content: `${PREFIX}-content`,
  listItem: `${PREFIX}-listItem`,
  listStyle: `${PREFIX}-listStyle`,
  nested: `${PREFIX}-nested`,
  subListItem: `${PREFIX}-subListItem`
};

const adminLinks = [
  ['/admin/permissions', 'Permissions'],
  ['/admin/roles', 'Roles'],
  ['/admin/users', 'Users'],
  ['/admin/email', 'Send Email'],
  ['/admin/edit-skills', 'Skills'],
  ['/admin/settings', 'Settings']
];

const checkInLinks = [
  ['/pulse', 'Pulse'],
  ['/checkins', 'Quarterly']
];

const directoryLinks = [
  ['/guilds', 'Guilds & Communities'],
  ['/people', 'People'],
  ['/teams', 'Teams']
];

const getFeedbackLinks = (
  canViewFeedbackAnswer,
  canViewFeedbackRequest,
  canViewReviewPeriod
) => {
  const links = [];
  if (canViewFeedbackAnswer) links.push(['/feedback/view', 'View Feedback']);
  if (canViewFeedbackRequest)
    links.push(['/feedback/received-requests', 'Received Requests']);
  if (canViewReviewPeriod)
    links.push(
      ['/feedback/reviews', 'Reviews'],
      ['/feedback/self-reviews', 'Self-Reviews']
    );
  return links;
};

const isCollapsibleListOpen = (linksArr, loc) => {
  for (let i = 0; i < linksArr.length; i++) {
    if (linksArr[i][0] === loc) return true;
  }
  return false;
};

function Menu({ children }) {
  const { state, dispatch } = useContext(AppContext);
  const { userProfile } = state;
  const csrf = selectCsrfToken(state);
  const { id, workEmail } =
    userProfile && userProfile.memberProfile ? userProfile.memberProfile : {};
  const isAdmin = selectIsAdmin(state);
  const hasReportPermission = selectHasReportPermission(state);
  const canViewFeedbackAnswer = selectCanViewFeedbackAnswerPermission(state);
  const canViewFeedbackRequest = selectCanViewFeedbackRequestPermission(state);
  const canViewReviewPeriod = selectCanViewReviewPeriodPermission(state);

  const theme = useTheme();
  const location = useLocation();

  const [mobileOpen, setMobileOpen] = useState(false);
  const [open, setOpen] = useState(false);
  const [showHoursUpload, setShowHoursUpload] = useState(false);
  const [selectedFile, setSelectedFile] = useState(null);
  const feedbackLinks = getFeedbackLinks(
    canViewFeedbackAnswer,
    canViewFeedbackRequest,
    canViewReviewPeriod
  );

  const getReportLinks = () => {
    const links = [];

    if (selectHasAnniversaryReportPermission(state)) {
      links.push(['/anniversary-reports', 'Anniversaries']);
    }

    if (selectHasBirthdayReportPermission(state)) {
      links.push(['/birthday-reports', 'Birthdays']);
    }

    if (selectHasCheckinsReportPermission(state)) {
      links.push(['/checkins-reports', 'Check-ins']);
    }

    if (selectHasViewPulseReportPermission(state)) {
      links.push(['/pulse-reports', 'Pulses']);
    }

    if (selectHasSkillsReportPermission(state)) {
      links.push(['/skills-reports', 'Skills']);
    }

    if (selectHasTeamSkillsReportPermission(state)) {
      links.push(['/team-skills-reports', 'Team Skills']);
    }

    return links;
  };

  const uploadFile = async file => {
    if (!file) {
      return;
    }
    let formData = new FormData();
    formData.append('file', file);
    let res = await postEmployeeHours(csrf, formData);
    if (res?.error) {
      let error = res?.error?.response?.data?.message;
      //parse employee id from error
      let tmpError = error.includes('Detail: Key (employeeid)=(')
        ? error.split('Detail: Key (employeeid)=(')
        : null;
      tmpError = tmpError && tmpError[1].split(' ')[0].slice(0, -1);
      let newError;
      if (tmpError) {
        newError = `Employee id ${tmpError} doesn't exist in system, please fix the .csv file and upload again`;
      } else {
        newError = "Hmm....we couldn't upload the file. Try again.";
      }
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'error',
          toast: newError
        }
      });
    }
    const data = res?.payload?.data;
    if (data) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'success',
          toast: `File was successfully uploaded`
        }
      });
      closeHoursUpload();
    }
  };

  const [checkInOpen, setCheckInOpen] = useState(
    isCollapsibleListOpen(checkInLinks, location.pathname)
  );
  const [directoryOpen, setDirectoryOpen] = useState(
    isCollapsibleListOpen(directoryLinks, location.pathname)
  );
  const [adminOpen, setAdminOpen] = useState(
    isCollapsibleListOpen(adminLinks, location.pathname)
  );
  const [reportsOpen, setReportsOpen] = useState(
    isCollapsibleListOpen(getReportLinks(), location.pathname)
  );
  const [feedbackOpen, setFeedbackOpen] = useState(
    isCollapsibleListOpen(feedbackLinks, location.pathname)
  );
  const anchorRef = useRef(null);
  const handleToggle = () => {
    setOpen(prevOpen => !prevOpen);
  };

  // return focus to the button when we transitioned from !open -> open
  const prevOpen = useRef(open);
  useEffect(() => {
    if (prevOpen.current === true && open === false) {
      anchorRef.current.focus();
    }

    prevOpen.current = open;
  }, [open]);

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const toggleReports = () => {
    setReportsOpen(!reportsOpen);
  };

  const toggleFeedback = () => {
    setFeedbackOpen(!feedbackOpen);
  };

  const toggleCheckIn = () => {
    setCheckInOpen(!checkInOpen);
  };

  const toggleDirectory = () => {
    setDirectoryOpen(!directoryOpen);
  };

  const toggleAdmin = () => {
    setAdminOpen(!adminOpen);
  };

  const closeSubMenus = () => {
    setReportsOpen(false);
    setDirectoryOpen(false);
    setFeedbackOpen(false);
  };

  const closeHoursUpload = () => {
    setShowHoursUpload(false);
    setSelectedFile(null);
  };

  const openHoursUpload = () => {
    setShowHoursUpload(true);
  };

  const isLinkSelected = path => {
    // /checkins route is special case as additional info is added to url
    if (path === '/checkins' && location.pathname.includes(`${path}/`))
      return true;
    return location.pathname === path ? true : false;
  };

  const createLinkJsx = (path, name, isSubLink) => {
    return (
      <ListItemButton
        key={path}
        component={Link}
        to={path}
        className={isSubLink ? classes.nested : null}
        onClick={
          isSubLink
            ? undefined
            : () => {
                closeSubMenus();
              }
        }
        selected={isLinkSelected(path)}
      >
        <ListItemText
          classes={isSubLink ? { primary: classes.subListItem } : null}
          primary={name}
        />
      </ListItemButton>
    );
  };

  const onFileSelected = e => {
    setSelectedFile(e.target.files[0]);
  };

  const createListJsx = (listArr, isSublink) => {
    return listArr.map(listItem => {
      const [path, name] = listItem;
      return createLinkJsx(path, name, isSublink);
    });
  };

  const drawer = (
    <div>
      <div className={classes.toolbar} />
      <div style={{ display: 'flex', justifyContent: 'center' }}>
        <img
          alt="Object Computing, Inc."
          src="/img/ocicube-color.png"
          style={{ width: '50%' }}
        />
      </div>

      <List component="nav" className={classes.listStyle}>
        <div>
          <span className="Menu-listItem">
            {createLinkJsx('/', 'HOME', false)}
          </span>

          {isAdmin && (
            <>
              <ListItemButton
                onClick={toggleAdmin}
                className={classes.listItem}
              >
                <ListItemText primary="ADMIN" />
              </ListItemButton>
              <Collapse in={adminOpen} timeout="auto" unmountOnExit>
                {createListJsx(adminLinks, true)}
                {isAdmin && (
                  <ListItemButton
                    sx={{ pl: 4, py: 1.5 }}
                    className={classes.listItem}
                    onClick={openHoursUpload}
                  >
                    Upload Hours
                  </ListItemButton>
                )}
              </Collapse>
            </>
          )}

          <ListItemButton onClick={toggleCheckIn} className={classes.listItem}>
            <ListItemText primary="CHECK-IN" />
          </ListItemButton>
          <Collapse in={checkInOpen} timeout="auto" unmountOnExit>
            {createListJsx(checkInLinks, true)}
          </Collapse>

          <ListItemButton
            onClick={toggleDirectory}
            className={classes.listItem}
          >
            <ListItemText primary="DIRECTORY" />
          </ListItemButton>
          <Collapse in={directoryOpen} timeout="auto" unmountOnExit>
            {createListJsx(directoryLinks, true)}
          </Collapse>

          <ListItemButton onClick={toggleFeedback} className={classes.listItem}>
            <ListItemText primary="FEEDBACK" />
          </ListItemButton>
          <Collapse in={feedbackOpen} timeout="auto" unmountOnExit>
            {createListJsx(feedbackLinks, true)}
          </Collapse>

          {hasReportPermission && (
            <React.Fragment>
              <ListItemButton
                onClick={toggleReports}
                className={classes.listItem}
              >
                <ListItemText primary="REPORTS" />
              </ListItemButton>
              <Collapse in={reportsOpen} timeout="auto" unmountOnExit>
                {createListJsx(getReportLinks(), true)}
              </Collapse>
            </React.Fragment>
          )}
        </div>
        {children}
      </List>
    </div>
  );

  return (
    <div className={classes.root}>
      <CssBaseline />
      <AppBar position="fixed" className={classes.appBar}>
        <Toolbar>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            edge="start"
            onClick={handleDrawerToggle}
            className={classes.menuButton}
            size="large"
          >
            <MenuIcon />
          </IconButton>
        </Toolbar>
        <Link to={`/profile/${id}`}>
          <Avatar
            src={getAvatarURL(workEmail)}
            style={{
              position: 'absolute',
              cursor: 'pointer',
              right: '5px',
              top: '10px',
              textDecoration: 'none'
            }}
            alt={`${userProfile?.name}'s avatar`}
          />
        </Link>
      </AppBar>
      <nav className={classes.drawer}>
        <Drawer
          sx={{ display: { sm: 'none', xs: 'block' } }}
          variant="temporary"
          disablePortal
          anchor={theme.direction === 'rtl' ? 'right' : 'left'}
          open={mobileOpen}
          onClose={handleDrawerToggle}
          classes={{
            paper: classes.drawerPaper
          }}
          ModalProps={{
            keepMounted: true // Better open performance on mobile.
          }}
        >
          {drawer}
        </Drawer>
        <Drawer
          classes={{
            paper: classes.drawerPaper
          }}
          variant="permanent"
          open
          sx={{ display: { xs: 'none', sm: 'block' } }}
        >
          {drawer}
        </Drawer>
        <Modal
          open={showHoursUpload}
          onBackdropClick={closeHoursUpload}
          onClose={closeHoursUpload}
        >
          <div className="hours-upload-modal">
            <Button color="primary">
              <label htmlFor="file-upload">
                <h3>Choose A CSV File</h3>
                <input
                  accept=".csv"
                  id="file-upload"
                  onChange={e => onFileSelected(e)}
                  style={{ display: 'none' }}
                  type="file"
                />
              </label>
            </Button>
            <div className="buttons">
              <Button color="secondary" onClick={closeHoursUpload}>
                Cancel
              </Button>
              {selectedFile && (
                <Button
                  color="primary"
                  onClick={() => uploadFile(selectedFile)}
                >
                  Upload &nbsp;<strong>{selectedFile.name}</strong>
                </Button>
              )}
            </div>
          </div>
        </Modal>
      </nav>
      <main className={classes.content}>
        <div className={classes.toolbar} />
      </main>
    </div>
  );
}

export default Menu;
