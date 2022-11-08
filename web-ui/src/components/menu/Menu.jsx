import React, { useContext, useEffect, useRef, useState } from "react";

import { postEmployeeHours } from "../../api/hours";
import { selectCsrfToken, selectIsAdmin, selectSupervisorByUserId } from "../../context/selectors";
import { UPDATE_TOAST } from "../../context/actions";

import { useLocation, Link } from "react-router-dom";
import { AppContext } from "../../context/AppContext";
import { getAvatarURL } from "../../api/api";
import AvatarMenu from "@mui/material/Menu";

import MenuIcon from "@mui/icons-material/Menu";
import { styled, useTheme } from "@mui/material/styles";
import {
  AppBar,
  Avatar,
  Button,
  CssBaseline,
  Collapse,
  Drawer,
  IconButton,
  List,
  ListItem,
  ListItemText,
  MenuItem,
  Modal,
  Toolbar,
} from "@mui/material";

import "./Menu.css";

const drawerWidth = 150;
const PREFIX = 'Menu';
const classes = {
  root: `${PREFIX}-root`,
  drawer: `${PREFIX}-drawer`,
  appBar: `${PREFIX}-appBar`,
  menuButton: `${PREFIX}-menuButton`,
  drawerPaper: `${PREFIX}-drawerPaper`,
  content: `${PREFIX}-content`,
  listStyle: `${PREFIX}-listStyle`,
  nested: `${PREFIX}-nested`,
  subListItem: `${PREFIX}-subListItem`
};

const Root = styled('div')(({theme}) => ({
  [`&.${classes.root}`]: {
    display: 'flex',
    paddingRight: `${drawerWidth}px`
  },
  [`& .${classes.drawer}`]: {
    [theme.breakpoints.up("sm")]: {
      width: drawerWidth,
      flexShrink: 0,
    },
  },
  [`& .${classes.appBar}`]: {
    backgroundColor: "#e4e3e4",
    [theme.breakpoints.up("sm")]: {
      width: `calc(100% - ${drawerWidth}px)`,
      marginLeft: drawerWidth,
    },
  },
  [`& .${classes.menuButton}`]: {
    marginRight: theme.spacing(2),
    [theme.breakpoints.up("sm")]: {
      display: "none",
    },
  },
  // necessary for content to be below app bar
  // toolbar: theme.mixins.toolbar,
  [`& .${classes.drawerPaper}`]: {
    width: drawerWidth,
    backgroundColor: "#a5a4a8",
  },
  [`& .${classes.content}`]: {
    flexGrow: 1,
    padding: theme.spacing(3),
  },
  [`& .${classes.listStyle}`]: {
    textDecoration: "none",
    color: "white",
    textAlign: "left",
  },
  [`& .${classes.nested}`]: {
    paddingLeft: theme.spacing(4),
  },
  [`& .${classes.subListItem}`]: {
    fontSize: "0.9rem",
  }
}));

const adminLinks = [
  // ["/admin/permissions", "Permissions"],
  ["/admin/roles", "Roles"],
  ["/admin/users", "Users"],
  ["/admin/email", "Send Email"],
  ["/admin/edit-skills", "Skills"],
];

const directoryLinks = [
  ["/guilds", "Guilds"],
  ["/people", "People"],
  ["/teams", "Teams"],
];

const getFeedbackLinks = (isAdmin, isPDL, isSupervisor) => {
  const links = [];
  if(isAdmin || isPDL) links.push(["/feedback/view", "View Feedback"]);
  links.push(["/feedback/received-requests", "Received Requests"]);
  if(isSupervisor || isAdmin) links.push(["/feedback/reviews", "Reviews"])
  links.push(["/feedback/self-reviews", "Self-Reviews"]);
  return links;
};

const reportsLinks = [
  ["/birthday-anniversary-reports", "Birthdays & Anniversaries"],
  ["/checkins-reports", "Check-ins"],
  ["/skills-reports", "Skills"],
  ["/team-skills-reports", "Team Skills"],
];

