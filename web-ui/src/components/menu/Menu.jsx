import React, { useContext, useEffect, useRef, useState } from "react";
import { useLocation, Link } from "react-router-dom";
import { AppContext } from "../../context/AppContext";
import { getAvatarURL } from "../../api/api";

import MenuIcon from "@material-ui/icons/Menu";
import { makeStyles, useTheme } from "@material-ui/core/styles";
import {
  AppBar,
  Avatar,
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
  listStyle: {
    textDecoration: "none",
    color: "white",
    textAlign: "left",
  },
  nested: {
    paddingLeft: theme.spacing(4),
  },
  subListItem: {
    fontSize: "0.9rem",
  }
}));

const directoryLinks = [
  ["/guilds", "Guilds"],
  ["/people", "People"],
  ["/teams", "Teams"],
];

const reportsLinks = [
  ["/checkins-reports", "Check-ins"],
  ["/skills-reports", "Skills"],
  ["/team-skills-reports", "Team Skills"],
  ["/birthday-anniversary-reports", "Birthdays & Anniversaries"],
]

const isCollapsibleListOpen = (linksArr, loc) => {
  for (let i = 0; i < linksArr.length; i++) {
    if (linksArr[i][0] === loc) return true;
  }
  return false;
};

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
  const location = useLocation();
  const [directoryOpen, setDirectoryOpen] = useState(
    isCollapsibleListOpen(directoryLinks, location.pathname)
  );
  const [reportsOpen, setReportsOpen] = useState(
    isCollapsibleListOpen(reportsLinks, location.pathname)
  );
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

  const toggleDirectory = () => {
    setDirectoryOpen(!directoryOpen);
  };

  const closeSubMenus = () => {
    setReportsOpen(false);
    setDirectoryOpen(false);
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
            : () => { closeSubMenus(); }
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

  const createListJsx = (listArr, isSublink) => {
    return listArr.map((listItem) => {
      const [path, name] = listItem;
      return createLinkJsx(path, name, isSublink);
    });
  };

  const drawer = (
    <>
      <div className={classes.toolbar} />
      <div style={{ display: "flex", justifyContent: "center" }}>
        <img
          alt="Object Computing, Inc."
          src="/img/ocicube-white.png"
          style={{ width: "50%" }}
        />
      </div>

      <List component="nav" className={classes.listStyle}>
        {createLinkJsx("/home", "HOME", false)}
        {isAdmin && createLinkJsx("/admin", "ADMIN", false)}
        {createLinkJsx("/checkins", "CHECK-INS", false)}
        <ListItem
            button
            onClick={toggleDirectory}
            className={classes.listItem}
          >
            <ListItemText primary="DIRECTORY" />
          </ListItem>
        <Collapse in={directoryOpen} timeout="auto" unmountOnExit>
          {createListJsx(directoryLinks, true)}
        </Collapse>
      
      {isAdmin && (
          <>
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
            {createLinkJsx("/edit-skills", "SKILLS", false)}
          </>
      )}
      </List>
    </>
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
          <Avatar
            component={Link}
            to={`/profile/${id}`}
            src={getAvatarURL(workEmail)}
            style={{
              position: "absolute",
              cursor: "pointer",
              right: "5px",
              top: "10px",
              textDecoration: "none",
            }}
          />
        </div>
      </AppBar>
      <nav className={classes.drawer}>
        <Hidden smUp implementation="css">
          <Drawer
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
