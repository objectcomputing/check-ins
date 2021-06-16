import React, { useContext, useEffect, useRef, useState } from "react";
import { Link } from "react-router-dom";
import { AppContext } from "../../context/AppContext";
import { getAvatarURL } from "../../api/api";

import MenuIcon from "@material-ui/icons/Menu";
import { makeStyles, useTheme } from "@material-ui/core/styles";
import {
  AppBar,
  Avatar,
  Button,
  CssBaseline,
  Collapse,
  Drawer,
  Hidden,
  IconButton,
  List,
  ListItem,
  ListItemText,
  Toolbar,
} from "@material-ui/core";

import "./Menu.css";

const drawerWidth = 150;

const useStyles = makeStyles((theme) => ({
  root: {
    display: "flex",
  },
  drawer: {
    [theme.breakpoints.up("sm")]: {
      width: drawerWidth,
      flexShrink: 0,
    },
  },
  appBar: {
    backgroundColor: "#e4e3e4",
    [theme.breakpoints.up("sm")]: {
      width: `calc(100% - ${drawerWidth}px)`,
      marginLeft: drawerWidth,
    },
  },
  menuButton: {
    marginRight: theme.spacing(2),
    [theme.breakpoints.up("sm")]: {
      display: "none",
    },
  },
  // necessary for content to be below app bar
  // toolbar: theme.mixins.toolbar,
  drawerPaper: {
    width: drawerWidth,
    backgroundColor: "#a5a4a8",
  },
  content: {
    flexGrow: 1,
    padding: theme.spacing(3),
  },
  nested: {
    paddingLeft: theme.spacing(4),
    textAlign: "center",
  },
  ListItemText : {
    fontSize: "0.9rem",
  }
}));

function Menu() {
  const { state } = useContext(AppContext);
  const { userProfile } = state;
  const { id, workEmail } =
    userProfile && userProfile.memberProfile ? userProfile.memberProfile : {};
  const isAdmin =
    userProfile && userProfile.role && userProfile.role.includes("ADMIN");
  const classes = useStyles();
  const theme = useTheme();
  const [mobileOpen, setMobileOpen] = useState(false);
  const [open, setOpen] = useState(false);
  const [reportsOpen, setReportsOpen] = useState(false);
  const [directoryOpen, setDirectoryOpen] = useState(false);
  const [fDirectoryOpen, setFDirectoryOpen] = useState(false);
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

  const toggleDirectory = (dNum) => {
    switch(dNum) {
      case 0:
        setDirectoryOpen(!directoryOpen);
        break;
      case 1:
        setFDirectoryOpen(!fDirectoryOpen);
        break;
      default:
        setDirectoryOpen(!directoryOpen);
    }
  };

  const closeSubMenus = () => {
    setReportsOpen(false);
    setDirectoryOpen(false);
  };

  const linkStyle = { textDecoration: "none", color: "white" };

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
      <br />
      <Button onClick={closeSubMenus} size="large" style={{ width: "100%" }}>
        <Link style={linkStyle} to="/home">
          Home
        </Link>
      </Button>
      <br />
      <Button onClick={closeSubMenus} size="large" style={{ width: "100%" }}>
        <Link style={linkStyle} to="/checkins">
          Check-ins
        </Link>
      </Button>
      <br />
      <Button
        onClick={toggleDirectory(0)}
        size="large"
        style={{ color: "white", width: "100%" }}
      >
        Directory
      </Button>
      <br />
      <Collapse in={directoryOpen} timeout="auto" unmountOnExit>
        <List component="div" disablePadding>
          <Link style={linkStyle} to="/guilds">
            <ListItem button className={classes.nested}>
              <ListItemText classes={{primary:classes.ListItemText}} primary="GUILDS" />
            </ListItem>
          </Link>
          <Link style={linkStyle} to="/directory">
            <ListItem button className={classes.nested}>
              <ListItemText classes={{primary:classes.ListItemText}} primary="PEOPLE" />
            </ListItem>
          </Link>
          <Link style={linkStyle} to="/teams">
            <ListItem button className={classes.nested}>
              <ListItemText classes={{primary: classes.ListItemText}} primary="TEAMS" />
            </ListItem>
          </Link>
        </List>
      </Collapse>
      <Button
        onClick={toggleDirectory(1)}
        size="large"
        style={{ color: "white", width: "100%" }}
      >
        Feedback
      </Button>
      <Collapse in={directoryOpen} timeout="auto" unmountOnExit>
        <List component="div" disablePadding>
          <Link style={linkStyle} to="/feedback/request">
            <ListItem button className={classes.nested}>
              <ListItemText classes={{primary:classes.ListItemText}} primary="REQUEST" />
            </ListItem>
          </Link>
          <Link style={linkStyle}>
            <ListItem button className={classes.nested}>
              <ListItemText classes={{primary:classes.ListItemText}} primary="VIEW" />
            </ListItem>
          </Link>
        </List>
      </Collapse>
      {isAdmin && (
        <div>
          <Button
            onClick={toggleReports}
            size="large"
            style={{ color: "white", width: "100%" }}
          >
            Reports
          </Button>
          <Collapse in={reportsOpen} timeout="auto" unmountOnExit>
            <List component="div" disablePadding>
              <Link style={linkStyle} to="/checkins-reports">
                <ListItem button className={classes.nested}>
                  <ListItemText classes={{primary: classes.ListItemText}} primary="CHECKINS" />
                </ListItem>
              </Link>
              <Link style={linkStyle} to="/skills-reports">
                <ListItem button className={classes.nested}>
                  <ListItemText classes={{primary: classes.ListItemText}} primary="SKILLS" />
                </ListItem>
              </Link>
            </List>
          <Link style={linkStyle} to="/team-skills-reports">
            <ListItem button className={classes.nested}>
              <ListItemText primary="TEAM SKILLS" />
            </ListItem>
          </Link>
          </Collapse>
        </div>
      )}
      {isAdmin && (
        <Button onClick={closeSubMenus} size="large" style={{ width: "100%" }}>
          <Link style={linkStyle} to="/edit-skills">
            Skills
          </Link>
        </Button>
      )}
    </div>
  );

  return (
    <div className={classes.root} style={{ paddingRight: `${drawerWidth}px` }}>
      <CssBaseline />
      <AppBar position="fixed" className={classes.appBar}>
        <Toolbar>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            edge="start"
            onClick={handleDrawerToggle}
            className={classes.menuButton}
          >
            <MenuIcon />
          </IconButton>
        </Toolbar>
        <div
          ref={anchorRef}
          aria-controls={open ? "menu-list-grow" : undefined}
          aria-haspopup="true"
          onClick={handleToggle}
        >
          <Link style={{ textDecoration: "none" }} to={`/profile/${id}`}>
            <Avatar
              src={getAvatarURL(workEmail)}
              style={{
                position: "absolute",
                right: "5px",
                top: "10px",
              }}
            />
          </Link>
        </div>
      </AppBar>
      <nav className={classes.drawer}>
        <Hidden smUp implementation="css">
          <Drawer
            variant="temporary"
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
        </Hidden>
        <Hidden xsDown implementation="css">
          <Drawer
            classes={{
              paper: classes.drawerPaper,
            }}
            variant="permanent"
            open
          >
            {drawer}
          </Drawer>
        </Hidden>
      </nav>
      <main className={classes.content}>
        <div className={classes.toolbar} />
      </main>
    </div>
  );
}

export default Menu;