const isCollapsibleListOpen = (linksArr, loc) => {
  for (let i = 0; i < linksArr.length; i++) {
    if (linksArr[i][0] === loc) return true;
  }
  return false;
};

function Menu() {
  const { state, dispatch } = useContext(AppContext);
  const { userProfile } = state;
  const csrf = selectCsrfToken(state);
  const { id, workEmail } =
    userProfile && userProfile.memberProfile ? userProfile.memberProfile : {};
  const isAdmin = selectIsAdmin(state);
  const isPDL =
    userProfile && userProfile.role && userProfile.role.includes("PDL");
  const isSupervisor = selectSupervisorByUserId(state) === true;


  const theme = useTheme();
  const location = useLocation();

  const [mobileOpen, setMobileOpen] = useState(false);
  const [open, setOpen] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);
  const [showHoursUpload, setShowHoursUpload] = useState(false);
  const [selectedFile, setSelectedFile] = useState(null);
  const feedbackLinks = getFeedbackLinks(isAdmin, isPDL, isSupervisor);

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const uploadFile = async (file) => {
    if (!file) {
      return;
    }
    let formData = new FormData();
    formData.append("file", file);
    let res = await postEmployeeHours(csrf, formData);
    if (res?.error) {
      let error = res?.error?.response?.data?.message;
      //parse employee id from error
      let tmpError = error.includes("Detail: Key (employeeid)=(")
        ? error.split("Detail: Key (employeeid)=(")
        : null;
      tmpError = tmpError && tmpError[1].split(" ")[0].slice(0, -1);
      let newError;
      if (tmpError) {
        newError = `Employee id ${tmpError} doesn't exist in system, please fix the .csv file and upload again`;
      } else {
        newError = "Hmm....we couldn't upload the file. Try again.";
      }
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: newError,
        },
      });
    }
    const data = res?.payload?.data;
    if (data) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "success",
          toast: `File was successfully uploaded`,
        },
      });
      closeHoursUpload();
    }
  };

  const [directoryOpen, setDirectoryOpen] = useState(
    isCollapsibleListOpen(directoryLinks, location.pathname)
  );
  const [adminOpen, setAdminOpen] = useState(
    isCollapsibleListOpen(adminLinks, location.pathname)
  );
  const [reportsOpen, setReportsOpen] = useState(
    isCollapsibleListOpen(reportsLinks, location.pathname)
  );
  const [feedbackOpen, setFeedbackOpen] = useState(
    isCollapsibleListOpen(feedbackLinks, location.pathname)
  )
  const anchorRef = useRef(null);
  const handleToggle = () => {
    setOpen((prevOpen) => !prevOpen);
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
  }

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

  const closeAvatarMenu = () => {
    setAnchorEl(null);
  };

  const closeHoursUpload = () => {
    setShowHoursUpload(false);
    setSelectedFile(null);
  };

  const openHoursUpload = () => {
    setShowHoursUpload(true);
  };

  const isLinkSelected = (path) => {
    // /checkins route is special case as additional info is added to url
    if (path === "/checkins" && location.pathname.includes(`${path}/`))
      return true;
    return location.pathname === path ? true : false;
  };

  const createLinkJsx = (path, name, isSubLink) => {
    return (
      <ListItem
        key={path}
        component={Link}
        to={path}
        className={isSubLink ? classes.nested : null}
        button
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
      </ListItem>
    );
  };

  const onFileSelected = (e) => {
    setSelectedFile(e.target.files[0]);
  };

  const createListJsx = (listArr, isSublink) => {
    return listArr.map((listItem) => {
      const [path, name] = listItem;
      return createLinkJsx(path, name, isSublink);
    });
  };

  const drawer = (
    <div>
      <div className={classes.toolbar} />
      <div style={{ display: "flex", justifyContent: "center" }}>
        <img
          alt="Object Computing, Inc."
          src="/img/ocicube-white.png"
          style={{ width: "50%" }}
        />
      </div>

      <List component="nav" className={classes.listStyle}>
        {createLinkJsx("/", "HOME", false)}
        {isAdmin && (
          <>
            <ListItem button onClick={toggleAdmin} className={classes.listItem}>
              <ListItemText primary="ADMIN" />
            </ListItem>
            <Collapse in={adminOpen} timeout="auto" unmountOnExit>
              {createListJsx(adminLinks, true)}
            </Collapse>
          </>
        )}
        {createLinkJsx("/checkins", "CHECK-INS", false)}
        <ListItem button onClick={toggleDirectory} className={classes.listItem}>
          <ListItemText primary="DIRECTORY" />
        </ListItem>
        <Collapse in={directoryOpen} timeout="auto" unmountOnExit>
          {createListJsx(directoryLinks, true)}
        </Collapse>
        <ListItem
          button
          onClick={toggleFeedback}
          className={classes.listItem}
        >
          <ListItemText primary="FEEDBACK" />
        </ListItem>
        <Collapse in={feedbackOpen} timeout="auto" unmountOnExit>
          {createListJsx(feedbackLinks, true)}
        </Collapse>
        {isAdmin && (
          <React.Fragment>
            <ListItem
              button
              onClick={toggleReports}
              className={classes.listItem}
            >
              <ListItemText primary="REPORTS" />
            </ListItem>
            <Collapse in={reportsOpen} timeout="auto" unmountOnExit>
              {createListJsx(reportsLinks, true)}
            </Collapse>
          </React.Fragment>
        )}
      </List>
    </div>
  );

  return (
    <Root className={classes.root}>
      <CssBaseline />
      <AppBar position="fixed" className={classes.appBar}>
        <Toolbar>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            edge="start"
            onClick={handleDrawerToggle}
            className={classes.menuButton}
            size="large">
            <MenuIcon />
          </IconButton>
        </Toolbar>
        <div
          ref={anchorRef}
          aria-controls={open ? "menu-list-grow" : undefined}
          aria-haspopup="true"
          onClick={handleToggle}
        >
          <Avatar
            onClick={handleClick}
            src={getAvatarURL(workEmail)}
            style={{
              position: "absolute",
              cursor: "pointer",
              right: "5px",
              top: "10px",
              textDecoration: "none",
            }}
          />
          <AvatarMenu
            id="simple-menu"
            anchorEl={anchorEl}
            keepMounted
            open={Boolean(anchorEl)}
            onClose={closeAvatarMenu}
          >
            <MenuItem
              component={Link}
              onClick={closeAvatarMenu}
              to={`/profile/${id}`}
            >
              Profile
            </MenuItem>
            {isAdmin && (
              <MenuItem
                onClick={() => {
                  closeAvatarMenu();
                  openHoursUpload();
                }}
              >
                Upload Hours
              </MenuItem>
            )}
          </AvatarMenu>
        </div>
      </AppBar>
      <nav className={classes.drawer}>
        <Drawer
          sx={{display: {sm: 'none', xs: 'block'}}}
          variant="temporary"
          disablePortal
          anchor={theme.direction === "rtl" ? "right" : "left"}
          open={mobileOpen}
          onClose={handleDrawerToggle}
          classes={{
            paper: classes.drawerPaper,
          }}
          ModalProps={{
            keepMounted: true, // Better open performance on mobile.
          }}
        >
          {drawer}
        </Drawer>
        <Drawer
          classes={{
            paper: classes.drawerPaper,
          }}
          variant="permanent"
          open
          sx={{display: { xs: 'none', sm: 'block'}}}
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
                  onChange={(e) => onFileSelected(e)}
                  style={{ display: "none" }}
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
    </Root>
  );
}

export default Menu;
