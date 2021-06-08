import React, { useContext, useEffect, useRef, useState } from "react";
import { Link, useHistory, useLocation } from "react-router-dom";
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
  },
  listStyle: {
    textDecoration: "none", color: "white", 
  },
  listItem: {
    textAlign: 'center'
  },
  subListItem: {
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
  const location = useLocation();
  const history = useHistory();
  const [reportsOpen, setReportsOpen] = useState(false);
  const [directoryOpen, setDirectoryOpen] = useState(false);
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

  useEffect(() => {
    const loc = location.pathname;
    if (loc === "/guilds" || loc === "/people" || loc === "/teams") {
      if (!directoryOpen) setDirectoryOpen(true);
    }
    if (loc === "/checkins-reports" || loc === "skills-reports") {
      if(!reportsOpen) setReportsOpen(true);
    }
  }, [location])

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
    if (path === "/checkins" && location.pathname.includes("/checkins/")) return true;
    return location.pathname === path ? true : false;
  }

  const createLinkJsx = (path, name, isSubLink) => {
    return (
      <ListItem
        key={path}
        className={isSubLink? `${classes.listItem} ${classes.nested}` : classes.listItem}
        button
        onClick={isSubLink? 
          () => history.push(path): 
          () => {
            history.push(path)
            closeSubMenus()
          }
        }
        selected={isLinkSelected(path)}
      >
        <ListItemText classes={isSubLink? {primary: classes.subListItem} : null} primary={name} />
      </ListItem>
    )
  }

  const createListJsx = (listArr, isSublink) => {
    return listArr.map(listItem => {
      const [path, name] = listItem;
      return createLinkJsx(path, name, isSublink);
    })
  }

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
        {createListJsx(
          [
            ["/home", "HOME",], 
            ["/checkins", "CHECK-INS",]
          ], 
          false)
        }
      </List>
      <Button
        onClick={toggleDirectory}
        size="large"
        style={{ color: "white", width: "100%" }}
      >
        Directory
      </Button>
      <Collapse in={directoryOpen} timeout="auto" unmountOnExit>
        <List className={classes.listStyle} component="nav" disablePadding>
          {createListJsx(
            [
              ["/guilds", "GUILDS"], 
              ["/people", "PEOPLE"], 
              ["/teams", "TEAMS"]
            ], 
              true)
          }
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
          <List className={classes.listStyle} component="nav" disablePadding>
            <Collapse in={reportsOpen} timeout="auto" unmountOnExit>
                {createListJsx(
                  [
                    ["/checkins-reports", "CHECK-INS"], 
                    ["/skills-reports", "SKILLS"]
                  ], 
                  true)
                }
            </Collapse>
            {createLinkJsx("/edit-skills", "SKILLS", false)}
          </List>
        </div>
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
